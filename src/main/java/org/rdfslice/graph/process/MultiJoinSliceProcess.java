package org.rdfslice.graph.process;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.rdfslice.MultiJoinExtractionRunnable;
import org.rdfslice.MultiPatternCandidateMatchRunnable;
import org.rdfslice.SliceProcess;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.sqlite.ConnectionPool;
import org.rdfslice.sqlite.ReaderCandidateManager;
import org.rdfslice.sqlite.SQLiteJCManager;
import org.rdfslice.sqlite.SliceSQLiteDAOV2;
import org.rdfslice.sqlite.WriteCandidateManager;
import org.rdfslice.sqlite.cache.JoinCandidateCache;
import org.rdfslice.util.DebugUtil;
import org.rdfslice.util.thread.MultiRunnerDam;

public class MultiJoinSliceProcess implements SliceProcess {
	Logger logger = Logger.getLogger(GeneralSliceProcess.class);
	List<BasicGraphPattern> BGPatterns;
	List<String> files;
	PrintStream ps;
	
	public MultiJoinSliceProcess(String order, List<BasicGraphPattern> BGPatterns, List<String> files, PrintStream ps) {
		this.BGPatterns = BGPatterns;
		this.files = files;
		this.ps = ps;
	}

	@Override
	public void run() {
		try {
			File tempCandidateFile = File.createTempFile("candidate", ".slice");
			tempCandidateFile.deleteOnExit();
			
			String CANDIDATE_FILE = tempCandidateFile.getAbsolutePath();
			
			long cacheSize = 10000;
			long freemem = (long) (Runtime.getRuntime().freeMemory()*0.75); // allocate 75% of the free memory
			if( cacheSize < freemem/1000) { // 1000 bytes estimation of 1 triple
				cacheSize = freemem/1000;
			}
			
			JoinCandidateCache jcCache = null;
//			if(RDFSliceStreamEngine.isCacheEnabled()) {
//				jcCache = new JoinCandidateCache(cacheSize);
//			}
			ConnectionPool pool = new ConnectionPool();
			Connection writerConnection = pool
					.getNewWriterConnection(CANDIDATE_FILE,
							false);
			WriteCandidateManager writerManager = new WriteCandidateManager(
					writerConnection, jcCache);
			writerManager.drop(SliceSQLiteDAOV2.CANDIDATE_TABLE);
			writerManager.createTable(); // create table

			ReaderCandidateManager readerManager = new ReaderCandidateManager(
					writerConnection, jcCache);

			SQLiteJCManager candidateManager = new SQLiteJCManager(writerManager, readerManager);
			BGPSelect bGPSelect = new BGPSelect(candidateManager);
			
			logger.debug("Selecting subset of candidates - COMPUTING MATCH - start");
			MultiPatternCandidateMatchRunnable multiJoinCandidateSelectionRunnable = new MultiPatternCandidateMatchRunnable(bGPSelect,
					BGPatterns,
					files.toArray(new String[files.size()]));
			multiJoinCandidateSelectionRunnable.run();
			
			writerManager.flush();
			
			writerManager = new WriteCandidateManager(writerConnection, jcCache);
			
			logger.debug("Selecting subset of candidates - end");
			if (bGPSelect.size() > 0) {
				if (logger.isDebugEnabled()) {
					DebugUtil.reset();
				}
				for (BasicGraphPattern BGPattern : BGPatterns) {
					bGPSelect.removeNotMachedMultiJoin(BGPatterns.indexOf(BGPattern), BGPattern);
				}				
				
				// close readers
				pool.closeAllReaders();
				
				logger.debug("Selecting subset of candidates - COMPUTING MATCH - end");
				if (logger.isDebugEnabled()) {
					DebugUtil.reset();
				}
				
				logger.debug("Selecting triples that match (final process) - start");
				List<MultiJoinExtractionRunnable> sliceExtractionRnnables = new ArrayList<MultiJoinExtractionRunnable>();
				for (String file : files) {
					Connection threadReaderConnection = SliceSQLiteDAOV2
							.getNewConnection(CANDIDATE_FILE,
									false, true);
					ReaderCandidateManager threadReaderManager = new ReaderCandidateManager(
							threadReaderConnection, jcCache);
					SQLiteJCManager threadSelectManager = new SQLiteJCManager(
							null, threadReaderManager);
					BGPSelect selectThread = new BGPSelect(threadSelectManager);
					MultiJoinExtractionRunnable sliceExtractionRunnable = new MultiJoinExtractionRunnable(
							file, selectThread, BGPatterns, ps);
					sliceExtractionRnnables.add(sliceExtractionRunnable);
				}
				if (sliceExtractionRnnables.size() > 0) {
					MultiRunnerDam sliceExtractionDam = new MultiRunnerDam(
							sliceExtractionRnnables);
					sliceExtractionDam.run();
				}
			}
			pool.closeAll();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
}
