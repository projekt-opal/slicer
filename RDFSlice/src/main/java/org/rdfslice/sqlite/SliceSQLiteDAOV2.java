package org.rdfslice.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rdfslice.RDFSliceStreamEngine;
import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.JoinCandidate;
import org.rdfslice.sqlite.cache.JoinCandidateCache;
import org.rdfslice.sqlite.cache.SQLiteCache;
import org.rdfslice.util.JoinConditionUtil;
import org.rdfslice.util.PatternUtil;
import org.rdfslice.util.RDFUtil;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.JournalMode;
import org.sqlite.SQLiteConfig.SynchronousMode;
import org.sqlite.SQLiteOpenMode;

public class SliceSQLiteDAOV2 {
	
	static Logger logger = Logger.getLogger(SliceSQLiteDAOV2.class);
	
	CandidateQueryFactory candidateQueryFactory = new CandidateQueryFactory();
	CheckQueryFactory checkQueryFactory = new CheckQueryFactory();	
	MatchQueryFactory matchQuery = new MatchQueryFactory();
	
	Map<Integer, UpdateQueryFactory> updateQueryFactories = new HashMap<Integer, UpdateQueryFactory>();
	
	public static final String CANDIDATE_FILE = "slice";
	public static final String CANDIDATE_TABLE = "jcandidates";
	
	public static volatile long cacheHits = 0;
	
	private static SQLiteCache candidateCache = new SQLiteCache();
	private static SQLiteCache matchCache = new SQLiteCache();
	private JoinCandidateCache jcCache;
	
	private static final String CREATE_SCRIPT = "create table jcandidates(s varchar(200), p varchar(200), o varchar(200), bgpIndx integer, joinType integer, pmatch varchar(15), patternIndx integer); ";
	
	public SliceSQLiteDAOV2(JoinCandidateCache jcCache) {
		this.jcCache = jcCache;
	}
		
	public SliceSQLiteDAOV2() {
	}

	public void createTable(Connection connection) throws SQLException {
		executeUpdate(connection, CREATE_SCRIPT);
		executeUpdate(connection, "CREATE INDEX jci1 ON jcandidates(s,bgpIndx,joinType);CREATE INDEX jci2 ON jcandidates(p,bgpIndx,joinType);CREATE INDEX jci3 ON jcandidates(o,bgpIndx,joinType);");
	}
		
	public void addBatch(PreparedStatement statement, Triple t, int joinType, int bgpIndx, String pmatch, int patternIndx) throws SQLException {
		statement.setString(1, t.getSubject());
		statement.setString(2, t.getPredicate());
		statement.setString(3, t.getObject());
		statement.setInt(4, bgpIndx);
		statement.setInt(5, JoinConditionUtil.getBasicJoinType(joinType)); // in case of SO join, (31 32 33 34) convert to 3
		statement.setString(6, pmatch);
		statement.setInt(7, patternIndx);
		if(jcCache != null) {
			JoinCandidate jc = new JoinCandidate(t, bgpIndx, joinType, pmatch);
			jcCache.put(jc);
		}
	}
	
	public void add(PreparedStatement statement, Triple t, int joinType, int bgpIndx, String pmatch, int patternIndx) throws SQLException {
		statement.setString(1, t.getSubject());
		statement.setString(2, t.getPredicate());
		statement.setString(3, t.getObject());
		statement.setInt(4, bgpIndx);
		statement.setInt(5, JoinConditionUtil.getBasicJoinType(joinType)); // in case of SO join, (31 32 33 34) convert to 3
		statement.setString(6, pmatch);
		statement.setInt(7, patternIndx);		
		if(jcCache != null) {
			JoinCandidate jc = new JoinCandidate(t, bgpIndx, joinType, pmatch);
			jcCache.put(jc);
		}
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
	
	public static Connection getNewConnection(String dbPath, boolean autoCommit, boolean readOnly) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");		
		SQLiteConfig config = new SQLiteConfig();
		if(readOnly) {
			config.setOpenMode(SQLiteOpenMode.READONLY);
			config.setReadOnly(readOnly);
		} else {
			config.setOpenMode(SQLiteOpenMode.DELETEONCLOSE);
		}
		config.setSynchronous(SynchronousMode.OFF);
		config.enableFullSync(false);
		config.enableCaseSensitiveLike(false);
		config.setSharedCache(true);
		config.enforceForeignKeys(false);
		config.setReadUncommited(true);
		config.setJournalMode(JournalMode.OFF);				
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath, config.toProperties()); // create a database connection
		connection.setAutoCommit(autoCommit);
		connection.setReadOnly(readOnly);
		return connection; // create a database connection
	}

	public ResultSet executeQuery(Connection connection, String query) throws Exception {
		Statement statement = null;
		statement = connection.createStatement();
		statement.setQueryTimeout(30);  // set timeout to 30 sec.		
		return statement.executeQuery(query);	
	}

	public boolean match(Connection connection, Triple triple, int bgpIndx, int joinType) throws Exception {
		Boolean isPlaceholderLiteral = isPlaceholderLiteral(triple, joinType);
		
		if(isPlaceholderLiteral) {
			return false;
		}
		
		if(jcCache != null) {
			JoinCandidate joinCandidate = jcCache.get(triple, bgpIndx, joinType);
			if(joinCandidate != null) {// exist someone that match
				return true;
			}
			if(jcCache.size() != jcCache.maxSize()) { // if the cache did not hit the maximum, there is no necessity to go further
				return false;
			}
		}		
		
		Boolean match = matchCache.get(triple, bgpIndx, joinType);		
		if(match != null) {
			cacheHits++;
			return match;
		}		
		
		synchronized (connection) {
			ResultSet rs = null;
			try {
				PreparedStatement ps = matchQuery.get(connection, triple, bgpIndx, joinType);
				rs = ps.executeQuery();
				match = rs.next();
				if(RDFSliceStreamEngine.isCacheEnabled()) {
					matchCache.put(triple, bgpIndx, joinType, match);
				}
				return match;
			} catch (Exception e) {
				logger.error("Error executing match ", e);
			} finally {
				if(rs!= null) {
					rs.close();
				}
			}
			
			return false;
		}
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
			System.out.println("s=" + rs.getString(1));
			System.out.println("p=" +rs.getString(2));
			System.out.println("o=" +rs.getString(3));
			System.out.println("BGP=" + rs.getString(4));
			System.out.println("Join=" + rs.getString(5));
			System.out.println("Pmatch=" + rs.getString(6));
			System.out.println("patternIndx=" + rs.getString(7));
		}
		close(rs);
	}

	public void remove(Connection connection, int bgpIndx, int size) throws Exception {
		String pattern = PatternUtil.patternMatchToString(size);
		
		if(jcCache != null) {
			if(jcCache.size() > 0) {
				jcCache.removeNotMatched();
			}
			if(jcCache.size() != jcCache.maxSize()) { // if the cache did not hit the maximum, there is no necessity to go further
				return;
			}
		}
		
		executeUpdate(connection, "Delete from jcandidates where bgpIndx=" + bgpIndx + " pmatch!='" + pattern + "'");
	}
	
	public void removeMultiJoin(Connection connection, int bgpIndx, int size) throws Exception {
		if(!isAllPatternsFound(connection, bgpIndx, size)) {
			executeUpdate(connection, "Delete from jcandidates where bgpIndx=" + bgpIndx);
		}
	}
	
	public boolean isAllPatternsFound(Connection connection, int bgpIndx, int size) throws Exception {
		synchronized (connection) {
			ResultSet rs = null;
			PreparedStatement ps = null;
			try {
				String query = "Select count(DISTINCT patternIndx) from jcandidates where bgpIndx=" + bgpIndx;
				ps = connection.prepareStatement(query);
				rs = ps.executeQuery();
				if(rs.next()) {
					int patterns = rs.getInt(1);
					return patterns == size;
				}
				return false;
			} finally {
				if(ps != null) {
					ps.close();
				}
			}
		}	
	}

	public void update(Connection connection, Triple triple, int bgpIndx, int joinType, int pos, int size, int subBgpIndex) throws Exception {
		
		if(jcCache != null) {
			JoinCandidate joinCandidate = jcCache.get(triple, bgpIndx, joinType);			
			if(joinCandidate != null) { // exist someone that match
				joinCandidate.setPatternMatch(pos);				
				return; // there is no necessity to use the persistence
			}		
			if(jcCache.size() != jcCache.maxSize()) { // if the cache did not hit the maximum, there is no necessity to go further
				return;
			}
		}
		
		PreparedStatement ps = null;
		Integer strIdx = (pos*2);
		String sufix;
		if(pos == 0) {
			sufix = "update jcandidates set pmatch = 1 || SUBSTR(pmatch,2,length(pmatch)) where ";
		} else {
			sufix = "update jcandidates set pmatch = SUBSTR(pmatch,1," + (strIdx) +
					" ) || '1' || SUBSTR(pmatch," + (strIdx + 2) + ", length(pmatch)) " +
					" where ";
		}
				
		UpdateQueryFactory updateQueryFactory = updateQueryFactories.get(pos);
		if(updateQueryFactory == null) {
			updateQueryFactory = new UpdateQueryFactory();
			updateQueryFactories.put(pos, updateQueryFactory);
		}
		ps = updateQueryFactory.get(connection, triple, bgpIndx, joinType, pos, size, sufix);
		ps.execute();
	}
	
	public boolean isChecked(Connection connection, Triple triple, int pos, int bgpIndx, int joinType) throws SQLException {
		
		if(jcCache != null) {
			JoinCandidate joinCandidate = jcCache.get(triple, bgpIndx, joinType);			
			if(joinCandidate != null) { // exist someone that match				
				return joinCandidate.isChecked(pos);
			}			
			if(jcCache.size() != jcCache.maxSize()) {// if the cache did not hit the maximum, there is no necessity to go further
				return false;
			}
		}
		
		Boolean isChecked;
		synchronized (connection) {
			ResultSet rs = null;
			try {
				PreparedStatement isCheckedStatement = checkQueryFactory.get(connection, triple, bgpIndx, joinType);
				rs = isCheckedStatement.executeQuery();
				String pmatch = "";
				if(rs.next()) {
					pmatch = rs.getString(1);
					Boolean [] pmatchList = PatternUtil.stringToPatternMatch(pmatch);
					isChecked = pmatchList[pos];
				} else {
					isChecked = false;
				}
				return isChecked;
			} finally {
				if(rs != null) {
					rs.close();
				}
			}
		}
	}
	
	private boolean isPlaceholderLiteral(Triple triple, int joinType) {
		if(joinType == BasicGraphPattern.SUBJECT_SUBJECT_JOIN) {
			return false;
		} else if(joinType == BasicGraphPattern.PREDICATE_PREDICATE_JOIN) {
			return false;
		} else if(joinType == BasicGraphPattern.OBJECT_OBJECT_JOIN) {
			return !RDFUtil.isURL(triple.getObject());
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS) {
			return false;
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO) {
			return !RDFUtil.isURL(triple.getObject());
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS) {
			return false;
		} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO) {
			return !RDFUtil.isURL(triple.getObject());
		}
		
		return false;
	}
	
	public boolean isCandidate(Connection connection, Triple triple, int bgpIndx, int joinType) throws SQLException {
		Boolean isPlaceholderLiteral = isPlaceholderLiteral(triple, joinType); 
		
		if(isPlaceholderLiteral) {
			return false;
		}
		
		if(jcCache != null) {
			JoinCandidate joinCandidate = jcCache.get(triple, bgpIndx, joinType);
			if(joinCandidate != null) { // exist someone that match 
				return true;
			}
			if(jcCache.size() != jcCache.maxSize()) {// if the cache did not hit the maximum, there is no necessity to go further
				return false;
			}
		}
		
		Boolean isCandidate = candidateCache.get(triple, bgpIndx, joinType);
		if(isCandidate != null) {
			cacheHits++;
			return isCandidate;
		}
		
		synchronized (connection) {
			PreparedStatement ps = candidateQueryFactory.get(connection, triple, bgpIndx, joinType);
			ResultSet rs = ps.executeQuery();
			try {
				isCandidate = rs.next();
				if(RDFSliceStreamEngine.isCacheEnabled()) {
					candidateCache.put(triple, bgpIndx, joinType, isCandidate);
				}
				return isCandidate;
			} finally {				
			}
		}
	}
	
	public boolean isCandidate(Connection connection, Triple triple, int bgpIndx, int joinType, int patternIndx) throws SQLException {
		Boolean isPlaceholderLiteral = isPlaceholderLiteral(triple, joinType);
		if(isPlaceholderLiteral) {
			return false;
		}
		synchronized (connection) {
			PreparedStatement ps = candidateQueryFactory.get(connection, triple, bgpIndx, joinType, patternIndx);
			ResultSet rs = ps.executeQuery();
			try {
				boolean isCandidate = rs.next();
				return isCandidate;
			} finally {
				if(rs != null) {
					rs.close();
				}
				if(ps != null) {
					ps.close();
				}
			}
		}
	}
	
	public void resetCache() {
		candidateCache = new SQLiteCache();
		matchCache = new SQLiteCache();
	}

}
