package com.keystone.support.cache.store;

import java.io.File;

public interface CachePersistor {

	
	
	/**
	 * 
	 * @param file
	 * @param cache
	 */
	<K, V> void recover(File file, StoreCache<K, V> cache) ;
	
	/**
	 * 
	 * @param cache
	 */
	File flush(StoreCache<?, ?> cache) ;
	
}
