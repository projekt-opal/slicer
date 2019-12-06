package org.rdfslice;
import java.io.InputStream;
import java.util.Iterator;

import org.rdfslice.model.Statement;


public class RDFFileIterable implements Iterable<Statement>{
	// formats
	public static final String NTRIPLES_FORMAT = "NTRIPLES";
	public static final String N3_FORMAT = "N3";
	public static final String TRIPLES_FORMAT = "TRIPLES";
	public static final String NQUADS_FORMAT = "NQUADS";
	public static final String RDFA_FORMAT = "RDFA";
	public static final String XML_FORMAT = "XML";
	public static final String JSONLD_FORMAT = "JSONLD";
	public static final String TURTLE_FORMAT = "TURTLE";
	
	Iterator<Statement> rdfIS;
	
	public RDFFileIterable(Iterator<Statement> rdfIS) throws Exception{
		this.rdfIS = rdfIS;
	}
	
	public RDFFileIterable(InputStream is, String format) throws Exception{
		this.rdfIS = new OpenRDFInputStreamInterator(is, format);
	}
	
	public RDFFileIterable(String file) throws Exception{
		this.rdfIS = new OpenRDFInputStreamInterator(file);
	}

	@Override
	public Iterator<Statement> iterator() {		
		return rdfIS;
	}
}
