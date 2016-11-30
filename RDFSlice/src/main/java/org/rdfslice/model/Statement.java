package org.rdfslice.model;

import java.util.ArrayList;
import java.util.Map;

import org.rdfslice.query.Pattern;
import org.rdfslice.query.QueryPatternFactory;
import org.rdfslice.query.StarPattern;
import org.rdfslice.query.TriplePattern;
import org.rdfslice.util.PatternUtil;
import org.rdfslice.util.SystemUtil;

public class Statement extends ArrayList<Triple> implements IInstanceStatement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2648723850736796105L;
	
	int length=0;

	@Override
	public String getSubject() {
		for(Triple triple: this)
			return triple.getSubject();
		return null;
	}

	@Override
	public String toString() {
		String outputString = "";
		int size = size();
		Triple lastTriple = get(size-1);
		for(Triple nTriple : this) {
			outputString += nTriple.toString();
			if(nTriple!=lastTriple)
				outputString += SystemUtil.getInstance().getLineSeparator();
		}
		return outputString;
	}

	public Triple[] getTriples() {
		Triple[] nTriple = new Triple[size()];
		return this.toArray(nTriple);
	}

	@Override
	public Pattern getPattern() {
		return QueryPatternFactory.getPattern(this);
	}
	
	@Override
	public boolean add(Triple e) {
		String subject = getSubject();
		
		if(subject != null && !e.getSubject().equals(subject))
			throw new RuntimeException("Incompatible subjects.");
		
		length += e.toString().length();
		return super.add(e);
	}
	
	@Override
	public boolean match(StarPattern composedStatement) {
		return PatternUtil.match(this, composedStatement);
	}

	@Override
	public boolean match(TriplePattern triplePattern) {
		return PatternUtil.match(this, triplePattern);
	}

	@Override
	public int getType() {
		return IInstanceStatement.STATEMENT;
	}

	/**
	 * TODO: move to abstract class
	 */
	@Override
	public boolean isPrefix() {
		return getSubject().charAt(0) == '@';
	}
	
	public int length(){
		return length;
	}

	@Override
	public void replacePrefixes(Map<String, String> prefixMap) {
		for(Triple triple : this){
			triple.replacePrefixes(prefixMap);
		}
	}
}