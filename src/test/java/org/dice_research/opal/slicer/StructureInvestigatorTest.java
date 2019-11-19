package org.dice_research.opal.slicer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	private static final boolean DELETE_FILE_ON_EXIT = true;

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

		// Get types
		List<String> types = dataInvestigator.getTypes(sparqlSource);

		// For every type: Get predicates and object-types
		StringBuilder stringBuilder = new StringBuilder();
		boolean predicateFound = false;
		Map<String, List<String>> predicates = null;
		for (String typeUri : types) {
			predicates = dataInvestigator.getPredicates(sparqlSource, typeUri, "LITERAL");
			for (String predicate : predicates.keySet()) {
				for (String objectType : predicates.get(predicate)) {
					stringBuilder.append(typeUri);
					stringBuilder.append(System.lineSeparator());
					stringBuilder.append(predicate);
					stringBuilder.append(System.lineSeparator());
					stringBuilder.append(objectType);
					stringBuilder.append(System.lineSeparator());
				}
				LOGGER.info("Type " + typeUri + " pred " + predicate + " types " + predicates.get(predicate));
				predicateFound = true;
			}
		}
		LOGGER.info("Predicates: " + predicates.size());
		Assert.assertTrue("At least one predicate found", predicateFound);

		// Write results to file
		File file = File.createTempFile(StructureInvestigatorTest.class.getName(), ".txt");
		LOGGER.info("File: " + file.getAbsolutePath());
		if (DELETE_FILE_ON_EXIT) {
			file.deleteOnExit();
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(stringBuilder.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}

}
