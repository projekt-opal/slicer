package org.rdfslice.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rdfslice.model.Triple;

public class CheckQueryFactory extends QueryFactory {
	
	private static final String QUERY_PREFIX = "Select pmatch from jcandidates where";
	private static final String QUERY_SUFIX = "LIMIT 1 ";
	
	public PreparedStatement get(Connection connection, Triple triple, int bgpIndx, int joinType) throws SQLException {
		return getStatement(connection, triple, bgpIndx, joinType, QUERY_PREFIX, QUERY_SUFIX);
	}
}
