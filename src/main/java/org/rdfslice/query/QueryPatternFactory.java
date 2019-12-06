package org.rdfslice.query;

import java.util.ArrayList;
import java.util.List;

import org.rdfslice.model.IInstanceStatement;
import org.rdfslice.model.ModelFactory;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;

public class QueryPatternFactory {
	
	public static List<Pattern> getPatterns(String query) throws Exception{
		List<Pattern> patterns = new ArrayList<Pattern>();
		
		List<IInstanceStatement> statements = ModelFactory.getStatements(query);
		
		for(IInstanceStatement statement : statements){
			patterns.add(statement.getPattern());
		}
		
		return patterns;
	}

	public static Pattern getPattern(Triple triple){
		TriplePattern pattern = new TriplePattern(triple);
		return pattern;
	}
	
	public static Pattern getPattern(Statement composedStatement){
		StarPattern pattern = new StarPattern(composedStatement);
		return pattern;
	}
}