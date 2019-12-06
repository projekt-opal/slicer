package org.rdfslice.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfslice.RDFFileInteratorTest;

public class FileUtilTest extends FileUtil {
		
	@Test
	public void testBinarySearchFile() throws Exception {
		URL sortedURLFile = RDFFileInteratorTest.class.getResource("/diseasome_slice_altered_sorted.nt");
		File sortedFile = new File(sortedURLFile.toURI());
		RandomAccessFile accessFile = new RandomAccessFile(sortedFile, "rw");
		long offset = FileUtil.binarySearch(accessFile, 0, accessFile.length(), "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1023>");
		Assert.assertEquals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1023>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases> .", FileUtil.pickLine(accessFile, offset));
		Assert.assertEquals(298, offset);
		
		offset = FileUtil.binarySearch(accessFile, 100, accessFile.length(), "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1493>");
		Assert.assertEquals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1493>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases> .", FileUtil.pickLine(accessFile, offset));
		Assert.assertEquals(9132, offset);
		
		offset = FileUtil.binarySearch(accessFile, 100, accessFile.length(), "<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/966>");
		Assert.assertEquals("<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/966>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases> .", FileUtil.pickLine(accessFile, offset));
		Assert.assertEquals(16348, offset);
	}
	
	@Test
	public void testExternalSort() throws IOException{
//		File in = new File("C:/Users/research/lod/instance_types_en.ttl/instance_types_en.ttl");
//		File out = new File("teste.n4");
//		Date start = new Date();
//		System.out.println(start.getTime());
//		ExternalSort.sort(in, out, ExternalSort.DEFAULTMAXTEMPFILES, Charset.defaultCharset());
//		Date end = new Date();		
//		System.out.println(end.getTime()-start.getTime());
	}
	
	@Test
	public void testSort2() throws Exception{
//		Date start = new Date();
//		System.out.println(start.getTime());
//		//File in = new File("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph2.n3");
//		File in = new File("C:/Users/research/lod/instance_types_en.ttl/instance_types_en.ttl");
//		File out = new File("teste.n3");
//		
//		FileUtil.sort2(in, out);
//		Date end = new Date();		
//		System.out.println(end.getTime()-start.getTime());
	}
}
