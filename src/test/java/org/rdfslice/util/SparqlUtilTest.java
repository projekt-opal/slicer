package org.rdfslice.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.rdfslice.query.BasicGraphPattern;

public class SparqlUtilTest {
	
	@Test
	public void testMultiJoinParserMultiJoin() throws Exception {
		String query = "Select * where {?s ?p1 ?o1. ?s ?p2 ?o2. ?o2 ?p3 ?o3.}";
		List<BasicGraphPattern> basicGraphPatterns = SparqlUtil.parser(query);
		Assert.assertTrue(basicGraphPatterns.size() == 1);
		Assert.assertTrue(basicGraphPatterns.get(0).getJoinCondition() == BasicGraphPattern.MULTI_JOIN);
	}
	
	@Test
	public void testMultiJoinParserSubjectSubject() throws Exception {
		String query = "Select * where {?s ?p1 ?o1. ?s ?p2 ?o2.}";
		List<BasicGraphPattern> basicGraphPatterns = SparqlUtil.parser(query);
		Assert.assertTrue(basicGraphPatterns.size() == 1);
		Assert.assertTrue(basicGraphPatterns.get(0).getJoinCondition() == BasicGraphPattern.SUBJECT_SUBJECT_JOIN);
	}
	
	@Test
	public void testMultiJoinParserSubjectSubjectTreePatterns() throws Exception {
		String query = "Select * where {?s ?p1 ?o1. ?s ?p2 ?o2. ?s ?p3 ?o3}";
		List<BasicGraphPattern> basicGraphPatterns = SparqlUtil.parser(query);
		Assert.assertTrue(basicGraphPatterns.size() == 1);
		Assert.assertTrue(basicGraphPatterns.get(0).getJoinCondition() == BasicGraphPattern.SUBJECT_SUBJECT_JOIN);
	}
	
	@Test
	public void testMultiJoinParserSubjectSubjectFourPatterns() throws Exception {
		String query = "Select * where {?s ?p1 ?o1. ?s ?p2 ?o2. ?s ?p3 ?o3. ?s ?p4 ?o4}";
		List<BasicGraphPattern> basicGraphPatterns = SparqlUtil.parser(query);
		Assert.assertTrue(basicGraphPatterns.size() == 1);
		Assert.assertTrue(basicGraphPatterns.get(0).getJoinCondition() == BasicGraphPattern.SUBJECT_SUBJECT_JOIN);
	}

}
