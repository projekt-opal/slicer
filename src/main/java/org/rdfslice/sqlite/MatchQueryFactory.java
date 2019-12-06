package org.rdfslice.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rdfslice.model.Triple;

public class MatchQueryFactory extends QueryFactory{
	private static final String QUERY_PREFIX = "Select s from jcandidates where";
	private static final String QUERY_SUFIX = "LIMIT 1 ";
	
	public PreparedStatement get(Connection connection, Triple triple, int bgpIndx, int joinCondition) throws SQLException {
		return getStatement(connection, triple, bgpIndx, joinCondition, QUERY_PREFIX, QUERY_SUFIX);
	}
}
