package org.rdfslice.all;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Date;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.rdfslice.RDFFileInteratorTest;
import org.rdfslice.RDFFileIterable;
import org.rdfslice.model.Statement;
import org.rdfslice.util.FileUtil;

public class Test {
	public static void main(String[] args) throws Exception {
		
		InputStream is = RDFFileInteratorTest.class.getResourceAsStream("/disambiguations_en.ttl.bz2");
    	BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(is, true);
		RDFFileIterable rdfFile = new RDFFileIterable(bzIn, FileUtil.getFileFormat("/disambiguations_en.ttl.bz2"));
		PrintStream ps = new PrintStream(new File("disambiguationSlice.ttl"));
		long i = 0;
		int length = 0;
		for(Statement t: rdfFile) {
			System.out.println(t.toString());
			ps.print(t.toString());
			length+=t.size();
			i++;
			if(i>=10000)
				break;
		}
		ps.close();
		
		 // Create a read/writeable file channel
	    File file = new File("C:\\Users\\research\\lod\\dbpedia_3.8\\en\\uncompressed\\instance_types_en.ttl");
//	    FileChannel channel = new RandomAccessFile(file, "rw").getChannel();

//	    InputStream is = Channels.newInputStream(channel);
//	    
//	    is.skip(400000);
//	    
//	    LineNumberReader bis= new LineNumberReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//	    
//	    System.out.println(bis.readLine());
//	    
//	    is.skip(0);
//	    
//	    System.out.println(bis.readLine());
//
//	    // Close the channel
//	    is.close();
		
		//File file = new File("C:/Users/research/semantic web leipzig/2013/ICSC_SLICE/evaluation/DBpedia_SS_off/t/sorted.nt");		
//		File file = new File(args[0]);		
//		BufferedReader br = new BufferedReader(new 
//				InputStreamReader(new FileInputStream(file), 
//						Charset.forName("UTF-8")));
//		String line;
//		java.util.Date start = new Date();
//		String lastLine = null;
//		int lineI = 0;
//		while ((line = br.readLine()) != null) {
//		    System.out.println(line);
//		    if(lastLine != null){
//		    	if(lastLine.compareTo(line)>0){
//		    		System.out.println("Nao");break;
//		    	}
//		    }
//		    lineI++;
//		}
//		Date end = new Date();
//		System.out.println(end.getTime()-start.getTime());
		
		//File file = new File("C:\\Users\\research\\lod\\dbpedia_3.8\\en\\uncompressed\\page_links_unredirected_en.ttl");
		java.util.Date start = new Date();
//		RDFFileIterable rdfFile = new RDFFileIterable(new FileInputStream(file));
		long s = 0;
		long trip = 0;
		for(Statement t: rdfFile) {
//			if(s==6190)
//				System.out.println(t);
//			s++;			
		}		
		Date end = new Date();
		System.out.println(end.getTime()-start.getTime());
		System.out.println(" subject: " + s + "triples: " + trip );
	}
}
