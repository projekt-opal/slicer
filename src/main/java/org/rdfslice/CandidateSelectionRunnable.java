package org.rdfslice;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.graph.process.BGPSelect;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.Pattern;
import org.rdfslice.util.DebugUtil;
import org.rdfslice.util.FileUtil;

public class CandidateSelectionRunnable implements Runnable {

		Logger logger = Logger.getLogger(CandidateSelectionRunnable.class);

		BGPSelect bGPSelect;
		volatile String file;
		List<BasicGraphPattern> BGPatterns;

		public CandidateSelectionRunnable(String file, 
				BGPSelect bGPSelect,
				List<BasicGraphPattern> BGPatterns) {
			this.bGPSelect = bGPSelect;
			this.file = file;
			this.BGPatterns = BGPatterns;
		}

		@Override
		public void run() {
			try {
				logger.debug("Processing file " + file);
				InputStream stream = InputStreamFactory.get(file);
				RDFFileIterable rdfIS = new RDFFileIterable(stream, FileUtil.getFileFormat(file));
				for (Statement statement : rdfIS) {
					// For each Basic Graph Pattern
					for (BasicGraphPattern BGPattern : BGPatterns) {
						Pattern pattern = BGPattern.get(0);
						int bgpIndx = BGPatterns.indexOf(BGPattern);
						
						for (Triple triple : statement) {
							// Select the pattern that have less variables
							if (RDFSliceStreamEngine.match(triple, pattern)) {
								bGPSelect.add(bgpIndx, BGPattern,
										triple);
							}
						}						
					}
					DebugUtil.debug(statement.length(), logger);
				}
				stream.close();
			} catch (Exception e) {
				logger.error("Error reading file " + file, e);
			}
		}
	}