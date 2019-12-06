package org.rdfslice.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rdfslice.model.Triple;

public class CandidateQueryFactory extends QueryFactory {
	
	private static final String QUERY_PREFIX = "Select joinType from jcandidates where";
	private static final String QUERY_SUFIX = "LIMIT 1 ";
	
	public PreparedStatement get(Connection connection, Triple triple, int bgpIndx, int joinCondition) throws SQLException {
		return getStatement(connection, triple, bgpIndx, joinCondition, QUERY_PREFIX, QUERY_SUFIX);
	}
	
	public PreparedStatement get(Connection connection, Triple triple, int bgpIndx, int joinCondition, int patternIndx) throws SQLException {
		return getStatement(connection, triple, bgpIndx, joinCondition, patternIndx, QUERY_PREFIX, QUERY_SUFIX);
	}
}
