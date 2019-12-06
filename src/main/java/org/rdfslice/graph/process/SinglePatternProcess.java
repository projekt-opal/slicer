package org.rdfslice.graph.process;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.RDFFileIterable;
import org.rdfslice.RDFSliceStreamEngine;
import org.rdfslice.model.Statement;
import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.Pattern;
import org.rdfslice.util.DebugUtil;

public class SinglePatternProcess {
	static Logger logger = Logger.getLogger(SinglePatternProcess.class);
	
	public static void process(InputStream stream, String format,
			List<BasicGraphPattern> BGPatterns,
			PrintStream ps) throws Exception {
		RDFFileIterable rdfIS = new RDFFileIterable(stream, format);
		
		for(Statement statement : rdfIS) {
			try {
				for(Triple triple : statement) {
					for(BasicGraphPattern bgpPattern: BGPatterns) {
						Pattern pattern = bgpPattern.get(0);						
						if(RDFSliceStreamEngine.match(triple, pattern
								)) {
							ps.println(triple.toString());
							break;
						}
						DebugUtil.debug(statement.length(), logger);
					}
				}
			} catch (Exception e) {
				logger.error("Error parsing", e);
			}
		}		
	}
}
