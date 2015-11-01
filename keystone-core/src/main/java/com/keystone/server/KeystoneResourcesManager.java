package com.keystone.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.keystone.server.config.KeystoneServerConfig;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.services.MethodSkeleton;
import com.keystone.server.contexts.services.ServiceSkeleton;
import com.keystone.share.locations.ServiceRegister;
import com.keystone.transpot.api.ReactorManager;


/**
 * 
 * 
 * @author wuqq
 *
 */
public class KeystoneResourcesManager {

	
	/** */
	private static KeystoneServerConfig serverConfig  ;
	
	/** */
	private static ReactorManager reactorManager ;
	
	/** */
	private static ServiceRegister serviceRegister ;
	
	/** */
	private final static ConcurrentMap<String, KeystoneAppContext> appContexts = new ConcurrentHashMap<>() ;
	
	
	
	/**
	 * 
	 * @param ksConfig
	 */
	public static synchronized void start(KeystoneServerConfig ksConfig) {
		if(ksConfig == null) throw new NullPointerException("ksConfig=null") ;
		
		serverConfig = ksConfig;
	}

	
	
	
	
	/**
	 * 
	 * @param reactorManager
	 */
	public static void registerReactorManager(ReactorManager reactorManager) {
		KeystoneResourcesManager.reactorManager = reactorManager ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static ReactorManager getReactorManager() {
		return reactorManager ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static KeystoneServerConfig getServerConfig() {
		return serverConfig;
	}
	
	
	/**
	 * 
	 * @param register
	 */
	public static void setServiceRegister(ServiceRegister register) {
		serviceRegister = register;
	}

	/**
	 * 
	 * @return
	 */
	public static ServiceRegister getServiceRegister() {
		return serviceRegister;
	}



	/**
	 * 
	 * @return
	 */
	public static ConcurrentMap<String, KeystoneAppContext> getAppcontexts() {
		return appContexts;
	}


	/**
	 * 
	 * @param contextName
	 * @return
	 */
	public static boolean containsAppContext(String contextName) {
		return appContexts.containsKey(contextName) ;
	}

	
	/**
	 * 
	 * @param appContext
	 */
	public static void registerAppContext(KeystoneAppContext appContext) {
		appContexts.put(appContext.getContextName(), appContext) ;
	}
	
	
	
	
	/**
	 * 
	 * @param contextName
	 * @param throwNotFound
	 * @return
	 */
	public static KeystoneAppContext getAppContext(String contextName, boolean throwNotFound) {
		KeystoneAppContext appContext = appContexts.get(contextName) ;
		if(appContext==null)
		{
			if(throwNotFound) throw new IllegalArgumentException("AppContextNotFound[contextName="+contextName+"]") ;
			else return null ;
		}
		return appContext ;
	}
	
	
	
	
	public static ServiceSkeleton getServiceSkeleton(String contextName, String serviceName, boolean throwNotFound) {
		KeystoneAppContext appContext = getAppContext(contextName, throwNotFound) ;
		ServiceSkeleton skeleton = appContext.getRemotingContainer().getServiceSkeleton(serviceName) ;
		if(skeleton==null)
		{
			if(throwNotFound) throw new IllegalArgumentException("ServiceNotFound[serviceName="+serviceName+"] @ contextName="+contextName) ;
			else return null ;
		}
		return skeleton ;
	}
	
	

	/**
	 * 
	 * @param skeleton
	 * @param methodName
	 * @param throwNotFound
	 * @return
	 */
	public static MethodSkeleton getMethodSkeleton(ServiceSkeleton skeleton, String methodName, boolean throwNotFound) {
		MethodSkeleton holder = skeleton.getMethodSkeleton(methodName) ;
		if(holder==null && throwNotFound) throw new IllegalArgumentException("MethodNotFound[methodName="+methodName+"]") ;
		return holder ;
	}
	
	
	
	/**
	 * 
	 * @param contextName
	 * @param serviceName
	 * @param methodName
	 * @param throwNotFound
	 * @return
	 */
	public static MethodSkeleton getMethodSkeleton(String contextName, String serviceName, String methodName, boolean throwNotFound) {
		ServiceSkeleton skeleton = getServiceSkeleton(contextName, serviceName, throwNotFound) ;
		return getMethodSkeleton(skeleton, methodName, throwNotFound) ;
	}
}
