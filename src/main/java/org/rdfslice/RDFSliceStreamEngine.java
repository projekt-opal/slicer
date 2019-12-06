package org.rdfslice;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.model.IInstanceStatement;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.Pattern;
import org.rdfslice.query.TriplePattern;
import org.rdfslice.util.SparqlUtil;
import org.rdfslice.util.thread.MultiRunnerDam;

public class RDFSliceStreamEngine {

	private static Logger logger = Logger.getLogger(RDFSliceStreamEngine.class);

	public static final String SUBJECT_ORDER = "SUBJECT_ORDER";
	public static final String INSTANCE_SEGMENTED_ORDER = "INSTANCE_SEGMENTED_ORDER";
	public static final String NO_ORDER = "NO_ORDER";

	List<BasicGraphPattern> BGPatterns;
	Collection<TriplePattern> patterns;

	private static Boolean cache = true;

	String[] files;
	PrintStream ps;
	String order = NO_ORDER;

	public RDFSliceStreamEngine(String patterns, PrintStream ps,
			String... dumpFiles) throws Exception {
		this(patterns, NO_ORDER, ps, dumpFiles);
	}

	public RDFSliceStreamEngine(String patterns, String order, PrintStream ps,
			String... dumpFiles) throws Exception {
		this.ps = ps;
		this.order = order;
		this.files = dumpFiles;
		BGPatterns = SparqlUtil.parser(patterns);
		logger.debug("Processing patterns: " + patterns);
	}

	public static boolean match(IInstanceStatement statement,
			BasicGraphPattern BGPattern) {
		if (match(statement, (Collection<TriplePattern>) BGPattern)) {
			return true;
		}
		BasicGraphPattern childBGP = BGPattern.getChild();
		if (childBGP != null) {
			return match(statement, childBGP);
		}
		return false;
	}

	public static boolean match(IInstanceStatement statement,
			Collection<TriplePattern> patterns) {
		for (Pattern pattern : patterns) {
			if (match(statement, pattern)) {
				return true;
			}
		}
		return false;
	}

	public static boolean match(IInstanceStatement statement, Pattern pattern) {
		return pattern.match(statement);
	}

	public static Pattern getMoreConstantPattern(BasicGraphPattern BGPattern) {
		int noConstants = -1;
		Pattern mcPattern = null;
		for (Pattern pattern : BGPattern) {
			if (pattern.getNumberOfContants() > noConstants) {
				mcPattern = pattern;
				noConstants = pattern.getNumberOfContants();
			}
		}
		return mcPattern;
	}

	public void run() {
		try {			
			logger.debug("Selecting subset of candidates - start");
			if(order == SUBJECT_ORDER) {
				logger.debug("Subject Order Found");
			}
			List<SliceProcess> sliceProcesses = SliceProcessFactory.create(order, BGPatterns, Arrays.asList(files), ps);
			MultiRunnerDam processes = new MultiRunnerDam(sliceProcesses);
			processes.run();
			logger.debug("Selecting triples that match (final process) - end");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
	
	public synchronized static void setCache(boolean enable) {
		cache = enable;
	}

	public synchronized static boolean isCacheEnabled() {
		return cache;
	}
	
}
