package com.keystone.share.services;

/**
 * 
 * @author wuqq
 *
 * @param <T>
 */
public interface AsyncCallback<T> {
	
	/**
	 * 
	 */
	void onCompleted(T result);
	
	/**
	 * 
	 */
	void onException(Throwable t) ;
	
	/**
	 * 
	 */
	void onExpired();
}
