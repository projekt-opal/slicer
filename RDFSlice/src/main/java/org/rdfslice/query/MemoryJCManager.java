package org.rdfslice.query;

import java.util.ArrayList;
import java.util.List;

import org.rdfslice.RDFSliceStreamEngine;
import org.rdfslice.model.Triple;

public class MemoryJCManager implements JoinCandidateManager {	
	List<JoinCandidate> candidates = new ArrayList<JoinCandidate>();
	
	public void add(JoinCandidate joinCandidade) {
		candidates.add(joinCandidade);
	}
	
	/**
	 * Compute the given statement match 
	 * @param statement
	 */
	public int computeMatch(Triple triple, int bgpIndx, BasicGraphPattern bGPattern) {
		for(JoinCandidate candidate : candidates) {
			if(candidate.match(triple)) {
				return candidate.computeMatch(triple, bGPattern);
			}
		}
		return -1;
	}
	
	public void removeNotMatched(BasicGraphPattern BGPattern) {
		int indx = 0;
		while(indx < candidates.size()) {
			JoinCandidate candidate = candidates.get(indx);
			if(!candidate.matchAll(BGPattern.size()))
				candidates.remove(candidate);
			else
				indx++;
		}
	}
	
	public boolean match(Triple triple, int bgpIndx, BasicGraphPattern BGPattern) {		
		if(RDFSliceStreamEngine.match(triple, BGPattern)){
			for(JoinCandidate candidate : candidates){
				if(candidate.match(triple))
					return true;
			}
		}		
		return false;
	}
	
	@Override
	public void addBach(JoinCandidate joinCandidade) throws Exception {
		throw new Exception("Not implemented.");
	}
	
	public void clear() {
		candidates.clear();
	}

	@Override
	public long size() {		
		return candidates.size();
	}

	@Override
	public boolean match(int bgpIndx, int patternIndx, int joinType,
			Triple triple) throws Exception {
		return false;
	}

	@Override
	public void removeNotMatched(int bgpIndx, BasicGraphPattern BGPattern)
			throws Exception {
		int indx = 0;
		while(indx < candidates.size()) {
			JoinCandidate candidate = candidates.get(indx);
			if(!candidate.matchAll(BGPattern.size()))
				candidates.remove(candidate);
			else
				indx++;
		}
	}

	@Override
	public void removeNotMatchedMultiJoin(int bgpIndx,
			BasicGraphPattern bGPattern) throws Exception {
		throw new Exception("Not implemented.");
	}
}