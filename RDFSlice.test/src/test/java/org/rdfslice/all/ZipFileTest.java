package org.rdfslice.all;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;


public class ZipFileTest {
	public static void main(String[] args) {
		try
	    {
			URL url = new URL("http://downloads.dbpedia.org/3.8/en/disambiguations_en.ttl.bz2");
			
			File file = new  File("C://Users//research//Downloads//disambiguations_en.ttl.bz2");
			//File file = new  File("C://Users//research//lod//specific_mappingbased_properties_en.ttl.bz2");
			//C:\Users\research\lod
			//ZipFile file = new ZipFile("C://Users//research//lod//instance_types_en.ttl//instance_types_en.zip");
			System.out.println(file.exists());
			InputStream is = new FileInputStream(file);
			//InputStream is = url.openStream();
			
	       // ZipInputStream stream = new ZipInputStream(fis);
	        ZipEntry zipEntry = null;
//	        while ((zipEntry = stream.getNextEntry()) != null)
//	        {
//	        	System.out.println(zipEntry.isDirectory());
//	        }
//	            // zipInput = new ZipInputStream(new FileInputStream(fileName));
//	            InputStream inputs= ZipFile.getInputStream(zipEntry);
	            //  final RandomAccessFile br = new RandomAccessFile(fileName, "r");
	            //ZipEntry ze = (ZipEntry) file.entries().nextElement();
	        	ZipInputStream zis = new ZipInputStream(is);
	        	FileInputStream fin = new FileInputStream(file);
	            BufferedInputStream bis = new BufferedInputStream(fin);
	            CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
	          //  BufferedReader br = new BufferedReader(new InputStreamReader(input));
	            BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(is, true);
	            BufferedReader br = new BufferedReader(new InputStreamReader(bzIn));
	           // while(is.getNextEntry()!=null){
	            String line;
	            int i=0;
	            while((line = br.readLine()) != null)
	            {
	            	if(i > 6190)
	            		System.out.println(line);
	                i++;
	            }
	            System.out.println(i);
	            //}
	            br.close();
	       // }


	    }
	    catch(Exception e)
	    {
	        System.out.print(e);
	    }
	    finally
	    {
	        System.out.println("\n\n\nThe had been extracted successfully");

	    }
	}
}
