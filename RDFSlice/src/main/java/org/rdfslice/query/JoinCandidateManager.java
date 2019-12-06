package org.rdfslice.query;
import org.rdfslice.model.Triple;

public interface JoinCandidateManager {		
		
	public void add(JoinCandidate joinCandidade) throws Exception;
	
	public void addBach(JoinCandidate joinCandidade) throws Exception;
	
	/**
	 * Compute the given statement match 
	 * @param statement
	 * @throws Exception 
	 */
	public int computeMatch(Triple triple, int bgpIndx, BasicGraphPattern bGPattern) throws Exception;
	
	public void removeNotMatched(int bgpIndx, BasicGraphPattern BGPattern) throws Exception;
	
	public boolean match(Triple triple, int bgpIndx, BasicGraphPattern BGPattern) throws Exception;
	
	public boolean match(int bgpIndx, int patternIndx, int joinType,
			Triple triple) throws Exception;

	public long size();

	public void removeNotMatchedMultiJoin(int bgpIndx, BasicGraphPattern bGPattern) throws Exception;

}
