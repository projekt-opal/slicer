package org.rdfslice;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class InputStreamFactory {
	public static InputStream get(String filePath) throws Exception{
		InputStream is = null;
		if(filePath.toLowerCase().startsWith("http")) {		
			is = new HttpInputStream(filePath, 30000);
		} else {
			File file = new File(filePath);
		    FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
			is = Channels.newInputStream(channel);
		}
		
		if(filePath.endsWith("bz2")) {
			return new BZip2CompressorInputStream(is, true);
		}
		
		return is;
	}
}
