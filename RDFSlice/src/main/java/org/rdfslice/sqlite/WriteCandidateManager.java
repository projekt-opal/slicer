package org.rdfslice.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rdfslice.model.Triple;
import org.rdfslice.sqlite.cache.JoinCandidateCache;

public class WriteCandidateManager {	

	private final static int BATCH_SIZE = 1000;
	
	private PreparedStatement statement = null;
	private int batchLenght = 0;
	private boolean flushed = true;
	
	SliceSQLiteDAOV2 dao;
	Connection connection;
	
	public WriteCandidateManager(Connection connection, JoinCandidateCache jcCache) {
		this.connection = connection;
		dao = new SliceSQLiteDAOV2(jcCache);
	}

	public void drop(String tableName) throws SQLException {
		flushed = false;
		dao.drop(connection, tableName);
	}

	public void createTable() throws SQLException {
		flushed = false;
		dao.createTable(connection);
	}

	public void remove(int bgpIndx, int size) throws Exception {
		flushed = false;
		dao.remove(connection, bgpIndx, size);
	}

	public void update(Triple triple, int bgpIndx, int joinCondition,
			int patternIndx, int size) throws Exception {
		flushed = false;
		dao.update(connection, triple, bgpIndx, joinCondition, patternIndx, size, 0);
	}

	public void addBatch(Triple t, int joinCondition, int bgpIdx,
			String patternMatch, int patternIndx) throws SQLException {
		flushed = false;
		if(this.statement == null) {
			this.statement = connection.prepareStatement("insert into jcandidates values(?,?,?,?,?,?,?)");
		}
		this.dao.addBatch(statement, t, joinCondition, bgpIdx, patternMatch, patternIndx);
		statement.addBatch();
		this.batchLenght++;
		if(this.batchLenght == BATCH_SIZE) {
			this.statement.executeBatch();
			this.batchLenght = 0;
		}
	}
	
	public void add(Triple t, int joinCondition, int bgpIdx,
			String patternMatch, int patternIndx) throws SQLException {
		flushed = false;
		PreparedStatement statement = connection.prepareStatement("insert into jcandidates values(?,?,?,?,?,?,?)");		
		this.dao.add(statement, t, joinCondition, bgpIdx, patternMatch, patternIndx);
		statement.executeUpdate();
		statement.close();
	}
	
	public void list() throws Exception {
		dao.listAll(connection);
	}

	public void flush() throws SQLException {
		if(!flushed) {
			dao.resetCache();
			if(statement != null) {
				statement.executeBatch();
				statement = null;
			}
			connection.commit();
		}
	}

	public void removeMultiJoin(int bgpIndx, int size) throws Exception {
		dao.removeMultiJoin(connection, bgpIndx, size);
	}
}
