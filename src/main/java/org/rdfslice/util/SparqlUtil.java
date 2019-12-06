package org.rdfslice.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.openrdf.query.algebra.Projection;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.TriplePattern;

public class SparqlUtil {
	public static Collection<TriplePattern> parserTriples(String query) throws Exception {
		SPARQLParser parser = new SPARQLParser();
		Collection<TriplePattern> patterns = null;
		
		ParsedQuery parsedQuery = parser.parseQuery(query, null);
		Projection projection = (Projection) parsedQuery.getTupleExpr();
		patterns = OpenRDFSparqlUtil.getPatterns(projection.getArg());
		
		return patterns;
	}
	
	public static List<BasicGraphPattern> parser(String query) throws Exception {
		SPARQLParser parser = new SPARQLParser();
		List<BasicGraphPattern> patterns = null;
		if(!query.toLowerCase().contains("select") && !query.toLowerCase().contains("describe")) {
			query = " Select * {" + query + "}";
		}
		ParsedQuery parsedQuery = parser.parseQuery(query, null);
		Projection projection = (Projection) parsedQuery.getTupleExpr();
		patterns = OpenRDFSparqlUtil.getBGPatterns(projection.getArg());
		return patterns;
	}
	
//	public static List<BasicGraphPattern> toBGP(String query) throws Exception{
//		List<BasicGraphPattern> patterns = new ArrayList<BasicGraphPattern>();
//		
//		SPARQLParser parser = new SPARQLParser();
//		
//		ParsedQuery parsedQuery = parser.parseQuery(query, null);
//		Projection projection = (Projection) parsedQuery.getTupleExpr();		
//		TupleExpr ex = projection.getArg();
//		
//		if(ex instanceof Join) {
//			Join join = (Join) ex;
//			Collection<TriplePattern> pattern1 = OpenRDFSparqlUtil.getPatterns(join.getLeftArg());
//			Collection<TriplePattern> pattern2 = OpenRDFSparqlUtil.getPatterns(join.getLeftArg());
//			Collection<BasicGraphPattern> bGPs = SparqlUtil.parser(pattern1.iterator().next(), pattern2.iterator().next());
//			patterns.addAll(bGPs);
//		} else if(ex instanceof Filter) {
////			Filter filter = (Filter) ex; TODO: implement
////			patterns.addAll(getPatterns(filter.getArg()));
//		} else if(ex instanceof Union){
////			Union union = (Union) ex;
////			patterns.addAll(getPatterns(union.getLeftArg()));			
////			patterns.addAll(getPatterns(union.getRightArg()));
//		} else if(ex instanceof LeftJoin){
//			LeftJoin leftJoin = (LeftJoin) ex;
//			Collection<TriplePattern> pattern1 = OpenRDFSparqlUtil.getPatterns(leftJoin.getLeftArg());
//			Collection<TriplePattern> pattern2 = OpenRDFSparqlUtil.getPatterns(leftJoin.getRightArg());
//			Collection<BasicGraphPattern> bGPs = SparqlUtil.parser(pattern1.iterator().next(), pattern2.iterator().next());
//			patterns.addAll(bGPs);
//		}
//		
//		return patterns;
//	}
	
	public static Collection<BasicGraphPattern> parser(TriplePattern... patterns) throws Exception {
		return parser(Arrays.asList(patterns));
	}

	public static List<BasicGraphPattern> parser(
			Collection<TriplePattern> patterns) throws Exception {
		List<BasicGraphPattern> BGPatterns = new ArrayList<BasicGraphPattern>();

		List<TriplePattern> patternArray = new ArrayList<TriplePattern>(patterns);
		// subject join
		while(patternArray.size() > 0) {
			TriplePattern pattern = patternArray.get(0);
			BasicGraphPattern BGPattern = null;
			while(patterns.size() > 0) {
				TriplePattern patternMatch = (TriplePattern) patterns.toArray()[0];
				if(!pattern.equals(patternMatch)) {
					if(BGPattern == null || BGPattern.getJoinCondition() != BasicGraphPattern.SUBJECT_SUBJECT_JOIN)
						throw new Exception("The Basic Graph Pattern could not contain diffrent joins. Different joins in BGP are not supported");
					if(pattern.getSubject().equals(patternMatch.getSubject())) { // subject subject join
						if(BGPattern == null || BGPattern.getJoinCondition() != BasicGraphPattern.SUBJECT_SUBJECT_JOIN) {							
							BGPattern = new BasicGraphPattern();
							BGPattern.setJoinCondition(BasicGraphPattern.SUBJECT_SUBJECT_JOIN);
						}
						BGPattern.add(pattern);
						BGPattern.add(patternMatch);
						patternArray.remove(patternMatch);
						patterns.remove(patternMatch);
					}
					else if(((TriplePattern)pattern).getPredicate().equals(((TriplePattern)patternMatch).getPredicate())) { // predicate predicate join
						if(BGPattern == null || BGPattern.getJoinCondition() != BasicGraphPattern.PREDICATE_PREDICATE_JOIN) {
							BGPattern = new BasicGraphPattern();
							BGPattern.setJoinCondition(BasicGraphPattern.PREDICATE_PREDICATE_JOIN);
						}
						BGPattern.add(pattern);
						BGPattern.add(patternMatch);
						patternArray.remove(patternMatch);
						patterns.remove(patternMatch);
					}
					else if(((TriplePattern)pattern).getObject().equals(((TriplePattern)patternMatch).getObject())) { // object object join
						if(BGPattern == null || BGPattern.getJoinCondition() != BasicGraphPattern.OBJECT_OBJECT_JOIN) {
							BGPattern = new BasicGraphPattern();
							BGPattern.setJoinCondition(BasicGraphPattern.OBJECT_OBJECT_JOIN);
						}
						BGPattern.add(pattern);
						BGPattern.add(patternMatch);
						patternArray.remove(patternMatch);
						patterns.remove(patternMatch);
					}
					else if(((TriplePattern)pattern).getSubject().equals(((TriplePattern)patternMatch).getObject()) || 
							((TriplePattern)pattern).getObject().equals(((TriplePattern)patternMatch).getSubject())) { // subject object join
						if(BGPattern == null || BGPattern.getJoinCondition() != BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
							BGPattern = new BasicGraphPattern();
							BGPattern.setJoinCondition(BasicGraphPattern.SUBJECT_OBJECT_JOIN);
						}
						BGPattern.add(pattern);
						BGPattern.add(patternMatch);
						patternArray.remove(patternMatch);
						patterns.remove(patternMatch);
					} else {
						patterns.remove(patternMatch);
					}
				} else {
					patterns.remove(patternMatch);
				}
			}
			if(BGPattern != null) {
				BGPatterns.add(BGPattern);
				patternArray.remove(pattern);
			}
			if(patternArray.size() > 0 && patternArray.get(0).sameAs(pattern)) {
				BGPattern = null;
				BGPattern = new BasicGraphPattern();
				BGPattern.add(pattern);
				BGPatterns.add(BGPattern);
				patternArray.remove(pattern);
			}
		}
		
		// disjoint
		for(TriplePattern pattern : patternArray) {
			BasicGraphPattern BGPattern = null;
			BGPattern = new BasicGraphPattern();
			BGPattern.add(pattern);
			BGPatterns.add(BGPattern);
		}

		return BGPatterns;
	}

	
}
