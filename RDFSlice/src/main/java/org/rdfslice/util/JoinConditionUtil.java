package org.rdfslice.util;

import org.rdfslice.model.Triple;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.TriplePattern;

public class JoinConditionUtil {	
	
	public static int getSubjectObjectSpecializedJoinType(BasicGraphPattern BGPattern, TriplePattern triplePattern) {
		int BGPJoinCondition = BGPattern.getJoinCondition();
		
		if(BGPJoinCondition == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
			TriplePattern moreConstantPatern = BGPattern.get(0); // BGP is ordered by DESC in relation to the number of constants, 
			                                                     // Always the first position is the more constant pattern			
			if(moreConstantPatern.sameAs(triplePattern)) {
				triplePattern = BGPattern.get(1);
				if(moreConstantPatern.getSubject().equals(triplePattern.getSubject())) {
					return BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS;
				} else if(moreConstantPatern.getObject().equals(triplePattern.getObject())) {
					return BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO;
				} else if(moreConstantPatern.getSubject().equals(triplePattern.getObject())) {
					return BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS;
				} else if(moreConstantPatern.getObject().equals(triplePattern.getSubject())) {
					return BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO;
				}
			}
			
			// The join candidates are according the more constant pattern
			if(triplePattern.getSubject().equals(moreConstantPatern.getSubject())) {
				return BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS;
			} else if(triplePattern.getObject().equals(moreConstantPatern.getObject())) {
				return BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO;
			} else if(moreConstantPatern.getSubject().equals(triplePattern.getObject())) {
				return BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO;
			} else if(moreConstantPatern.getObject().equals(triplePattern.getSubject())) {
				return BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS;
			}
		}
		
		return BGPJoinCondition;
	}
	
	public static int getSubjectObjectSpecializedJoinType(TriplePattern triplePattern, TriplePattern triplePatternTarget) {
		// The join candidates are according the more constant pattern
		if(triplePattern.getSubject().equals(triplePatternTarget.getSubject())) {
			return BasicGraphPattern.SUBJECT_OBJECT_JOIN_SS;
		} else if(triplePattern.getObject().equals(triplePatternTarget.getObject())) {
			return BasicGraphPattern.SUBJECT_OBJECT_JOIN_OO;
		} else if(triplePattern.getSubject().equals(triplePatternTarget.getObject())) {
			return BasicGraphPattern.SUBJECT_OBJECT_JOIN_SO;
		} else if(triplePattern.getObject().equals(triplePatternTarget.getSubject())) {
			return BasicGraphPattern.SUBJECT_OBJECT_JOIN_OS;
		}		
		return -1;
	}
	
	public static int getJoinType(BasicGraphPattern BGPattern, Triple triple) {
		for(TriplePattern pattern : BGPattern) {
			if(pattern.match(triple)) {
				int BGPJoinCondition = BGPattern.getJoinCondition();
				if(BGPJoinCondition == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
					BGPJoinCondition = JoinConditionUtil.getSubjectObjectSpecializedJoinType(BGPattern, pattern);
					return BGPJoinCondition;
				}
				return BGPJoinCondition;
			}
		}
		return -1;
	}
	
	public static int getJoinType(BasicGraphPattern BGPattern, int patternIndx) {
		int joinType = -1;
		if(patternIndx == 0) {
			joinType = getBasicJoinType(BGPattern.get(0), BGPattern.get(1));
//			if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
//				return getSubjectObjectSpecializedJoinType(BGPattern.get(0), BGPattern.get(1));
//			}
		} else {
			joinType = getBasicJoinType(BGPattern.get(patternIndx), BGPattern.get(patternIndx-1));
//			if(joinType == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
//				return getSubjectObjectSpecializedJoinType(BGPattern.get(patternIndx), BGPattern.get(-1));
//			}
		}
		return joinType;
	}
	
	public static int getBasicJoinType(int joinType) {
		if(joinType>30) {
			return 3;
		}
		return joinType;
	}
	
	public static int getBasicJoinType(TriplePattern triplePattern1, TriplePattern triplePattern2){	
		if(triplePattern1.getSubject().equals(triplePattern2.getSubject())) { // subject subject join			
			return BasicGraphPattern.SUBJECT_SUBJECT_JOIN;
		}
		else if(triplePattern1.getPredicate().equals(triplePattern2.getPredicate())) { // predicate predicate join			
			return BasicGraphPattern.PREDICATE_PREDICATE_JOIN;			
		}
		else if(triplePattern1.getObject().equals(triplePattern2.getObject())) { // object object join
			return BasicGraphPattern.OBJECT_OBJECT_JOIN;
		}
		else if(triplePattern1.getSubject().equals(triplePattern2.getObject()) || 
				triplePattern1.getObject().equals(triplePattern2.getSubject())) { // subject object join
			return BasicGraphPattern.SUBJECT_OBJECT_JOIN;
		}
		
		return BasicGraphPattern.DISJOINT;
	}
	
	public static String getPlaceholder(TriplePattern triplePattern1, TriplePattern triplePattern2){	
		if(triplePattern1.getSubject().equals(triplePattern2.getSubject())) { // subject subject join			
			return triplePattern1.getSubject();
		}
		else if(triplePattern1.getPredicate().equals(triplePattern2.getPredicate())) { // predicate predicate join			
			return triplePattern2.getPredicate();			
		}
		else if(triplePattern1.getObject().equals(triplePattern2.getObject())) { // object object join
			return triplePattern2.getObject();
		}
		else if(triplePattern1.getSubject().equals(triplePattern2.getObject()) || 
				triplePattern1.getObject().equals(triplePattern2.getSubject())) { // subject object join
			if(triplePattern1.getObject().equals(triplePattern2.getSubject())) {
				return triplePattern1.getObject();
			} else {
				return triplePattern1.getSubject();
			}
		}		
		return null;
	}

	public static boolean isPatternLeef(BasicGraphPattern bGPattern,
			int patternIndx) {
		int joinCount = 0;
		TriplePattern tp = bGPattern.get(patternIndx);
		for(TriplePattern bTp : bGPattern) {
			int joinType = JoinConditionUtil.getBasicJoinType(tp, bTp);
			if(joinType != BasicGraphPattern.DISJOINT && !bTp.equals(tp)) {
				joinCount++;
			}
		}
		return joinCount==1;
	}
}
