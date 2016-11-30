package org.rdfslice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.routines.UrlValidator;
import org.rdfslice.model.IStatement;
import org.rdfslice.model.Statement;

public class RDFUtil extends PatternUtil {
	public static String[] splitTriple(String statement){
		String[] terms = splitTerms(statement);
		for(int i=0; i< 3; i++) {
			if(RDFUtil.isVariableCoted(terms[i])) {
				terms[i] = removeCotation(terms[i]);
			}
		}
		terms[2] = removeDots(terms[2]);
		return  terms;
	}
	
	public static String[] splitTerms(String statement){
//		List<String> terms = new ArrayList<String>();		
//		String[] terms = null;
//		try {
//			
//			boolean isStatement = statement.lastIndexOf(".") == statement.length()-1;
//			if(isStatement)
//				terms= new String[4];
//			else
//				terms= new String[3];
//			
//			int i=0,j=0;
//			char[] statementChar = statement.toCharArray();
//			String component="";
//			while(i<statementChar.length){
//				while((i<statementChar.length) 
//						&& (statementChar[i] != ' ') 
//						&& (statementChar[i] != '\t')) {
//					component+=statementChar[i];
//					i++;
//				}
//				terms[j] = component;
//				component="";
//				j++;
//				while((i<statementChar.length) && (
//						(statementChar[i] == ' ') 
//						|| (statementChar[i] == '\t'))) {					
//					i++;
//				}
//				if(j==2)
//					break;
//			}
//			
//			int sIndx = statement.lastIndexOf(' ');
//			int tIndx = statement.lastIndexOf('\t');
//			int fIndx=0;
//			if(sIndx>tIndx)
//				fIndx = sIndx;
//			else
//				fIndx = tIndx;
//			
//			if(isStatement) {
//				terms[2] = statement.substring(i, fIndx);
//				terms[3] = statement.substring(statement.length()-1, statement.length());
//			} else
//				terms[2] = statement.substring(fIndx + 1, statement.length());
//		} catch (Exception e) {
//			System.out.println(e);
//			return terms;
//		}
		
//		Pattern regex = Pattern.compile("[^\\s\"]+|\"[^\"]*\"|'[^']*'");
//		
//		Matcher regexMatcher = regex.matcher(statement);
//		
//		while (regexMatcher.find()) {
//			String var = regexMatcher.group();
//			terms.add(var);
//		}
//		
//		return  terms.toArray(new String[terms.size()]);		
		String[] terms = null;
		try {
			boolean isStatement = statement.lastIndexOf(".") == statement.length()-1;
			if(isStatement)
				terms= new String[4];
			else
				terms= new String[3];
			int fIndx = 0;
			int fIndx1 = statement.indexOf('\t', 0);
			int fIndx2 = statement.indexOf(' ', 0);
			int sIndx = 0;
			if(fIndx1<fIndx2 && fIndx1>0)
				fIndx = fIndx1;
			else
				fIndx = fIndx2;			
			terms[0] = statement.substring(0, fIndx);
			sIndx=fIndx;
			fIndx1 = statement.indexOf('\t', sIndx + 1);
			fIndx2 = statement.indexOf(' ', sIndx + 1);
			if(fIndx1<fIndx2 && fIndx1>0)
				fIndx = fIndx1;
			else
				fIndx = fIndx2;			
			while(statement.charAt(sIndx+1) == ' '
					|| statement.charAt(sIndx+1) == '\r' 
					|| statement.charAt(sIndx+1) == '\t')
				sIndx++;
			terms[1] = statement.substring(sIndx + 1, fIndx);
			while(statement.charAt(fIndx+1) == ' '
					|| statement.charAt(fIndx+1) == '\r'
					|| statement.charAt(fIndx+1) == '\t')
				fIndx++;			
			if(isStatement) {
				terms[2] = statement.substring(fIndx + 1, statement.length()-2);
				terms[3] = statement.substring(statement.length()-1, statement.length());
			} else
				terms[2] = statement.substring(fIndx + 1, statement.length());
		} catch (Exception e) {
			System.out.println(e);
			return terms;
		}
		
		return terms;
	}
	
	public static String[] splitComposedStatement(String statement){
		List<String> statements = new ArrayList<String>();
		String[] terms = splitTerms(statement);
		statements.add(terms[0] + IStatement.NTRIPLE_SEPARATOR + terms[1] + IStatement.NTRIPLE_SEPARATOR + terms[2]);
		for(int j = 1; j < (terms.length-3)/2+1; j++){
			statements.add(terms[0] + IStatement.NTRIPLE_SEPARATOR + terms[(j*2)+1] + IStatement.NTRIPLE_SEPARATOR + terms[(j*2)+2]);
		}
		
		return statements.toArray(new String[statements.size()]);
	}	
	
	public static boolean isASimpleStatement(String line) {
		int pos = getNTripleTeminatorPos(line);
		
		if(pos==-1)
			return false;
		
		return line.charAt(pos) == IStatement.NTRIPLE_TERMINATOR;
	}
	
	public static int getNTripleTeminatorPos(String triple) {
		int length = triple.length()-1;
		int pos = length;
		char c = 0;
		while(pos >= 0 && (triple.charAt(pos) != IStatement.NTRIPLE_TERMINATOR) && ((triple.charAt(pos) == IStatement.NTRIPLE_SEPARATOR) || (triple.charAt(pos) == '\t') || (triple.charAt(pos) == '\r')
				|| (triple.charAt(pos) == c)))
			pos--;
		
		if(triple.charAt(pos) != IStatement.NTRIPLE_TERMINATOR)
			return -1;
		
		return pos;
	}

	public static String[] splitStatement(String expression){
		String[] statements;
		if(expression.endsWith(""+IStatement.NTRIPLE_TERMINATOR)){
			statements = expression.split(""+IStatement.NTRIPLE_TERMINATOR);
			return statements;
		}
		
		statements = new String[1];
		statements[0] = expression;
		
		return statements;
	}

	public static String replacePrefixes(String value,
			Map<String, String> prefixMap){
		for(String prefix : prefixMap.keySet()){
			String satementComponent = prefixMap.get(prefix);
			if(value.contains(prefix)){
				value = value.replaceFirst(prefix, satementComponent);
				break;
			}
		}
		
		return value;
	}
	
	public static boolean isVariableCoted(String var){
		return StringUtil.startWith(var, IStatement.START_COTATION) &&
				StringUtil.endWith(var,IStatement.END_COTATION);
	}
	
	public static String removeCotation(String var){
		return var.substring(1, var.length()-1);
	}
	
	public static String removeDots(String var){
		if(StringUtil.startWith(var, Statement.NTRIPLE_TERMINATOR))
			return "";

		if(StringUtil.endWith(var, Statement.NTRIPLE_TERMINATOR))
			return var.substring(0, var.length()-1);

		return var;
	}
	
	public static String cote(String var){
		return IStatement.START_COTATION + var + IStatement.END_COTATION;
	}

	public static boolean isURL(String url) {
		
		String lowerCaseURL = url.toLowerCase();
		
		if(lowerCaseURL.startsWith("http:") || 
				lowerCaseURL.startsWith("ftp:") || 
				lowerCaseURL.startsWith("file:") ||
				lowerCaseURL.startsWith("mailto:") ||
				lowerCaseURL.startsWith("news:") ||
				lowerCaseURL.startsWith("urn:") ||
				lowerCaseURL.startsWith("https:")) 
				return true;
			
		UrlValidator urlValidator = new UrlValidator();
		return urlValidator.isValid(url);		
	}
}
