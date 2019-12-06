package org.rdfslice;

import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.graph.process.BGPSelect;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.util.DebugUtil;

public class CandidateMatchRunnable implements Runnable {

		Logger logger = Logger.getLogger(CandidateMatchRunnable.class);

		BGPSelect bGPSelect;
		volatile String[] files;
		List<BasicGraphPattern> BGPatterns;

		public CandidateMatchRunnable(BGPSelect bGPSelect,
				List<BasicGraphPattern> BGPatterns, String... files) {
			this.bGPSelect = bGPSelect;
			this.files = files;
			this.BGPatterns = BGPatterns;
		}

		@Override
		public void run() {
			for (String file : files) {
				try {
					logger.debug("Processing file " + file);
					RDFFileIterable rdfIS = new RDFFileIterable(file);
					try {
						for (Statement statement : rdfIS) {
							for (Triple triple : statement) {
								for (BasicGraphPattern BGPattern : BGPatterns) {
									if (RDFSliceStreamEngine.match(triple,
											BGPattern)) { // if match
																	// some
																	// pattern
										try {
											bGPSelect
													.computeMatch(triple,
															BGPatterns.indexOf(BGPattern),
															BGPattern);
										} catch (Exception e) {
											logger.error(
													"Error computing match file:"
															+ file + " tiple "
															+ triple, e);
										}
									}
								}
							}
							DebugUtil.debug(statement.length(), logger);
						}
					} catch (Exception e) {
						logger.error("Error reading file " + file, e);
					}
				} catch (Exception e) {
					logger.error("Error reading file " + file, e);
				}
			}
		}
	}