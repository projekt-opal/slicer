package org.rdfslice.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrdf.query.algebra.Filter;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.LeftJoin;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.Var;
import org.rdfslice.query.BasicGraphPattern;
import org.rdfslice.query.TriplePattern;

public class OpenRDFSparqlUtil {
	
	public static Collection<TriplePattern> getPatterns(TupleExpr ex) {
		List<TriplePattern> patterns = new ArrayList<TriplePattern>();
		
		if(ex instanceof Join) {
			Join join = (Join) ex;
			patterns.addAll(getPatterns(join.getLeftArg()));
			patterns.addAll(getPatterns(join.getRightArg()));
		} else if(ex instanceof Filter) {
			Filter filter = (Filter) ex;
			patterns.addAll(getPatterns(filter.getArg()));
		} else if(ex instanceof Union){
			Union union = (Union) ex;
			patterns.addAll(getPatterns(union.getLeftArg()));
			patterns.addAll(getPatterns(union.getRightArg()));
		} else if(ex instanceof LeftJoin){
			LeftJoin leftJoin = (LeftJoin) ex;
			patterns.addAll(getPatterns(leftJoin.getLeftArg()));
			patterns.addAll(getPatterns(leftJoin.getRightArg()));
		} else {
			StatementPattern rightSPattern = (StatementPattern) ex;
			patterns.add(getTriple(rightSPattern));
		}		
		return patterns;
	}
	
	public static List<BasicGraphPattern> getBGPatterns(TupleExpr ex) {
		List<BasicGraphPattern> patterns = new ArrayList<BasicGraphPattern>();
		if(ex instanceof Join) {
			Join join = (Join) ex;
			List<BasicGraphPattern> BGPsLeft = getBGPatterns(join.getLeftArg());
			List<BasicGraphPattern> BGPsRight = getBGPatterns(join.getRightArg());
			List<BasicGraphPattern> BGPs = new ArrayList<BasicGraphPattern>(); 
			BasicGraphPattern nBGP = new BasicGraphPattern();
			for(BasicGraphPattern BGP : BGPsLeft) {
				nBGP.addAll(BGP);
			}			
			for(BasicGraphPattern BGP : BGPsRight) {
				nBGP.addAll(BGP);
			}
			if(BGPsLeft.get(0).size() == 1 || BGPsRight.get(0).size() == 1) {
				TriplePattern triplePattern1 = BGPsLeft.get(0).get(0);
				TriplePattern triplePattern2 = BGPsRight.get(0).get(0);
				int joinType = JoinConditionUtil.getBasicJoinType(triplePattern1, triplePattern2);
				if((BGPsLeft.get(0).size() == 1 && BGPsRight.get(0).size() == 1) ||
						((BGPsLeft.get(0).size() > 1 && BGPsLeft.get(0).getJoinCondition() == joinType) || 
						(BGPsRight.get(0).size() > 1 && BGPsRight.get(0).getJoinCondition() == joinType)) ||
						(isUniquePlaceHolderJoin(BGPsLeft, BGPsRight))) {
					nBGP.setJoinCondition(joinType);
				} else {
					nBGP.setJoinCondition(BasicGraphPattern.MULTI_JOIN);
				}
			} else {
				if(BGPsLeft.get(0).getJoinCondition() == BGPsRight.get(0).getJoinCondition()) {
					nBGP.setJoinCondition(BGPsLeft.get(0).getJoinCondition());
				} else {
					nBGP.setJoinCondition(BasicGraphPattern.MULTI_JOIN);
				}
			}			
			BGPs.add(nBGP);
			return BGPs;
		} else if(ex instanceof Filter) {
			Filter filter = (Filter) ex;
			patterns.addAll(getBGPatterns(filter.getArg()));
		} else if(ex instanceof Union) {
			Union union = (Union) ex;
			List<BasicGraphPattern> BGPsLeft = getBGPatterns(union.getLeftArg());
			List<BasicGraphPattern> BGPsRight = getBGPatterns(union.getRightArg());
			BGPsRight.addAll(BGPsLeft);
			return BGPsRight;
		} else if(ex instanceof LeftJoin) {
			LeftJoin leftJoin = (LeftJoin) ex;
			List<BasicGraphPattern> BGPsLeft = getBGPatterns(leftJoin.getLeftArg());
			List<BasicGraphPattern> BGPsRight = getBGPatterns(leftJoin.getRightArg());
			List<BasicGraphPattern> BGPs = new ArrayList<BasicGraphPattern>(); 
			BasicGraphPattern nBGP = new BasicGraphPattern();			
			for(BasicGraphPattern BGP : BGPsLeft) {
				nBGP.addAll(BGP);
			}			
			for(BasicGraphPattern BGP : BGPsRight) {
				nBGP.addAll(BGP);
			}
			
			TriplePattern triplePattern1 = BGPsLeft.get(0).get(0);
			TriplePattern triplePattern2 = BGPsRight.get(0).get(0);			
			nBGP.setJoinCondition(JoinConditionUtil.getBasicJoinType(triplePattern1, triplePattern2));
			
			BGPs.add(nBGP);
			return BGPs;
		} else {
			StatementPattern rightSPattern = (StatementPattern) ex;
			BasicGraphPattern BGP = new BasicGraphPattern();
			TriplePattern tp = getTriple(rightSPattern);
			if(!RDFUtil.isVariable(tp.getSubject())) {
				BGP.setJoinCondition(BasicGraphPattern.SUBJECT_CONSTANT_DISJOINT);
			} else {
				BGP.setJoinCondition(BasicGraphPattern.DISJOINT);
			}
			BGP.add(tp);
			patterns.add(BGP);
		}	
		
		return patterns;
	}
	
	public static boolean isUniquePlaceHolderJoin(List<BasicGraphPattern> bGPsLeft,
			List<BasicGraphPattern> bGPsRight) {
		String placeholder = null;
		for(BasicGraphPattern bGP : bGPsLeft) {
			String bPlaceHolder = bGP.getPlaceHolder();
			if(bGP.size() == 1) {
				bPlaceHolder = JoinConditionUtil.getPlaceholder(bGP.get(0), bGPsRight.get(0).get(0));
			}
			if(placeholder == null) {
				placeholder = bPlaceHolder;
			}
			if(!placeholder.equals(bPlaceHolder)) {
				return false;
			}
		}
		
		for(BasicGraphPattern bGP : bGPsRight) {
			String bPlaceHolder = bGP.getPlaceHolder();
			if(bGP.size() == 1) {
				bPlaceHolder = JoinConditionUtil.getPlaceholder(bGP.get(0), bGPsLeft.get(0).get(0));
			}
			if(placeholder == null) {
				placeholder = bPlaceHolder;
			}
			if(!placeholder.equals(bPlaceHolder)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isUniqueJoin(List<BasicGraphPattern> bGPsLeft,
			List<BasicGraphPattern> bGPsRight) {
		int join = bGPsLeft.get(0).getJoinCondition();
		for(BasicGraphPattern bGP : bGPsLeft) {
			int bJoin = bGP.getJoinCondition();
			if(bGP.size() == 1) {
				bJoin = JoinConditionUtil.getBasicJoinType(bGP.get(0), bGPsRight.get(0).get(0));
			}
			if(bJoin != join) {
				return false;
			}
		}		
		for(BasicGraphPattern bGP : bGPsRight) {
			int bJoin = bGP.getJoinCondition();
			if(bGP.size() == 1) {
				bJoin = JoinConditionUtil.getBasicJoinType(bGP.get(0), bGPsLeft.get(0).get(0));
			}
			if(bJoin != join) {
				return false;
			}
		}
		return true;
	}

	public static TriplePattern getTriple(StatementPattern statementPattern){
		TriplePattern triplePattern = new TriplePattern();
		
		Var subjectVar = statementPattern.getSubjectVar();
		triplePattern.setSubject(subjectVar.isAnonymous()?subjectVar.getValue().stringValue():"?"+subjectVar.getName());
		
		Var predicateVar = statementPattern.getPredicateVar();
		triplePattern.setPredicate(predicateVar.isAnonymous()?predicateVar.getValue().stringValue():"?"+predicateVar.getName());
		
		Var objectVar = statementPattern.getObjectVar();
		triplePattern.setObject(objectVar.isAnonymous()?objectVar.getValue().stringValue():"?"+objectVar.getName());
		
		return triplePattern;
	}
}
