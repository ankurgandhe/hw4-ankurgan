package edu.cmu.lti.f13.hw4.hw4_ankurgan.annotators;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_ankurgan.utils.Utils;

import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	//Properties props;
	StanfordCoreNLP pipeline ;
	private Pattern tokenPattern = Pattern
			.compile("\\b[a-zA-Z_]([a-zA-Z0-9_^])*\\b");
	
	public void initialize(UimaContext aContext)throws ResourceInitializationException {
		super.initialize(aContext);
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit,pos,lemma");
		pipeline = new StanfordCoreNLP(props);


	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}

	/**
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		// TO DO: construct a vector of tokens and update the tokenList in CAS
		
		// Map of token to frequency
		Map<String, Integer> tokenFrequencyMap = new HashMap<String, Integer>();
		/*
		// Tokenization using pattern matcher
		Matcher matcher = tokenPattern.matcher(docText);
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
		*/
		
		// Tokenization using stanford CoreNLP
		Annotation document = new Annotation(docText);
		pipeline.annotate(document);
		List<CoreLabel> tokens = document.get(TokensAnnotation.class);
		Map<String, String> tokenLemmaMap = new HashMap<String, String>();
		for (CoreLabel token : tokens) {
			String word = token.get(TextAnnotation.class);
			String lemma = token.get(LemmaAnnotation.class);
			if (tokenFrequencyMap.containsKey(word)) {
				tokenFrequencyMap.put(word,
						tokenFrequencyMap.get(word) + 1);
			} else
				tokenFrequencyMap.put(word, 1);
			tokenLemmaMap.put(word, lemma);
		}
			
		// token annotations
		ArrayList<Token> tokenList = new ArrayList<Token>();
		for (String token : tokenFrequencyMap.keySet()) {

			Token annotation = new Token(jcas);
			annotation.setText(token);
			annotation.setLemma(tokenLemmaMap.get(token));
			annotation.setFrequency(tokenFrequencyMap.get(token));
			annotation.addToIndexes();
			tokenList.add(annotation);
			// System.out.println(token);
		}
		// Put tokenList into document FSList
		FSList tokenFSList;
		tokenFSList = Utils.fromCollectionToFSList(jcas, tokenList);
		doc.setTokenList(tokenFSList);
	}

}
