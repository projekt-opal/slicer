package org.rdfslice.all;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.rdfslice.query.Pattern;
import org.rdfslice.query.TriplePattern;

public class SesameSparqlPaserTest {

	@Test	
	public void testSparql(){
		SPARQLParser parser = new SPARQLParser();
		try {
			ParsedQuery query = parser.parseQuery("PREFIX foaf: <http://xmlns.com/foaf/0.1/> Select * where {?a foaf:name ?b. ?a foaf:name ?c}", null);
			Projection projection = (Projection) query.getTupleExpr();
			List<Pattern> patterns = getPatterns(projection.getArg());
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	List<Pattern> getPatterns(TupleExpr ex){
		List<Pattern> patterns = new ArrayList<Pattern>();
		
		if(ex instanceof Join){
			Join join = (Join) ex;
			if(join.getLeftArg() instanceof StatementPattern){				
				patterns.addAll(getPatterns(join.getLeftArg()));
			} else
				patterns.addAll(getPatterns(join.getLeftArg()));
			
			if(join.getRightArg() instanceof StatementPattern){
				patterns.addAll(getPatterns(join.getRightArg()));
			} else
				patterns.addAll(getPatterns((Join) join.getRightArg()));
		} else {
			StatementPattern rightSPattern = (StatementPattern) ex;
			patterns.add(sesame2SparqlFile(rightSPattern));
		}
		
		return patterns;
	}
	
	public TriplePattern sesame2SparqlFile(StatementPattern statementPattern){
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
