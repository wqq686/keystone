package com.keystone.support.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public static RuntimeException convertRuntimeException(Exception e) {
		return new RuntimeException(e) ;
	}
	
	
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public static IllegalStateException illegalStateException(Throwable t) {
		return new IllegalStateException(t) ;
	}
	
	
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public static IllegalStateException illegalStateException(String message) {
		return new IllegalStateException(message) ;
	}
	
	
	
	/**
	 * 
	 * @param message
	 * @param t
	 * @return
	 */
	public static IllegalStateException illegalStateException(String message, Throwable t) {
		return new IllegalStateException(message, t) ;
	}
	
	
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public static IllegalArgumentException illegalArgumentException(String message) {
		return new IllegalArgumentException(message) ;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public static UnsupportedOperationException unsupportedMethodException(){
		return new UnsupportedOperationException("unsupport this method");
	}
	
	
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public static RuntimeException convertRuntimeException(Throwable t) {
		return (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t) ;
	}
	
	
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public static Throwable foundRealThrowable(Throwable t) {
		Throwable cause = t.getCause() ;
		if(cause==null) return t ;
		return foundRealThrowable(cause) ;
	}
	
	
	
	/**
	 * 格式化异常
	 * @param t
	 * @return
	 */
	public static String formatThrowable(Throwable t) {
		if (t == null) return "";
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}
	
	
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public static String formatThrowableForHtml(Throwable t) {
		String ex = formatThrowable(t) ;
		return ex.replaceAll("\n\t", " ") ;
	}
	
	
	
	/**
	 * 从String的异常堆栈中提取出message
	 * 
	 * @param cause
	 * @return
	 */
	public static String foundMessage(String cause) {
		if(cause!=null)
		{
			int begin = cause.indexOf(":"), end = cause.indexOf("\n\t") ;
			if(end>begin+1)
			{
				return cause.substring(begin+2, end) ;//:+blank
			}
		}
		return null ;
	}
	
	/**
	 * 通过stdout、stderr全量输出错误日志
	 * @param prefix
	 * @param t
	 */
	public static void fullError(String prefix, Throwable t) {
		fullError(prefix, t, null);
	}
	
	
	/**
	 * 通过stdout、stderr全量输出错误日志
	 * @param prefix
	 * @param t
	 * @param tail
	 */
	public static void fullError(String prefix, Throwable t, String tail) {
		String message = prefix == null ? "" : prefix ;
		if(t!=null) {
			message += formatThrowable(t) ;
		}
		
		if(tail!=null) {
			message += tail ;
		}
		
		System.out.println(message);
		System.err.println(message);
	}
}
