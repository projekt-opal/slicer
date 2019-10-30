package org.dice_research.opal.slicer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jena.query.Query;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;

/**
 * A connection to a SPARQL endpoint. Usage:
 * 
 * - {@link #setUrl(String)}
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

	public Endpoint setUrl(String sparqlEndpointUrl) {
		this.sparqlEndpointUrl = sparqlEndpointUrl;
		return this;
	}

	public boolean isAvailable() {

		// Check correct URL
		URL url = null;
		try {
			url = new URL(sparqlEndpointUrl);
		} catch (MalformedURLException e) {
			return false;
		}

		// Ping machine
		if (!IoUtils.pingHost(url.getHost(), url.getPort(), 100)) {
			return false;
		}

		// Check endpoint
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			if (connection.getResponseCode() == 404) {
				if (connection.getResponseMessage().contains("Service Description")) {
					// FUSEKI returns message, if no query was given
					return true;
				} else {
					return false;
				}
			}
		} catch (IOException e) {
			return false;
		}

		return true;
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