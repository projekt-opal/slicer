package org.rdfslice.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;

public abstract class QueryFactory {
	
	private volatile Map<Integer, PreparedStatement> preparedSSStatement = new HashMap<Integer, PreparedStatement>();
	private volatile Map<Integer, PreparedStatement> preparedPPStatement = new HashMap<Integer, PreparedStatement>();
	private volatile Map<Integer, PreparedStatement> preparedOOStatement = new HashMap<Integer, PreparedStatement>();

	private Map<Integer, PreparedStatement> preparedSOSSStatement = new HashMap<Integer, PreparedStatement>();
	private Map<Integer, PreparedStatement> preparedSOSOStatement = new HashMap<Integer, PreparedStatement>();
	private Map<Integer, PreparedStatement> preparedSOOOStatement = new HashMap<Integer, PreparedStatement>();
	private Map<Integer, PreparedStatement> preparedSOOSStatement = new HashMap<Integer, PreparedStatement>();
	
	protected PreparedStatement createStatement(Connection connection, int bgpIndx, int joinType, String prefix, String sufix) throws SQLException {
		PreparedStatement ps = null;

		String query = prefix + " ";
		if(joinType == BasicGraphPattern.SUBJECT_SUBJECT_JOIN) {
			query += "joinType=0 and s=?" + " and bgpIndx=" + bgpIndx + " " + sufix;
			ps = connection.prepareStatement(query);
		} else if(joinType == BasicGraphPattern.PREDICATE_PREDICATE_JOIN) {
			query += "joinType=1 and p=?" + " and bgpIndx=" + bgpIndx + " " + sufix;
			ps = connection.prepareStatement(query);			
		} else if(joinType == BasicGraphPattern.OBJECT_OBJECT_JOIN) {
			query += "joinType=2 and o=?" + " and bgpIndx=" + bgpIndx + " " + sufix;
			ps = connection.prepareStatement(query);
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS) {
			query += "joinType=3 and s=?" + " and bgpIndx=" + bgpIndx + " " + sufix;
			ps = connection.prepareStatement(query);
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO) {
			query += "joinType=3 and s=?" + " and bgpIndx=" + bgpIndx + " " + sufix;
			ps = connection.prepareStatement(query);
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS) {
			query += "joinType=3 and o=?" + " and bgpIndx=" + bgpIndx + " " + sufix;
			ps = connection.prepareStatement(query);
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO) {
			query += "joinType=3 and o=?" + " and bgpIndx=" + bgpIndx + " " + sufix;
			ps = connection.prepareStatement(query);
		}
		
		return ps;
	}
	
	protected PreparedStatement createStatement(Connection connection, int joinType, String prefix, String sufix) throws SQLException {
		PreparedStatement ps = null;

		String query = prefix + " ";
		if(joinType == BasicGraphPattern.SUBJECT_SUBJECT_JOIN) {
			query += "s=? and bgpIndx=? and patternIndx=? "  + sufix;
			ps = connection.prepareStatement(query);
		} else if(joinType == BasicGraphPattern.PREDICATE_PREDICATE_JOIN) {
			query += "p=? and bgpIndx=? and patternIndx=? " + sufix;
			ps = connection.prepareStatement(query);			
		} else if(joinType == BasicGraphPattern.OBJECT_OBJECT_JOIN) {
			query += "o=? and bgpIndx=? and patternIndx=? " + sufix;
			ps = connection.prepareStatement(query);
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO) {
			query += "s=? and bgpIndx=? and patternIndx=? " + sufix;
			ps = connection.prepareStatement(query);
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS) {
			query += "o=? and bgpIndx=? and patternIndx=? " + sufix;
			ps = connection.prepareStatement(query);
		}
		return ps;
	}
	
	protected PreparedStatement getStatement(Connection connection, Triple triple,
			int bgpIndx, int joinType, String prefix,
			String sufix) throws SQLException {
		if(joinType == BasicGraphPattern.SUBJECT_SUBJECT_JOIN) {
			PreparedStatement ssStatement = preparedSSStatement.get(bgpIndx);
			if(ssStatement == null) {
				ssStatement = createStatement(connection, bgpIndx, joinType, prefix, sufix);
				synchronized (preparedSSStatement) {
					preparedSSStatement.put(bgpIndx, ssStatement);
				}
			}
			ssStatement.setString(1, triple.getSubject());
			return ssStatement;
		} else if(joinType == BasicGraphPattern.PREDICATE_PREDICATE_JOIN) {
			PreparedStatement ppStatement = preparedPPStatement.get(bgpIndx);
			if(ppStatement == null) {
				ppStatement = createStatement(connection, bgpIndx, joinType, prefix, sufix);
				synchronized (preparedPPStatement) {
					preparedPPStatement.put(bgpIndx, ppStatement);
				}
			}
			ppStatement.setString(1, triple.getPredicate());
			return ppStatement;
		} else if(joinType == BasicGraphPattern.OBJECT_OBJECT_JOIN) {
			PreparedStatement ooStatement = preparedOOStatement.get(bgpIndx);
			if(ooStatement == null) {
				ooStatement = createStatement(connection, bgpIndx, joinType, prefix, sufix);
				synchronized (preparedOOStatement) {
					preparedOOStatement.put(bgpIndx, ooStatement);
				}
			}
			ooStatement.setString(1, triple.getObject());
			return ooStatement;
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS) {
			PreparedStatement sossStatement = preparedSOSSStatement.get(bgpIndx);
			if(sossStatement == null) {
				sossStatement = createStatement(connection, bgpIndx, joinType, prefix, sufix);
				synchronized (preparedSOSSStatement) {
					preparedSOSSStatement.put(bgpIndx, sossStatement);
				}
			}
			sossStatement.setString(1, triple.getSubject());
			return sossStatement;
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO) {
			PreparedStatement sosoStatement = preparedSOSOStatement.get(bgpIndx);
			if(sosoStatement == null) {
				sosoStatement = createStatement(connection, bgpIndx, joinType, prefix, sufix);
				synchronized (preparedSOSOStatement) {
					preparedSOSOStatement.put(bgpIndx, sosoStatement);
				}
			}
			sosoStatement.setString(1, triple.getObject());
			return sosoStatement;
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS) {
			PreparedStatement soosStatement = preparedSOOSStatement.get(bgpIndx);
			if(soosStatement == null) {
				soosStatement = createStatement(connection, bgpIndx, joinType, prefix, sufix);
				synchronized (preparedSOOSStatement) {
					preparedSOOSStatement.put(bgpIndx, soosStatement);
				}
			}
			soosStatement.setString(1, triple.getSubject());
			return soosStatement;
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO) {
			PreparedStatement soooStatement = preparedSOOOStatement.get(bgpIndx);
			if(soooStatement == null) {
				soooStatement = createStatement(connection, bgpIndx, joinType, prefix, sufix);
				synchronized (preparedSOOOStatement) {
					preparedSOOOStatement.put(bgpIndx, soooStatement);
				}
			}
			soooStatement.setString(1, triple.getObject());
			return soooStatement;
		}
		return null;
	}
	
	protected PreparedStatement getStatement(Connection connection, 
			Triple triple,
			int bgpIndx, 
			int joinType,
			int patternIndx,
			String prefix,
			String sufix) throws SQLException {
		if(joinType == BasicGraphPattern.SUBJECT_SUBJECT_JOIN) {
			PreparedStatement ssStatement = createStatement(connection, joinType, prefix, sufix);
			ssStatement.setString(1, triple.getSubject());
			ssStatement.setInt(2, bgpIndx);
			ssStatement.setInt(3, patternIndx);
			return ssStatement;
		} else if(joinType == BasicGraphPattern.PREDICATE_PREDICATE_JOIN) {
			PreparedStatement ppStatement = createStatement(connection, joinType, prefix, sufix);
			ppStatement.setString(1, triple.getPredicate());
			ppStatement.setInt(2, bgpIndx);
			ppStatement.setInt(3, patternIndx);
			return ppStatement;
		} else if(joinType == BasicGraphPattern.OBJECT_OBJECT_JOIN) {
			PreparedStatement ooStatement = createStatement(connection, joinType, prefix, sufix);				
			ooStatement.setString(1, triple.getObject());
			ooStatement.setInt(2, bgpIndx);
			ooStatement.setInt(3, patternIndx);
			return ooStatement;
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO) {
			PreparedStatement sosoStatement = createStatement(connection, joinType, prefix, sufix);
			sosoStatement.setString(1, triple.getObject());
			sosoStatement.setInt(2, bgpIndx);
			sosoStatement.setInt(3, patternIndx);
			return sosoStatement;
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS) {
			PreparedStatement soosStatement = createStatement(connection, joinType, prefix, sufix);
			soosStatement.setString(1, triple.getSubject());
			soosStatement.setInt(2, bgpIndx);
			soosStatement.setInt(3, patternIndx);
			return soosStatement;
		}
		return null;
	}

}
