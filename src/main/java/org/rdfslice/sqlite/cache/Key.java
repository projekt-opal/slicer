package org.rdfslice.sqlite.cache;

import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.util.JoinConditionUtil;

public class Key {

		private Triple triple;
		private int bgpIndx;
		private int joinType;
		
		public Key(Triple triple, int bgpIndx, int joinType) {
			this.triple = triple;
			this.bgpIndx = bgpIndx;
			this.joinType = joinType;
		}
		
		private String getString(Triple triple, int joinType) {
			if(joinType == BasicGraphPattern.SUBJECT_SUBJECT_JOIN) {
				return triple.getSubject();
			} else if(joinType == BasicGraphPattern.PREDICATE_PREDICATE_JOIN) {
				return triple.getPredicate();
			} else if(joinType == BasicGraphPattern.OBJECT_OBJECT_JOIN) {
				return triple.getObject();
			} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS) {
				return triple.getSubject();
			} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO) {
				return triple.getObject();
			} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS) {
				return triple.getSubject();
			} else if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO) {
				return triple.getObject();
			}	
			return "";
		}
		
		public int getBGPIndx() {
			return bgpIndx;
		}
		
		public int getJoinType() {
			return joinType;
		}
		
		public String getCandidate() {
			return getString(triple, joinType);
		}

		@Override
		public int hashCode() {
			String hashCode = "" + bgpIndx + JoinConditionUtil.getBasicJoinType(joinType) + getString(triple, joinType);
			return hashCode.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null) {
				return false;
			} if(obj instanceof Key) {
				Key entity = (Key) obj;
				return	(entity.getBGPIndx() == getBGPIndx()) &&
						(entity.getCandidate().equals(getCandidate())) &&
						(JoinConditionUtil.getBasicJoinType(entity.getJoinType()) == JoinConditionUtil.getBasicJoinType(getJoinType()));
			}
			return false;
		}
	}