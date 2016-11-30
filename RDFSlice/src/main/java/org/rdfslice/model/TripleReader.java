package org.rdfslice.model;

import org.apache.jena.riot.system.StreamRDF;

import com.hp.hpl.jena.sparql.core.Quad;

public class TripleReader implements StreamRDF {
		Triple t;
		public TripleReader() {
		}
		
		@Override
		public void triple(com.hp.hpl.jena.graph.Triple arg0) {
			String subject = arg0.getSubject().toString();
			String predicate = arg0.getPredicate().toString();
			String object = arg0.getObject().toString();
			
			t = new Triple();
			t.setObject(object);
			t.setSubject(subject);
			t.setPredicate(predicate);				
		}
		
		public Triple getTriple() {
			return t;
		}
		
		@Override
		public void start() {
		}
		
		@Override
		public void quad(Quad arg0) {			
		}
		
		@Override
		public void prefix(String arg0, String arg1) {
		}
		
		@Override
		public void finish() {
		}
		
		@Override
		public void base(String arg0) {
		}
	}