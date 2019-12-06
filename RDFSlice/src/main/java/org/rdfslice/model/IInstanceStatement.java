package org.rdfslice.model;

import java.util.Map;

import org.rdfslice.query.Pattern;
import org.rdfslice.query.StarPattern;
import org.rdfslice.query.TriplePattern;

public interface IInstanceStatement extends IStatement {
	public final static int TRIPLE = 0;
	public final static int STATEMENT = 1;	
	
	Pattern getPattern();
	boolean isPrefix();
	boolean match(StarPattern composedStatement);
	boolean match(TriplePattern triple);
	void replacePrefixes(Map<String, String> prefixMap);
}
