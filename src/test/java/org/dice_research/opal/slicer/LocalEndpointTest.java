package org.dice_research.opal.slicer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.ResultSet;
import org.junit.Before;
import org.junit.Test;

public class LocalEndpointTest {

	private Endpoint endpoint;

	@Before
	public void setUp() throws Exception {
		Cfg.createEndpoints();
		endpoint = Endpoints.getInstance().get(Cfg.OPAL);
		assumeTrue(endpoint.isAvailable());
	}

	/**
	 * Tests, if local endpoint has at least one triple.
	 */
	@Test
	public void testSelect() {
		String queryString = "SELECT DISTINCT ?s WHERE { GRAPH ?g { ?s ?p ?o } } LIMIT 1";
		ResultSet resultSet = endpoint.select(queryString);
		assertTrue(resultSet.hasNext());
	}

	/**
	 * Tests, if local endpoint has at least one triple. Uses SelectBuilder.
	 */
	@Test
	public void testSelectBuilder() {
		SelectBuilder builder = new SelectBuilder()

				.setDistinct(true)

				.addVar("?s")

				.addGraph("?g", "?s", "?p", "?o")

				.setLimit(1);

		ResultSet resultSet = endpoint.select(builder.build());
		assertTrue(resultSet.hasNext());
	}

}