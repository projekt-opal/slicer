package org.dice_research.opal.slicer.investigation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.arq.querybuilder.ConstructBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A connection to a SPARQL endpoint. Usage:
 * 
 * - {@link #setUrl(String)}
 * 
 * - {@link #setNamedGraph(String)} (optional)
 * 
 * - {@link #construct(ConstructBuilder)} or {@link #select(SelectBuilder)}
 * 
 * - {@link #close()}
 * 
 * Use {@link #isAvailable()} to check, if an SPARQL endpoint is reachable.
 * 
 * @author Adrian Wilke
 */
public class SparqlSource {

	private static final Logger LOGGER = LogManager.getLogger();

	private String sparqlEndpointUrl;
	private String namedGraph = null;
	private RDFConnection rdfConnection;

	public SparqlSource setUrl(String sparqlEndpointUrl) {
		this.sparqlEndpointUrl = sparqlEndpointUrl;
		return this;
	}

	public SparqlSource setNamedGraph(String namedGraph) {
		this.namedGraph = namedGraph;
		return this;
	}

	public boolean isAvailable() {

		// Check correct URL
		URL url = null;
		try {
			url = new URL(sparqlEndpointUrl);
		} catch (MalformedURLException e) {
			LOGGER.warn("Malformed URL: " + sparqlEndpointUrl, e);
			return false;
		}

		// Ping machine
		if (!IoUtils.pingHost(url.getHost(), url.getPort(), 100)) {
			LOGGER.warn("Could not ping: " + url);
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
					LOGGER.warn("HTTP 404: " + url);
					return false;
				}
			}
		} catch (IOException e) {
			LOGGER.warn("I/O exception: " + url, e);
			return false;
		}

		return true;
	}

	/**
	 * Adds named graph, if is set.
	 */
	public ResultSet select(SelectBuilder selectBuilder) {
		checkConnection();
		if (this.namedGraph != null) {
			selectBuilder.from(this.namedGraph);
		}
		return rdfConnection.query(selectBuilder.build()).execSelect();
	}

	public Collection<String> getNamedGraphs() {
		List<String> graphs = new LinkedList<>();
		SelectBuilder selectBuilder = new SelectBuilder().setDistinct(true).addVar("?g").addGraph("?g", "?s", "?p",
				"?o");
		ResultSet resultSet = rdfConnection.query(selectBuilder.build()).execSelect();
		while (resultSet.hasNext()) {
			graphs.add(resultSet.next().get("?g").toString());
		}
		return graphs;
	}

	/**
	 * Adds named graph, if is set.
	 */
	public Model construct(ConstructBuilder constructBuilder) {
		checkConnection();
		if (this.namedGraph != null) {
			constructBuilder.from(this.namedGraph);
		}
		return rdfConnection.query(constructBuilder.build()).execConstruct();
	}

	public void close() {
		if (rdfConnection != null && !rdfConnection.isClosed()) {
			rdfConnection.close();
		}
	}

	private SparqlSource connect() {
		LOGGER.info("Connecting to " + this);
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

	@Override
	public String toString() {
		if (namedGraph == null) {
			return sparqlEndpointUrl;
		} else {
			return sparqlEndpointUrl + " (" + namedGraph + ")";
		}
	}
}