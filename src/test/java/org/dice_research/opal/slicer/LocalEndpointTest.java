package org.dice_research.opal.slicer;

import static org.junit.Assert.assertTrue;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.ResultSet;
import org.junit.Test;

public class LocalEndpointTest {

	/**
	 * Tests, if local endpoint has at least one triple.
	 */
	@Test
	public void testSelect() {
		Endpoint connection = new Endpoint().setEndpoint(Configuration.localEndpointVbb);
		String queryString = "SELECT DISTINCT ?s WHERE { ?s ?p ?o } LIMIT 1";
		ResultSet resultSet = connection.select(queryString);
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

				.addWhere("?s", "?p", "?o")

				.setLimit(1);

		Endpoint connection = new Endpoint().setEndpoint(Configuration.localEndpointVbb);
		ResultSet resultSet = connection.select(builder.build());
		assertTrue(resultSet.hasNext());
	}

}