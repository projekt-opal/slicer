package org.rdfslice.util;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.query.StarPattern;
import org.rdfslice.query.TriplePattern;

public class PatternUtil {
	
	private static Map<String, String> likedVariablesInstances = new HashMap<String, String>();
	
	private static final String PATTERN_SEPARATOR_TOKEN = " ";
	
	public static boolean match(Triple nTriple, TriplePattern pattern) {
		
		boolean subjectMatch, objectMatch, predicateMatch = false;
		
		String subject = pattern.getSubject();
		subjectMatch = match(subject, nTriple.getSubject());
		
		String predicate = pattern.getPredicate();
		predicateMatch = match(predicate, nTriple.getPredicate());
		
		String object = pattern.getObject();
		objectMatch = match(object, nTriple.getObject());
		
		if(subjectMatch && predicateMatch && objectMatch)
			return true;
		
		return false;
	}
	
	public static boolean match(String patternVar, String statementVar) {
		if(isVariable(patternVar))
			return true;
		else{
			String localStatementVar = RDFUtil.isVariableCoted(statementVar)?RDFUtil.removeCotation(statementVar):statementVar;
			return patternVar.equals(localStatementVar);
		}
	}
	
	public static boolean match(Triple triple, StarPattern composedStatement) {
		for(TriplePattern triplePattern : composedStatement) {
			if(!PatternUtil.match(triple, triplePattern))
				return false;
		}			
		return true;
	}
	
	public static boolean match(Statement composedStatement, TriplePattern triplePattern){
		for(Triple statement : composedStatement) {
			if(PatternUtil.match(statement, triplePattern))
				return true;
		}		
		return false;
	}
	
	public static boolean match(Statement statement, StarPattern composedPattern) {
		likedVariablesInstances.clear();
		LinkedList<Triple> statementTriplesQueue = new LinkedList<Triple>(statement);
		LinkedList<TriplePattern> triplesPatternQueue = new LinkedList<TriplePattern>(composedPattern);
		
		if(statementTriplesQueue.size()<triplesPatternQueue.size())
			return false;
		
		Triple triple = null;
		while((triple = statementTriplesQueue.poll()) != null){
			if(statementTriplesQueue.size()<triplesPatternQueue.size()-1)
				return false;
			for(TriplePattern triplePattern : triplesPatternQueue) {
				if(PatternUtil.match(triple, triplePattern)) { // TODO: create an way to never check the subject in InstancePattern
					if(linkVariables(triple, triplePattern)) {										
						triplesPatternQueue.pop();
						if(triplesPatternQueue.size()==0)
							return true;
						break;
					} else {
						return false;
					}
				}
			}
		}
		return triplesPatternQueue.size() == 0;
	}
	
	private static boolean linkVariable(String variable, String instance) {
		if(PatternUtil.isVariable(variable)){
			String savedInstance = likedVariablesInstances.get(variable);
			if(savedInstance==null){
				likedVariablesInstances.put(variable, instance);
			}else if(!savedInstance.equals(instance)){
				return false;
			}
		}
		return true;
	}
	
	private static boolean linkVariables(Triple nTriple, TriplePattern pattern) {
		/**
		 * Be in an Instance Pattern means that all triples have the same subject,
		 * witch means that the check of subject is unnecessary.
		 */	
		String predicatePattern = pattern.getPredicate();
		if(PatternUtil.isVariable(predicatePattern) &&
				!linkVariable(predicatePattern, nTriple.getPredicate())) {
			return false;
		}
		
		String objectPattern = pattern.getObject();
		if(PatternUtil.isVariable(objectPattern) &&
				!linkVariable(objectPattern, nTriple.getObject())) {
			return false;
		}			
		return true;
	}

	public static boolean isVariable(String component) {
		return component.toCharArray()[0]=='?';
	}
	
	public static String patternMatchToString(Boolean[] matchPattern) {
		String patternMatch = "";
		for(Boolean b : matchPattern) {
			patternMatch += (b!=null&&b?"1":"0") + PATTERN_SEPARATOR_TOKEN;
		}
		return patternMatch;
	}
	
	public static String patternMatchToString(int size){
		Boolean[] pattern = new Boolean[size];
		for(int i=0; i< size; i++) {
			pattern[i] = true;
		}
		return patternMatchToString(pattern);
	}

	public static Boolean[] stringToPatternMatch(String patternMatchString) {	
		String[] patterns = patternMatchString.split(PATTERN_SEPARATOR_TOKEN);
		Boolean[] patternMatch = new Boolean[patterns.length];
		int indx = 0;
		for(String pattern : patterns) {			
			patternMatch[indx] = (pattern.equals("1"));
			indx++;
		}
		return patternMatch;
	}
}
