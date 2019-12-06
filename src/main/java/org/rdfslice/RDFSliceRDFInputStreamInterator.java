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
import org.rdfslice.model.IStatement;
import org.rdfslice.model.ModelFactory;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.util.RDFUtil;
import org.rdfslice.util.SystemUtil;

public class RDFSliceRDFInputStreamInterator implements Iterator<Statement> {
	static Logger logger = Logger.getLogger(RDFSliceRDFInputStreamInterator.class);
	
	String currentLine = null; // current line of the iterator
	InputStream is = null; // RDF iterator file
	LineNumberReader br;
	BufferedInputStream bIS;
	Statement triple;
	Statement nextStatement;
	Map<String, String> prefixTable;
	
	public RDFSliceRDFInputStreamInterator(InputStream is) throws Exception {
		if(is == null) {
			throw new Exception("InputStream could not be Null.");
		}
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
					|| readLine()!=null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String readLine() throws IOException {
		while((currentLine = br.readLine())!=null && !isValid(currentLine)){}
		return currentLine;
	}

	private boolean isValid(String currentLine) throws IOException {
		
		if(currentLine.length() <= 6)
			return false;
		
		boolean comment = (currentLine.charAt(0)=='#');
		boolean error = (currentLine.charAt(currentLine.length()-1)!='.');
		
		if(!comment && !error && (currentLine.charAt(currentLine.length()-1)!=IStatement.NTRIPLE_SEPARATOR)) { // to avoid extra empty space
			int pos = RDFUtil.getNTripleTeminatorPos(currentLine);
			
			if(pos == -1) {
				return false;
			}
				
			error = (currentLine.charAt(pos) != IStatement.NTRIPLE_TERMINATOR);
		}
		if(!comment && error)
			logger.warn("Invalid line " + br.getLineNumber() + ": " + currentLine);
		
		return !comment && !error;
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
			while((currentLine != null && !RDFUtil.isASimpleStatement(currentLine))) {
				line += (currentLine) + SystemUtil.getInstance().getLineSeparator();
				currentLine = readLine();
			}
			
			if(currentLine == null)
				throw new Exception("Line could not be null");
			
			line += currentLine;
			Triple triple = ModelFactory.getNTriple(line);
			Statement statement = new Statement();
			statement.add(triple);
			if(statement.isPrefix()) {
				Triple prefix = (Triple) statement.get(0);
				prefixTable.put(prefix.getPredicate(), prefix.getObject());
			} else
				statement.replacePrefixes(prefixTable);
			
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
