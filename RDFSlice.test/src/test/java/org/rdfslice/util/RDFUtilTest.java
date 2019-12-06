package org.rdfslice.util;
import junit.framework.Assert;

import org.junit.Test;


public class RDFUtilTest {
	
	@Test
	public void testFTPURL () {
		String FTPURL = "ftp://ftp.idsia.ch/pub/juergen/zuse67scan.pdf";		
		Assert.assertTrue(RDFUtil.isURL(FTPURL));
	}
	
	@Test
	public void testHTTPSURL () {
		String httpsURL = "https://www.globo.com";		
		Assert.assertTrue(RDFUtil.isURL(httpsURL));
	}
	
	@Test
	public void testMalformedHTTPSURL () {
		String httpsURL = "https://www.globo/com";		
		Assert.assertTrue(RDFUtil.isURL(httpsURL));
	}
	
	@Test
	public void testFILEURL () {
		String fileURL = "file://localhost/c/WINDOWS/clock.avi";	
		Assert.assertTrue(RDFUtil.isURL(fileURL));
	}
}
