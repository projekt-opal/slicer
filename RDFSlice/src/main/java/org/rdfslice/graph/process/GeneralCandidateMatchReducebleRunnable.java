package org.rdfslice.graph.process;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.rdfslice.CandidateSelectionRunnable;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.sqlite.ConnectionPool;
import org.rdfslice.sqlite.ReaderCandidateManager;
import org.rdfslice.sqlite.SQLiteJCManager;
import org.rdfslice.sqlite.SliceSQLiteDAOV2;
import org.rdfslice.sqlite.WriteCandidateManager;
import org.rdfslice.sqlite.cache.JoinCandidateCache;
import org.rdfslice.util.thread.ReducebleRunnable;

public class GeneralCandidateMatchReducebleRunnable implements ReducebleRunnable<File> {
	
	private JoinCandidateCache jcCache = null;
	private File tempCandidateFile = null;
	private String file = null;
	private List<BasicGraphPattern> BGPatterns = null;
	
	public GeneralCandidateMatchReducebleRunnable(JoinCandidateCache jcCache, 
			String file, 
			List<BasicGraphPattern> BGPatterns) {
		this.jcCache = jcCache;
		this.file = file;
		this.BGPatterns = BGPatterns;
	}

	@Override
	public void run() {
		try {
			tempCandidateFile = File.createTempFile("candidate", 
					"slice");
			tempCandidateFile.deleteOnExit();

			String CANDIDATE_FILE = tempCandidateFile.getAbsolutePath();
			ConnectionPool pool = new ConnectionPool();
			Connection writerConnection = pool
					.getNewWriterConnection(CANDIDATE_FILE,
							false);
			WriteCandidateManager writerManager = new WriteCandidateManager(
					writerConnection, 
					jcCache);
			writerManager.drop(SliceSQLiteDAOV2.CANDIDATE_TABLE);
			writerManager.createTable(); // create table

			ReaderCandidateManager readerManager = new ReaderCandidateManager(
					writerConnection, 
					jcCache);

			SQLiteJCManager candidateManager = new SQLiteJCManager(writerManager, readerManager);
			BGPSelect bGPSelect = new BGPSelect(candidateManager);

			CandidateSelectionRunnable candidateSelectionRunnable = new CandidateSelectionRunnable(
						file, bGPSelect, BGPatterns);
			candidateSelectionRunnable.run();		

			writerManager.flush();
			
			pool.closeAll();
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
		
	}

	@Override
	public File reduce(File result) {
		return tempCandidateFile;
	}

	@Override
	public File getResult() {
		return tempCandidateFile;
	}

}
