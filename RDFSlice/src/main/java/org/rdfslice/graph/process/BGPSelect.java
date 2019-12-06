package org.rdfslice.graph.process;

import java.io.InputStream;

import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.JoinCandidate;
import org.rdfslice.query.JoinCandidateManager;
import org.rdfslice.util.JoinConditionUtil;

public class BGPSelect {
		JoinCandidateManager candidateManager;

		public BGPSelect(JoinCandidateManager candidateManager) throws Exception {
			this.candidateManager = candidateManager;
		}

		public boolean match(Triple statement, int bgpIndx,
				BasicGraphPattern bGPattern, InputStream is) throws Exception{
			return candidateManager.match(statement, bgpIndx, bGPattern);
		}

		public synchronized void add(int bgpIdx, BasicGraphPattern BGPattern, Triple triple) throws Exception {
			// Saving the statements that match this pattern
			JoinCandidate joinCandidade = new JoinCandidate(bgpIdx, BGPattern);
			int joinType = BGPattern.getJoinCondition();
			if(BGPattern.getJoinCondition() == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
				joinType = JoinConditionUtil.getJoinType(BGPattern, triple);
			}
			joinCandidade.setJoinCondition(joinType);
			joinCandidade.setStatement(triple);
			candidateManager.add(joinCandidade); // add join candidate
		}
		
		public synchronized void add(int bgpIdx, BasicGraphPattern BGPattern, Triple triple, int patternIndx) throws Exception {
			// Saving the statements that match this pattern
			JoinCandidate joinCandidade = new JoinCandidate(bgpIdx, BGPattern);
			int joinType = BGPattern.getJoinCondition();
			if(BGPattern.getJoinCondition() == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
				joinType = JoinConditionUtil.getJoinType(BGPattern, triple);
			}
			if(joinType == BasicGraphPattern.MULTI_JOIN) {
				joinType = JoinConditionUtil.getJoinType(BGPattern, patternIndx);
			}
			joinCandidade.setPatternIndx(patternIndx);
			joinCandidade.setJoinCondition(joinType);
			joinCandidade.setStatement(triple);
			candidateManager.add(joinCandidade); // add join candidate
		}
		
		public int computeMatch(Triple triple, int bgpIndx, BasicGraphPattern BGPattern) throws Exception {			
			return candidateManager.computeMatch(triple, bgpIndx, BGPattern); // computes the match of all patterns
		}
				
		public void removeNotMached(int bdpIndx, BasicGraphPattern BGPattern) throws Exception {
			candidateManager.removeNotMatched(bdpIndx, BGPattern); // remove not matched terms
		}
		
		public long size() {
			return candidateManager.size();
		}

		public boolean match(Triple triple, int bgpIndx, int patternIndx,
				BasicGraphPattern bGPattern) throws Exception {
			int joinType = JoinConditionUtil.getBasicJoinType(bGPattern.get(patternIndx-1), bGPattern.get(patternIndx));
			int i = 1;
			while(joinType == BasicGraphPattern.DISJOINT && i < bGPattern.size()) {
				i++;
				joinType = JoinConditionUtil.getBasicJoinType(bGPattern.get(patternIndx-i), bGPattern.get(patternIndx));
			}
						
			if(joinType == BasicGraphPattern.DISJOINT) {
				throw new Exception("Disjoint basic graph pattern found.");
			}
			
			if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
				joinType = JoinConditionUtil.getSubjectObjectSpecializedJoinType(bGPattern.get(patternIndx-i), bGPattern.get(patternIndx));
			}
			return candidateManager.match(bgpIndx, patternIndx-i, joinType, triple);
		}
		
		public boolean exists(Triple triple, int bgpIndx, int patternIndx, BasicGraphPattern bGPattern) throws Exception {
			int joinType =  BasicGraphPattern.DISJOINT;
			if(patternIndx != 0) {
				int i=0;
				while(joinType == BasicGraphPattern.DISJOINT && i < bGPattern.size()) {
					i++;
					joinType = JoinConditionUtil.getBasicJoinType(bGPattern.get(patternIndx), bGPattern.get(patternIndx-i));
				}
				if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
					joinType = JoinConditionUtil.getSubjectObjectSpecializedJoinType(bGPattern.get(patternIndx-i), bGPattern.get(patternIndx));
				}
			} else {
				int i=0;
				while(joinType == BasicGraphPattern.DISJOINT && i < bGPattern.size()) {
					i++;
					joinType = JoinConditionUtil.getBasicJoinType(bGPattern.get(patternIndx+i), bGPattern.get(patternIndx));
				}
				if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
					joinType = JoinConditionUtil.getSubjectObjectSpecializedJoinType(bGPattern.get(patternIndx+i), bGPattern.get(patternIndx));
				}
			}
			return candidateManager.match(bgpIndx, patternIndx, joinType, triple);
		}

		public void removeNotMachedMultiJoin(int bdpIndx, BasicGraphPattern bGPattern) throws Exception {
			candidateManager.removeNotMatchedMultiJoin(bdpIndx, bGPattern); // remove not matched terms
		}
	}