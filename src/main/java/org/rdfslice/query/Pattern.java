package org.rdfslice.query;

import org.rdfslice.model.IInstanceStatement;
import org.rdfslice.model.IStatement;

public interface Pattern extends IStatement{
	
	public final static int TRIPLE_PATTERN = 0;
	public final static int STATEMENT_PATTERN = 1;
	
	public boolean match(IInstanceStatement statement);

	public int getNumberOfContants();
}