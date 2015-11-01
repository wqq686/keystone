package com.keystone.share.services;

import java.lang.reflect.Method;

/**
 * Invoke's Interceptor, executor...
 * try
 * {
 *     before()....
 *     
 *     invokeServiceMethod()....
 *     
 *     after()...
 * }
 * catch(Throwable t){
 *     onThrowable()...
 * }
 * finally
 * {
 *    onComplete()....
 * }
 * @author wuqq
 *
 */
public interface InvokeAdvisor {
	
	/**
	 * 
	 * @param method
	 * @param args
	 * @param target
	 * @throws Throwable
	 */
	void 	before(Method method, Object[] args, Object target) throws Throwable ;
	
	
	
	/**
	 * 
	 * @param returnValue
	 * @param method
	 * @param args
	 * @param target
	 * @throws Throwable
	 */
	void 	after(Object returnValue, Method method, Object[] args, Object target) throws Throwable;  
	
	
	/**
	 * 
	 * @param t
	 * @param method
	 * @param args
	 * @param target
	 * @throws Throwable
	 */
	void 	onThrowable(Throwable t, Method method, Object[] args, Object target) throws Throwable ;
	
	
	/**
	 * 
	 * @param returnValue
	 * @param method
	 * @param args
	 * @param target
	 * @throws Throwable
	 */
	void 	onComplete(Object returnValue, Method method, Object[] args, Object target) throws Throwable ;
	
}
