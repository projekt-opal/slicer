package org.rdfslice.structures;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class MaxSizeHashMapTest {
	
	@Test
	public void testMaxSize() {
		MaxSizeHashMap<String, String> maxMap = new MaxSizeHashMap<String, String>(2);
		
		maxMap.put("1", "value1");
		maxMap.put("2", "value2");
		maxMap.put("3", "value3");
		
		Assert.assertEquals(maxMap.size(), 2);
		
		Set<String> keySet = maxMap.keySet();
		
		Assert.assertTrue(keySet.contains("2"));
		Assert.assertTrue(keySet.contains("3"));
		Assert.assertFalse(keySet.contains("1"));		
	}
}
