package org.rdfslice.sqlite.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.rdfslice.model.Triple;
import org.rdfslice.query.JoinCandidate;
import org.rdfslice.structures.MaxSizeHashMap;

public class JoinCandidateCache {
	
	long maxSize = 1000;
	MaxSizeHashMap<Key, JoinCandidate> cachedCandidates;
	
	public JoinCandidateCache() {
		cachedCandidates = new MaxSizeHashMap<Key, JoinCandidate>(maxSize);
	}
	
	public JoinCandidateCache(long size) {
		cachedCandidates = new MaxSizeHashMap<Key, JoinCandidate>(size);
	}
	
	public synchronized JoinCandidate get(Triple triple, int bgpIndx, int joinType) {
		Key entity = new Key(triple, bgpIndx, joinType);
		return cachedCandidates.get(entity);
	}
	
	public synchronized JoinCandidate put(JoinCandidate joinCandidate) {
		Key key = new Key(joinCandidate.getStatement(),
				joinCandidate.getBgpIndx(), joinCandidate.getJoinCondition());
		return cachedCandidates.put(key, joinCandidate);
	}	
	
	public synchronized int size() {
		return cachedCandidates.size();
	}

	public synchronized long maxSize() {
		return cachedCandidates.getMaxSize();
	}

	public synchronized void removeNotMatched() {
		Collection<Key> keys = cachedCandidates.keySet();
		List<Key> keyList = new ArrayList<Key>(keys);
		for(Key key : keyList) {
			JoinCandidate jc = cachedCandidates.get(key);
			if(!jc.matchAll()) {
				cachedCandidates.remove(key);
			}
		}
	}
	
	public void list() {
		for(Entry<Key, JoinCandidate> e : cachedCandidates.entrySet()) {
			System.out.println(e.getValue().toString());
		}
	}
}
