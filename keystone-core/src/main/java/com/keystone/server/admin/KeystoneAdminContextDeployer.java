package com.keystone.server.admin;

import com.keystone.server.KeystoneResourcesManager;
import com.keystone.server.config.AppContextConfig;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.KeystoneAppContextDefaultDeployer;


/**
 * 
 * @author wuqq
 *
 */
public class KeystoneAdminContextDeployer extends KeystoneAppContextDefaultDeployer {

	@Override
	public KeystoneAppContext deployAppContext(AppContextConfig acc) {
		//1. 
		KeystoneAdminContext adminContext = new KeystoneAdminContext() ;
		
		//2. init context resource
		initAdminContext(adminContext, acc) ;
		
		//5. register context
		KeystoneResourcesManager.registerAppContext(adminContext) ;
		return adminContext ;
	}

	
	private void initAdminContext(KeystoneAdminContext adminContext, AppContextConfig acc) {
		adminContext.setContextConfig(acc);
		adminContext.setContextClassLoader(Thread.currentThread().getContextClassLoader());
	}
	
}
