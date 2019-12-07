package org.dice_research.opal.slicer.investigation;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Tests {@link DataInvestigator}.
 * 
 * Simple queries.
 *
 * @author Adrian Wilke
 */
@FixMethodOrder((MethodSorters.NAME_ASCENDING))
public class DataInvestigatorTest {

	// Test configuration
	private static final String SPARQL_SOURCE_ID = Cfg.OPAL_LOCAL;

	private static final Logger LOGGER = LogManager.getLogger();
	private SparqlSource sparqlSource;

	@Before
	public void setUp() throws Exception {
		Cfg.createEndpoints();
		sparqlSource = SparqlSources.getInstance().get(SPARQL_SOURCE_ID);
		Assume.assumeTrue(sparqlSource.isAvailable());
	}

	/**
	 * Tests {@link DataInvestigator#getTypes(SparqlSource)}
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAtypesNonEmpty() throws Exception {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> types;
		if (IoUtils.fileForKeyExists(methodName, false)) {
			types = (List<String>) IoUtils.deserialize(methodName, false);
		} else {
			long time = System.currentTimeMillis();
			types = new DataInvestigator().getTypes(sparqlSource);
			LOGGER.info("Secs: " + (System.currentTimeMillis() - time) / 1000);
			IoUtils.serialize(types, methodName, false);
		}
		LOGGER.info("Types: " + types.toString());
		Assert.assertFalse(types.isEmpty());
	}

}