package com.keystone.support.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * .properties文件的帮助类
 * @author wuqq
 *
 */
public class PropertiesUtils {

	
	/**
	 * 根据文件名进行加载
	 * 
	 * @param filename 可以不加上.properties, 内部会根据filename自动进行判断
	 * @return HashMap
	 */
	public static Map<String, String> load(String filename) {
		if(!filename.endsWith("properties")) filename += ".properties" ;
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		return load(is) ;
	}
	
	
	
	
	/**
	 * 根据文件进行加载
	 * 
	 * @param properties
	 * @return HashMap
	 */
	public static Map<String, String> load(File properties) {
		try
		{
			return load(new FileInputStream(properties)) ;
		} catch(Exception e){throw new IllegalStateException(e);}
	}
	
	
	/**
	 * 根据io输入流进行加载
	 * 
	 * @param is
	 * @return HashMap
	 */
	public static Map<String, String> load(InputStream is) {
		try
		{
			Properties prop=new Properties();
			prop.load(new InputStreamReader(is, "UTF-8"));
			Map<String, String> map = new HashMap<String, String>(prop.size(), 1.0f) ;
			for(Object k : prop.keySet())
			{
				Object v = prop.get(k) ;
				String key = String.valueOf(k), value = String.valueOf(v) ;
				map.put(key, value) ;
			}
			return map ;
		} catch(Exception e){throw new IllegalStateException(e);}
		finally{if(is!=null) try{is.close();}catch (Exception ignore) {}}
	}
	
}
