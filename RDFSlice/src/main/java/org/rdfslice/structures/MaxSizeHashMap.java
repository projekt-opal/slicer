package org.rdfslice.structures;

import java.util.LinkedHashMap;
import java.util.Map;

public class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7908963507696361591L;
	private final long maxSize;

    public MaxSizeHashMap(long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
    
    public long getMaxSize() {
    	return maxSize;
    }
}