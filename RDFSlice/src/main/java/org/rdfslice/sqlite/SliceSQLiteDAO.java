package org.rdfslice.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.util.PatternUtil;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.SynchronousMode;
import org.sqlite.SQLiteOpenMode;

public class SliceSQLiteDAO {
	
	public static final String CANDIDATE_FILE = "slice";
	private static final String CREATE_SCRIPT = "create virtual table jcandidates USING fts4(s, p, o, joinType, pmatch)";
	private static final String TOKEN = "[$]"; // used for "
	private static final String TOKEN2 = "[,]"; // used for )
	private static final String TOKEN3 = "[,,]";// used for (
		
	public void createTable(Connection connection) throws SQLException {
		executeUpdate(connection, CREATE_SCRIPT);
	}
		
	public PreparedStatement prepareInsertStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into jcandidates values(?,?,?,?,?)");
		return statement;
	}
	
	public void addBatch(PreparedStatement statement, Triple t, int joinType, String pmatch) throws SQLException {
		statement.setString(1, tokenize(t.getSubject()));
		statement.setString(2, t.getPredicate());
		statement.setString(3, tokenize(t.getObject()));
		statement.setInt(4, joinType);
		statement.setString(5, pmatch);
		statement.addBatch();
	}
	
	public void execute(PreparedStatement statement) throws SQLException{
		statement.execute();
	}
	
	public void drop(Connection connection, String table) throws SQLException {
		executeUpdate(connection, "drop table if exists " + table);
	}
	
	public void executeUpdate(Connection connection, String command) throws SQLException {
		Statement statement = null;
		statement = connection.createStatement();
		statement.setQueryTimeout(30);  // set timeout to 30 sec.
		statement.executeUpdate(command);
		statement.close();
	}
	
	public Connection getNewConnection(String dbPath) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		SQLiteConfig config = new SQLiteConfig();
		config.setOpenMode(SQLiteOpenMode.DELETEONCLOSE);
		config.setSynchronous(SynchronousMode.OFF);
		//config.setJournalMode(JournalMode.MEMORY);
		config.enforceForeignKeys(false);
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath, config.toProperties()); // create a database connection
		connection.setAutoCommit(false);
		return connection; // create a database connection
	}

	public ResultSet executeQuery(Connection connection, String query) throws Exception {
		Statement statement = null;
		statement = connection.createStatement();
		statement.setQueryTimeout(30);  // set timeout to 30 sec.		
		return statement.executeQuery(query);	
	}

	public boolean match(Connection connection, Triple triple, int joinCondition) throws Exception {		
		ResultSet rs = null;
		try {
			String match = getMatchQuery(joinCondition, triple);
			PreparedStatement ps = connection.prepareStatement("select * from jcandidates where jcandidates MATCH ? LIMIT 1");		
			ps.setString(1, match);
			rs = ps.executeQuery();
			return rs.next();
	   } finally {
		   close(rs);
	   }
	}
	
	private String getMatchQuery(int joinType, Triple triple) {
		String match = "";

		if(joinType == BasicGraphPattern.SUBJECT_SUBJECT_JOIN)
			match = "joinType:0 s:" + tokenize(triple.getSubject());
		else if(joinType == BasicGraphPattern.PREDICATE_PREDICATE_JOIN)
			match = "joinType:1 p:" + triple.getPredicate();
		else if(joinType == BasicGraphPattern.OBJECT_OBJECT_JOIN)
			match = "joinType:2 o:" + tokenize(triple.getObject());
		else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS) 
			match = "joinType:3 s:" + tokenize(triple.getSubject());
		else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO) 
			match = "joinType:3 s:" + tokenize(triple.getObject());		
		else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS) 
			match = "joinType:3 o:" + tokenize(triple.getSubject());
		else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO) 
			match = "joinType:3 o:" + tokenize(triple.getObject());

		return match;
	}
	
	public void close(Connection connection) throws SQLException {
		if(connection != null) {
			connection.close();
		}
	}
	
	public void close(ResultSet rs) throws SQLException {
		if(rs != null) {
			Statement s = rs.getStatement();
			s.close();
		}
	}
	
	public void listAll(Connection connection) throws SQLException {				
		ResultSet rs = connection.createStatement().executeQuery("Select * from jcandidates");
		while(rs.next()) {
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getString(3));
			System.out.println(rs.getString(4));
			System.out.println(rs.getString(5));
		}
		close(rs);
	}

	public void remove(Connection connection, int size) throws Exception {
		executeUpdate(connection, "Delete from jcandidates where pmatch MATCH '0'");
	}

	public void update(Connection connection, Triple triple, int joinType, int pos, int size) throws Exception {
		String match = getMatchQuery(joinType, triple);
		Integer strIdx = (pos*2);
		PreparedStatement ps = null;
		if(pos == 0){
			ps = connection.prepareStatement("update jcandidates set pmatch = 1 || SUBSTR(pmatch,2,length(pmatch)) " + 
					" where jcandidates MATCH ?");
		} else {		
			ps = connection.prepareStatement("update jcandidates set pmatch = SUBSTR(pmatch,1," + (strIdx) + 
					" ) || '1' || SUBSTR(pmatch," + (strIdx + 2) + ", length(pmatch)) " + 
					" where jcandidates MATCH ?");
		}
		ps.setString(1, match);
		ps.execute();
	}
	
	public boolean isChecked(Connection connection, Triple triple, int pos, int joinType) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("select * from jcandidates where jcandidates MATCH ? LIMIT 1");		
		String match = getMatchQuery(joinType, triple);

//		if(joinType == BasicGraphPattern.SUBJECT_SUBJECT_JOIN)
//			match = "joinType:0";
//		else if(joinType == BasicGraphPattern.PREDICATE_PREDICATE_JOIN)
//			match = "joinType:1";
//		else if(joinType == BasicGraphPattern.OBJECT_OBJECT_JOIN)
//			match = "joinType:2";
//		else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN 
//				|| joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS
//				|| joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO
//				|| joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS
//				|| joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO) 
//			match = "joinType:3";
		
//		match += " s:" + tokenize(triple.getSubject()) + " p:" + triple.getPredicate() + " o:" + tokenize(triple.getObject());
		
		ps.setString(1, match);
		ResultSet rs = ps.executeQuery();
		try {		
				String pmatch = "";
				if(rs.next()) {
					pmatch = rs.getString(5);
				Boolean [] pmatchList = PatternUtil.stringToPatternMatch(pmatch);
				return pmatchList[pos];
			} else
				return false;
		} finally {			
			close(rs);
		}
	}
	
	public boolean isCandidate(Connection connection, Triple triple, int joinCondition) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("select * from jcandidates where jcandidates MATCH ? LIMIT 1");		
		String match = getMatchQuery(joinCondition, triple);
		
		ps.setString(1, match);
		ResultSet rs = ps.executeQuery();
		try {	
			return rs.next();
		} finally {
			close(rs);
		}
	}
	
	public PreparedStatement prepareUpdateStatement(Connection connection,
			int joinCondition, int pos) throws SQLException {
		Integer strIdx = (pos*2);
		PreparedStatement ps = null;
		if(pos == 0) {
			ps = connection.prepareStatement("update jcandidates set pmatch = 1 || SUBSTR(pmatch,2,length(pmatch)) " + 
					" where jcandidates MATCH ?");
		} else {
			ps = connection.prepareStatement("update jcandidates set pmatch = SUBSTR(pmatch,1," + (strIdx) + 
					" ) || '1' || SUBSTR(pmatch," + (strIdx + 2) + ", length(pmatch)) " + 
					" where jcandidates MATCH ?");
		}
		return ps;
	}
	
	private static String tokenize(String target){
		return target.replace("\"", TOKEN).replace(")", TOKEN2).replace("(", TOKEN3);
	}
}
