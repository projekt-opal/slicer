package org.rdfslice;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.graph.process.GeneralSliceProcess;
import org.rdfslice.graph.process.MultiJoinSliceProcess;
import org.rdfslice.graph.process.MultiSiglePatternSliceProcess;
import org.rdfslice.graph.process.MultiSortedObjectSubjectSliceProcess;
import org.rdfslice.graph.process.MultiSortedSubjectSubjectConstantSliceProcess;
import org.rdfslice.graph.process.MultiSubjectSubjectVariableSliceProcess;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.util.PatternUtil;

public class SliceProcessFactory {
	private static Logger logger = Logger.getLogger(SliceProcessFactory.class);
	
	public static List<SliceProcess> create(String order, List<BasicGraphPattern> BGPatterns, List<String> files, PrintStream ps) throws Exception {
		List<SliceProcess> sliceProcesses = new ArrayList<SliceProcess>();
		List<BasicGraphPattern> notProcessedPatterns = new ArrayList<BasicGraphPattern>(); 
		List<BasicGraphPattern> singleTriplePatterns = getSingleTriplePatterns(BGPatterns);		
		BGPatterns.removeAll(singleTriplePatterns);
		if (BGPatterns.size() == 0 && 
				singleTriplePatterns.size() == 1) {
			logger.debug("Single pattern found");
			for(BasicGraphPattern pattern : singleTriplePatterns) {
				List<BasicGraphPattern> patterns = new ArrayList<BasicGraphPattern>();
				patterns.add(pattern);
				MultiSiglePatternSliceProcess sliceProcess = new MultiSiglePatternSliceProcess(patterns, files, ps);
				sliceProcesses.add(sliceProcess);
			}
		} else {
			notProcessedPatterns.addAll(singleTriplePatterns);
		}

		if (!isCompressed(files)) {
			List<BasicGraphPattern> SScPatterns = getSSc(BGPatterns);
			BGPatterns.removeAll(SScPatterns);
			if ((notProcessedPatterns.size()==0 && BGPatterns.size()==0) && 
					(order == RDFSliceStreamEngine.SUBJECT_ORDER ||
					order == RDFSliceStreamEngine.INSTANCE_SEGMENTED_ORDER)
					&&
					SScPatterns.size() == 1) {
				logger.debug("Pattern SSc found");
				for(BasicGraphPattern pattern : SScPatterns) {
					List<BasicGraphPattern> patterns = new ArrayList<BasicGraphPattern>();
					patterns.add(pattern);
					MultiSortedSubjectSubjectConstantSliceProcess sliceProcess = new MultiSortedSubjectSubjectConstantSliceProcess(patterns, files, ps);
					sliceProcesses.add(sliceProcess);
				}
			} else {
				notProcessedPatterns.addAll(SScPatterns);
			}
			List<BasicGraphPattern> OSPatterns = getOS(BGPatterns);
			BGPatterns.removeAll(OSPatterns);
			if ((notProcessedPatterns.size()==0 && BGPatterns.size()==0) && 
					order == RDFSliceStreamEngine.SUBJECT_ORDER &&
					OSPatterns.size() == 1) {
				logger.debug("Pattern OS found");
				for(BasicGraphPattern pattern : OSPatterns) {
					List<BasicGraphPattern> patterns = new ArrayList<BasicGraphPattern>();
					patterns.add(pattern);
					MultiSortedObjectSubjectSliceProcess sliceProcess = new MultiSortedObjectSubjectSliceProcess(patterns, files, ps);
					sliceProcesses.add(sliceProcess);
				}
			} else {
				notProcessedPatterns.addAll(OSPatterns);
			}
		}
		List<BasicGraphPattern> SSvPatterns = getSSv(BGPatterns);
		BGPatterns.removeAll(SSvPatterns);
		if((notProcessedPatterns.size()==0 && 
				BGPatterns.size()==0) && 
				SSvPatterns.size() == 1 && 
				(order == RDFSliceStreamEngine.INSTANCE_SEGMENTED_ORDER || order == RDFSliceStreamEngine.SUBJECT_ORDER)) {
			logger.debug("Pattern SSv found");
			for(BasicGraphPattern pattern : SSvPatterns) {
				List<BasicGraphPattern> patterns = new ArrayList<BasicGraphPattern>();
				patterns.add(pattern);
				MultiSubjectSubjectVariableSliceProcess sliceProcess = new MultiSubjectSubjectVariableSliceProcess(patterns, files, ps);
				sliceProcesses.add(sliceProcess);
			}
		} else {
			notProcessedPatterns.addAll(SSvPatterns);
		}
		
		List<BasicGraphPattern> multiJoinPatterns = getJoinType(BGPatterns, BasicGraphPattern.MULTI_JOIN);		
		if (multiJoinPatterns.size() > 0) {
			logger.debug("Multi Join Type found");
			BGPatterns.removeAll(multiJoinPatterns);
			MultiJoinSliceProcess sliceProcess = new MultiJoinSliceProcess(order, multiJoinPatterns, files, ps);
			sliceProcesses.add(sliceProcess);
		}
		
		if (BGPatterns.size() > 0 || notProcessedPatterns.size() > 0) {
			BGPatterns.addAll(notProcessedPatterns);
			GeneralSliceProcess sliceProcess = new GeneralSliceProcess(order, BGPatterns, files, ps);
			sliceProcesses.add(sliceProcess);
		}
		
		return sliceProcesses;
	}
	
	private static boolean isCompressed(List<String> files) {
		for(String file : files) {
			if((file.endsWith(".zip") || file.endsWith(".bz2") || file.endsWith(".rar"))) {
				return true;
			}				
		}		
		return false;
	}
	
	private static List<BasicGraphPattern> getJoinType(List<BasicGraphPattern> bGPatternsList, int joinType) {
		List<BasicGraphPattern> pattern = new ArrayList<BasicGraphPattern>();
		for (BasicGraphPattern BGP : bGPatternsList) {
			if (BGP.getJoinCondition() == joinType) {
				pattern.add(BGP);
			}
		}
		return pattern;
	}
	
	private static List<BasicGraphPattern> getSingleTriplePatterns(List<BasicGraphPattern> bGPatternsList) {
		List<BasicGraphPattern> singleBGPattern = new ArrayList<BasicGraphPattern>();
		for (BasicGraphPattern BGP : bGPatternsList) {
			if (BGP.size() == 1) {
				singleBGPattern.add(BGP);
			}
		}
		return singleBGPattern;
	}

	private static List<BasicGraphPattern> getOS(List<BasicGraphPattern> bGPatternsList) {
		List<BasicGraphPattern> SOBGPattern = new ArrayList<BasicGraphPattern>();
		for (BasicGraphPattern BGP : bGPatternsList) {
			if (BGP.getJoinCondition() == BasicGraphPattern.SUBJECT_OBJECT_JOIN
					&& BGP.get(0).getObject().equals(BGP.get(1).getSubject())) {
				SOBGPattern.add(BGP);
			}
		}
		return SOBGPattern;
	}

	private static List<BasicGraphPattern> getSSc(
			List<BasicGraphPattern> bGPatternsList) {
		List<BasicGraphPattern> SScBGPattern = new ArrayList<BasicGraphPattern>();
		for (BasicGraphPattern BGP : bGPatternsList) {
			if ((BGP.getJoinCondition() == BasicGraphPattern.SUBJECT_SUBJECT_JOIN
					|| BGP.getJoinCondition() == BasicGraphPattern.SUBJECT_CONSTANT_DISJOINT)
					&& !PatternUtil.isVariable(BGP.get(0).getSubject())) {
				SScBGPattern.add(BGP);
			}
		}
		return SScBGPattern;
	}
	
	private static List<BasicGraphPattern> getSSv(
			List<BasicGraphPattern> bGPatternsList) {
		List<BasicGraphPattern> SSvBGPattern = new ArrayList<BasicGraphPattern>();
		for (BasicGraphPattern BGP : bGPatternsList) {
			if ((BGP.getJoinCondition() == BasicGraphPattern.SUBJECT_SUBJECT_JOIN)
					&& PatternUtil.isVariable(BGP.get(0).getSubject())) {
				SSvBGPattern.add(BGP);
			}
		}
		return SSvBGPattern;
	}
}
