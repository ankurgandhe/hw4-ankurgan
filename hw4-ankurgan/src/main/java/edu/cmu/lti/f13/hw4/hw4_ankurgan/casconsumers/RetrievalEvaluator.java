package edu.cmu.lti.f13.hw4.hw4_ankurgan.casconsumers;

import java.io.IOException;
import java.util.ArrayList;
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

	public class DocumentScore{
		public Integer queryID;
		public Integer relevanceValue;
		public String text;
		public Map<String,Integer> docTokenMap;
		public double score; 
	}
	/** query id number **/
	/*public ArrayList<Integer> qIdList
	public ArrayList<Integer> relList;
	public ArrayList<String> textList;
	public ArrayList<Map<String,Integer>> docTokenMapList;*/
	public ArrayList<DocumentScore> documentScoreList;
	public Map<String,Integer>  globalTokenMap;
	//public ArrayList<DocumentScore> documentScoreList ;
	public void initialize() throws ResourceInitializationException {

		/*qIdList = new ArrayList<Integer>();

		relList = new ArrayList<Integer>();
		textList = new ArrayList<String>();
		//documentScoreList = new ArrayList<DocumentScore>();
		docTokenMapList = new ArrayList<Map<String,Integer>>();
		*/
		documentScoreList = new ArrayList<DocumentScore>();
		globalTokenMap = new HashMap<String,Integer>();
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}
		
		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
	
		if (it.hasNext()) {
			Document doc = (Document) it.next();
			/*(DocumentScore annotation = new DocumentScore(jcas); 
			annotation.setScore(0);
			annotation.setTokenList(doc.getTokenList());
			annotation.setText(doc.getText());
			annotation.setRelevanceValue(doc.getRelevanceValue());
			annotation.setQueryID(doc.getQueryID());
			System.out.print(annotation.getRelevanceValue()+":");
			System.out.println(annotation.getText());
			
			documentScoreList.add(annotation);
			*/
			//Make sure that your previous annotators have populated this in CAS
			FSList fsTokenList = doc.getTokenList();
			ArrayList<Token>tokenList=Utils.fromFSListToCollection(fsTokenList, Token.class);
			
			DocumentScore docScore = new DocumentScore();
			docScore.queryID = doc.getQueryID();
			docScore.relevanceValue = doc.getRelevanceValue();
			docScore.text = doc.getText();
			
			/*qIdList.add(doc.getQueryID());
			relList.add(doc.getRelevanceValue());
			textList.add(doc.getText());*/
			//Do something useful here
			String word;
			Integer freq;
			Map<String,Integer> tokenMap = new HashMap<String,Integer>();
			for (Token token: tokenList){
				word = token.getText();
				freq = token.getFrequency();
				tokenMap.put(word, freq);
				if ( globalTokenMap.containsKey(word)){
					globalTokenMap.put(word, globalTokenMap.get(word)+freq);
				}
				else
					globalTokenMap.put(word, freq);
					
			}
			//docTokenMapList.add(tokenMap);
			docScore.docTokenMap = tokenMap;
			documentScoreList.add(docScore);
		}
		

	}

	public Map<String,Integer> fsListToHashMap(FSList fsTokenList ){
		//FSList fsTokenList = doc.getTokenList();
		ArrayList<Token>tokenList=Utils.fromFSListToCollection(fsTokenList, Token.class);
		String word;
		Integer freq;
		Map<String,Integer> tokenMap = new HashMap<String,Integer>();
		for (Token token: tokenList){
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
		Integer currentQid = -1,qid;
		int idx=0;
		Map<String,Integer> queryTokenMap =null ;
		Map<String,Integer> docTokenMap =null ;
		int docRelevance = -1;
		double cosineSimilarity=0.0;
		Document doc;
		// We are assuming 
		for (DocumentScore docScore : documentScoreList){
			//doc = docScore.getDocument();
			docTokenMap = docScore.docTokenMap;//fsListToHashMap(docScore.getTokenList());
			docRelevance = docScore.relevanceValue;//relList.get(idx);
			qid = docScore.queryID;
			
			if (qid!=currentQid){
				// TODO : compute rank of retrieved sentences
				queryTokenMap = docTokenMap;
				assert docRelevance == -99; 
				currentQid = qid;
				continue ;
			}	
			
			// TODO :: compute the cosine similarity measure
			cosineSimilarity = computeCosineSimilarity(queryTokenMap,docTokenMap);
			System.out.println(currentQid+":"+cosineSimilarity);
			idx++;
		}
		
		
		// TODO :: compute the rank of retrieved sentences
		
		
		
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr();
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}

	/*
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);
		HashSet<Integer> uniqueIds = new HashSet<Integer>(qIdList);
		Integer currentQid = -1;
		int idx=0;
		Map<String,Integer> queryTokenMap =null ;
		Map<String,Integer> docTokenMap =null ;
		int docRelevance = -1;
		double cosineSimilarity=0.0;
		
		// We are assuming 
		for (Integer qid : qIdList){
			docTokenMap = docTokenMapList.get(idx);
			docRelevance = relList.get(idx);
			if (qid!=currentQid){
				// TODO : compute rank of retrieved sentences
				queryTokenMap = docTokenMapList.get(idx);
				assert docRelevance == -99; 
				currentQid = qid;
				continue ;
			}	
			
			// TODO :: compute the cosine similarity measure
			cosineSimilarity = computeCosineSimilarity(queryTokenMap,docTokenMap);
			System.out.println(currentQid+":"+cosineSimilarity);
			idx++;
		}
		
		
		// TODO :: compute the rank of retrieved sentences
		
		
		
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr();
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}
	*/
	/**
	 * 
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double cosine_similarity=0.0;

		// TODO :: compute cosine similarity between two sentences
		double dotProduct = 0.0,normQuery=0.0,normDoc=0.0;
		
		for ( String word : queryVector.keySet()){
			if (docVector.containsKey(word)){
				dotProduct = queryVector.get(word) * docVector.get(word);
			}
			normQuery += queryVector.get(word)*queryVector.get(word);
		}
		normQuery = Math.sqrt(normQuery);
		
		for ( String word : docVector.keySet()){
			normDoc += docVector.get(word)*docVector.get(word);
		}
		normDoc = Math.sqrt(normDoc);
		
		cosine_similarity = dotProduct / ( normQuery * normDoc) ;
		
		return cosine_similarity;
		
	}

	/**
	 * 
	 * @return mrr
	 */
	private double compute_mrr() {
		double metric_mrr=0.0;

		// TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
		
		return metric_mrr;
	}

}
