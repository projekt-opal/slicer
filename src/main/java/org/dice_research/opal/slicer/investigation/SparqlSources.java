package org.dice_research.opal.slicer.investigation;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages SPARQL endpoints.
 *
 * @author Adrian Wilke
 */
public class SparqlSources {

	private static SparqlSources instance;

	private Map<String, SparqlSource> index = new HashMap<String, SparqlSource>();

	static public SparqlSources getInstance() {
		if (instance == null) {
			instance = new SparqlSources();
		}
		return instance;
	}

	public static boolean isEmpty() {
		return getInstance().index.isEmpty();
	}

	private SparqlSources() {
		// Singleton
	}

	public SparqlSource create(String endpointId, String sparqlEndpointUrl) {
		SparqlSource sparqlSource = new SparqlSource().setUrl(sparqlEndpointUrl);
		index.put(endpointId, sparqlSource);
		return sparqlSource;
	}

	public SparqlSource create(String endpointId, String sparqlEndpointUrl, String namedGraph) {
		SparqlSource sparqlSource = new SparqlSource().setUrl(sparqlEndpointUrl).setNamedGraph(namedGraph);
		index.put(endpointId, sparqlSource);
		return sparqlSource;
	}

	public SparqlSource get(String id) {
		return index.get(id);
	}

}