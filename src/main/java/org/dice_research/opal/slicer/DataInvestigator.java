package org.dice_research.opal.slicer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SPARQL queries to investigate RDF.
 * 
 * @see QueryBuilder examples:
 *      https://github.com/apache/jena/tree/master/jena-extras/jena-querybuilder
 * 
 * @author Adrian Wilke
 */
public class DataInvestigator {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Gets all RDF types (concepts).
	 */
	public List<String> getTypes(SparqlSource sparqlSource) {
		List<String> types = new LinkedList<>();
		SelectBuilder builder = new SelectBuilder().setDistinct(true).addVar("?type").addWhere("?s", RDF.type, "?type");
		LOGGER.info(builder.toString());
		ResultSet resultSet = sparqlSource.select(builder);
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			if (querySolution.get("type").isURIResource()) {
				types.add(querySolution.getResource("type").getURI());
			} else {
				LOGGER.warn("Found type: " + querySolution.get("type").toString());
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
		LOGGER.info(builder.toString());
		ResultSet resultSet = sparqlSource.select(builder);
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			if (querySolution.get("type").isURIResource()) {
				types.put(querySolution.getResource("type").getURI(),
						Integer.parseInt(querySolution.getLiteral("size").getString()));
			} else {
				LOGGER.warn("Found type: " + querySolution.get("type").toString() + " "
						+ Integer.parseInt(querySolution.getLiteral("size").getString()));
			}
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
		LOGGER.info(builder.toString());
		ResultSet resultSet = sparqlSource.select(builder);
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			if (querySolution.get("resource").isURIResource()) {
				instances.add(querySolution.getResource("?resource").getURI());
			} else {
				LOGGER.warn("Found instance: " + querySolution.get("resource").toString());
			}

		}
		return instances;
	}

	/**
	 * Gets predicate-URIs and list of related type-URIs.
	 * 
	 * @return Map of predicates to list of object-types
	 */
	public Map<String, List<String>> getPredicates(SparqlSource sparqlSource, String typeUri,
			String optionalLiteralPlaceholder) throws Exception {
		Map<String, List<String>> predicates = new HashMap<>();
		SelectBuilder builder = null;
		try {
			builder = new SelectBuilder().setDistinct(true).addVar("?p").addVar("?otype")
					.addWhere("?s", RDF.type, ResourceFactory.createResource(typeUri)).addWhere("?s", "?p", "?o")
					.addOptional("?o", RDF.type, "?otype");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		LOGGER.info(builder.toString());
		ResultSet resultSet = sparqlSource.select(builder);
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();

			String predicateUri = querySolution.get("p").asResource().getURI();
			if (!predicates.containsKey(predicateUri)) {
				predicates.put(predicateUri, new LinkedList<>());
			}

			RDFNode oTypeNode = querySolution.get("otype");
			if (oTypeNode != null) {
				predicates.get(predicateUri).add(oTypeNode.asResource().getURI());
			} else if (optionalLiteralPlaceholder != null) {
				// Add also placeholder for literals
				predicates.get(predicateUri).add(optionalLiteralPlaceholder);
			}
		}

		return predicates;
	}

}