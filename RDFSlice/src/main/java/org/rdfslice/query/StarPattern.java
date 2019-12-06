package org.rdfslice.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.rdfslice.model.IInstanceStatement;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;

public class StarPattern extends ArrayList<TriplePattern> implements Pattern{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7153999704554425364L;
	
	Map<String, String> likedVariablesInstances;
		
	public StarPattern(){
		likedVariablesInstances = new HashMap<String, String>();
	}
	
	public StarPattern(Statement composedStatement){
		for(Triple nTriple : composedStatement){
			add(new TriplePattern(nTriple));
		}
	}	

	public int geType() {
		return Pattern.STATEMENT_PATTERN;
	}

	@Override
	public String getSubject() {
		if(size()>0)
			return get(0).getSubject();
		return null;
	}

	@Override
	public boolean match(IInstanceStatement statement) {		
		return statement.match(this);
	}

	@Override
	public int getType() {
		return Pattern.STATEMENT_PATTERN;
	}

	@Override
	public int getNumberOfContants() {
		int sNConstants = 0;
		for(TriplePattern pattern : this) {
			int pSNConstants = pattern.getNumberOfContants();
			if(pattern.getNumberOfContants() > pSNConstants)
				sNConstants = pSNConstants;
		}
		return sNConstants;
	}
	
}
