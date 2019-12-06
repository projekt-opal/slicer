package org.rdfslice.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rdfslice.RDFSlice;
import org.rdfslice.sqlite.SliceSQLiteDAOV2;

public class DebugUtil {
	
	public static long debugSize = 1073741824;
	
	private static String threadName = "1";	
	private static Map<String, Long> sizeMap = new HashMap<String, Long>();
	private static Map<String, Long> stepMap = new HashMap<String, Long>();
	
	public static long folderSize(File directory) {
	    long length = 0;
	    for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += folderSize(file);
	    }
	    return length;
	}
	
	public static void reset() {
		synchronized (sizeMap) {
			sizeMap.clear(); 
		}
		synchronized (stepMap) {
			stepMap.clear(); 
		}
		SliceSQLiteDAOV2.cacheHits = 0;
	}

	public static void debug(long size, Logger logger, String file) {			
		if(logger.isDebugEnabled()) {
			Long totalSize;			
			synchronized (sizeMap) {
				totalSize = sizeMap.get(threadName);
				if(totalSize == null) {
					totalSize = new Long(0);
				}
				totalSize += size;
				sizeMap.put(threadName, totalSize);
			}
			
			Long currentStep;
			synchronized (stepMap) {
				currentStep = stepMap.get(threadName);
				if(currentStep == null) {
					currentStep = new Long(0);
					stepMap.put(threadName, currentStep);
				}
			}			
			
			synchronized (stepMap) {
				if(((totalSize/debugSize) > (currentStep/debugSize))) {
					File candidatesFile = new File(file);
					long extractedSize = 0;
					if(RDFSlice.dest != null)
						extractedSize = RDFSlice.dest.length();
					//long fileSize = folderSize(candidatesFile);
					long fileSize = candidatesFile.length();
					long memory = Runtime.getRuntime().totalMemory();
					synchronized (stepMap) {
						currentStep = totalSize;
						stepMap.put(threadName, currentStep);
					}
					logger.debug("Explored(1:" + debugSize + "):" + (totalSize/debugSize) +
							"\t Extracted:" + extractedSize +
							"\t RAM: " + memory +
							"\t Disk space: " + fileSize +
							"\t Cache hits:" + SliceSQLiteDAOV2.cacheHits);
				}
			}			
		}
	}
	
	public static void debug(long size, Logger logger) {			
		if(logger.isDebugEnabled()) {
			Long totalSize;			
			synchronized (sizeMap) {
				totalSize = sizeMap.get(threadName);
				if(totalSize == null) {
					totalSize = new Long(0);
				}
				totalSize += size;
				sizeMap.put(threadName, totalSize);
			}
			
			Long currentStep;
			synchronized (stepMap) {
				currentStep = stepMap.get(threadName);
				if(currentStep == null) {
					currentStep = new Long(0);
					stepMap.put(threadName, currentStep);
				}
			}			
			
			synchronized (stepMap) {
				if(((totalSize/debugSize) > (currentStep/debugSize))) {
					long extractedSize = 0;
					if(RDFSlice.dest != null) {
						extractedSize = RDFSlice.dest.length();
					}
					//long fileSize = folderSize(candidatesFile);
					long memory = Runtime.getRuntime().totalMemory();
					synchronized (stepMap) {
						currentStep = totalSize;
						stepMap.put(threadName, currentStep);
					}
					logger.debug("Explored(1:" + debugSize + "):" + (totalSize/debugSize) +
							"\t Extracted:" + extractedSize +
							"\t RAM: " + memory +
							"\t Cache hits:" + SliceSQLiteDAOV2.cacheHits);
				}
			}			
		}
	}
}
