package com.keystone.server.contexts;

import com.keystone.server.config.AppContextConfig;


/**
 * 
 * @author wuqq
 *
 */
public interface KeystoneAppContextDeployer {

	
	/**
	 * 
	 * @param acc
	 * @return
	 */
	KeystoneAppContext		deployAppContext(AppContextConfig acc) ;
	
	
	
	/**
	 * 
	 * @param appContext
	 */
	void					releaseAppContext(KeystoneAppContext appContext) ;
	
	
	
}
