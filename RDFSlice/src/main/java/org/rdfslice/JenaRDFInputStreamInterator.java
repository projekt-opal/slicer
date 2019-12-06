package org.rdfslice;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rdfslice.model.JenaModelFactory;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;

public class JenaRDFInputStreamInterator implements Iterator<Statement> {
	static Logger logger = Logger.getLogger(JenaRDFInputStreamInterator.class);
	
	String currentLine = null; // current line of the iterator
	Triple currentTriple = null; // current Triple
	InputStream is = null; // RDF iterator file
	LineNumberReader br;
	BufferedInputStream bIS;
	Statement nextStatement;
	Map<String, String> prefixTable;
	
	public JenaRDFInputStreamInterator(InputStream is) throws Exception {
		if(is == null)
			throw new Exception("InputStream could not be Null.");
		this.is = is;
		init(is);
	}
	
	private void init(InputStream is) throws Exception {
		br = new LineNumberReader(new InputStreamReader(is, Charset.forName("UTF-8")));	
		bIS = new BufferedInputStream(is);
		prefixTable = new HashMap<String, String>();
	}

	@Override
	public boolean hasNext() {
		try {
			return  (nextStatement!=null 
					|| readTriple()!=null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Triple readTriple() throws IOException {
		while((currentLine = br.readLine())!=null && !readTriple(currentLine)){}
		if(currentLine == null) {
			return null;
		}
		return currentTriple;
	}

	private boolean readTriple(String currentLine) throws IOException {
		currentTriple = JenaModelFactory.getNTriple(currentLine);
		if(currentTriple == null) {
			logger.warn("Invalid line " + br.getLineNumber() + ": " + currentLine);
			return false;
		}
		return true;
	}

	@Override
	public Statement next() {
		Statement currentStatement;
		
		if(this.nextStatement!=null) {
			currentStatement = this.nextStatement;
			this.nextStatement = null;
		}
		else {
			currentStatement = getNextStatement();
		}
		
		Statement statement = currentStatement;
		
		while(hasNext() && currentStatement.getSubject().equals((this.nextStatement = getNextStatement()).getSubject())) {
			statement.add(nextStatement.get(0));
			this.nextStatement = null;
		}
		
		return statement;
	}
	
	public Statement getNextStatement() {
		String line="";
		try {
			while(currentTriple == null) {
				currentTriple = readTriple();
			}
			
			if(currentTriple == null) {
				throw new Exception("Triple can not be null");
			}
			
			Statement statement = new Statement();
			statement.add(currentTriple);
			
			return statement;
		} catch (Exception e) {
			logger.error("Error parsing the line: " + line, e);
			if(hasNext())
				return getNextStatement();
			return null;
		}
	}

	@Override
	public void remove() {
		// for security reasons not supported
	}

	public void reset() throws IOException {
	}
}
