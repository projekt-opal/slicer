package org.rdfslice;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.graph.process.BGPSelect;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.TriplePattern;
import org.rdfslice.util.DebugUtil;
import org.rdfslice.util.FileUtil;

public class MultiJoinExtractionRunnable implements Runnable {

		Logger logger = Logger.getLogger(MultiJoinExtractionRunnable.class);

		BGPSelect bGPSelect;
		volatile String file;
		List<BasicGraphPattern> BGPatterns;
		PrintStream ps;

		public MultiJoinExtractionRunnable(String file, 
				BGPSelect bGPSelect,
				List<BasicGraphPattern> BGPatterns,
				PrintStream ps) {
			this.bGPSelect = bGPSelect;
			this.file = file;
			this.BGPatterns = BGPatterns;
			this.ps = ps;
		}

		@Override
		public void run() {
			try {
				logger.debug("Processing file " + file);
				InputStream stream = InputStreamFactory.get(file);
				RDFFileIterable rdfIS = new RDFFileIterable(stream, FileUtil.getFileFormat(file));
				// Printing the pattern that match
				try {
					for (Statement statement : rdfIS) {
						for (Triple triple : statement) {
							for (BasicGraphPattern BGPattern : BGPatterns) {
								try {
									boolean match = false;
									for(TriplePattern pattern : BGPattern) {
										match = bGPSelect.exists(triple,
												BGPatterns.indexOf(BGPattern),
												BGPattern.indexOf(pattern),
												BGPattern);
										if (match) {
											synchronized (ps) {
												ps.println(triple.toString());
											}
											break; // if the triple is selected
													// with one basic graph
													// pattern the
													// other does not interest (
													// avoid print a triple two
													// times )
										}
									}
									if(match) {
										break;
									}
								} catch (Exception e) {
									logger.error("Error matching triple "
											+ file, e);
								}
							}
						}
						DebugUtil.debug(statement.length(), logger);
					}
				} catch (Exception e) {
					logger.error("Error reading file " + file, e);
				}
				stream.close();
			} catch (Exception e) {
				logger.error("Error reading file " + file, e);
			}
		}
	}