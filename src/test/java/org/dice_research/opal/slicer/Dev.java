package org.dice_research.opal.slicer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.arq.querybuilder.ConstructBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class Dev {

	public static void main(String[] args) {
		new Dev().main();
	}

	SparqlSource endpoint;

	private void main() {
		Cfg.createEndpoints();
		endpoint = SparqlSources.getInstance().get(Cfg.OPAL);

		String typeUrl = "http://www.w3.org/ns/dcat#Catalog";

		// get different set sources
		Collection<Resource> instances = getInstancesOfType(typeUrl);

		// print
		System.out.println("INSTANCES");
		Iterator<Resource> it = instances.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().asResource());
		}

		// extract
		System.out.println("SLICE");
		sliceByType(typeUrl, 1);
	}

	private Collection<Resource> getInstancesOfType(String typeUrl) {
		Set<Resource> instances = new HashSet<>();
		SelectBuilder builder = new SelectBuilder().setDistinct(true).addVar("?s").addWhere("?s", "a",
				ResourceFactory.createResource(typeUrl));
		ResultSet resultSet = endpoint.select(builder);
		while (resultSet.hasNext()) {
			instances.add(resultSet.next().getResource("s"));
		}
		return instances;
	}

	private void sliceByType(String typeUrl, int pathLength) {
		Collection<Resource> instances = getInstancesOfType(typeUrl);

		for (Resource resource : instances) {
			ConstructBuilder builder = getConstructBuilder(resource, pathLength);

			// TODO
			System.out.println(builder);
		}

	}

	private ConstructBuilder getConstructBuilder(Resource resource, int pathLength) {
		ConstructBuilder constructBuilder = new ConstructBuilder();
		int sourceLength = 0;
		constructBuilder.addConstruct(resource, "?p", "?o0");
		constructBuilder.addConstruct("?s0", "?p", resource);
		constructBuilder.addWhere(resource, "?p", "?o0");
		constructBuilder.addWhere("?s0", "?p", resource);
		if (sourceLength <= pathLength) {
			addToConstructBuilder(constructBuilder, true, sourceLength, pathLength);
			addToConstructBuilder(constructBuilder, false, sourceLength, pathLength);
		}
		return constructBuilder;
	}

	private void addToConstructBuilder(ConstructBuilder constructBuilder, boolean isSubject, int sourceLength,
			int pathLength) {
		String sourceVar = isSubject ? "?s" + sourceLength : "?o" + sourceLength;
		constructBuilder.addConstruct(sourceVar, "?p", "?o" + (sourceLength + 1));
		constructBuilder.addConstruct("?s" + (sourceLength + 1), "?p", sourceVar);
		constructBuilder.addWhere(sourceVar, "?p", "?o" + (sourceLength + 1));
		constructBuilder.addWhere("?s" + (sourceLength + 1), "?p", sourceVar);
		if (sourceLength <= pathLength) {
			addToConstructBuilder(constructBuilder, true, sourceLength + 1, pathLength);
			addToConstructBuilder(constructBuilder, false, sourceLength + 1, pathLength);
		}
	}
}