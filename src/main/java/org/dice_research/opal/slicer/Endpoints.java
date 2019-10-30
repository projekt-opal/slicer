package org.dice_research.opal.slicer;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages endpoints.
 *
 * @author Adrian Wilke
 */
public class Endpoints {

	private static Endpoints instance;

	private Map<String, Endpoint> endpoints = new HashMap<String, Endpoint>();

	static public Endpoints getInstance() {
		if (instance == null) {
			instance = new Endpoints();
		}
		return instance;
	}

	public static boolean isEmpty() {
		return getInstance().endpoints.isEmpty();
	}

	private Endpoints() {
	}

	public Endpoint create(String endpointId, String sparqlEndpointUrl) {
		Endpoint endpoint = new Endpoint().setUrl(sparqlEndpointUrl);
		endpoints.put(endpointId, endpoint);
		return endpoint;
	}

	public Endpoint get(String endpointId) {
		return endpoints.get(endpointId);
	}

	public void put(String endpointId, Endpoint endpoint) {
		endpoints.put(endpointId, endpoint);
	}

}