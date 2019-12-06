package org.rdfslice.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectionPool {
	private static ConnectionPool connectionPool = null;
	ArrayList<Connection> readers = new ArrayList<Connection>();
	ArrayList<Connection> writers = new ArrayList<Connection>();
	
	public static ConnectionPool getInstance() {
		if(connectionPool == null) {
			connectionPool = new ConnectionPool();
		}
		return connectionPool;
	}
	
	public Connection getNewReaderConnection(String dbPath) throws ClassNotFoundException, SQLException {
		Connection connection = SliceSQLiteDAOV2.getNewConnection(dbPath, false, true);
		readers.add(connection);
		return connection;
	}
	
	public Connection getNewWriterConnection(String dbPath, boolean autoCommit) throws ClassNotFoundException, SQLException {
		Connection connection = SliceSQLiteDAOV2.getNewConnection(dbPath, autoCommit, false);
		writers.add(connection);
		return connection;
	}
	
	public void closeAllReaders() throws SQLException {
		for(Connection connection : readers) {
			connection.close();
		}
	}
	
	public void closeAll() throws SQLException {
		for(Connection connection : readers) {
			connection.close();			
		}
		for(Connection connection : writers) {
			connection.close();
		}
	}
}
