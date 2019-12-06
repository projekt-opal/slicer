package org.rdfslice.util;

public class StringUtil {
	public static boolean endWith(String value, char c){
		if(value.length()==0)
			return false;
		
		return value.charAt(value.length()-1) == c;
	}
	
	public static boolean startWith(String value, char c){
		if(value.length()==0)
			return false;
		
		return value.charAt(0) == c;
	}
}
