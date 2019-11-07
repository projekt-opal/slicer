package org.dice_research.opal.slicer;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class StructureInvestigatorTest {

	// Test configuration
	private static final String SPARQL_SOURCE_ID = Cfg.VBB_LOCAL;

	private static final Logger LOGGER = LogManager.getLogger();
	private SparqlSource sparqlSource;

	@Before
	public void setUp() throws Exception {
		Cfg.createEndpoints();
		sparqlSource = SparqlSources.getInstance().get(SPARQL_SOURCE_ID);
		Assume.assumeTrue(sparqlSource.isAvailable());
	}

	@Test
	public void test() throws Exception {
		DataInvestigator dataInvestigator = new DataInvestigator();
		List<String> types = dataInvestigator.getTypes(sparqlSource);

		boolean predicateFound = false;
		for (String typeUri : types) {
			Map<String, List<String>> predicates = dataInvestigator.getPredicates(sparqlSource, typeUri);
			for (String pUri : predicates.keySet()) {
				LOGGER.info("Type " + typeUri + " pred " + pUri + " types " + predicates.get(pUri));
				predicateFound = true;
			}
		}

		Assert.assertTrue("At least one predicate found", predicateFound);
	}

}
