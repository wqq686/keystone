package com.keystone.share.locations;

/**
 * 
 * @author wuqq
 *
 */
public interface ServiceRegister {

	
	/**
	 * 
	 * @param business
	 * @param service
	 * @param host
	 * @param port
	 * @param context
	 */
	ServiceLocation register(String business, String service, String host, int port, String context) ;
	
}
