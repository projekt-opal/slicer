package org.rdfslice.sort;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TmpFileManager
{
	private ArrayBlockingQueue<File> fileBuffer = new ArrayBlockingQueue<File>(100);
	private File tempDir;
	private final static String FILE_PREFIX = "producer";
	private static TmpFileManager tempFileManager;
	private TempFileProduce producer;
	
	public static TmpFileManager getInstace(File tempDir) {
		if(tempFileManager==null){
			try {
				tempFileManager = new TmpFileManager(tempDir);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return tempFileManager;
	}
	
	
	public TmpFileManager(File tempDir) throws Exception {
		this.tempDir = tempDir;		
		producer = new TempFileProduce();
		producer.start();
	}
	
	public synchronized void put(File file) throws InterruptedException
	{	
		while(TmpFileManager.this.fileBuffer.size()==5){
			wait();
		}
		this.fileBuffer.put(file);
		notifyAll();
	}
	
	public synchronized File get()
	{		
		try{
			File file = this.fileBuffer.poll(0, TimeUnit.SECONDS); 
			while(file==null){
				wait();
				file = this.fileBuffer.poll(0, TimeUnit.SECONDS);
			}
			notifyAll();
			return file;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void finalize() throws Throwable {		
		super.finalize();
		producer.interrupt();
	}
	
	private class TempFileProduce extends Thread{
		
		boolean stop;
		
		@Override
		public void run() {	
			
			while(!stop ){				
				try {
					File tmpFile = File.createTempFile(FILE_PREFIX, "tmp", tempDir);
					tmpFile.deleteOnExit();
					put(tmpFile);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		@Override
		public void interrupt() {
			super.interrupt();
			stop = true;
		}
	}
}