package org.rdfslice;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class HttpInputStream extends InputStream {
	
	private static Logger logger = Logger.getLogger(HttpInputStream.class);
	
	HttpURLConnection connection = null;
	String url;
	int timeout;
	long sleep= 10000; // time to wait until next try
	boolean isConnected;
	InputStream urlInputStream = null;
	long totalReadBytes = 0;
	URL URL = null;
	
	public HttpInputStream(String url, int timeout) {
		this.url = url;
		this.timeout = timeout;
	}
	
	public void connect() throws IOException {
		if(URL == null) {
			URL = new URL(url);
		}
		
		// Open connection to URL.
		connection = (HttpURLConnection) URL.openConnection();
		connection.setConnectTimeout(timeout);
		connection.setReadTimeout(timeout);
		
		 if(totalReadBytes != 0) {
	        	connection.setRequestProperty("Range",
	                "bytes=" + totalReadBytes + "-");
		 }
	}

	@Override
	public int read() throws IOException {
		int readByte = 0;
		boolean read = true;
		try {
			while(read) {
				try {
					while(!isConnected) {
						logger.debug("Trying to connect!");
						connect();
						urlInputStream = connection.getInputStream();
						isConnected = true;
						logger.debug("The connection was estabilished!");
					}
					readByte = urlInputStream.read();
					totalReadBytes += readByte;
					read = false;
				} catch (Exception e) {
					logger.warn("The connection was interrupted!", e);
					isConnected = false;
					if(urlInputStream != null) {
						urlInputStream.close();
					}
					if(connection != null) {
						connection.disconnect();
					}
					isConnected = false;
					read = true;					
					Thread.sleep(sleep);				
				}				
			}
		} catch (InterruptedException e1) {			
		}
		
		return readByte;		
	}
}
