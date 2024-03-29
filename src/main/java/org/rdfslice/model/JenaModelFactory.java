package org.rdfslice.model;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class JenaModelFactory {

	public static Triple getNTriple(String pattern) {
		Triple t = null;

		TripleReader rdfStream = new TripleReader();

		ByteArrayInputStream is;
		try {
			is = new ByteArrayInputStream(pattern.getBytes("UTF8"));
			RDFDataMgr.parse(rdfStream, is, Lang.NT);
			org.apache.jena.graph.Triple jenaTriple = rdfStream.getTriple();
			t = new Triple();
			t.setSubject(jenaTriple.getSubject().toString());
			t.setPredicate(jenaTriple.getPredicate().toString());
			t.setObject(jenaTriple.getObject().toString());
		} catch (UnsupportedEncodingException e) {
		} catch (RuntimeException e) {
		}
		return t;

	}

}
