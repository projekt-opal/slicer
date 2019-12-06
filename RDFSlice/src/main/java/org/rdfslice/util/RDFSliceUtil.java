package org.rdfslice.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.rdfslice.InputStreamFactory;

public class RDFSliceUtil {
	public static String[] getFileListOnFile(InputStream file) throws IOException{
		List<String> files = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(file, Charset.forName("UTF-8")));
		String line;
		while ((line = br.readLine()) != null) {
		    files.add(line);
		}
		// Done 
		br.close();
		return files.toArray(new String[files.size()]);
	}	

	public static String[] listFiles(String path, final String extension) throws Exception {
		List<String> files = new ArrayList<String>();
			
		File file = new File(path);
		
		if(file.isFile() && file.isDirectory()) {
			File[] fileList = file.listFiles(new FilenameFilter() {
		        @Override
		        public boolean accept(File dir, String name) {
		        	if(extension != null)
		        		return name.endsWith(extension.replace("*", "")); // or something else
		        	return true;
		        }
		    });
			
			String[] pathList = new String[fileList.length];
			
		    for(String filePath : pathList) {
		    	files.add(filePath);
		    }
		} else {
			InputStream inputStream = InputStreamFactory.get(path);
			if(isAValidTripleFile(inputStream)) {
				files.add(path);
			} else {				
				inputStream = InputStreamFactory.get(path); // open another input Stream
				return getFileListOnFile(inputStream);
			}
		}
		
		return files.toArray(new String[files.size()]);
	}
	
	public static boolean isAValidTripleFile(InputStream inputStream) throws IOException{		
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
		String line;
		while(((line = br.readLine()) != null) && line.startsWith("#")){}

		if(line != null) {
			String[] terms = line.split(" ");
			return RDFUtil.isASimpleStatement(line) && terms.length>=3;
		}
		br.close();
		return false;
	}
}
