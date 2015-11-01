package com.keystone.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.keystone.support.cache.store.CachePersistor;
import com.keystone.support.cache.store.StoreCache;
import com.keystone.support.cache.store.StoreCache.CacheEntry;
import com.keystone.support.common.ExceptionUtils;

public class JSONCachePersistor implements CachePersistor {

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <K, V> void recover(File file, StoreCache<K, V> cache) {
		BufferedReader reader = null ;
		try
    	{
    		reader = new BufferedReader(new FileReader(file)) ; String line = null ; long now = System.currentTimeMillis() ;
    		while((line=reader.readLine())!=null)
    		{
    			CacheEntry<K,V> e = JSON.parseObject(line, CacheEntry.class) ;
				if( !cache.isTimeout(e, now) ) {
					cache.putEntry(e);
				}
    		}
		} catch(Exception e) {
    		throw ExceptionUtils.convertRuntimeException(e) ;
		} finally {
			if (reader != null) { try { reader.close(); } catch (IOException ignore) {} }
		}
	}

	
	
	/**
	 * 
	 */
	@Override
	public File flush(StoreCache<?, ?> cache) {
		String filepath = cache.getCacheFilePath() ;
		File file = new File(filepath); BufferedWriter writer = null ;
		try 
		{
			file = new File(filepath);
			if(!file.exists()) {
				file.createNewFile() ;
			}
			writer = new BufferedWriter(new FileWriter(file)) ;
			for(Object k : cache.keySet())
			{
				CacheEntry<?, ?> e = cache.getEntry(k) ;
				if(e!=null) {
//					JSON.toJSONString(e, SerializerFeature.WriteClassName) ;
					String line = JSON.toJSONString(e) ;
					writer.write(line);
				}
			}
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) { try { writer.close(); } catch (IOException ignore) {} }
		}
		return file ;
	}
	
	
	

	public static void main(String[] args) {
		StoreCache<String, String> cache = new StoreCache<>("test.cache.txt", "d:/", true, new JSONCachePersistor(), StoreCache.NEVER_TIMEOUT, null) ;
		cache.put("a", "aaa") ;
//		cache.clear();
		StoreCache.flush();
//		System.out.println("done");
		System.out.println(cache);
	}
}
