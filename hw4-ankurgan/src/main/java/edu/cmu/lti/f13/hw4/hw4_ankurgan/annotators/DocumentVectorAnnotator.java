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

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	StanfordCoreNLP pipeline;

	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
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

		// Map of token to frequency
		Map<String, Integer> tokenFrequencyMap = new HashMap<String, Integer>();

		// Tokenization using Stanford CoreNLP
		Annotation document = new Annotation(docText);
		pipeline.annotate(document);
		List<CoreLabel> tokens = document.get(TokensAnnotation.class);
		Map<String, String> tokenLemmaMap = new HashMap<String, String>();

		// Sift through all tokens found by stanford pipeline
		for (CoreLabel token : tokens) {
			String word = token.get(TextAnnotation.class);
			String lemma = token.get(LemmaAnnotation.class);
			if (tokenFrequencyMap.containsKey(word)) {
				tokenFrequencyMap.put(word, tokenFrequencyMap.get(word) + 1);
			} else
				tokenFrequencyMap.put(word, 1);
			// Add lemma of each token (same tokens with have same lemma)
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
		}
		// Put tokenList into document FSList
		FSList tokenFSList;
		tokenFSList = Utils.fromCollectionToFSList(jcas, tokenList);
		doc.setTokenList(tokenFSList);
	}

}
