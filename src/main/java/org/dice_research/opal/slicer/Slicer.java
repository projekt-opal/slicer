package org.dice_research.opal.slicer;

import org.rdfslice.RDFSlice;

/**
 * Slicer.
 * 
 * Usage:
 * 
 * -source <sourceFile> - a triple file or a directory. e.g. directory\*.ttl
 * 
 * -sourceList <sourceFileList> - file containing a list of triple files. e.g.
 * http://downloads.dbpedia.org/3.8/en/contents-nt.txt
 * 
 * -patterns <query> - desired query, e.g. "SELECT * WHERE {?s ?p ?o}" or graph
 * pattern e.g. "{?s ?p ?o}"
 * 
 * -out <file> - destination file.
 * 
 * -order <order> - order of the source files IS (instance segmented) S
 * (subject) and N (no order). Optional, default N.
 * 
 * -debug - enable debug mode, optional.
 * 
 * -debugGraphSize <debugGraphSize> - the size of the graph in bytes to
 * register. When RDFSlice reach this number a debug statistic register is
 * generated, e.g. 1024, 1048576. Optional. Default 102400.
 * 
 * -nocache - disable the cache
 */
public class Slicer {
	public static void main(String[] args) throws Exception {
		RDFSlice.main(args);
	}
}