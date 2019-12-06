package org.rdfslice.graph.process;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.CandidateSelectionRunnable;
import org.rdfslice.InputStreamFactory;
import org.rdfslice.SliceProcess;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.util.FileUtil;

public class MultiSiglePatternSliceProcess implements SliceProcess {
	Logger logger = Logger.getLogger(CandidateSelectionRunnable.class);
	
	List<BasicGraphPattern> BGPatterns;
	List<String> files;
	PrintStream ps;
	
	public MultiSiglePatternSliceProcess( List<BasicGraphPattern> BGPatterns, List<String> files, PrintStream ps) {
		this.BGPatterns = BGPatterns;
		this.files = files;
		this.ps = ps;
	}

	@Override
	public void run() {
		try {
			for(String file : files) {
				logger.debug("Processing file " + file);
				InputStream isFile = InputStreamFactory.get(file);
				SinglePatternProcess.process(isFile,
						FileUtil.getFileFormat(file),
						BGPatterns, ps);			
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
