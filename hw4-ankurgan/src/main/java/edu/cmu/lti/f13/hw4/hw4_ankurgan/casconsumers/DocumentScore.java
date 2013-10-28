package edu.cmu.lti.f13.hw4.hw4_ankurgan.casconsumers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;



//Class to store the Document information created by annotations
	public class DocumentScore {
		public Integer queryID;
		public Integer relevanceValue;
		public String text;
		public Map<String, Integer> docTokenMap;
		public Map<String, Integer> docLowerTokenMap;
		public double score;
		public Map<String, Integer> docNGramMap;
		public Map<String, Integer> docLemmaMap;
		
	}
	