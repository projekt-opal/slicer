package org.rdfslice.graph.process;

import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.CandidateSelectionRunnable;
import org.rdfslice.SliceProcess;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.util.FileUtil;

public class MultiSortedSubjectSubjectConstantSliceProcess implements SliceProcess {
	Logger logger = Logger.getLogger(CandidateSelectionRunnable.class);
	
	List<BasicGraphPattern> BGPatterns;
	List<String> files;
	PrintStream ps;
	
	public MultiSortedSubjectSubjectConstantSliceProcess(List<BasicGraphPattern> BGPatterns, List<String> files, PrintStream ps) {
		this.BGPatterns = BGPatterns;
		this.files = files;
		this.ps = ps;
	}

	@Override
	public void run() {
		try {
			for(String file : files) {
				logger.debug("Processing file " + file);
				for (BasicGraphPattern BGPattern : BGPatterns) {
					RandomAccessFile rFile = new RandomAccessFile(file,
							"r");
					SScSubjectOrderedPatternProcess.process(rFile,
							FileUtil.getFileFormat(file),
							BGPattern, 
							ps);
				}		
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
