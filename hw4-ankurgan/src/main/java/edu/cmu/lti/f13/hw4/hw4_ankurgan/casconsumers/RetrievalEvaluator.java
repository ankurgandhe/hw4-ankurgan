package edu.cmu.lti.f13.hw4.hw4_ankurgan.casconsumers;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_ankurgan.utils.Utils;

public class RetrievalEvaluator extends CasConsumer_ImplBase {

	public ArrayList<DocumentScore> documentScoreList;
	public Map<String, Integer> globalTokenMap;

	public void initialize() throws ResourceInitializationException {
		documentScoreList = new ArrayList<DocumentScore>();
		globalTokenMap = new HashMap<String, Integer>();
	}

	/**
	 * 1. construct the global word dictionary 2. keep the word frequency for
	 * each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas = aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
		// Iterate over the annotated documents
		if (it.hasNext()) {
			Document doc = (Document) it.next();

			// Put annotated information into a Document score object
			DocumentScore docScore = new DocumentScore();
			docScore.queryID = doc.getQueryID();
			docScore.relevanceValue = doc.getRelevanceValue();
			docScore.text = doc.getText();

			// Read FSlist and create a Token -> frequency Map
			FSList fsTokenList = doc.getTokenList();
			docScore.docTokenMap = createMapFromFsList(fsTokenList, "upper");
			docScore.docLowerTokenMap = createMapFromFsList(fsTokenList, "lower");
			docScore.docLemmaMap = createMapFromFsList(fsTokenList, "lemma");
			documentScoreList.add(docScore);
		}
	}

	/*
	 * Creates a Map out of the given fsList
	 */
	private Map<String, Integer> createMapFromFsList(FSList fsTokenList,
			String type) {
		ArrayList<Token> tokenList = Utils.fromFSListToCollection(fsTokenList,
				Token.class);
		String word;
		Integer freq;
		Map<String, Integer> tokenMap = new HashMap<String, Integer>();
		for (Token token : tokenList) {
			word = token.getText();
			if (type=="lower")
				word = token.getText().toLowerCase();
			else if (type=="lemma"){
				word = token.getLemma();
			}
			else
				word = token.getText();
			
			freq = token.getFrequency();
			tokenMap.put(word, freq);
			if (globalTokenMap.containsKey(word)) {
				globalTokenMap.put(word, globalTokenMap.get(word) + freq);
			} else
				globalTokenMap.put(word, freq);
		}
		return tokenMap;

	}

	
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);
		Integer currentQid = -1, qid;
		DocumentScore queryDocScore = null;
		List<DocumentScore> documentPerQueryList = new ArrayList<DocumentScore>();
		Integer rank;
		ArrayList<Integer> queryRanks = new ArrayList<Integer>();
		int docRelevance = -1;
		double scoreSimilarity = 0.0;
		// Stop Words list
		ArrayList<String> stopWords = null;
		if (true)
			stopWords = Utils.readStopWords();

		// We are assuming documents come in order : first query, followed by
		// retrieved documents
		for (DocumentScore docScore : documentScoreList) {
			docRelevance = docScore.relevanceValue;
			qid = docScore.queryID;

			if (qid != currentQid) {
				// compute rank of retrieved sentences, stored in DocScoreList
				if (documentPerQueryList.size() > 0) {
					rank = computeRank(documentPerQueryList);
					queryRanks.add(rank);
					documentPerQueryList.clear();
				}
				queryDocScore = docScore;
				//System.out.println("Question:" + docScore.text);
				assert docRelevance == -99;
				currentQid = qid;
				continue;
			}

			// compute the cosine similarity measure
			scoreSimilarity = computeSimilarity(queryDocScore, docScore,
					stopWords);
			//System.out.println(currentQid + ":" + scoreSimilarity + ":"
			//		+ docScore.text);
			docScore.score = scoreSimilarity;
			documentPerQueryList.add(docScore);
		}

		if (documentPerQueryList.size() > 0) {
			rank = computeRank(documentPerQueryList);
			queryRanks.add(rank);
			documentPerQueryList.clear();
		}

		// compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr(queryRanks);
		System.out.println("(MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}

	private double computeSimilarity(DocumentScore queryDocScore,
			DocumentScore docScore, ArrayList<String> stopWords) {
		ArrayList<String> stopWordsEmpty = new ArrayList<String>();
		stopWords = stopWordsEmpty;
		double score=0.0;
		ArrayList<Double> scoreList = new ArrayList<Double>();
		ArrayList<Double> weightList = new ArrayList<Double>();
		score = Utils.computeCosineSimilarity(queryDocScore.docLowerTokenMap,
				docScore.docLowerTokenMap, stopWords);
		scoreList.add(score);
		weightList.add(0.4);
		score = Utils.computeJaccardSimilarity(queryDocScore.docTokenMap,
				docScore.docTokenMap, stopWords);
		scoreList.add(score);
		weightList.add(0.0);
		score = Utils.computeJaccardSimilarity(queryDocScore.docLemmaMap,
				docScore.docLemmaMap, stopWords);
		scoreList.add(score);
		weightList.add(1.0);
		
		score = Utils.computeScore(scoreList,weightList);
				
		score = Math.round(score * 1000) / 1000.0d;
		return score;
	}

	/*
	 * Rank computation given a list of document score objects
	 */
	private Integer computeRank(List<DocumentScore> documentPerQueryList) {
		Integer rank;
		Collections.sort(documentPerQueryList, new ScoreComparator());
		rank = 1;
		for (DocumentScore docScore : documentPerQueryList) {
			// System.out.println(docScore.queryID + ":" + docScore.score + ":"
			// + docScore.relevanceValue);
			if (docScore.relevanceValue == 1) {
				System.out.println("Score:" + docScore.score + "\trank=" + rank
						+ "\trel=" + docScore.relevanceValue + "\tqid="
						+ docScore.queryID + " " + docScore.text);
				break;
			}
			rank++;
		}
		return rank;
	}

	/**
	 * @return mrr
	 */
	private double compute_mrr(ArrayList<Integer> queryRanks) {
		double metric_mrr = 0.0;

		// TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
		for (Integer rank : queryRanks) {
			metric_mrr +=  (1.0 / rank);
		}
		metric_mrr /= queryRanks.size();
		metric_mrr = Math.round(metric_mrr * 100) / 100.0d;
		return metric_mrr;
	}

	/*
	 * Class implements comparator for the documentScore class
	 */
	class ScoreComparator implements Comparator<DocumentScore> {
		public int compare(DocumentScore o1, DocumentScore o2) {
			if (o1.score <= o2.score)
				return 1;
			else
				return -1;

		}
	}

}
