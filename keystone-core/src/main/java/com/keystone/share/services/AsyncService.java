package com.keystone.share.services;


/**
 * 
 * @author wuqq
 *
 * @param <T>
 */
public interface AsyncService<T> {
	
	/**
	 * 
	 * @return
	 */
	AsyncCallback<?>	getCallback();
	
	/**
	 * 
	 * @param callback
	 */
	void	setCallback(AsyncCallback<?> callback);
	
	/**
	 * 
	 * @return
	 */
	T	getService();
	
	
}
