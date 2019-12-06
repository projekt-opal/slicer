package org.rdfslice;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

public class RDFSliceStreamEngineTest {
		
	@Test
	public void testSelectAll() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?p ?k ?n}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(100, tps.getSize());
	}
	
	@Test
	public void testSelectOneSubject() throws Exception{
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/844> ?k ?n}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, tps, source.getCanonicalPath());
		spe.run();		
		Assert.assertEquals(9, tps.getSize());
	}
		
	@Test
	public void testSelectFilterPredicatesDesordedPatterns() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?resource ?object ?predicate. ?resource <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(98, tps.getSize());
	}
	
	@Test
	public void testSelectFilterPredicates() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result1.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?resource <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. ?resource ?object ?predicate.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(98, tps.getSize());
	}
	
	@Test
	public void testSelectFilterObjects1() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result1.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?resource ?type <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases>. ?resource ?object ?predicate.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(98, tps.getSize());
	}
	
	@Test
	public void testSelectFilterObjects2() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result1.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?resource ?type <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114>. ?resource ?object ?predicate.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(11, tps.getSize());
	}
	
	@Test
	public void testSelectFilterComposedObjects() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result1.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { {?resource ?type <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114>. ?resource ?object ?predicate.} " +
				"UNION {?resource ?type <http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/AGA>. ?resource ?object ?predicate.} }";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(11, tps.getSize());
	}
	
	@Test
	public void testSelectFilterComposedObjects2() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result1.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { {?resource ?type <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114>. ?resource ?object ?predicate.} " +
				"UNION {?resource ?type <http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/SAR1B>. ?resource ?object ?predicate.} }";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(21, tps.getSize());
	}
	
	@Test
	public void testSelectFilterPreficatesWithoutOrder() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(11, tps.getSize());
	}
		
	@Test
	public void testSelectObjectObjectJoin() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?subject ?predicate ?object. ?subject2 ?predicate2 ?object.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(23, tps.getSize());
	}
	
	@Test
	public void testSelectDesorderedSubjectPredicateJoin() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?subject ?predicate ?object. ?subject2 ?subject ?object2.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectInstaceSegmentedSubjectObjectJoin() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?subject2 ?predicate2 ?subject. ?subject ?predicate ?object. }";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.INSTANCE_SEGMENTED_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectInstaceSortedObjectSubjectJoin() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?subject2 ?predicate2 ?subject. ?subject ?predicate ?object. }";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.SUBJECT_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectDesorderedSubjectObjectJoin2() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?subject2 <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseaseSubtypeOf> ?subject. ?subject ?predicate ?object. }";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectDesorderedObjectSubjectJoin2() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { ?subject ?predicate ?object. ?subject2 <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseaseSubtypeOf> ?subject.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectDesorderedSubjectObjectJoin() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { ?subject ?predicate ?object. ?subject2 ?predicate2 ?subject.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectDesorderedSubjectObjectJoin3Triples() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { ?subject ?predicate ?object. " +
				" ?subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?object2. " +
				" ?subject2 ?predicate2 ?subject.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectDesorderedSubjectObjectJoin3Triples2() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { ?subject ?predicate ?object. " +
				" ?subject a ?object2. " +
				" ?subject2 ?predicate2 ?subject.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectDesorderedSubjectObjectJoin3Triples3() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { ?subject ?a <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases>. " +
				" ?subject ?p ?object2. " +
				" ?subject2 ?p2 ?subject.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectDesorderedObjectSubjectJoin() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?subject2 ?predicate2 ?subject. ?subject ?predicate ?object. }";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectSubject() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114> ?predicate ?subject.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.SUBJECT_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(9, tps.getSize());
	}
	
	@Test
	public void testSelectSubjectSubject2() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114> ?predicate ?object." +
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114> ?predicate2 ?object2}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.SUBJECT_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(9, tps.getSize());
	}
	
	@Test
	public void testSelectSubjectSubject3() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1493> ?predicate ?object." +
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1493> ?predicate2 ?object2}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.SUBJECT_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(11, tps.getSize());
	}
	
	@Test
	public void testSelectSubjectSubject4() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/239> ?predicate ?object." +
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/239> ?predicate2 ?object2}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.SUBJECT_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectSubjectSubject5() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/970> ?predicate ?object." +
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/970> ?predicate2 ?object2}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.SUBJECT_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(0, tps.getSize());
	}
	
	@Test
	public void testSelectSubjectSubjec6() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/965> ?predicate ?object." +
				"<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/965> ?predicate2 ?object2}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.SUBJECT_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(0, tps.getSize());
	}
	
	@Test
	public void testSelectSubjectVariable1() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases>." +
				"?s ?predicate2 ?object2}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.SUBJECT_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(107, tps.getSize());
	}
	
	@Test
	public void testSelectSubjectObjectMultipleFiles() throws Exception {
		URL url1 = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered.nt");
		File source1 = new File(url1.toURI());
		URL url2 = RDFFileInteratorTest.class.getResource("/dbpedia_chylomicron.nt");
		File source2 = new File(url2.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { " +
				" ?subject <http://www.w3.org/2002/07/owl#sameAs> ?object. " +
				" ?object  ?predicate2 ?object2} ";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source1.getCanonicalPath(), source2.getCanonicalPath());
		spe.run();
		Assert.assertEquals(46, tps.getSize());
	}
	
	@Test
	public void testFTPURls() throws Exception {
		URL url1 = RDFFileInteratorTest.class.getResource("/url_test.nt");
		File source1 = new File(url1.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { " +
				" ?subject <http://dbpedia.org/ontology/wikiPageExternalLink> ?object. " +
				" ?subject ?predicate ?object2} ";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source1.getCanonicalPath());
		spe.run();
		Assert.assertEquals(2, tps.getSize());
	}
	
	@Test
	public void testFTPURls2() throws Exception {
		URL url1 = RDFFileInteratorTest.class.getResource("/url_test.nt");
		File source1 = new File(url1.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where { " +				
				" ?s ?p ?o} ";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source1.getCanonicalPath());
		spe.run();
		Assert.assertEquals(2, tps.getSize());
	}
	
	@Test
	public void testSelectMultiJoin() throws Exception {
		URL url1 = RDFFileInteratorTest.class.getResource("/foaf_graph.n3");
		File source1 = new File(url1.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		
		String pattern = "Select * where { " +
				" ?a ?p ?o. ?a <http://xmlns.com/foaf/0.1/knows> ?d. ?d ?p2 ?o2.} ";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source1.getCanonicalPath());
		spe.run();
		Assert.assertEquals(11, tps.getSize());
	}
	
	@Test
	public void testSelectMultiJoin2() throws Exception {		
		URL url1 = RDFFileInteratorTest.class.getResource("/foaf_graph.n3");
		File source1 = new File(url1.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		
		String pattern = "Select * where { " +
				" ?a ?p ?o. ?a <http://xmlns.com/foaf/0.1/knows> ?d. ?d <http://xmlns.com/foaf/0.1/mbox> ?o2.} ";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source1.getCanonicalPath());
		spe.run();
		Assert.assertEquals(8, tps.getSize());
	}
	
	@Test
	public void testSelectMultiJoinMultipleFiles() throws Exception {		
		URL url1 = RDFFileInteratorTest.class.getResource("/foaf_graph.n3");
		URL url3 = RDFFileInteratorTest.class.getResource("/foaf_graph.n3");
		File source1 = new File(url1.toURI());
		File source2 = new File(url3.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		
		String pattern = "Select * where { " +
				" ?a ?p ?o. ?a <http://xmlns.com/foaf/0.1/knows> ?d. ?d <http://xmlns.com/foaf/0.1/mbox> ?o2.} ";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source1.getCanonicalPath(), source2.getCanonicalPath());
		spe.run();
		Assert.assertEquals(16, tps.getSize());
	}
	
	@Test
	public void testSelectComplexJoinWithFilter() throws Exception {		
//		String file = new String("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		//File file = new File("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		File outPutFile = new File("result.out");
//		String pattern = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> select * where {?d ?l ?k. ?a foaf:knows ?d. ?d foaf:mbox ?n. FILTER (?d = <_:a>)}";
//		
//		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, System.out, file);
//		spe.run();
	}
	
	@Test
	public void testSelectComplexJoinWithUnion() throws Exception {
//		String file = new String("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		//File file = new File("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		File outPutFile = new File("result.out");
//		String pattern = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> select * where {{?d ?l ?k. ?a foaf:knows ?d.} UNION { ?d foaf:mbox ?n. } }";
//		
//		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, System.out, file);		
//		spe.run();
	}
	
	@Test
	public void testSelectComplexJoinWithUnionAndFilter() throws Exception {
//		String file = new String("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		//File file = new File("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		File outPutFile = new File("result.out");
//		String pattern = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> select * where {?d ?l ?k. ?a foaf:knows ?d. ?d foaf:mbox ?n.}";
//		
//		FileInputStream fis = new FileInputStream(file);
//		PrintStream ps = new PrintStream(outPutFile);
//		
//		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, System.out, file);
//		spe.run();
	}
	
	@Test
	public void testSelectComplexJoinWithOptional() throws Exception {
//		String file = new String("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		//File file = new File("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		File outPutFile = new File("result.out");
//		String pattern = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> select * where {?d ?l ?k. ?a foaf:knows ?d. OPTIONAL { ?d foaf:mbox ?n. } }";
//		
//		FileInputStream fis = new FileInputStream(file);
//		PrintStream ps = new PrintStream(outPutFile);
//		
//		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, System.out, file);
//		spe.run();
	}
	
	@Test
	public void testSelectComplexJoinWithOptionalMultiFiles() throws Exception {
//		String file1 = new String("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		String file2 = new String("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		//File file = new File("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph.n3");
//		File outPutFile = new File("result.out");
//		String pattern = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> select * where {?d ?l ?k. ?a foaf:knows ?d. OPTIONAL { ?d foaf:mbox ?n. } }";
//		
//		PrintStream ps = new PrintStream(outPutFile);
//		
//		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, System.out, file1, file2);		
//		spe.run();
	}
	
	@Test
	public void testSelectFilterTypesWithZipFile() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.bz2");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testTriplesCount() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/diseasome_slice.bz2");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(10, tps.getSize());
	}
	
	@Test
	public void testSelectWithComplexTriples() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/dbpedia_complexTriples.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?s ?p ?o.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(5, tps.getSize());
	}
	
	@Test
	public void testSelectWithComplexTriples2() throws Exception {
		URL url = RDFFileInteratorTest.class.getResource("/out.nt");
		File source = new File(url.toURI());
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "Select * where {?s ?p ?o.}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, source.getCanonicalPath());
		spe.run();
		Assert.assertEquals(8643, tps.getSize());
	}
	
	@Test
	public void testSelectWithComplexTriples3() throws Exception {
		URL url1 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-infobox.ttl");
		URL url2 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-literals.ttl");
		URL url3 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-objects.ttl");
		
		File source1 = new File(url1.toURI());
		File source2 = new File(url2.toURI());
		File source3 = new File(url3.toURI());
		
		
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "select * where {{?s a <http://dbpedia.org/ontology/Organisation>.?s ?p ?o.} " +
				" union {?s1 a <http://dbpedia.org/ontology/Organisation>.?o1 ?p1 ?s1. ?o1 ?p2 ?o3} union " +
				"{?s2 a <http://schema.org/Organization>.?s2 ?p2 ?o2.} union " +
				"{?s3 a <http://schema.org/Organization>.?o3 ?p3 ?s3.} union " +
				"{?s4 a <http://dbpedia.org/class/yago/Organization108008335>.?s4 ?p4 ?o4.} union " +
				"{?s5 a <http://dbpedia.org/class/yago/Organization108008335>.?o5 ?p5 ?s5.} union " +
				"{?s6 a <http://dbpedia.org/class/yago/Organization101136519>.?s6 ?p6 ?o6.} union " +
				"{?s7 a <http://dbpedia.org/class/yago/Organization101136519>.?o7 ?p7 ?s7.}}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, 
				source1.getCanonicalPath(),
				source2.getCanonicalPath(),
				source3.getCanonicalPath());
		spe.run();
		Assert.assertEquals(8643, tps.getSize());
	}
	
	@Test
	public void testSelectWithComplexTriples5() throws Exception {
		URL url1 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-infobox.ttl");
		URL url2 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-literals.ttl");
		URL url3 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-objects.ttl");
		
		File source1 = new File(url1.toURI());
		File source2 = new File(url2.toURI());
		File source3 = new File(url3.toURI());
		
		
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "select * where {{?s a <http://dbpedia.org/ontology/Organisation>.?s ?p ?o.} " +
				" union {?s1 a <http://dbpedia.org/ontology/Organisation>.?o1 ?p1 ?s1.} union " +
				"{?s2 a <http://schema.org/Organization>.?s2 ?p2 ?o2.} union " +
				"{?s3 a <http://schema.org/Organization>.?o3 ?p3 ?s3.} union " +
				"{?s4 a <http://dbpedia.org/class/yago/Organization108008335>.?s4 ?p4 ?o4.} union " +
				"{?s5 a <http://dbpedia.org/class/yago/Organization108008335>.?o5 ?p5 ?s5.} union " +
				"{?s6 a <http://dbpedia.org/class/yago/Organization101136519>.?s6 ?p6 ?o6.} union " +
				"{?s7 a <http://dbpedia.org/class/yago/Organization101136519>.?o7 ?p7 ?s7.}}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, 
				source1.getCanonicalPath(),
				source2.getCanonicalPath(),
				source3.getCanonicalPath());
		spe.run();
		Assert.assertEquals(18734, tps.getSize());
	}
	
	@Test
	public void testSelectWithComplexTriples6() throws Exception {
		URL url1 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-infobox.ttl");
		URL url2 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-literals.ttl");
		URL url3 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-objects.ttl");
		
		File source1 = new File(url1.toURI());
		File source2 = new File(url2.toURI());
		File source3 = new File(url3.toURI());
		
		
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "select * where {{?s a <http://dbpedia.org/ontology/Organisation>.?s ?p ?o.} " +
				" union {?s1 a <http://dbpedia.org/ontology/Organisation>.?o1 ?p1 ?s1.?o1 ?p8 ?o8} }";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, 
				RDFSliceStreamEngine.NO_ORDER, 
				tps, 
				source1.getCanonicalPath(),
				source2.getCanonicalPath(),
				source3.getCanonicalPath());
		spe.run();
		Assert.assertEquals(23015, tps.getSize());
	}
	
	public void testSelectWithComplexTriples7() throws Exception {
		URL url1 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-infobox.ttl");
		URL url2 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-literals.ttl");
		URL url3 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-objects.ttl");
		
		File source1 = new File(url1.toURI());
		File source2 = new File(url2.toURI());
		File source3 = new File(url3.toURI());
		
		
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "select * where {{?s a <http://dbpedia.org/ontology/Organisation>.?s ?p ?o.} " +
				" union {?s1 a <http://dbpedia.org/ontology/Organisation>.?o1 ?p1 ?s1.?o1 ?p8 ?o8} union " +
				"{?s2 a <http://schema.org/Organization>.?s2 ?p2 ?o2.} union " +
				"{?s3 a <http://schema.org/Organization>.?o3 ?p3 ?s3.} union " +
				"{?s4 a <http://dbpedia.org/class/yago/Organization108008335>.?s4 ?p4 ?o4.} union " +
				"{?s5 a <http://dbpedia.org/class/yago/Organization108008335>.?o5 ?p5 ?s5.} union " +
				"{?s6 a <http://dbpedia.org/class/yago/Organization101136519>.?s6 ?p6 ?o6.} union " +
				"{?s7 a <http://dbpedia.org/class/yago/Organization101136519>.?o7 ?p7 ?s7.}}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, 
				source1.getCanonicalPath(),
				source2.getCanonicalPath(),
				source3.getCanonicalPath());
		spe.run();
		Assert.assertEquals(23015, tps.getSize());
	}
	
	@Test
	public void testSelectWithComplexTriples4() throws Exception {
		URL url1 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-infobox.ttl");
		URL url2 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-literals.ttl");
		URL url3 = RDFFileInteratorTest.class.getResource("/dataset/microsoft-mappingbased-objects.ttl");
		
		File source1 = new File(url1.toURI());
		File source2 = new File(url2.toURI());
		File source3 = new File(url3.toURI());
		
		
		File mockOutPutFile = new File("result.out");
		MockPrintSream tps = new MockPrintSream(mockOutPutFile);
		String pattern = "select * where { " +
				" ?s1 a <http://dbpedia.org/ontology/Organisation>.?o1 ?p1 ?s1. ?o1 ?p2 ?o3}";
		RDFSliceStreamEngine spe = new RDFSliceStreamEngine(pattern, RDFSliceStreamEngine.NO_ORDER, tps, 
				source1.getCanonicalPath(),
				source2.getCanonicalPath(),
				source3.getCanonicalPath());
		spe.run();
		Assert.assertEquals(23015, tps.getSize());
	}

}