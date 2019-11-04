package org.dice_research.opal.slicer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataInvestigator {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Gets all RDF types (concepts).
	 */
	public List<String> getTypes(SparqlSource sparqlSource) {
		List<String> types = new LinkedList<>();
		SelectBuilder builder = new SelectBuilder().setDistinct(true).addVar("?type").addWhere("?s", RDF.type, "?type");
		ResultSet resultSet = sparqlSource.select(builder);
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			try {
				types.add(querySolution.getResource("type").getURI());
			} catch (ClassCastException e) {
				types.add(querySolution.toString());
				LOGGER.warn("Could not get URI of resource. ", e);
			}
		}
		return types;
	}

	/**
	 * Gets all RDF types (concepts) and the number of instances.
	 */
	public Map<String, Integer> getTypesAndSizes(SparqlSource sparqlSource) {
		Map<String, Integer> types = new HashMap<String, Integer>();
		SelectBuilder builder = null;
		try {
			builder = new SelectBuilder().setDistinct(true).addVar("?type").addVar("COUNT(?resource)", "?size")
					.addWhere("?resource", RDF.type, "?type").addGroupBy("?type");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		ResultSet resultSet = sparqlSource.select(builder);
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			types.put(querySolution.getResource("type").getURI(),
					Integer.parseInt(querySolution.getLiteral("size").getString()));
		}
		return types;
	}

	/**
	 * Filters candidates (Type URI, number of instances) by the given maxLimit for
	 * the number of instances.
	 */
	public Map<String, Integer> filterCandidates(Map<String, Integer> typesSizes, int maxLimit) {
		Map<String, Integer> candidates = new HashMap<>();
		for (Entry<String, Integer> entry : typesSizes.entrySet()) {
			if (entry.getValue() <= maxLimit) {
				candidates.put(entry.getKey(), entry.getValue());
			}
		}
		return candidates;
	}

	/**
	 * Gets all instances which are of type typeUri in given SPARQL-source.
	 */
	public List<String> getInstances(SparqlSource sparqlSource, String typeUri) {
		List<String> instances = new LinkedList<>();
		SelectBuilder builder = null;

		try {
			builder = new SelectBuilder().setDistinct(true).addVar("?resource").addWhere("?resource", RDF.type,
					ResourceFactory.createResource(typeUri));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		ResultSet resultSet = sparqlSource.select(builder);
		while (resultSet.hasNext()) {
			instances.add(resultSet.next().getResource("?resource").getURI());
		}
		return instances;
	}

}