package edu.cmu.lti.f13.hw4.hw4_ankurgan.annotators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_ankurgan.utils.Utils;


public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	private Pattern tokenPattern = Pattern
			.compile("\\b[a-zA-Z_]([a-zA-Z0-9_^])*\\b");

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		
		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}

	/**
	 * 
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		// TO DO: construct a vector of tokens and update the tokenList in CAS
		Matcher matcher = tokenPattern.matcher(docText);
		// Map of token to frequency
		Map<String, Integer> tokenFrequencyMap = new HashMap<String, Integer>();
		String tokenText;
		while (matcher.find()) {
			// found one - create Map entry
			tokenText = matcher.group();
			if (tokenFrequencyMap.containsKey(tokenText)) {
				tokenFrequencyMap.put(tokenText,
						tokenFrequencyMap.get(tokenText) + 1);
			} else
				tokenFrequencyMap.put(tokenText, 1);

		}
		//Create Token annotations
		ArrayList<Token> tokenList = new ArrayList<Token>();
		for (String token : tokenFrequencyMap.keySet()) {

			Token annotation = new Token(jcas);
			annotation.setText(token);
			annotation.setFrequency(tokenFrequencyMap.get(token));
			annotation.addToIndexes();
			tokenList.add(annotation);
			//System.out.println(token);
		}
		// Put tokenList into document FSList 
		FSList tokenFSList; 
		tokenFSList = Utils.fromCollectionToFSList(jcas,tokenList);
		doc.setTokenList(tokenFSList);
	}

}
