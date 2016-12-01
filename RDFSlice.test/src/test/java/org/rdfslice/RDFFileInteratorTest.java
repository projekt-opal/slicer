package org.rdfslice;
import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.rdfslice.model.Statement;
import org.rdfslice.util.FileUtil;

public class RDFFileInteratorTest {
	
	static Logger logger = Logger.getLogger(RDFFileInteratorTest.class);
	
	@Test
	public void test() throws Exception {
		InputStream is = RDFFileInteratorTest.class.getResourceAsStream("/diseasome_slice.nt");		
		RDFFileIterable rdfFile = new RDFFileIterable(is, FileUtil.getFileFormat("/diseasome_slice.nt"));
		long i = 0;
		int length = 0;
		for(Statement t: rdfFile) {
			length+=t.size();
			i++;
		}
		
		Assert.assertEquals(11, i);
		
		Assert.assertEquals(100, length);
	}
	
	@Test
	public void testBigZipedFile() throws Exception {
		InputStream is = RDFFileInteratorTest.class.getResourceAsStream("/diseasome_slice.bz2");
    	BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(is, true);
		RDFFileIterable rdfFile = new RDFFileIterable(bzIn, FileUtil.getFileFormat("/diseasome_slice.bz2"));
		long i = 0;
		int length = 0;
		for(Statement t: rdfFile) {
			length+=t.size();
			i++;
		}
		
		Assert.assertEquals(10, i);
		
		Assert.assertEquals(98, length);
	}
	
	@Test
	public void testBigZipedFile2() throws Exception {
		InputStream is = RDFFileInteratorTest.class.getResourceAsStream("/disambiguations_en.ttl.bz2");
    	BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(is, true);
		RDFFileIterable rdfFile = new RDFFileIterable(bzIn, FileUtil.getFileFormat("disambiguations_en.ttl.bz2"));
		long i = 0;
		int length = 0;
		for(Statement t: rdfFile) {
			length+=t.size();
			i++;
		}
		
		Assert.assertEquals(197781, i);
		
		Assert.assertEquals(1157482, length);
	}

}
