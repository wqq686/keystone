package com.keystone.share.services;


public interface ParallelFuture<V> extends java.util.concurrent.Future<V> {

	/**
	 * is timeout
	 * @return
	 */
	boolean isExpire();
	
}
