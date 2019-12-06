package org.dice_research.opal.slicer.investigation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.arq.querybuilder.ConstructBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.opal.slicer.investigation.SparqlSource;
import org.dice_research.opal.slicer.investigation.SparqlSources;

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
		sliceByType(typeUrl, 3);
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

	private void sliceByType(String typeUrl, int maxDepth) {
		Collection<Resource> instances = getInstancesOfType(typeUrl);

		for (Resource resource : instances) {
			ConstructBuilder builder = getConstructBuilder(resource, maxDepth, false);

			// TODO
			System.out.println(builder);

			Model m = endpoint.construct(builder);
			System.out.println(m.size());
		}

	}

	private ConstructBuilder getConstructBuilder(Resource resource, int maxDepth, boolean asObject) {
		ConstructBuilder constructBuilder = new ConstructBuilder();
		String source = "?x";

		// Adding depth 1
		String sub = source + "s";
		String subPred = sub + "p";
		if (asObject) {
			constructBuilder.addConstruct(sub, subPred, resource);
			constructBuilder.addOptional(sub, subPred, resource);
		}
		String obj = source + "o";
		String objPred = obj + "p";
		constructBuilder.addConstruct(resource, objPred, obj);
		constructBuilder.addOptional(resource, objPred, obj);
		if (maxDepth > 1) {
			if (asObject) {
				addToConstructBuilder(constructBuilder, sub, 2, maxDepth, asObject);
			}
			addToConstructBuilder(constructBuilder, obj, 2, maxDepth, asObject);
		}

		return constructBuilder;
	}

	private void addToConstructBuilder(ConstructBuilder constructBuilder, String source, int depth, int maxDepth,
			boolean asObject) {

		String sub = source + "s";
		String subPred = sub + "p";
		if (asObject) {
			constructBuilder.addConstruct(sub, subPred, source);
			constructBuilder.addOptional(sub, subPred, source);
		}
		String obj = source + "o";
		String objPred = obj + "p";
		constructBuilder.addConstruct(source, objPred, obj);
		constructBuilder.addOptional(source, objPred, obj);
		if (depth < maxDepth) {
			if (asObject) {
				addToConstructBuilder(constructBuilder, sub, depth + 1, maxDepth, asObject);
			}
			addToConstructBuilder(constructBuilder, obj, depth + 1, maxDepth, asObject);
		}
	}
}