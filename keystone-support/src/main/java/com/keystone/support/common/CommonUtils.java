package com.keystone.support.common;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * 
 * @author wuqq
 *
 */
public class CommonUtils {
	
	/**
	 * 
	 */
	private static Boolean isWindowsOS = null ;
	
	/**
	 * 
	 */
	public static final List<?> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<Object>());
	
	/**
	 * 
	 */
	public static final Map<?,?> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<Object, Object>());
	
	
	
	
	/**
	 * 是否是windows操作系统
	 * 
	 * @return
	 */
	public static boolean isWindowsOS() {
		if(isWindowsOS==null)
		{
			isWindowsOS = (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) ;
		}
		return isWindowsOS.booleanValue() ;
	}

	
	/**
	 * 
	 * @return
	 */
	public static String getOSVersion() {
		Properties props = System.getProperties();
		return props.getProperty("os.name") + " " + props.getProperty("os.version") ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static String getJvmVersion() {
		return System.getProperty("java.specification.version") ;
	}
	
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input) {
		return null == input || input.trim().isEmpty() ;
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(Object[] array) {
		return array==null || array.length==0 ;
	}
	
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isEmpty(Collection<?> c) {
		return c==null || c.isEmpty() ;
	}
	
	
    /**
     * 
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?,?> map) {
    	return map==null || map.isEmpty() ;
    }

	/**
	 * 
	 * @param c
	 * @return
	 */
	public static int size(Collection<?> c) {
		return c==null ? 0 : c.size() ;
	}
	
	
	/**
	 * 
	 * @return
	 */
    @SuppressWarnings("unchecked")
	public static <T> List<T> emptyList() {
    	return (List<T>) EMPTY_LIST;
    }
    
    
    /**
     * 获取一个空List
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> List<T> emptyList(List<T> list) {
    	return (List<T>) (list==null ? emptyList() : list) ;
    }
    
    
    /**
     * 
     * @param c
     * @return
     */
	public static <T> List<T> newArrayList(Collection<?> c) {
		return new ArrayList<>(size(c));
	}
	

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <K,V> Map<K,V> emptyMap() {
    	return (Map<K,V>) EMPTY_MAP;
    }

    /**
     * 
     * @param map
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <K,V> Map<K,V> emptyMap(Map<K,V> map) {
    	return (Map<K, V>) (map == null ? EMPTY_MAP : map) ;
    }
    
    
    /**
     * 
     * @param size
     * @return
     */
	public static <K,V> Map<K,V> stableMap(int size) {
    	return new HashMap<K, V>(size, 1.0f) ;
    }
    
  

	
	/**
	 * 
	 * @param map
	 * @param key
	 * @param value
	 */
	public static  <K,V> void putIfNotNull(Map<K,V>  map, K key, V value) {
		if(map!=null && key!=null && value !=null) map.put(key, value) ;
	}

	
	/**
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Map<?, ?> map, Object key) {
		return (T) (map==null ? null : map.get(key)) ;
	}
	
	
	
	/**
	 * 
	 * @param map
	 * @param key
	 * @param def
	 * @return
	 */
	public static <T> T get(Map<?, ?> map, Object key, T def) {
		T t = get(map, key) ;
		return t == null ? def : t ;
	}
	
	

	
	
	
	
	
	
	
	
	/**
	 * 实例化对象,注意该对象必须有无参构造函数
	 * 
	 * @param klass
	 * @return
	 */
	public static <T> T newInstance(Class<T> klass) {
		try {
			return (T) klass.newInstance();
		} catch (Exception e){ throw new IllegalArgumentException("instance class[" + klass.getName() + "] with ex:", e); }
	}

	
	/**
	 * 
	 * @param klass
	 * @param cstTypes
	 * @param cstParameters
	 * @return
	 */
	public static <T> T newInstance(Class<T> klass, Class<?>[] cstTypes, Object... cstParameters) {
		try {
			Constructor<T> cst = klass.getConstructor(cstTypes) ;
			return cst.newInstance(cstParameters) ;
		} catch (Exception e){ throw new IllegalArgumentException("instance class[" + klass.getName() + "], cstTypes="+Arrays.toString(cstTypes)+", "+Arrays.toString(cstParameters)+" with ex:", e); }
	}
	
	
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className) {
		try {
			return (T) newInstance(classForName(className)) ;
		} catch (Exception e){ throw new IllegalArgumentException("instance class[" + className + "] with ex:", e); }
	}
	
	
	
	
	/**
	 * 优先通过Thread ContextClassLoader查找Class
	 * 
	 * @param className
	 * @return
	 */
	public static Class<?> classForName(String className) {
		try 
		{
			return Class.forName(className, false, Thread.currentThread().getContextClassLoader()) ;
			
		} catch(Exception ignore) {
			try 
			{
				return Class.forName(className) ;
			} catch (Exception e){ throw new IllegalArgumentException("classForName(" + className + ")  with ex:", e); }
		}
	}

	
	
	
	/**
	 * 
	 * @param className
	 * @param classLoader
	 * @return
	 */
	public static Class<?> loadClass(String className, ClassLoader classLoader) {
		try {
			return classLoader.loadClass(className) ;
		} catch (Exception e){ throw new IllegalArgumentException("loadClass(" + className + ")  with ex:", e); }
	}
	

}
