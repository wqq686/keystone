package com.keystone.support.common;

public class NumberUtils {

	
	
	/**
	 * 
	 * @param data
	 * @param def
	 * @return
	 */
	public static int parseInt(Object data, int def) {
		if(data!=null)
		{
			try
			{
				return (data instanceof Number) ? ((Number)data).intValue() : Integer.valueOf(String.valueOf(data)) ;
			} catch(Exception ignore){} ;
		}
		return def ;
	}
	
	
	
	/**
	 * 
	 * @param data
	 * @param def
	 * @return
	 */
	public static long parseLong(Object data, long def) {
		if(data!=null)
		{
			try
			{
				return (data instanceof Number) ? ((Number)data).intValue() : Long.valueOf(String.valueOf(data)) ;
			} catch(Exception ignore){} ;
		}
		return def ;
	}
	
	
	/**
	 * 
	 * @param data
	 * @param def
	 * @return
	 */
	public static double parseDouble(Object data, double def) {
		if(data!=null) {
			try 
			{
				return (data instanceof Number) ? ((Number)data).doubleValue() : Double.valueOf(String.valueOf(data)) ;
			} catch(Exception ignore){} ;
		}
		return def ;
	}
	
}
