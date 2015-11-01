package com.keystone.remoting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.keystone.remoting.locator.RemotingProxy;
import com.keystone.remoting.locator.ServiceLocator4Config;
import com.keystone.remoting.locator.ServiceLocator4RouterByZookeeper;
import com.keystone.share.locations.Locators;
import com.keystone.share.locations.ServiceLocation;
import com.keystone.share.locations.ServiceLocator;
import com.keystone.share.services.AsyncService;
import com.keystone.share.services.ParallelService;
import com.keystone.share.services.Router;
import com.keystone.support.common.CommonUtils;

public class KeystoneServiceFactory {

	/** */
	static final ConcurrentMap<String, RemotingServiceRouter<?>> remotingContainer = new ConcurrentHashMap<>() ;
	
	/** */
	static ServiceLocator4Config configLocator = new ServiceLocator4Config() ;

	/** */
	static ServiceLocator routerLocator ;

	static {
		init(); 
	}
	
	
	static void init() {
		if(!CommonUtils.isEmpty(configLocator.getZookeeperLocation())) {
			routerLocator = new ServiceLocator4RouterByZookeeper(configLocator.getZookeeperLocation(), remotingContainer);
		}
	}
	
	/**
	 * 
	 * @param api
	 * @return
	 */
	public static <T> T getService(Class<T> api) {
		RemotingServiceRouter<T> serviceInfo = getRemotingServiceRouter(api) ;
		if(serviceInfo!=null) {
			return serviceInfo.getSyncProxy() ;
		}
		return null ;
	}
	
	
	
	
    /**
     * 
     * @param api
     * @return
     */
    public static <T> AsyncService<T> getAsyncService(Class<T> api) {
    	RemotingServiceRouter<T> serviceRouter = getRemotingServiceRouter(api) ;
		if(serviceRouter!=null) {
			return serviceRouter.getAsyncProxy() ;
		}
		return null ;
    }
    
    
    
    /**
     * 
     * @param api
     * @return
     */
    public static <T> ParallelService<T> getParallelService(Class<T> api) {
    	RemotingServiceRouter<T> serviceInfo = getRemotingServiceRouter(api) ;
		if(serviceInfo!=null) {
			return serviceInfo.getParallelProxy() ;
		}
		return null ;
    }
    
    
    public static <T> T getSinleService(Class<T> api) {
    	RemotingServiceRouter<T> serviceInfo = getRemotingServiceRouter(api) ;
		if(serviceInfo!=null) {
			return serviceInfo.getSingleProxy() ;
		}
		return null ;
    }
    
    
    
	/**
	 * 
	 * @param api
	 * @param uid
	 * @return
	 */
	public static <T> T getService(Class<T> api, long uid) {
		return getService(api) ;
	}
	
	
	
	
	
	
	
	/**
	 * 
	 * @param api
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> RemotingServiceRouter<T> getRemotingServiceRouter(Class<T> api) {
		Router router = Locators.extractRouter(api) ;
		String business = router.business(), service = router.service() ;
		String key = Locators.getServiceLocationKey(business, service) ;
		RemotingServiceRouter<T> serviceRouter = (RemotingServiceRouter<T>) remotingContainer.get(key) ;
		if(serviceRouter==null)
		{
			synchronized (key) 
			{
				serviceRouter = (RemotingServiceRouter<T>) remotingContainer.get(key) ;
				if(serviceRouter==null)
				{
					List<ServiceLocation> locations = configLocator.locate(business, service) ;
					if(locations==null && routerLocator!=null) {
						locations = routerLocator.locate(business, service) ;
					}
					
					List<RemotingProxy> proxys = createRemotingProxys(locations) ;
					if(proxys!=null) {
						serviceRouter = new RemotingServiceRouter<T>(api, proxys) ;
						RemotingServiceRouter<T> absent = (RemotingServiceRouter<T>) remotingContainer.putIfAbsent(key, serviceRouter) ;
						if(absent!=null) {
							serviceRouter = absent ;
						}
					}
				}
			}
		}
		return serviceRouter ;
	}

	
	static List<RemotingProxy> createRemotingProxys(List<ServiceLocation> locations) {
		try
		{
			List<RemotingProxy> proxys = null ;
			if(!CommonUtils.isEmpty(locations)) {
				proxys = new ArrayList<>(locations.size()) ;
				for(ServiceLocation location : locations) {
					RemotingProxy proxy = new RemotingProxy(location) ;
					proxys.add(proxy) ;
				}
			}
			return proxys ;
		} catch(Exception e){ throw new IllegalStateException(e);}
	}
	
	
}
