package org.rdfslice.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class FileCopyTest {
	public static void main(String[] args) throws IOException {
		String s = "I was here!\n";
		byte data[] = s.getBytes();
		ByteBuffer out = ByteBuffer.wrap(data);

		ByteBuffer copy = ByteBuffer.allocate(12);		
				
		File file = new File("C:/Users/research/workspaces/jena/mulata/src/test/resources/foaf_graph2.n3");
//		FileOutputStream fs = new FileOutputStream(file, true);
//		
//		FileChannel fc = fs.getChannel();
		
		RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
		FileChannel fc = accessFile.getChannel();
		//fc.
		
		try  {
		    // Read the first 12
		    // bytes of the file.
		    int nread;
		    do {
		        nread = fc.read(copy);
		    } while (nread != -1 && copy.hasRemaining());

		    // Write "I was here!" at the beginning of the file.
		    fc.position(0);
		    while (out.hasRemaining())
		        fc.write(out);
		    out.rewind();

		    // Move to the end of the file.  Copy the first 12 bytes to
		    // the end of the file.  Then write "I was here!" again.
		    long length = fc.size();
		    fc.position(length-1);
		    copy.flip();
		    while (copy.hasRemaining())
		        fc.write(copy);
		    while (out.hasRemaining())
		        fc.write(out);
		} catch (IOException x) {
		    System.out.println("I/O Exception: " + x);
		}
	}
}
