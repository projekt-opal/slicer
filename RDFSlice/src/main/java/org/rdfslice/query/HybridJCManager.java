package org.rdfslice.query;

import org.rdfslice.model.Triple;
import org.rdfslice.sqlite.SQLiteJCManager;

public class HybridJCManager implements JoinCandidateManager {
	MemoryJCManager memoryJCManager = new MemoryJCManager();
	SQLiteJCManager sqliteJCManager = null;
	
	long maxsize = 5;
	long size = 0;
	
	public HybridJCManager(long maxMemorySize, long size,
			MemoryJCManager mJCManager,
			SQLiteJCManager sqliteJCManager) {
		this.maxsize = maxMemorySize;
		this.memoryJCManager = mJCManager;
		this.sqliteJCManager = sqliteJCManager;
		this.size = size;
	}

	@Override
	public synchronized void add(JoinCandidate joinCandidade) throws Exception {		
		/*if(size>maxsize) {
			sqliteJCManager.add(joinCandidade);
		} else*/
		memoryJCManager.add(joinCandidade);
		size++;
	}

	@Override
	public synchronized int computeMatch(Triple triple, int bgpIndx,
			BasicGraphPattern bGPattern) throws Exception {
		/*if(size>maxsize);
			sqliteJCManager.computeMatch(triple, bgpIndx, bGPattern, stream);*/
		return memoryJCManager.computeMatch(triple, bgpIndx, bGPattern);
	}

	@Override
	public synchronized void removeNotMatched(int bgpIndx, BasicGraphPattern BGPattern) throws Exception {
		/*if(size>maxsize)
			sqliteJCManager.removeNotMatched(BGPattern);*/
		memoryJCManager.removeNotMatched(BGPattern);
	}


	@Override
	public synchronized boolean match(Triple triple, int bgpIndx,
			BasicGraphPattern BGPattern) throws Exception {
		/*if(size>maxsize)
			sqliteJCManager.removeNotMatched(BGPattern);*/
		memoryJCManager.removeNotMatched(BGPattern);
		return false;
	}


	@Override
	public synchronized long size() {
		long size = memoryJCManager.size();		
		if(size>maxsize) {
			size += sqliteJCManager.size();
		}
		return size;
	}
	
	@Override
	public void addBach(JoinCandidate joinCandidade) throws Exception {
		throw new Exception("Not implemented.");
	}

	@Override
	public boolean match(int bgpIndx, int patternIndx, int joinType,
			Triple triple) throws Exception {
		return false;
	}

	@Override
	public void removeNotMatchedMultiJoin(int bgpIndx,
			BasicGraphPattern bGPattern) throws Exception {
		throw new Exception("Not implemented.");
	}
	
}
