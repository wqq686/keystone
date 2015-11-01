package com.keystone.server;

import com.keystone.server.admin.AdminContextConfig;
import com.keystone.server.admin.KeystoneAdminContextDeployer;
import com.keystone.server.config.AppContextConfig;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.KeystoneAppContextDefaultDeployer;

public class KeystoneAppContextDeployManager {

	
	/**
	 * 
	 */
	private static KeystoneAppContextDefaultDeployer defaultDeployer = new KeystoneAppContextDefaultDeployer() ;
	
	
	/**
	 * 
	 */
	private static KeystoneAdminContextDeployer adminDeployer = new KeystoneAdminContextDeployer() ;
	
	
	
	
	/**
	 * 
	 * @param acc
	 * @return
	 */
	public static KeystoneAppContext deployAppContext(AppContextConfig acc) {
		KeystoneAppContext appContext = null ;
		if(acc.getClass()==AdminContextConfig.class)
		{
			appContext = adminDeployer.deployAppContext(acc) ;
		}
		else
		{
			appContext = defaultDeployer.deployAppContext(acc) ;
		}
		return appContext ;
	}
	
	
	
	public static void releaseAppContext(KeystoneAppContext appContext) {
		AppContextConfig acc = appContext.getContextConfig() ;
		if(acc.getClass()==AdminContextConfig.class)
		{
			adminDeployer.releaseAppContext(appContext);
		}
		else
		{
			defaultDeployer.releaseAppContext(appContext);
		}
	}
}
