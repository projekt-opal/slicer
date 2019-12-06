package org.rdfslice.graph.process;

import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.rdfslice.SliceProcess;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.util.FileUtil;

public class MultiSubjectSubjectVariableSliceProcess implements SliceProcess {
	 List<BasicGraphPattern> BGPatterns;
	 List<String> files;
	 PrintStream ps;
	
	 public MultiSubjectSubjectVariableSliceProcess( List<BasicGraphPattern> BGPatterns, List<String> files, PrintStream ps) {
		this.BGPatterns = BGPatterns;
		this.files = files;
		this.ps = ps;
	}

	@Override
	public void run() {
		try {
			for(String file : files) {
				RandomAccessFile rFile = new RandomAccessFile(file,
							"r");
				
				SSGeneralOrderedPatternProcess.process(rFile, 
						FileUtil.getFileFormat(file),
						BGPatterns, ps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
