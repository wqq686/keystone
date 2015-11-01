package com.keystone.share.services;


/**
 * 
 * @author wuqq
 *
 * @param <T>
 */
public interface ParallelService<T> {
	
	/**
	 * 
	 * @return
	 */
	<E> ParallelFuture<E>	getFuture();
	
	/**
	 * 
	 * @param future
	 */
	void	setFuture(ParallelFuture<?> future);
	
	/**
	 * 
	 * @return
	 */
	T	getService();
	
	
}
