package org.rdfslice.sqlite.cache;

import java.util.HashMap;

import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.structures.MaxSizeHashMap;

public class SQLiteCache {
	
	HashMap<CacheCandidateEntity, Boolean> cachedCandidates = new MaxSizeHashMap<CacheCandidateEntity, Boolean>(1000);
	
	public synchronized Boolean get(Triple triple, int bgpIndx, int joinType) {
		CacheCandidateEntity entity = new CacheCandidateEntity(triple, bgpIndx, joinType);
		return cachedCandidates.get(entity);
	}
	
	public synchronized Boolean put(Triple triple, int bgpIndx, int joinType, boolean isChecked) {
		CacheCandidateEntity entity = new CacheCandidateEntity(triple, bgpIndx, joinType);
		return cachedCandidates.put(entity, isChecked);
	}
	
	private class CacheCandidateEntity {

		private Triple triple;
		private int bgpIndx;
		private int joinType;
		
		public CacheCandidateEntity(Triple triple, int bgpIndx, int joinType) {
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
		
		public String getCandidate() {
			return getString(triple, joinType);
		}

		@Override
		public int hashCode() {
			String hashCode = "" + bgpIndx + getString(triple, joinType).hashCode();
			return hashCode.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null)
				return false;
			if(obj instanceof CacheCandidateEntity) {
				CacheCandidateEntity entity = (CacheCandidateEntity) obj;				
				return	(entity.getBGPIndx() == getBGPIndx()) &&
						(entity.getCandidate().equals(getCandidate()));
			}
			return false;
		}
	}
}
