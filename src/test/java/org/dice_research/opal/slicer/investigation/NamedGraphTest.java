package org.dice_research.opal.slicer.investigation;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.ResultSet;
import org.dice_research.opal.slicer.investigation.SparqlSource;
import org.dice_research.opal.slicer.investigation.SparqlSources;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Checks, if the use of named graph and default graph works.
 * 
 * Named graphs can be listed using {@link SparqlSource#getNamedGraphs()}.
 *
 * The used endpoints are configured in {@link #setUp()}.
 *
 * @author Adrian Wilke
 */
public class NamedGraphTest {

	private SparqlSource endpointDefaultGraph;
	private SparqlSource endpointNamedGraph;

	@Before
	public void setUp() throws Exception {
		Cfg.createEndpoints();

		// An end-point using named graphs
		endpointNamedGraph = SparqlSources.getInstance().get(Cfg.OPAL);

		// An end-point using the default graph
		endpointDefaultGraph = SparqlSources.getInstance().get(Cfg.OPAL_LOCAL);
	}

	@Test
	public void testNamedGraph() {
		// To perform test, the endpoint has to be reachable
		Assume.assumeTrue(endpointNamedGraph.isAvailable());

		ResultSet resultSet = endpointNamedGraph.select(getSimpleSelectBuilder());
		Assert.assertFalse(endpointNamedGraph.getNamedGraphs().isEmpty());
		Assert.assertTrue(resultSet.hasNext());
	}

	@Test
	public void testDefaultGraph() {
		// To perform test, the endpoint has to be reachable
		Assume.assumeTrue(endpointDefaultGraph.isAvailable());

		ResultSet resultSet = endpointNamedGraph.select(getSimpleSelectBuilder());
		Assert.assertFalse(endpointNamedGraph.getNamedGraphs().isEmpty());
		Assert.assertTrue(resultSet.hasNext());
	}

	private SelectBuilder getSimpleSelectBuilder() {
		SelectBuilder builder = new SelectBuilder()

				.setDistinct(true)

				.addVar("?s").addWhere("?s", "?p", "?o")

				.setLimit(1);
		return builder;
	}

}