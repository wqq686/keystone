package com.keystone.support.common;

import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * 
 * @author wuqq
 *
 */
public class StringUtils {

	/**
	 * 
	 */
	public static final String UTF8 = "UTF-8" ;
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input) {
		return input==null || input.trim().isEmpty() ;
	}
	
	
	
	/**
	 * 
	 * @param input 如果为null, 则返回空字符串, 否则调用trim()
	 * @return
	 */
	public static String emptyOrTrim(String input) {
		return input==null ? "" : input.trim() ;
	}
	
	
	/**
	 * 如果是空串(null or empty), 返回def, 否则返回input.trim()
	 * @param input
	 * @param def
	 * @return
	 */
	public static String isEmpty(String input, String def) {
		input = emptyOrTrim(input) ;
		return input.isEmpty() ? def : input ;
	}
	
	
	
	
	/**
	 * 
	 * @param data
	 * @param def
	 * @return
	 */
	public static String toString(Object data, String def) {
		return data == null ? def : isEmpty(data.toString(), def) ;
		
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String urlDecodeUTF8(String input) {
		try
		{
			return input==null ? null : URLDecoder.decode(input, UTF8) ;
		}catch(Exception e){throw ExceptionUtils.illegalStateException(e);}
	}
	
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String urlEncodeUTF8(String input) {
		try
		{
			return input==null ? null : URLEncoder.encode(input, UTF8) ;
		}catch(Exception e){throw ExceptionUtils.illegalStateException(e);}
	}

}
