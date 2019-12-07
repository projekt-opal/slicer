package org.dice_research.opal.slicer.investigation;

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
/**
 * Tests {@link DataInvestigator}.
 * 
 * Advanced queries.
 *
 * @author Adrian Wilke
 */
@FixMethodOrder((MethodSorters.NAME_ASCENDING))
public class DataInvestigatorTestB {

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

	/**
	 * Tests {@link DataInvestigator#getTypesAndSizes(SparqlSource)}
	 */
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

	/**
	 * Tests {@link DataInvestigator#filterCandidates(Map, int)}
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testClimit() throws Exception {
		Assume.assumeTrue(IoUtils.fileForKeyExists(KEY_TYPES_SIZES, false));
		Map<String, Integer> typesSizes = (Map<String, Integer>) IoUtils.deserialize(KEY_TYPES_SIZES, false);

		Map<String, Integer> candidates = new DataInvestigator().filterCandidates(typesSizes, MAX_INSTANCES_SLICING);
		LOGGER.info("Candidates: " + candidates.toString());
		Assert.assertFalse(candidates.isEmpty());
		Assert.assertTrue(candidates.size() < typesSizes.size());
	}

	/**
	 * Tests:
	 * 
	 * {@link DataInvestigator#filterCandidates(Map, int)}
	 * 
	 * {@link DataInvestigator#getInstances(SparqlSource, String)}
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDinstances() throws Exception {
		Assume.assumeTrue(IoUtils.fileForKeyExists(KEY_TYPES_SIZES, false));
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