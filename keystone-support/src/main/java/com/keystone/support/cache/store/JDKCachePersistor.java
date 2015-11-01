package com.keystone.support.cache.store;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.keystone.support.cache.store.StoreCache.CacheEntry;
import com.keystone.support.common.ExceptionUtils;

public class JDKCachePersistor implements CachePersistor {

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <K, V> void recover(File file, StoreCache<K, V> cache) {
		FileInputStream fis = null; ObjectInputStream ois = null;
    	try
    	{
    		fis = new FileInputStream(file); ois = new ObjectInputStream(fis); long now = System.currentTimeMillis() ;
			while(true) 
			{
				try
				{
					CacheEntry<K,V> e = (CacheEntry<K,V>) ois.readObject() ;
					if( !cache.isTimeout(e, now) ) {
						cache.putEntry(e);
					}
				}catch(EOFException ignore) { break ; }
			}
		} 
    	catch(Exception e) { throw ExceptionUtils.convertRuntimeException(e) ; } 
    	finally {
			if (ois != null) { try { ois.close(); } catch (IOException ignore) {} }
			if (fis != null) { try { fis.close(); } catch (IOException ignore) {} }
		}
	}
	
	/**
	 * 
	 */
	@Override
	public File flush(StoreCache<?, ?> cache) {
		String filepath = cache.getCacheFilePath() ;
		File file = new File(filepath); FileOutputStream fos = null; ObjectOutputStream oos = null;
		try 
		{
			file = new File(filepath);
			if(!file.exists()) {
				file.createNewFile() ;
			}
			fos = new FileOutputStream(filepath); oos = new ObjectOutputStream(fos);
			for(Object k : cache.keySet())
			{
				CacheEntry<?, ?> e = cache.getEntry(k) ;
				if(e!=null) {
					oos.writeObject(e);
				}
			}
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oos != null) { try { oos.close(); } catch (IOException ignore) {} }
			if (fos != null) { try { fos.close(); } catch (IOException ignore) {} }
		}
		return file ;
	}

}
