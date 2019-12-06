package org.rdfslice.query;

import org.rdfslice.model.IInstanceStatement;
import org.rdfslice.model.Triple;
import org.rdfslice.util.PatternUtil;

public class TriplePattern extends Triple implements Pattern {
	
	public TriplePattern() {	
	}
	
	public TriplePattern(Triple nTriple) {		
		setSubject(nTriple.getSubject());
		setObject(nTriple.getObject());
		setPredicate(nTriple.getPredicate());
	}
	
	public boolean match(IInstanceStatement statement){		
		return statement.match(this);
	}

	@Override
	public int getType() {
		return Pattern.TRIPLE_PATTERN;
	}

	@Override
	public int getNumberOfContants() {
		int nConstants = 0;
		
		if(!PatternUtil.isVariable(subject))
			nConstants++;
		if(!PatternUtil.isVariable(predicate))
			nConstants++;
		if(!PatternUtil.isVariable(object))
			nConstants++;
		
		return nConstants;
	}
}
