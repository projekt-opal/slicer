package org.rdfslice.sqlite.cache;

import org.junit.Assert;
import org.junit.Test;
import org.rdfslice.model.Triple;

public class SQLiteCacheTest {
	
	@Test
	public void testCache() {
		SQLiteCache cache = new SQLiteCache();
		
		Triple triple1 = new Triple();
		
		triple1.setSubject("A");
		triple1.setPredicate("B");
		triple1.setObject("C");
		
		cache.put(triple1, 1, 1, true);
		
		Triple triple2 = new Triple();
		
		triple2.setSubject("A");
		triple2.setPredicate("B");
		triple2.setObject("C");
		
		boolean value = cache.get(triple2, 1, 1);
		
		Assert.assertEquals(true, value);
		
		Boolean value2 = cache.get(triple2, 2, 0);
		
		Assert.assertEquals(null, value2);
		
		cache.put(triple1, 2, 2, true);
		
		value = cache.get(triple1, 2, 2);
		
		Assert.assertEquals(true, value);
		
		value2 = cache.get(triple2, 2, 0);
		
		Assert.assertEquals(null, value2);
	}
	
	public void testCacheBig() {
		SQLiteCache cache = new SQLiteCache();
		
		for(int i=0 ; i< 100000; i++) {
			Triple triple1 = new Triple();
			
			triple1.setSubject("A");
			triple1.setPredicate("B");
			triple1.setObject("C");
		}
	}
}
