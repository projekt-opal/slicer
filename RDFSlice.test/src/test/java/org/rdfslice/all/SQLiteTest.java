package org.rdfslice.all;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.rdfslice.model.Triple;
import org.rdfslice.sqlite.SliceSQLiteDAOV2;


public class SQLiteTest {
	public static void main(String[] args) throws Exception {
		Class.forName("org.sqlite.JDBC");
//		connection.setAutoCommit(false);
//		Statement statement = connection.createStatement();
//		//statement.setQueryTimeout(30);  // set timeout to 30 sec.
//		
//		ResultSet rs = statement.executeQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'jcandidates'");
//		if(rs.next())
//			statement.executeUpdate("drop table if exists jcandidates");
//		statement.executeUpdate("create virtual table jcandidates USING fts4(s, p, o, joinType, pmatch)");
//		PreparedStatement ps = connection.prepareStatement("insert into jcandidates values(?, ?, ?)");	
//		ps.setString(1, "<_:a> <http://xmlns.com/foaf/0.1/knows> <[']Jon Foobar[']>.");
//		ps.setInt(2, 1);
//		ps.setString(3, "<_:a> <http://xmlns.com/foaf/0.1/knows> <[']Jon Foobar[']>.");
//		ps.addBatch();
//		ps.executeBatch();
//		ps.setString(1, "<_:b> <http://xmlns.com/foaf/0.1/knows> <[']Jon Foobar[']>.");
//		ps.setInt(2, 1);
//		ps.setString(3, "<_:a> <http://xmlns.com/foaf/0.1/knows> <[']Jon Foobar[']>.");
//		ps.addBatch();
//		ps.executeBatch();
//		ps.setString(1, "<_:c> <http://xmlns.com/foaf/0.1/knows> <[']Jon Foobar[']>.");
//		ps.setInt(2, 1);
//		ps.setString(3, "<_:a> <http://xmlns.com/foaf/0.1/knows> <[']Jon Foobar[']>.");
//		ps.addBatch();
//		ps.executeBatch();
//		
//		connection.commit();
		
//		
//		SQLiteJCManager jcm = new SQLiteJCManager();
//		JoinCandidate jc = new JoinCandidate("a", "b", "c", 1, "2");
//		jcm.add(jc);
//		jc = new JoinCandidate("c", "b", "c", 1, "2");
//		jcm.add(jc);
//		jc = new JoinCandidate("e", "b", "c", 1, "2");
//		jcm.add(jc);		
//		jcm.flush();
		
//		<_:a> <http://xmlns.com/foaf/0.1/knows> <_:c>.
//		3
//		0 0 
//		<_:d> <http://xmlns.com/foaf/0.1/knows> <_:a>.
//		3
//		0 0 
//		<_:d> <http://xmlns.com/foaf/0.1/knows> <_:c>.
//		3
//		0 0 
		
		SliceSQLiteDAOV2 dao = new SliceSQLiteDAOV2();
		Connection c = dao.getNewConnection("./candidates/teste.db", false, false);
		dao.drop(c, "jcandidates");
		dao.createTable(c);
		//dao.createTable(c);
		PreparedStatement ps =  c.prepareStatement("insert into jcandidates values(?,?,?,?,?,?)");
		Triple t = new Triple();
		for(int i = 0; i<10000; i++) {
			t.setSubject(i+"<http://dbpedia.org/resource/Autism>");
			t.setPredicate("http://xmlns.com/foaf/0.1/knowss");
			t.setObject("_:c");
			dao.addBatch(ps, t, 3,0, "0 0 ", 0);
		}
		t.setSubject("<http://dbpedia.org/resource/Autism>2");
		t.setPredicate("http://xmlns.com/foaf/0.1/knows");
		t.setObject("_:a");
		dao.addBatch(ps, t, 3,0, "0 0 ", 0);
		t.setSubject("<http://dbpedia.org/resource/Autism>3");
		t.setPredicate("http://xmlns.com/foaf/0.1/knows");
		t.setObject("_:c");
		dao.addBatch(ps, t, 3,0, "0 0 ", 0);
		ps.executeBatch();
		dao.listAll(c);
		c.commit();
		ps.close();
		
//		ps = c.prepareStatement("select * from jcandidates where s=");
//		ps.setString(1, "_a");
				
//		dao.listAll(c);		
		dao.listAll(c);
		
		//c.close();
		
		//c = dao.getNewConnection("./candidates/teste.db", true);
		
		Date start = new Date();
		Date end = null;
		ps = c.prepareStatement("Select * from jcandidates where s=?");
		for(int i=0; i< 10000000; i++){
			dao.match(c, t, 3, 0);
//			ps.setString(1, i+"<http://dbpedia.org/resource/Autism>");
//			ResultSet rs = ps.executeQuery();
//			rs.close();
			
			if(i%1000000==0) {				
				end = new Date();
				System.out.println("passou:" + (end.getTime() - start.getTime()));
				start = new Date();
			}
		}
		
		ps.close();
		
		
		
//
		ps = c.prepareStatement("select * from jcandidates where ?");
		ps.setString(1, "s='_:a'");
		ResultSet rs = ps.executeQuery();
		while(rs.next())// <_:a> <http://xmlns.com/foaf/0.1/knows> <_:c>.
		{
	        // read the result set
	        System.out.println("name = " + rs.getString(1));
	        System.out.println("2 = " + rs.getString(2));
	        System.out.println("3 = " + rs.getString(3));
	        System.out.println("5 = " + rs.getString(5));
	        //System.out.println("id = " + rs.getString("p"));
		}
		
//		dao.listAll(c);		
		dao.listAll(c);
////		dao.listAll(c);
//		
		ps = c.prepareStatement("update jcandidates set pmatch = SUBSTR(pmatch,1, 2) || '2' || SUBSTR(pmatch,4, length(pmatch))  WHERE ");
		ps.setString(1, "s='_:a'");
		ps.execute();
		
		dao.listAll(c);
		
//		ps.addBatch();
//		ps.executeBatch();
//		c.commit();
//		ps.setString(2, "<_:d>");
//////		//statement.executeUpdate("insert into jcandidates values('<_:a> <http://xmlns.com/foaf/0.1/knows> <'Jon Foobar'>.', 5,  '0 1 0')");
		//ps.execute();
//		//statement.executeUpdate("update jcandidates set s = 'ab' where s='aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab'");
//		//statement.executeUpdate("insert into jcandidates values('d', 'leo', 'b', 1,  'c')");
//		//connection.prepareStatement(sql)
		ps.executeBatch();
		c.commit();
		dao.listAll(c);
		
//		InputStream stream = InputStreamFactory.get("C:/Users/research/lod/t/instance_types_en.ttl");
//		RDFFileIterable rdfIS = new RDFFileIterable(stream);
//		ps = dao.prepareInsertStatement(c);
//		int i=0;
//		for(org.sparqlfile.model.Statement statement: rdfIS) {
//			for(Triple triple : statement) {
//					dao.addBatch(ps, triple, 3, "0 0 ");
//					if((i%100)==0)
//						ps.executeBatch();
//					i++;
//					System.out.println(i);
//			}
//		}
		
		
			
		ps = c.prepareStatement("update jcandidates set pmatch='00' where jcandidates MATCH ?");
		ps.setString(1, "s:http://dbpedia.org/resource/432");
		ps.executeUpdate();
		ps = c.prepareStatement("select * from jcandidates where jcandidates MATCH ?");
		ps.setString(1, "s:http://dbpedia.org/resource/432");
		rs = ps.executeQuery();
//		//ps.setString(1, "<_:a> <http://xmlns.com/foaf/0.1/knows> <[']Jon Foobar[']>.");
//		ps.executeUpdate();
//		
//		//select * from jcandidates where t MATCH '<_:a> <http://xmlns.com/foaf/0.1/knows> <_:c>.' limit 1"
//		
		while(rs.next())// <_:a> <http://xmlns.com/foaf/0.1/knows> <_:c>.
		{
	        // read the result set
	        System.out.println("name = " + rs.getString(1));
	        System.out.println("2 = " + rs.getString(2));
	        System.out.println("3 = " + rs.getString(3));
	        System.out.println("5 = " + rs.getString(5));
	        //System.out.println("id = " + rs.getString("p"));
		}
//		SQLiteConnection db = new SQLiteConnection(new File("/tmp/database"));
//	    db.open(true);
//	    
//	    String createScript = "CREATE TABLE jcandidate(s VARCHAR(200),p VARCHAR(200),o VARCHAR(200),pmatch VARCHAR(50));";    
//	    db.exec(createScript);
	    
	    
	    // INSERT INTO "product" VALUES('Shady Oak','Milk',12,0); 
	    
//	    db.createArray(name, cached)
//	    db.
//	    SQLiteLongArray ll;
//	    ll.
//	    
//	    SQLiteStatement st = db.prepare("SELECT order_id FROM orders WHERE quantity >= ?");
//	    try {
//	      st.bind(1, minimumQuantity);
//	      while (st.step()) {
//	        orders.add(st.columnLong(0));
//	      }
//	    } finally {
//	      st.dispose();
//	    }
//	    db.dispose();
	}
}
