package org.rdfslice;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rdfslice.cmdline.CommandLine;
import org.rdfslice.util.DebugUtil;
import org.rdfslice.util.RDFSliceUtil;

public class RDFSlice {
	
	static Logger logger = Logger.getLogger(RDFSlice.class);

	// commands
	private static final String SOURCE = "source";
	private static final String SOURCE_LIST = "sourceList";
	private static final String PATTERNS = "patterns";
	private static final String OUT = "out";
	private static final String ORDER = "order";
	private static final String DEBUG = "debug";
	private static final String DEBUG_SIZE = "debugGraphSize";
	private static final String NO_CACHE = "nocache";
	
	public static File dest;
	public static void main(String[] args) throws Exception {
		CommandLine commandLine = new CommandLine();
		commandLine.add(true, SOURCE);
		commandLine.add(true, SOURCE_LIST);
		commandLine.add(true, PATTERNS);
		commandLine.add(true, OUT);
		commandLine.add(true, ORDER);
		commandLine.add(false, DEBUG);
		commandLine.add(true, DEBUG_SIZE);
		commandLine.add(false, NO_CACHE);
		
		try {
			commandLine.process(args);
		} catch (Exception e) {
			printCommands();
			return;
		}		

		if(args.length<3) {
			printCommands();
			return;
		}

		DebugUtil.debugSize = 102400;

		//debug
		if(commandLine.hasArg(DEBUG)) {
			String value = commandLine.getValue(DEBUG_SIZE);
			if(value != null) {
				DebugUtil.debugSize = new Long(value);
			}
		} else {
			@SuppressWarnings("unchecked")
			List<Logger> loggers = Collections.list(LogManager.getCurrentLoggers());
			loggers.add(LogManager.getRootLogger());
			for ( Logger logger : loggers ) {
			    logger.setLevel(Level.INFO);
			}
		}

		//query
		String patterns = "";
		if(commandLine.hasArg(PATTERNS)) {
			patterns = commandLine.getValue(PATTERNS);
		} else {
			printCommands();
			return;
		}

		//dest
		File dest = new File("out.nt");
		RDFSlice.dest = dest;
		if(commandLine.hasArg(OUT)) {
			dest = new File(commandLine.getValue(OUT));
			RDFSlice.dest = dest;
		}		

		//order
		String order = RDFSliceStreamEngine.NO_ORDER;
		if(commandLine.hasArg(ORDER)) {
			String value = commandLine.getValue(ORDER);
			if(value.equals("S")) {
				order = RDFSliceStreamEngine.SUBJECT_ORDER;
			} else if(value.equals("IS")) {
				order = RDFSliceStreamEngine.INSTANCE_SEGMENTED_ORDER;
			} else if(value.equals("N")) {
				order = RDFSliceStreamEngine.NO_ORDER;
			} else {
				printCommands();
				return;
			}
		}		

		// cache
		RDFSliceStreamEngine.setCache(!commandLine.hasArg(NO_CACHE));

		 // = preprocess(SOURCE, args);
		List<String> tripleFileList = new ArrayList<String>(); // = preprocess(SOURCE, args);

		// source
		if(commandLine.hasArg(SOURCE)) {
			List<String> sourceList = commandLine.getValues(SOURCE);
			for(String sourceFile : sourceList) {
				logger.info("Souce File : " + sourceFile);
				tripleFileList.add(sourceFile);
			}
		} 
		
		if(commandLine.hasArg(SOURCE_LIST)) {
			List<String> sourceList = commandLine.getValues(SOURCE_LIST);
			for(String sourceFile : sourceList) {
				logger.info("Souce List : " + sourceFile);
				InputStream sourceIS = InputStreamFactory.get(sourceFile);
				tripleFileList.addAll(Arrays.asList(RDFSliceUtil.getFileListOnFile(sourceIS)));				
			}
		} 
		
		if(tripleFileList.size() == 0){
			logger.error("There was no triple files in the given sources.");
			printCommands();
			return;
		}

		String[] sFiles;
		sFiles = tripleFileList.toArray(new String[tripleFileList.size()]);
		
		PrintStream ps = new PrintStream(dest);
		RDFSliceStreamEngine sliceEngine = new RDFSliceStreamEngine(patterns, order, ps, sFiles);
		
		sliceEngine.run();
	}

	private static void printCommands() {
		System.out.println("Use: java -jar slice.jar [parameters]: ");
		System.out.println();
		System.out.println("e.g. java -jar slice.jar -source <source> -sourceList <sourceFileList> -patterns <query> -out <file> -order <order> -debug -debugGraphSize <debugGraphSize>");
		System.out.println();
		System.out.println("-sourceList      <sourceFileList>			      - file containing a list of triple files.");
		System.out.println("                                                   e.g. http://downloads.dbpedia.org/3.8/en/contents-nt.txt");
		System.out.println("-source          <sourceFile>     				  - a triple file or a directory.");
		System.out.println("                                                   e.g. directory\\*.ttl");
		System.out.println("-patterns        <query>                           - desired query, e.g. \"SELECT * WHERE {?s ?p ?o}\" or graph pattern e.g. \"{?s ?p ?o}\"");		
		System.out.println("-out             <file>                            - destination file.");
		System.out.println("-order           <order>                           - order of the source files IS (instance segmented) S (subject) and N (no order).");
		System.out.println("                                                   Optional, default N.");
		System.out.println("-debug                                             - enable debug mode, optional.");
		System.out.println("-debugGraphSize  <debugGraphSize>                  - the size of the graph in bytes to register. When RDFSlice reach this number a debug");
		System.out.println("											       statistic register is generated, e.g. 1024, 1048576. Optional. Default 102400.");
		System.out.println("-nocache                                           - disable the cache");
		System.out.println();
	}
}