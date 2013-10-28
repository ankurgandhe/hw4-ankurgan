package edu.cmu.lti.f13.hw4.hw4_ankurgan.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.EmptyStringList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.NonEmptyStringList;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import edu.cmu.lti.f13.hw4.hw4_ankurgan.VectorSpaceRetrieval;

public class Utils {
	public static <T extends TOP> ArrayList<T> fromFSListToCollection(
			FSList list, Class<T> classType) {

		Collection<T> myCollection = JCasUtil.select(list, classType);
		/*
		 * for(T element:myCollection){ System.out.println(.getText()); }
		 */

		return new ArrayList<T>(myCollection);
	}

	public static StringList createStringList(JCas aJCas,
			Collection<String> aCollection) {
		if (aCollection.size() == 0) {
			return new EmptyStringList(aJCas);
		}

		NonEmptyStringList head = new NonEmptyStringList(aJCas);
		NonEmptyStringList list = head;
		Iterator<String> i = aCollection.iterator();
		while (i.hasNext()) {
			head.setHead(i.next());
			if (i.hasNext()) {
				head.setTail(new NonEmptyStringList(aJCas));
				head = (NonEmptyStringList) head.getTail();
			} else {
				head.setTail(new EmptyStringList(aJCas));
			}
		}

		return list;
	}

	public static <T extends Annotation> FSList fromCollectionToFSList(
			JCas aJCas, Collection<T> aCollection) {
		if (aCollection.size() == 0) {
			return new EmptyFSList(aJCas);
		}

		NonEmptyFSList head = new NonEmptyFSList(aJCas);
		NonEmptyFSList list = head;
		Iterator<T> i = aCollection.iterator();
		while (i.hasNext()) {
			head.setHead(i.next());
			if (i.hasNext()) {
				head.setTail(new NonEmptyFSList(aJCas));
				head = (NonEmptyFSList) head.getTail();
			} else {
				head.setTail(new EmptyFSList(aJCas));
			}
		}

		return list;
	}

	/**
	 * @return cosine_similarity
	 */
	public static double computeCosineSimilarity(
			Map<String, Integer> queryVector, Map<String, Integer> docVector,
			ArrayList<String> stopWords) {
		double cosine_similarity = 0.0;

		double crossProduct = 0.0, normQuery = 0.0, normDoc = 0.0;

		for (String word : queryVector.keySet()) {
			if (stopWords.contains(word))
				continue;
			if (docVector.containsKey(word)) {
				crossProduct = crossProduct + queryVector.get(word)
						* docVector.get(word);
			}
			normQuery += queryVector.get(word) * queryVector.get(word);
		}
		normQuery = Math.sqrt(normQuery);

		for (String word : docVector.keySet()) {
			if (stopWords.contains(word))
				continue;
			normDoc += docVector.get(word) * docVector.get(word);
		}
		normDoc = Math.sqrt(normDoc);

		cosine_similarity = crossProduct / (normQuery * normDoc);
		cosine_similarity = Math.round(cosine_similarity * 1000) / 1000.0d;
		return cosine_similarity;

	}

	/**
	 * @return jaccard_similarity
	 */
	public static double computeJaccardSimilarity(
			Map<String, Integer> queryVector, Map<String, Integer> docVector,
			ArrayList<String> stopWords) {
		double jaccard_similarity = 0.0;
		double intersectionValue = 0.0, unionValue = 0.0;
		// ArrayList<String> Union = new ArrayList<String>();
		Map<String, Integer> union = new HashMap<String, Integer>();
		union.putAll(queryVector);
		union.putAll(docVector);

		for (String word : union.keySet()) {
			if (stopWords.contains(word))
				continue;
			if (queryVector.containsKey(word)) {
				if (docVector.containsKey(word)) {
					intersectionValue += Math.min(queryVector.get(word),
							docVector.get(word));
					unionValue += docVector.get(word);
				}
				unionValue += queryVector.get(word);

			} else {
				if (queryVector.containsKey(word)) {
					intersectionValue += Math.min(queryVector.get(word),
							docVector.get(word));
					unionValue += docVector.get(word);
				}
				unionValue += docVector.get(word);
			}

		}
		jaccard_similarity = Math
				.round((intersectionValue / unionValue) * 1000) / 1000.0d;
		return jaccard_similarity;

	}

	public static double computeScore(ArrayList<Double> scoreList,
			ArrayList<Double> weightList) {
		double score = 0.0,totwt =0.0;
		int idx=0;
		for ( double sc : scoreList){
			double wt = weightList.get(idx);
			score+= sc*wt;
			totwt+=wt;
			idx=idx+1;
		}
		return score/totwt;
	}

	/**
	 * Read the stop words in src/main/resources/stopwords.txt
	 * 
	 * @author ankurgan
	 * @throws IOException
	 */
	public static ArrayList<String> readStopWords() throws IOException {
		ArrayList<String> stopWords = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(
				"src/main/resources/stopwords.txt"));
		try {
			String line = br.readLine();
			while (line != null) {
				stopWords.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stopWords;
	}

}
