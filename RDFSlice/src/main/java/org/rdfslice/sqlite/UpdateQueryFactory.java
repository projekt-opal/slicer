package org.rdfslice.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rdfslice.model.Triple;

public class UpdateQueryFactory extends QueryFactory {
	
	public PreparedStatement get(Connection connection, Triple triple, int bgpIndx, int joinCondition, int pos, int size, String sufix) throws SQLException {		
		return getStatement(connection, triple, bgpIndx, joinCondition, sufix, "");
	}
}
