package org.rdfslice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class HttpInputStreamTest {
	
	@Test
	public void testRead() throws IOException {
		HttpInputStream in = new HttpInputStream("https://bitbucket.org/emarx/rdfslice/downloads/slice_v1.1.pdf", 20000);
		BufferedInputStream bfs = new BufferedInputStream(in);
		File dest = new File("test.pdf");
		FileOutputStream out = new FileOutputStream(dest);
		try {
	        try {
	            final byte[] buffer = new byte[1024];
	            int n;
	            while ((n = bfs.read(buffer)) != -1)
	                out.write(buffer, 0, n);
	        }
	        finally {
	            out.close();
	        }
	    }
	    finally {
	        in.close();
	    }
		
		Assert.assertEquals(559248, dest.length());
	}
}
