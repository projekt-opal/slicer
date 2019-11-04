package org.dice_research.opal.slicer;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder((MethodSorters.NAME_ASCENDING))
public class DataInvestigatorTest {

	// Test configuration
	private static final String SPARQL_SOURCE_ID = Cfg.VBB_LOCAL;
	private static final int MAX_INSTANCES_SLICING = 100;

	private SparqlSource sparqlSource;
	private File typesSizesFile = new File(System.getProperty("java.io.tmpdir"),
			"DataInvestigatorTest.typesSizesFile.dat");

	@Before
	public void setUp() throws Exception {
		Cfg.createEndpoints();
		sparqlSource = SparqlSources.getInstance().get(SPARQL_SOURCE_ID);
		Assume.assumeTrue(sparqlSource.isAvailable());
	}

	@Test
	public void testAtypesNonEmpty() throws Exception {
		DataInvestigator dataInvestigator = new DataInvestigator();
		List<String> types = dataInvestigator.getTypes(sparqlSource);
		Assert.assertFalse(types.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testBtypesSizesNonEmpty() throws Exception {
		DataInvestigator dataInvestigator = new DataInvestigator();

		// Use cache, if already requested
		Map<String, Integer> typesSizes;
		if (typesSizesFile.exists()) {
			typesSizes = (Map<String, Integer>) IoUtils.deserialize(typesSizesFile);
		} else {
			typesSizes = dataInvestigator.getTypesAndSizes(sparqlSource);
			IoUtils.serialize(typesSizes, typesSizesFile);
		}
		Assert.assertFalse(typesSizes.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testClimit() throws Exception {
		Assume.assumeTrue(typesSizesFile.canRead());
		Map<String, Integer> typesSizes = (Map<String, Integer>) IoUtils.deserialize(typesSizesFile);

		DataInvestigator dataInvestigator = new DataInvestigator();
		Map<String, Integer> candidates = dataInvestigator.filterCandidates(typesSizes, MAX_INSTANCES_SLICING);
		Assert.assertFalse(candidates.isEmpty());
		Assert.assertTrue(candidates.size() < typesSizes.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDinstances() throws Exception {
		Assume.assumeTrue(typesSizesFile.canRead());
		Map<String, Integer> typesSizes = (Map<String, Integer>) IoUtils.deserialize(typesSizesFile);

		DataInvestigator dataInvestigator = new DataInvestigator();
		Map<String, Integer> candidates = dataInvestigator.filterCandidates(typesSizes, MAX_INSTANCES_SLICING);

		for (String typeUri : candidates.keySet()) {
			List<String> instances = dataInvestigator.getInstances(sparqlSource, typeUri);
			Assert.assertFalse(instances.isEmpty());
		}
	}

}