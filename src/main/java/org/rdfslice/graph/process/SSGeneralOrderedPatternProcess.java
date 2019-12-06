package org.rdfslice.graph.process;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.RDFFileIterable;
import org.rdfslice.RDFSliceStreamEngine;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.MemoryJCManager;
import org.rdfslice.query.Pattern;
import org.rdfslice.util.DebugUtil;

public class SSGeneralOrderedPatternProcess {
	
	static Logger logger = Logger.getLogger(SSGeneralOrderedPatternProcess.class);
	
	public static void process(Statement statement, BasicGraphPattern BGPattern,
			PrintStream ps) throws Exception {
		MemoryJCManager memoryManager = new MemoryJCManager();
		BGPSelect bGPMemorySelect = new BGPSelect(memoryManager);
		//For each Basic Graph Pattern		
		Pattern pattern = BGPattern.get(0);
		int bgpIndx = 0;
		memoryManager.clear();
		for(Triple triple : statement) {
			if(RDFSliceStreamEngine.match(triple, pattern)) {
				bGPMemorySelect.add(bgpIndx, BGPattern, triple);
			}
		}
		if(bGPMemorySelect.size()>0) {
			for(Triple triple : statement) {
				if(RDFSliceStreamEngine.match(triple, BGPattern)) { // if match some pattern
					bGPMemorySelect.computeMatch(triple, bgpIndx, BGPattern);
				}
			}
			bGPMemorySelect.removeNotMached(bgpIndx, BGPattern);
			for(Triple triple : statement) {
				//For each Basic Graph Pattern
				if(bGPMemorySelect.match(triple, bgpIndx, BGPattern, null)) {
					ps.println(triple.toString());
				}
			}
		}	
	}
	
	public static void process(Statement statement,
			List<BasicGraphPattern> BGPatterns,
			PrintStream ps) throws Exception {
		MemoryJCManager memoryManager = new MemoryJCManager();
		BGPSelect bGPMemorySelect = new BGPSelect(memoryManager);
		//For each Basic Graph Pattern		
		memoryManager.clear();
		for(Triple triple : statement) {
			for(BasicGraphPattern BGPattern : BGPatterns) {
				Pattern pattern = BGPattern.get(0);
				int bgpIndx = BGPatterns.indexOf(BGPattern);
				if(RDFSliceStreamEngine.match(triple, pattern)) {
					bGPMemorySelect.add(bgpIndx, BGPattern, triple);
				}
			}
		}
		if(bGPMemorySelect.size()>0) {
			for(BasicGraphPattern BGPattern : BGPatterns) {
				int bgpIndx = BGPatterns.indexOf(BGPattern);
				for(Triple triple : statement) {
					if(RDFSliceStreamEngine.match(triple, BGPattern)) { // if match some pattern
						bGPMemorySelect.computeMatch(triple, bgpIndx, BGPattern);
					}
				}
				bGPMemorySelect.removeNotMached(bgpIndx, BGPattern);
			}
			for(Triple triple : statement) {
				for(BasicGraphPattern BGPattern : BGPatterns) {
					int bgpIndx = BGPatterns.indexOf(BGPattern);
					//For each Basic Graph Pattern
					if(bGPMemorySelect.match(triple, bgpIndx, BGPattern, null)) {
						ps.println(triple);
						break;
					}
				}
			}
		}
	}
	
	public static void process(RandomAccessFile accessFile, String format,
			List<BasicGraphPattern> BGPatterns,
			PrintStream ps) throws Exception {
		FileChannel channel = accessFile.getChannel();
		InputStream stream = Channels.newInputStream(channel);
		RDFFileIterable rdfIS = new RDFFileIterable(stream, format);
		
		for(Statement statement : rdfIS) {
			try {
				process(statement, BGPatterns, ps);
				DebugUtil.debug(statement.length(), logger);
			} catch (Exception e) {
				logger.error("Error parsing", e);
			}
		}
	}
}
