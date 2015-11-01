package com.keystone.support.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author admin
 *
 */
public class TimeUtils {

	/**
	 * 取得yyyy-MM-dd HH:mm:ss格式的当前时间
	 * 
	 * @return
	 */
	public static String yyyyMMddHHmmssNow() {
		return yyyyMMddHHmmss(new Date());
	}
	
	
	
	
	/**
	 * 取得yyyy-MM-dd HH:mm:ss格式的当前时间
	 * 
	 * @return
	 */
	public static String yyyyMMddHHmmss(long timestamp) {
		return yyyyMMddHHmmss(new Date(timestamp));
	}

	
	
	/**
	 * 取得yyyy-MM-dd HH:mm:ss格式的时间
	 * 
	 * @return
	 */
	public static String yyyyMMddHHmmss(Date date) {
		return dateFormat4yyyyMMddHHmmss().format(date);
	}



	/**
	 * 将yyyy-MM-dd HH:mm:ss转换为Date
	 * 
	 * @param formatdate
	 * @return
	 */
	public static Date yyyyMMddHHmmss(String formatdate) {
		try 
		{
			return dateFormat4yyyyMMddHHmmss().parse(formatdate);
		} catch (Exception e){throw new IllegalStateException("parse["+formatdate+"] with ex.", e);}
	}
	
	public static long getMilliseconds(String unixtime) {
		return Long.valueOf(unixtime)*1000 ;
	}
	
	
	public static Date setUnixTime(Date date, String unixtime) {
		if(date==null) date = new Date() ;
		date.setTime(getMilliseconds(unixtime)) ;
		return date ;
	}
	
	
	
	public static String format(DateFormat format, Date date) {
		try{
			return format.format(date) ;
		} catch (Exception e){throw new IllegalArgumentException("format["+date+"] with ex.", e);}
	}
	
	/**
	 * 
	 * @return
	 */
	public static SimpleDateFormat dateFormatyyyyMMddHHmmss() {
		return new SimpleDateFormat("yyyyMMddHHmmss") ;
	}
	
	/**
	 * 
	 * @return
	 */
	public static SimpleDateFormat dateFormat4yyyyMMddHHmmss() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
	}
	
	
	/**
	 * 
	 * @return yyyy-MM-dd格式
	 */
	public static SimpleDateFormat dateFormat4yyyyMMdd() {
		return new SimpleDateFormat("yyyy-MM-dd") ;
	}
	
	
	/**
	 * 
	 * @return yyyyMMdd格式
	 */
	public static SimpleDateFormat yyyyMMddFormatter() {
		return new SimpleDateFormat("yyyyMMdd") ;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String yyyyMMdd(Date date) {
		return dateFormat4yyyyMMdd().format(date) ;
	}
	
	
	public static Date newDateByUnixTimestamp(long unixTimestamp) {
		return unixTimestamp==0 ? null : new Date(unixTimestamp*1000) ;
	}
	
	public static long getUnixTimestamp(Date date) {
		return date == null ? 0 : date.getTime()/1000 ;
	}
	
	public static String stringOfUnixtimestampNow() {
		return String.valueOf(System.currentTimeMillis()/1000) ;
	}
	
	public static String stringOfUnixtimestamp(Date date) {
		return String.valueOf(date.getTime()/1000) ;
	}
	
	public static String yyyyMMddChianise(Date date) {
		return new SimpleDateFormat("yyyy年MM月dd日").format(date) ;
	}
	public static void main(String[] args) {
		Date now = newDateByUnixTimestamp(1385027819) ;
		System.out.println(TimeUtils.yyyyMMdd(now));
	}
	
	public static Date newDateByUnixTimestamp(String timestamp) {
		timestamp = StringUtils.emptyOrTrim(timestamp) ;
		if(timestamp.isEmpty()) return null ;
		return newDateByUnixTimestamp(Long.valueOf(timestamp)) ;
	}
	
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateZero(Date date) {
		Calendar c = Calendar.getInstance() ;
		if(date!=null) c.setTime(date) ;
		setCalendarToZero(c) ;
		return c.getTime() ;		
	}
	
	/**
	 * 获取今天的截至时间
	 * @return
	 */
	public static Date getTodayLimit() {
		return getDateEndTime(new Date()) ;
	}
	
	
	
	/**
	 * 获取某一天的开始时间
	 * @param date
	 * @return
	 */
	public static Date getDateStartTime(Date date) {
		if(date==null) return null ;
		Calendar c = Calendar.getInstance() ;
		c.setTime(date) ;
		setCalendarToZero(c) ;
		return c.getTime() ;
	}
	
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public static void setCalendarToZero(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0) ;
		c.set(Calendar.MINUTE, 0) ;
		c.set(Calendar.SECOND, 0) ;
		c.set(Calendar.MILLISECOND, 0) ;
	}
	
	
	/**
	 * 获取某一天的截至时间
	 * @param date
	 * @return
	 */
	public static Date getDateEndTime(Date date) {
		if(date==null) return null ;
		Calendar c = Calendar.getInstance() ;
		c.setTime(date) ;
		c.add(Calendar.DAY_OF_MONTH, 1) ;
		setCalendarToZero(c) ;
		return c.getTime() ;
	}
	/**
	 * 
	 * @return
	 */
	public static Date getTodayZero() {
		return getDateZero(null) ;
	}

	
	public static Date parse(java.sql.Timestamp timestamp) {
		if(timestamp==null) return null ;
		return new Date(timestamp.getTime()*1000) ;
	}
	
	
	/**
	 * 
	 * @param birthday
	 * @return
	 */
	public static int getAge(Date birthday) {
		long day = TimeUnit.DAYS.convert(System.currentTimeMillis()-birthday.getTime(), TimeUnit.MILLISECONDS) ;
		return (int)(day/365) ;
	}



			
}
