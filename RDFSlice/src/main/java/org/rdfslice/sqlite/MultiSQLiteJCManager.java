package org.rdfslice.sqlite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.JoinCandidate;
import org.rdfslice.query.JoinCandidateManager;
import org.rdfslice.util.FileUtil;

public class MultiSQLiteJCManager implements JoinCandidateManager {
	private static final int MAX_TRIPLE_SIZE = 600000;	
	public static final String CANDIDATE_DIR = "./candidates"; 
	
	private File storageDir = new File(CANDIDATE_DIR);
	private List<SQLiteConnectionManager> managers = new ArrayList<SQLiteConnectionManager>();
	
	public MultiSQLiteJCManager() throws IOException {
		if(storageDir.exists())
			FileUtil.delete(storageDir);
		storageDir.mkdir();
		storageDir.deleteOnExit();
	}
	
	@Override
	public void add(JoinCandidate joinCandidade) throws Exception {
		SQLiteConnectionManager connectionManager = null;
		for(SQLiteConnectionManager manager : managers) {
			if(manager.size < MAX_TRIPLE_SIZE) {
				connectionManager = manager;
				break;
			}
		}

//		if(connectionManager == null) {
//			connectionManager = new SQLiteConnectionManager();			
//			SQLiteJCManager jcManager = new SQLiteJCManager();
//			connectionManager.jcManager = jcManager;
//			managers.add(connectionManager);
//			dbInstanceSize++;
//		}

		connectionManager.jcManager.add(joinCandidade);
		connectionManager.size++;
	}

	@Override
	public int computeMatch(Triple triple, int bgpIndx, BasicGraphPattern bGPattern) throws Exception {
		for(SQLiteConnectionManager manager : managers) {
			return manager.jcManager.computeMatch(triple, bgpIndx, bGPattern);
		}
		return -1;
	}

	@Override
	public void removeNotMatched(int bgpIndx, BasicGraphPattern BGPattern) throws Exception {
		for(SQLiteConnectionManager manager : managers) {
			manager.jcManager.removeNotMatched(bgpIndx, BGPattern);
		}
	}

	@Override
	public boolean match(Triple triple, int bgpIndx,BasicGraphPattern BGPattern) throws Exception {
		for(SQLiteConnectionManager manager : managers) {
			if(manager.jcManager.match(triple, bgpIndx, BGPattern))
				return true;
		}
		return false;
	}
	
	@Override
	protected void finalize() throws Throwable {	
		super.finalize();
		if(storageDir.exists())
			FileUtil.delete(storageDir);
	}
	
	private class SQLiteConnectionManager {
		SQLiteJCManager jcManager;
		int size;
	}

	@Override
	public long size() {	
		int size = 0;
		for(SQLiteConnectionManager manager : managers) {
			size += manager.size;
		}
		return size;
	}

	@Override
	public void addBach(JoinCandidate joinCandidade) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean match(int bgpIndx, int patternIndx, int joinType,
			Triple triple) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeNotMatchedMultiJoin(int bgpIndx,
			BasicGraphPattern bGPattern) throws Exception {
		// TODO Auto-generated method stub
		
	}
}