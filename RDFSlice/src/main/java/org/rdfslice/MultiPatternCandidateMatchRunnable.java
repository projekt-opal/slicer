package org.rdfslice;

import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.graph.process.BGPSelect;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.TriplePattern;
import org.rdfslice.util.DebugUtil;
import org.rdfslice.util.JoinConditionUtil;

public class MultiPatternCandidateMatchRunnable implements Runnable {

	Logger logger = Logger.getLogger(MultiPatternCandidateMatchRunnable.class);

	BGPSelect bGPSelect;
	volatile String[] files;
	List<BasicGraphPattern> BGPatterns;

	public MultiPatternCandidateMatchRunnable(BGPSelect bGPSelect,
			List<BasicGraphPattern> BGPatterns, String... files) {
		this.bGPSelect = bGPSelect;
		this.files = files;
		this.BGPatterns = BGPatterns;
	}

	@Override
	public void run() {
		for (BasicGraphPattern BGPattern : BGPatterns) {
			for (TriplePattern triplePattern : BGPattern) {
				for (String file : files) {
					try {
						logger.debug("Processing file " + file);
						RDFFileIterable rdfIS = new RDFFileIterable(file);
						for (Statement statement : rdfIS) {
							for (Triple triple : statement) {
								if (RDFSliceStreamEngine.match(triple,
										triplePattern)) { // if match the
															// pattern								
									try {
										int patternIndx = BGPattern
												.indexOf(triplePattern);
										boolean insert = (patternIndx == 0 || (bGPSelect
												.match(triple,
														BGPatterns.indexOf(BGPattern),
														patternIndx,
														BGPattern)
														
												&& (JoinConditionUtil.isPatternLeef(BGPattern, patternIndx) 
														||
														!bGPSelect
														.exists(triple,
																BGPatterns.indexOf(BGPattern),
																patternIndx,
																BGPattern))
												)
											 );
										if (insert) {
											bGPSelect.add(BGPatterns
													.indexOf(BGPattern),
													BGPattern, triple, patternIndx);
										}
									} catch (Exception e) {
										logger.error(
												"Error computing match file:"
														+ file + " triple "
														+ triple, e);
									}
								}

							}
							DebugUtil.debug(statement.length(), logger);
						}						
					} catch (Exception e) {
						logger.error("Error reading file " + file, e);
					}
				}
			}
		}
	}
}