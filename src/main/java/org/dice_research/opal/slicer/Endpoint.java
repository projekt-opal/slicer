package org.dice_research.opal.slicer;

import org.apache.jena.query.Query;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;

/**
 * A connection to a SPARQL endpoint. Usage:
 * 
 * - {@link #setEndpoint(String)}
 * 
 * - Perform queries
 * 
 * - {@link #close()}
 * 
 * @author Adrian Wilke
 */
public class Endpoint {

	private String sparqlEndpointUrl;
	private RDFConnection rdfConnection;

	public Endpoint setEndpoint(String sparqlEndpointUrl) {
		this.sparqlEndpointUrl = sparqlEndpointUrl;
		return this;
	}

	public ResultSet select(Query query) {
		checkConnection();
		return rdfConnection.query(query).execSelect();
	}

	public ResultSet select(String queryString) {
		checkConnection();
		return rdfConnection.query(queryString).execSelect();
	}

	public Model construct(Query query) {
		checkConnection();
		return rdfConnection.query(query).execConstruct();
	}

	public Model construct(String queryString) {
		checkConnection();
		return rdfConnection.query(queryString).execConstruct();
	}

	public void close() {
		if (rdfConnection != null && !rdfConnection.isClosed()) {
			rdfConnection.close();
		}
	}

	private Endpoint connect() {
		this.rdfConnection = RDFConnectionRemote.create().destination(sparqlEndpointUrl).build();
		return this;
	}

	private void checkConnection() {
		if (sparqlEndpointUrl == null) {
			throw new RuntimeException("No endpoint set.");

		} else if (rdfConnection == null || rdfConnection.isClosed()) {
			connect();
		}
	}
}