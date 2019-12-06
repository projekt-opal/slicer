package org.rdfslice.model;



public interface IStatement {
	
	public final static char NTRIPLE_TERMINATOR = '.';
	public final static char MULTIPLE_STATEMENT_SEPARATOR = ';';
	public final static char NTRIPLE_SEPARATOR = ' ';
	public final static char START_COTATION = '<';
	public final static char END_COTATION = '>';
	
	String getSubject();
	String toString();
	int getType();
}
