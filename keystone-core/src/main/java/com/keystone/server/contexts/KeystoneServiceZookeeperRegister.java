package com.keystone.server.contexts;

import org.apache.zookeeper.CreateMode;

import com.keystone.share.locations.Locators;
import com.keystone.share.locations.ServiceLocation;
import com.keystone.share.locations.ServiceRegister;
import com.keystone.support.common.StringUtils;
import com.keystone.support.zkproxy.SimpleZKProxy;
import com.keystone.support.zkproxy.ZKProxy;

public class KeystoneServiceZookeeperRegister implements ServiceRegister {
	
	static int RETRY_NTIMES = 3 ;
	
	String zookeeperAddresses ;
	
	ZKProxy zkProxy ;
	
	public KeystoneServiceZookeeperRegister(String zookeeperAddresses) {
		this.zookeeperAddresses = zookeeperAddresses ;
		if(zookeeperAddresses!=null) {
			zkProxy = new SimpleZKProxy(zookeeperAddresses) ;
		}
	}
	
	

	
	/**
	 * 
	 * @param business
	 * @param service
	 * @param host
	 * @param port
	 * @param context
	 */
	@Override
	public ServiceLocation register(String business, String service, String host, int port, String context) {
		ServiceLocation location = null ;
		if(zkProxy!=null)
		{
			createServiceNode(business, service, RETRY_NTIMES);
			
			location = registerLocation(business, service, host, port, context, RETRY_NTIMES);
		}
		return location ;
	}
	
	
	
	
	/**
	 * 
	 * @param business
	 * @param service
	 * @param retryNTimes
	 */
	void createServiceNode(String business, String service, int retryNTimes) {
		String path = null ; boolean successed = false ; RuntimeException ex = null ;
		for(int index=0; index<retryNTimes; index++) {
			try
			{
				//1. init root.
				if(!zkProxy.exists(Locators.KEYSTONE_RPC_ZOOKEEPER_ROOT)) {
					String[] array = Locators.KEYSTONE_RPC_ZOOKEEPER_ROOT.split("/") ;
					String keystone = array[1], rpc = array[2] ;
					path = Locators.builderPath(keystone) ;
					if(!zkProxy.blockingExists(path)) {
						zkProxy.blockingCreate(path, "", CreateMode.PERSISTENT) ;
					}
					
					path = Locators.builderPath(keystone, rpc) ;
					if(!zkProxy.blockingExists(path)) {
						zkProxy.blockingCreate(path, "", CreateMode.PERSISTENT) ;
					}
				}
				
				//2. create business's node				
				path = Locators.builderPath(Locators.KEYSTONE_RPC_ZOOKEEPER_ROOT, business) ;
				if(!zkProxy.blockingExists(path)) {
					zkProxy.blockingCreate(path, "", CreateMode.PERSISTENT) ;
				}
				
				//3. create service's node
				path = Locators.getServicePath(business, service) ;
				if(!zkProxy.blockingExists(path)) {
					zkProxy.blockingCreate(path, "", CreateMode.PERSISTENT) ;
				}
				
				successed = true ;
				break ;
			}catch(RuntimeException e) {
				ex = e ;
			}
		}
		if(!successed && ex!=null) throw ex ;
	}
	
	
	
	
	
	/**
	 * 
	 * @param business
	 * @param service
	 * @param host
	 * @param port
	 * @param context
	 * @param retryNTimes
	 */
	ServiceLocation registerLocation(String business, String service, String host, int port, String context, int retryNTimes) {
		context = StringUtils.emptyOrTrim(context) ;
		ServiceLocation location = new ServiceLocation(business, service, host, port, context, Locators.DEFAULT_SERVICE_CONNECT_TIMEOUT, Locators.DEFAULT_SERVICE_READ_TIMEOUT) ;
		
		String path = Locators.getLocationPath(business, service, host, port, context) ; boolean successed = false ; RuntimeException ex = null ;
		for(int index=0; index<retryNTimes; index++) {
			try
			{
				if(!zkProxy.blockingExists(path)) {
					zkProxy.blockingCreate(path, location, CreateMode.EPHEMERAL) ;
				}
				successed = true ;
				break ;
			}catch(RuntimeException e) {
				ex = e ;
			}
		}
		if(!successed && ex!=null) throw ex ;
		return location ;
	}
	
	

}
