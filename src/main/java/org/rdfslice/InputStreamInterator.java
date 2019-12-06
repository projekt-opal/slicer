package org.rdfslice;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.Iterator;

public class InputStreamInterator implements Iterator<String>{
	String currentLine = null; // current line of the iterator
	InputStream is = null; // RDF iterable file
	LineNumberReader br;
	
	public InputStreamInterator(InputStream is) throws Exception {
		if(is == null)
			throw new Exception("InputStream could not be Null.");
		
		this.is = is;
		
		init(is);
	}
	
	private void init(InputStream is) throws Exception{
		br = new LineNumberReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	}

	public void setStartLineAfterBytes(long bytes) throws IOException {
		br.skip(bytes);		
		currentLine= br.readLine();
	}

	@Override
	public boolean hasNext() {
		try {
			return (currentLine= br.readLine())!=null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String next() {
				
		try {
            currentLine = br.readLine();
                        
            return currentLine;
            
		} catch (Exception e) {			
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		// for security reasons not supported
	}
}
