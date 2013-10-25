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

	public class DocumentScore {
		public Integer queryID;
		public Integer relevanceValue;
		public String text;
		public Map<String, Integer> docTokenMap;
		public double score;

	}

	public ArrayList<DocumentScore> documentScoreList;
	public Map<String, Integer> globalTokenMap;

	public void initialize() throws ResourceInitializationException {

		documentScoreList = new ArrayList<DocumentScore>();
		globalTokenMap = new HashMap<String, Integer>();
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
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

		if (it.hasNext()) {
			Document doc = (Document) it.next();

			// Make sure that your previous annotators have populated this in
			// CAS
			FSList fsTokenList = doc.getTokenList();
			ArrayList<Token> tokenList = Utils.fromFSListToCollection(
					fsTokenList, Token.class);

			DocumentScore docScore = new DocumentScore();
			docScore.queryID = doc.getQueryID();
			docScore.relevanceValue = doc.getRelevanceValue();
			docScore.text = doc.getText();

			// Do something useful here
			String word;
			Integer freq;
			Map<String, Integer> tokenMap = new HashMap<String, Integer>();
			for (Token token : tokenList) {
				word = token.getText();
				freq = token.getFrequency();
				tokenMap.put(word, freq);
				if (globalTokenMap.containsKey(word)) {
					globalTokenMap.put(word, globalTokenMap.get(word) + freq);
				} else
					globalTokenMap.put(word, freq);

			}
			docScore.docTokenMap = tokenMap;
			documentScoreList.add(docScore);
		}

	}

	public Map<String, Integer> fsListToHashMap(FSList fsTokenList) {
		ArrayList<Token> tokenList = Utils.fromFSListToCollection(fsTokenList,
				Token.class);
		String word;
		Integer freq;
		Map<String, Integer> tokenMap = new HashMap<String, Integer>();
		for (Token token : tokenList) {
			word = token.getText();
			freq = token.getFrequency();
			tokenMap.put(word, freq);

		}
		return tokenMap;
	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);
		Integer currentQid = -1, qid;
		Map<String, Integer> queryTokenMap = null;
		Map<String, Integer> docTokenMap = null;
		List<DocumentScore> documentPerQueryList = new ArrayList<DocumentScore>();
		Integer rank;
		ArrayList<Integer> queryRanks = new ArrayList<Integer>();
		int docRelevance = -1;
		double cosineSimilarity = 0.0;
		// We are assuming documents come in order : first query, followed by
		// retrieved documents
		for (DocumentScore docScore : documentScoreList) {
			docTokenMap = docScore.docTokenMap;
			docRelevance = docScore.relevanceValue;
			qid = docScore.queryID;

			if (qid != currentQid) {
				// TODO : compute rank of retrieved sentences
				if (documentPerQueryList.size() > 0) {
					rank = computeRank(documentPerQueryList);
					// System.out.println(rank);
					queryRanks.add(rank);
					documentPerQueryList.clear();
				}
				queryTokenMap = docTokenMap;
				//System.out.println("Question:"+docScore.text);
				assert docRelevance == -99;
				currentQid = qid;
				continue;
			}

			// TODO :: compute the cosine similarity measure
			cosineSimilarity = computeCosineSimilarity(queryTokenMap,
					docTokenMap);
			//System.out.println(currentQid + ":" + cosineSimilarity + ":"
			//		+ docScore.text);
			docScore.score = cosineSimilarity;
			documentPerQueryList.add(docScore);
		}
		
		if (documentPerQueryList.size() > 0) {
			rank = computeRank(documentPerQueryList);
			// System.out.println(rank);
			queryRanks.add(rank);
			documentPerQueryList.clear();
		}
		 
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr(queryRanks);
		System.out.println("(MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}

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
	 * 
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double cosine_similarity = 0.0;

		double crossProduct = 0.0, normQuery = 0.0, normDoc = 0.0;

		for (String word : queryVector.keySet()) {
			if (docVector.containsKey(word)) {
				crossProduct = crossProduct + queryVector.get(word)
						* docVector.get(word);
			}
			normQuery += queryVector.get(word) * queryVector.get(word);
		}
		normQuery = Math.sqrt(normQuery);

		for (String word : docVector.keySet()) {
			normDoc += docVector.get(word) * docVector.get(word);
		}
		normDoc = Math.sqrt(normDoc);

		cosine_similarity = crossProduct / (normQuery * normDoc);

		return cosine_similarity;

	}

	/**
	 * 
	 * @return mrr
	 */
	private double compute_mrr(ArrayList<Integer> queryRanks) {
		double metric_mrr = 0.0;

		// TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
		for (Integer rank : queryRanks) {
			metric_mrr += 1 / rank;
		}
		metric_mrr /= queryRanks.size();
		return metric_mrr;
	}

	class ScoreComparator implements Comparator<DocumentScore> {
		public int compare(DocumentScore o1, DocumentScore o2) {
			if (o1.score <= o2.score)
				return 1;
			else
				return -1;

		}
	}

}
