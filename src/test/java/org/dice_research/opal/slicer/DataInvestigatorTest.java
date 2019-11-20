package org.dice_research.opal.slicer;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder((MethodSorters.NAME_ASCENDING))
public class DataInvestigatorTest {

	// Test configuration
	private static final String SPARQL_SOURCE_ID = Cfg.OPAL_LOCAL;
	private static final int MAX_INSTANCES_SLICING = 100;

	private static final String KEY_TYPES_SIZES = "types-sizes";
	private static final Logger LOGGER = LogManager.getLogger();
	private SparqlSource sparqlSource;

	@Before
	public void setUp() throws Exception {
		Cfg.createEndpoints();
		sparqlSource = SparqlSources.getInstance().get(SPARQL_SOURCE_ID);
		Assume.assumeTrue(sparqlSource.isAvailable());
	}

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

	@SuppressWarnings("unchecked")
	@Test
	public void testBtypesSizesNonEmpty() throws Exception {
		Map<String, Integer> typesSizes;
		if (IoUtils.fileForKeyExists(KEY_TYPES_SIZES, false)) {
			typesSizes = (Map<String, Integer>) IoUtils.deserialize(KEY_TYPES_SIZES, false);
		} else {
			long time = System.currentTimeMillis();
			typesSizes = new DataInvestigator().getTypesAndSizes(sparqlSource);
			LOGGER.info("Secs: " + (System.currentTimeMillis() - time) / 1000);
			IoUtils.serialize(typesSizes, KEY_TYPES_SIZES, false);
		}
		LOGGER.info("Types/sizes: " + typesSizes.toString());
		Assert.assertFalse(typesSizes.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testClimit() throws Exception {
		Map<String, Integer> typesSizes = (Map<String, Integer>) IoUtils.deserialize(KEY_TYPES_SIZES, false);

		Map<String, Integer> candidates = new DataInvestigator().filterCandidates(typesSizes, MAX_INSTANCES_SLICING);
		LOGGER.info("Candidates: " + candidates.toString());
		Assert.assertFalse(candidates.isEmpty());
		Assert.assertTrue(candidates.size() < typesSizes.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDinstances() throws Exception {
		Map<String, Integer> typesSizes = (Map<String, Integer>) IoUtils.deserialize(KEY_TYPES_SIZES, false);

		DataInvestigator dataInvestigator = new DataInvestigator();
		Map<String, Integer> candidates = dataInvestigator.filterCandidates(typesSizes, MAX_INSTANCES_SLICING);

		for (String typeUri : candidates.keySet()) {
			List<String> instances = dataInvestigator.getInstances(sparqlSource, typeUri);
			LOGGER.info("Type: " + typeUri + ", instances: " + instances.toString());
			Assert.assertFalse(instances.isEmpty());
		}
	}

}