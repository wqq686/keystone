package com.keystone.support.cache.store;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StoreCache<K, V> {
	
	public static final long NEVER_TIMEOUT = -1L ;
	
	static long storePeriod = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES) ;
	
	static Lock storeLock = new ReentrantLock() ;
	
	static ConcurrentMap<String, StoreCache<?, ?>> register = new ConcurrentHashMap<>() ;
	
	static Thread storeWorker ;
	
	String name;
	
	String storepath ;
	
	boolean recover = true ;
	
	long timeout = NEVER_TIMEOUT ;
			
	TimeUnit unit = null ;	
	
	ConcurrentMap<K, CacheEntry<K, V>> memories = new ConcurrentHashMap<>() ;
	
	AtomicBoolean recovering = new AtomicBoolean(false) ;
	
	CachePersistor cachePersistor = new JDKCachePersistor() ;
	
	static { init() ; } 
	
	
	public StoreCache(String name, String storepath) {
		this(name, storepath, true, null, NEVER_TIMEOUT, TimeUnit.MINUTES);
	}

	
	/**
	 * 
	 * @param name
	 * @param storepath
	 * @param cacheTimeout -1则为永不过期
	 * @param unit
	 */
	public StoreCache(String name, String storepath, boolean recover, CachePersistor cachePersistor, long cacheTimeout, TimeUnit unit) {
		this.name = name;
		this.storepath = storepath ;
		if(timeout>0) {
			this.timeout = TimeUnit.MILLISECONDS.convert(timeout, unit) ;
		}
		this.recover = recover ;
		if(cachePersistor!=null) {
			this.cachePersistor = cachePersistor ;
		}
		if(register.putIfAbsent(name, this)!=null) throw new IllegalStateException("StoreCache[name="+name+"] is exist.") ;
		recoverCache(this);
	}

	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	public V put(K k, V v) {
		if ((k == null) || (v == null)) {
			throw new NullPointerException("k=" + k + " or v=" + v + " is NULL.");
		}
		
		V old = null;
		CacheEntry<K, V> e = memories.get(k);
		if (e == null) {
			e = new CacheEntry<>() ;
			memories.put(k, e);
		} else {
			old = e.value;
		}
		e.key = k;
		e.value = v;
		e.borntime = System.currentTimeMillis();
		return old;
	}
	
	
	
	/**
	 * 
	 * @param k
	 * @return
	 */
	public V get(Object k) {
		V v = null;
		CacheEntry<K, V> entry = getEntry(k) ;
		if(entry!=null) {
			entry.borntime = System.currentTimeMillis() ;
			v = entry.value ;
		}
		return v;
	}

	
	/**
	 * 
	 * @param k
	 * @return
	 */
	public V remove(Object k) {
		CacheEntry<K, V> e = memories.remove(k);
		V v = e == null ? null : e.value;
		return v;
	}

	
	/**
	 * 
	 * @param c
	 */
	public void remove(Collection<K> c) {
		if(c!=null && !c.isEmpty()) {
			for(K k : c) {
				remove(k) ;
			}
		}
	}

	
	/**
	 * 
	 */
	public void clear() {
		remove(keySet());
	}

	/**
	 * 
	 * @return
	 */
	public Collection<CacheEntry<K, V>> entrySet() {
		return memories.values();
	}

	/**
	 * 
	 * @return
	 */
	public Set<K> keySet() {
		return memories.keySet();
	}

	
	

	/**
	 * 
	 * @param e
	 * @param now
	 * @return
	 */
	public boolean isTimeout(CacheEntry<K, V> e, long now) {
		return e == null ? true : e.isTimeout(now, timeout) ;
	}

	
	
	
	/**
	 * 
	 * @param k
	 * @return
	 */
	public CacheEntry<K, V> getEntry(Object k) {
		CacheEntry<K, V> e = memories.get(k);
		if(e!=null && e.isTimeout(System.currentTimeMillis(), timeout)) {
			memories.remove(k) ;
			e = null ;
		}
		return e ;
	}
	
	
	
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void putEntry(CacheEntry<K, V>  e) {
		memories.put(e.key, e);
	}
	
	
	
    public String toString() {
        Iterator<CacheEntry<K,V>> i = entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
        	CacheEntry<K,V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key   == this ? "(this Cache)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Cache)" : value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }
	
	
	public static class CacheEntry<K, V> implements Serializable {
		private static final long serialVersionUID = 5098821682258925720L;
		
		K key; V value; long borntime;

		boolean isTimeout(long now, long timeout) {
			if(timeout == -1) return false ;
			return now - borntime >= timeout ;
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}
		
		public long getBorntime() {
			return borntime;
		}

		public void setBorntime(long borntime) {
			this.borntime = borntime;
		}

		@Override
		public final String toString() {
            return key + "=" + value ;
        }
	}

	
	
	
	private static void init() {
		storeWorker = new Thread(new Runnable() {
			@Override
			public void run() {
				for(;;) 
				{
					try{
						TimeUnit.MILLISECONDS.sleep(storePeriod);
					} catch(InterruptedException ie) { break; } catch (Exception ignore) {}
					
					saveCache();
				}
			}
		}, "store-cache-thread") ;
		storeWorker.setDaemon(true);
		storeWorker.start();
	}
	
	
	
	
	/**
	 * 
	 * @param cache
	 */
	private static <K, V> void recoverCache(StoreCache<K, V> cache) {
		if(cache.recovering.compareAndSet(false, true)) 
		{
			String filepath = cache.getCacheFilePath() ;
			File file = new File(filepath) ;
    		if(file.exists()) 
    		{
				if(!cache.recover)  {
					file.delete() ;
				} else {
					try
					{
						cache.cachePersistor.recover(file, cache);
					}catch(Exception e) {
						e.printStackTrace(); 
						try{if(file.exists()) file.delete();} catch (Exception ignore) {}
					}
				}
    		}
    		cache.recovering.set(false);
        }
	}
	
	
	
	
	
	/**
	 * 
	 */
	public static void flush() {
		if(storeLock.tryLock())
		{
			try 
			{
                saveCache();
            } finally {
            	storeLock.unlock();
            }
		}
	}
	
	
	
	/**
	 * 
	 */
	private static void saveCache() {
		storeLock.lock();
        try {
        	for (String name : register.keySet()) {
    			StoreCache<?, ?> cache = register.get(name);
    			if(!cache.recovering.get())
    			{
    				cache.cachePersistor.flush(cache) ;
    			}
    		}
        } finally {
            storeLock.unlock();
        }
	}
	
	
	
	/**
	 * 
	 * @param cache
	 * @return
	 */
	public String getCacheFilePath() {
		String pathname = this.storepath;
		if (!pathname.endsWith(File.separator)) {
			pathname += File.separator;
		}
		
		File file = new File(pathname);
		if (!file.exists()) file.mkdirs();
		
		String filename = this.name ;
		if(!filename.contains(".cache")) {
			filename  += ".cache" ;
		}
		String filepath = pathname + filename ;
		return filepath ;
	}
	

	public static void main(String[] args) {
		StoreCache<String, String> cache = new StoreCache<>("test.cache.txt", "d:/", true, null, NEVER_TIMEOUT, null) ;
		cache.put("a", "aaa") ;
//		cache.clear();
		StoreCache.flush();
//		System.out.println("done");
		System.out.println(cache);
	}
}
