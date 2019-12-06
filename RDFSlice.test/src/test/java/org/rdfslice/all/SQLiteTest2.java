package org.rdfslice.all;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

import org.rdfslice.InputStreamFactory;
import org.rdfslice.RDFFileIterable;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.JoinCandidate;
import org.rdfslice.query.TriplePattern;
import org.rdfslice.sqlite.MultiSQLiteJCManager;
import org.rdfslice.util.FileUtil;

public class SQLiteTest2 {
	public static void main(String[] args) throws Exception {
		MultiSQLiteJCManager manager = new MultiSQLiteJCManager();
		InputStream stream = InputStreamFactory.get("C:/Users/research/lod/t/instance_types_en.ttl");
		RDFFileIterable rdfIS = new RDFFileIterable(stream, FileUtil.getFileFormat("C:/Users/research/lod/t/instance_types_en.ttl"));
		int i=0;
		Date start = new Date();
		for(org.rdfslice.model.Statement statement: rdfIS) {
			for(Triple triple : statement) {
					i++;
					manager.add(new JoinCandidate(triple.getSubject(), triple.getPredicate(),
							triple.getObject(), 0, "0 "));
			}
		}
		
		Date end = new Date();
		
		System.out.println(" primeiro processo  = " + (end.getTime() - start.getTime()));
		
		BasicGraphPattern BGPattern = new BasicGraphPattern();

		TriplePattern tp = new TriplePattern();
		tp.setSubject("http://dbpedia.org/resource/Autism");
		//tp.setSubject("http://dbpedia.org/resource/432");
		tp.setPredicate("?a");
		tp.setObject("?b");
		BGPattern.add(tp);
		
		Statement st = new Statement();
		Triple triple = new Triple();
		triple.setSubject("http://dbpedia.org/resource/Autism");
		//triple.setSubject("http://dbpedia.org/resource/432");
		triple.setPredicate("predicate");
		triple.setObject("object");
		st.add(triple);
		
		stream.close();
		stream = InputStreamFactory.get("C:/Users/research/lod/t/instance_types_en.ttl");
		rdfIS = new RDFFileIterable(stream, FileUtil.getFileFormat("C:/Users/research/lod/t/instance_types_en.ttl"));
		
		for(Triple t : st) {
			manager.computeMatch(t, 0, BGPattern);
		}
		start = new Date();
		manager.removeNotMatched(0, BGPattern);
		end = new Date();
		System.out.println(" remover nao match  = " + (end.getTime() - start.getTime()));
		i=0;
		start = new Date();
		for(org.rdfslice.model.Statement statement: rdfIS) {
			for(Triple trip : statement) {
				if(manager.match(trip, 0, BGPattern))
					System.out.println(triple);				
			}
		}
		end = new Date();
		System.out.println(" selecionar match final = " + (end.getTime() - start.getTime()));
	}
}
