package org.rdfslice.sqlite;

import java.io.File;
import java.sql.SQLException;

import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.JoinCandidate;
import org.rdfslice.query.JoinCandidateManager;
import org.rdfslice.query.TriplePattern;
import org.rdfslice.util.JoinConditionUtil;

public class SQLiteJCManager implements JoinCandidateManager {
	
	private WriteCandidateManager writer;
	private ReaderCandidateManager reader;	
	
	public final static String TABLE_NAME = "jcandidates";	
	private static long size = 0;	

	public SQLiteJCManager(WriteCandidateManager writer, ReaderCandidateManager reader) throws Exception {
		this.writer = writer;
		this.reader = reader;
	}
	
	public static void createTable(String dbPath, WriteCandidateManager write) throws SQLException {
		File candidateFile = new File(dbPath);
		candidateFile.delete();
		write.drop(TABLE_NAME);
		write.createTable();
	}

	@Override
	public void addBach(JoinCandidate joinCandidade) throws Exception {
		Triple t = joinCandidade.getStatement();
		this.writer.addBatch(t, 
				joinCandidade.getJoinCondition(), 
				joinCandidade.getBgpIndx(),
				joinCandidade.getPatternMatch(),
				joinCandidade.getPatternIndx());
		SQLiteJCManager.size++;
	}
	
	public void add(JoinCandidate joinCandidade) throws Exception {
		Triple t = joinCandidade.getStatement();
		this.writer.add(t, 
				joinCandidade.getJoinCondition(), 
				joinCandidade.getBgpIndx(),
				joinCandidade.getPatternMatch(),
				joinCandidade.getPatternIndx());
		SQLiteJCManager.size++;
	}

	@Override
	public int computeMatch(Triple triple, int bgpIndx, BasicGraphPattern BGPattern) throws Exception {
		for(TriplePattern pattern : BGPattern) {
			int patternIndx = BGPattern.indexOf(pattern);
			int BGPJoinCondition = BGPattern.getJoinCondition();
			if(BGPJoinCondition == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
				BGPJoinCondition = JoinConditionUtil.getSubjectObjectSpecializedJoinType(BGPattern, pattern);
			}
			if(pattern.match(triple)
					&& reader.isCandidate(triple, bgpIndx, BGPJoinCondition)
					&& !reader.isChecked(triple, patternIndx, bgpIndx, BGPJoinCondition)) {
				this.writer.update(triple, bgpIndx, BGPJoinCondition, patternIndx, BGPattern.size());
				return patternIndx;
			}
		}
		return -1;
	}

	@Override
	public void removeNotMatched(int bgpIndx, BasicGraphPattern BGPattern) throws Exception {
		this.writer.remove(bgpIndx, BGPattern.size());
	}

	@Override
	public boolean match(Triple triple, int bgpIndx, BasicGraphPattern BGPattern) throws Exception {
		for(TriplePattern pattern : BGPattern) {
			if(pattern.match(triple)) {
				int BGPJoinCondition = BGPattern.getJoinCondition();
				if(BGPJoinCondition == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
					BGPJoinCondition = JoinConditionUtil.getSubjectObjectSpecializedJoinType(BGPattern, pattern);
				}
				if(reader.match(triple, bgpIndx, BGPJoinCondition)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean match(int bgpIndx, int patternIndx, int joinType,
			Triple triple) throws Exception {
		return reader.isCandidate(triple, bgpIndx, patternIndx, joinType);
	}

	@Override
	public synchronized long size() {
		return SQLiteJCManager.size;
	}

	@Override
	public void removeNotMatchedMultiJoin(int bgpIndx, BasicGraphPattern bGPattern) throws Exception {
		this.writer.removeMultiJoin(bgpIndx, bGPattern.size());
	}
}
