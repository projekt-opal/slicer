package org.rdfslice.query;

import org.rdfslice.model.Triple;
import org.rdfslice.util.PatternUtil;

public class JoinCandidate {
	private int joinCondition;
	private int bgpIdx;
	private int patternIdx;
	private Triple statement;
	Boolean[] patternMatch;

	public JoinCandidate(int bgpIdx, BasicGraphPattern BGPattern) {
		this.patternMatch = new Boolean[BGPattern.size()];
		this.setBgpIndx(bgpIdx);
	}
	
	public JoinCandidate(String s, String p, String o, int joinCondition, String patternMatch) {
		this.statement = new Triple();
		this.statement.setSubject(s);
		this.statement.setPredicate(p);
		this.statement.setObject(o);
		
		this.joinCondition = joinCondition;		
		this.patternMatch = PatternUtil.stringToPatternMatch(patternMatch);
	}
	
	public JoinCandidate(Triple triple, int bgpIndex, int joinCondition, String patternMatch) {
		this.statement = triple;
		this.joinCondition = joinCondition;
		this.patternMatch = PatternUtil.stringToPatternMatch(patternMatch);
		this.bgpIdx = bgpIndex;
	}
	
	public synchronized int getPatternIndx() {
		return patternIdx;
	}
	
	public synchronized void setPatternIndx(int patternIndx) {
		this.patternIdx = patternIndx;
	}

	public synchronized int getJoinCondition() {
		return joinCondition;
	}

	public synchronized void setJoinCondition(int joinCondition) {
		this.joinCondition = joinCondition;
	}

	public synchronized Triple getStatement() {
		return statement;
	}

	public synchronized void setStatement(Triple statement) {
		this.statement = statement;
	}

	public synchronized int computeMatch(Triple statement,
			BasicGraphPattern bGPattern) {
		for(Pattern pattern : bGPattern) {
			int patternIndx = bGPattern.indexOf(pattern);
			if(pattern.match(statement)
					&& (patternMatch[patternIndx] == null
					|| patternMatch[patternIndx] != true)) {
				patternMatch[patternIndx] = true;
				return patternIndx;
			}
		}
		return -1;
	}
	
	public synchronized boolean match(Triple statement) {
		return statement.sameAs(this.statement) || joinMatch(statement);
	}
	
	private boolean joinMatch(Triple statement) {
		if(joinCondition == BasicGraphPattern.SUBJECT_SUBJECT_JOIN) {
			return this.statement.getSubject().equals(statement.getSubject());
		} else if(joinCondition == BasicGraphPattern.PREDICATE_PREDICATE_JOIN) {
			return this.statement.getPredicate().equals(statement.getPredicate());
		} else if(joinCondition == BasicGraphPattern.OBJECT_OBJECT_JOIN) {
			return this.statement.getObject().equals(statement.getObject());
		} else if(joinCondition == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
			return this.statement.getObject().equals(statement.getSubject()) || 
					this.statement.getObject().equals(statement.getObject()) ||
					this.statement.getSubject().equals(statement.getObject()) || 
					this.statement.getSubject().equals(statement.getSubject());
		}
		
		return false;
	}

	public synchronized boolean matchAll(int trashold) {		
		if(this.patternMatch.length != trashold)
			return false;
		for(int i=0; i < trashold; i++)
			if(this.patternMatch[i] == null)
				return false;
		return true;
	}
	
	public synchronized boolean matchAll() {
		for(int i=0; i < this.patternMatch.length; i++) {
			if(this.patternMatch[i] != true)
				return false;
		}
		return true;
	}
	
	public synchronized String getPatternMatch() {
		return PatternUtil.patternMatchToString(this.patternMatch);
	}
	
	public synchronized void setPatternMatch(Boolean[] patternMatch) {
		this.patternMatch = patternMatch;
	}
	
	public synchronized void setPatternMatch(int pos) {
		if(patternMatch.length > pos)
			this.patternMatch[pos] = true;
	}

	public synchronized int getBgpIndx() {
		return bgpIdx;
	}

	public synchronized void setBgpIndx(int bgpIdx) {
		this.bgpIdx = bgpIdx;
	}

	public synchronized boolean isChecked(int pos) {
		if(patternMatch.length > pos)
			return patternMatch[pos];
		return false;
	}
	
	@Override
	public String toString() {
		String patternMatch ="";
		for(boolean value : this.patternMatch) {
			patternMatch += value + " ";
		}
		return joinCondition + " " + bgpIdx + " <" + statement.getSubject() + 
				"," + statement.getPredicate() + "," + statement.getObject() + "> " + patternMatch;
	}
}