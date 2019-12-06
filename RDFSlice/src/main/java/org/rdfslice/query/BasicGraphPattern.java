package org.rdfslice.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class BasicGraphPattern extends ArrayList<TriplePattern> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3745492958788719935L;

	/**
	 * 
	 */	
	public static final int SUBJECT_SUBJECT_JOIN = 0;
	public static final int PREDICATE_PREDICATE_JOIN = 1;
	public static final int OBJECT_OBJECT_JOIN = 2;
	
	public static final int SUBJECT_OBJECT_JOIN = 3;	
	public static final int SUBJECT_OBJECT_JOIN_SS = 31;
	public static final int SUBJECT_OBJECT_JOIN_SO = 32;
	public static final int SUBJECT_OBJECT_JOIN_OO = 33;
	public static final int SUBJECT_OBJECT_JOIN_OS = 34;
	
	public static final int DISJOINT = 4;
	public static final int SUBJECT_CONSTANT_DISJOINT = 41;
	
	public static final int MULTI_JOIN = 5;
	
	private int joinCondition;
	private int childJoinCondition;	
	private BasicGraphPattern child;
	private Comparator<TriplePattern> comparator;
	
	public BasicGraphPattern() {
		comparator = new Comparator<TriplePattern>() {
			@Override
			public int compare(TriplePattern o1, TriplePattern o2) {
				return o2.getNumberOfContants() - o1.getNumberOfContants();
			}
		};
	}

	public BasicGraphPattern getChild() {
		return child;
	}

	public void setChild(BasicGraphPattern child) {
		this.child = child;
	}

	public int getJoinCondition() {
		return joinCondition;
	}

	public void setJoinCondition(int setJoinCondition) {
		this.joinCondition = setJoinCondition;
	}
	
	public int getChildJoinCondition() {
		return childJoinCondition;
	}
	
	public String getPlaceHolder() {
		if(joinCondition == BasicGraphPattern.SUBJECT_SUBJECT_JOIN) {
			return get(0).getSubject();
		} else if(joinCondition == BasicGraphPattern.PREDICATE_PREDICATE_JOIN) {
			return get(0).getPredicate();
		} else if(joinCondition == BasicGraphPattern.OBJECT_OBJECT_JOIN) {
			return get(0).getObject();
		} else if(joinCondition == BasicGraphPattern.SUBJECT_OBJECT_JOIN) {
			if(get(0).getObject().equals(get(1).getSubject())) {
				return get(0).getObject();
			} else {
				return get(0).getSubject();
			}
		}
		return null;
	}

	public void setChildJoinCondition(int childJoinCondition) {
		this.childJoinCondition = childJoinCondition;
	}
	
	@Override
	public boolean add(TriplePattern e) {
		boolean added = super.add(e);
		if(added) {
			Collections.sort(this, comparator);
		}
		return added;
	}
	
	@Override
	public boolean addAll(Collection<? extends TriplePattern> c) {
		boolean added = super.addAll(c);
		if(added) {
			Collections.sort(this, comparator);
		}
		return added;
	}
}

