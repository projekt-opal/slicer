package org.rdfslice.util;


public class SystemUtil {
	private static SystemUtil instance = null;
	
	String lineSeparator = (String) java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));
	
	public static SystemUtil getInstance(){
		if(instance == null)
			instance = new SystemUtil();
		
		return instance;
	}
	
	public String getLineSeparator() {
		return lineSeparator;
	}
	
}
