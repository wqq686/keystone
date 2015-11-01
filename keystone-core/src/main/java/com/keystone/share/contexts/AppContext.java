package com.keystone.share.contexts;


/**
 * 
 * @author wuqq
 *
 */
public interface AppContext {
	
	/**
	 * 
	 * @return
	 */
	public String getContextName();
	
	
	/**
	 * 
	 * @param klass
	 * @return
	 */
	public <T> T getService(Class<T> klass) ;
}
