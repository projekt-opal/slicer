package org.rdfslice.model;

import java.util.Map;

import org.rdfslice.query.Pattern;
import org.rdfslice.query.QueryPatternFactory;
import org.rdfslice.query.StarPattern;
import org.rdfslice.query.TriplePattern;
import org.rdfslice.util.PatternUtil;
import org.rdfslice.util.RDFUtil;

public class Triple implements IInstanceStatement {

	protected String subject;
	protected String predicate;
	protected String object;
	protected String nTriple;
	
	protected String triple = null;

	public Triple(){
	}
	
	public Triple(String triple) {
		this.triple = triple;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	@Override
	public String toString() {
		if(triple != null) {
			return triple;
		}
		
		return (RDFUtil.isURL(subject)?RDFUtil.cote(subject):subject) 
				+ NTRIPLE_SEPARATOR + (PatternUtil.isVariable(predicate)?predicate:RDFUtil.cote(predicate)) 
				+ NTRIPLE_SEPARATOR + (RDFUtil.isURL(object)?RDFUtil.cote(object):object) 
				+ NTRIPLE_SEPARATOR 
				+ NTRIPLE_TERMINATOR;
	}

	@Override
	public Pattern getPattern() {
		return QueryPatternFactory.getPattern(this);
	}

	@Override
	public boolean match(StarPattern composedStatement) {
		return PatternUtil.match(this, composedStatement);
	}

	@Override
	public boolean match(TriplePattern triple) {
		return PatternUtil.match(this, triple);
	}

	@Override
	public int getType() {
		return IInstanceStatement.TRIPLE;
	}

	/**
	 * @ TODO: move to abstract class
	 */
	@Override
	public boolean isPrefix() {
		return this.subject.charAt(0) == '@';
	}

	@Override
	public void replacePrefixes(Map<String, String> prefixMap) {
		subject = RDFUtil.replacePrefixes(subject, prefixMap);
		predicate = RDFUtil.replacePrefixes(predicate, prefixMap);
		object = RDFUtil.replacePrefixes(object, prefixMap);
	}
	
	public boolean sameAs(Triple triple){
		return triple.getSubject().equals(this.getSubject()) &&
				triple.getPredicate().equals(this.getPredicate()) &&
				triple.getObject().equals(this.getObject());
	}
}
