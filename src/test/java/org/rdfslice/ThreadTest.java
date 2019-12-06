package org.rdfslice;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

public class ThreadTest {
	
	@Test
	public void testMultiThreadWithoutCache() throws Exception {		
		URL url = RDFFileInteratorTest.class.getResource("/disambiguationSlice.ttl");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("/result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		RDFSliceStreamEngine.setCache(false);
		
		String pattern = "Select * where { ?subject <http://dbpedia.org/ontology/wikiPageDisambiguates> ?object. ?subject ?predicate ?object2.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, 
				source.getCanonicalPath(), 
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath());
		spe.run();
		
		Assert.assertEquals(3223584, tps.getSize());
	}
	
	@Test
	public void testMultiThreadWithCache() throws Exception {		
		URL url = RDFFileInteratorTest.class.getResource("/disambiguationSlice.ttl");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("/result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		RDFSliceStreamEngine.setCache(true);
		
		String pattern = "Select * where { ?subject <http://dbpedia.org/ontology/wikiPageDisambiguates> ?object. ?subject ?predicate ?object2.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, 
				source.getCanonicalPath(), 
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath());
		spe.run();
		
		Assert.assertEquals(3223584, tps.getSize());
	}
	
	@Test
	public void testMultiThreadWithoutCache2() throws Exception {		
		URL url = RDFFileInteratorTest.class.getResource("/disambiguationSlice2.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("/result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		RDFSliceStreamEngine.setCache(false);
		
		String pattern = "Select * where { ?subject <http://dbpedia.org/ontology/wikiPageDisambiguates> ?object. ?subject ?predicate ?object2.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, 
				source.getCanonicalPath(), 
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath());
		spe.run();
		
		Assert.assertEquals(624, tps.getSize());
	}
	
	@Test
	public void testMultiThreadWithCache2() throws Exception {		
		URL url = RDFFileInteratorTest.class.getResource("/disambiguationSlice2.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("/result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		RDFSliceStreamEngine.setCache(true);
		
		String pattern = "Select * where { ?subject <http://dbpedia.org/ontology/wikiPageDisambiguates> ?object. ?subject ?predicate ?object2.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, 
				source.getCanonicalPath(), 
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath(),
				source.getCanonicalPath());
		spe.run();
		
		Assert.assertEquals(624, tps.getSize());
	}

}
