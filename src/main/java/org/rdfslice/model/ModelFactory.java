package org.rdfslice.model;

import java.util.ArrayList;
import java.util.List;

import org.rdfslice.query.TriplePattern;
import org.rdfslice.util.RDFUtil;

public class ModelFactory {
	
	public static List<IInstanceStatement> getStatements(String expression) throws Exception{
		List<IInstanceStatement> patterns = new ArrayList<IInstanceStatement>();
		String[] statements = RDFUtil.splitStatement(expression);
		
		for(String statement: statements){
			if(RDFUtil.isASimpleStatement(statement)){
				patterns.add(getNTriple(statement));				
			} else
				patterns.add(getComposedStatement(statement));
		}
		
		return patterns;
	}
	
	public static IInstanceStatement getStatement(String expression) throws Exception{
		if(RDFUtil.isASimpleStatement(expression)){
			return getNTriple(expression);
		} else
			return getComposedStatement(expression);
	}
	
	public static Triple getNTriple(String pattern) throws Exception{
		String[] patternParams = RDFUtil.splitTriple(pattern);
		
		TriplePattern triplePattern = new TriplePattern();
		
		triplePattern.setSubject(patternParams[0]);
		
		triplePattern.setPredicate(patternParams[1]);
		
		triplePattern.setObject(patternParams[2]);
		
		return triplePattern;
	}
	
	public static Statement getComposedStatement(String pattern) throws Exception{
		
		String[] patternParams = RDFUtil.splitComposedStatement(pattern);
						
		Statement composedPattern = new Statement();
		
		String subject = null;
		
		for(String patternParam : patternParams){
			Triple nTriple = null;
			String[] simplePatternParams = RDFUtil.splitTriple(patternParam);
			if(subject==null || simplePatternParams.length>2){
				subject = simplePatternParams[0];
				nTriple = getNTriple(patternParam);
			} else
				nTriple = getNTriple(subject + IStatement.NTRIPLE_SEPARATOR + patternParam);
			
			composedPattern.add(nTriple);
		}
		
		return composedPattern;
	}
	
}
