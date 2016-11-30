package org.rdfslice.sqlite;

import java.sql.Connection;

import org.rdfslice.model.Triple;
import org.rdfslice.sqlite.cache.JoinCandidateCache;

public class ReaderCandidateManager {
	SliceSQLiteDAOV2 dao;
	Connection connection;
	
	public ReaderCandidateManager(Connection connection, JoinCandidateCache jcCache) {
		this.connection = connection;
		this.dao = new SliceSQLiteDAOV2(jcCache);
	}
	
	public boolean match(Triple triple, int bgpIndx, int joinType) throws Exception {
		return this.dao.match(this.connection, triple, bgpIndx, joinType);
	}
	
	public boolean isCandidate(Triple triple, int bgpIndx, int joinType) throws Exception {
		return this.dao.isCandidate(connection, triple, bgpIndx, joinType);
	}
	
	public boolean isChecked(Triple triple, int pos, int bgpIndx, int joinType) throws Exception {
		return this.dao.isChecked(connection, triple, pos, bgpIndx, joinType);
	}
	
	public void list() throws Exception {
		dao.listAll(connection);
	}

	public boolean isCandidate(Triple triple, int bgpIndx, int patternIndx,
			int joinType) throws Exception {		
		return dao.isCandidate(connection, triple, bgpIndx, joinType, patternIndx);
	}
}
