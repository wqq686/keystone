package com.keystone.remoting.locator;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.zookeeper.WatchedEvent;

import com.keystone.remoting.RemotingServiceRouter;
import com.keystone.share.locations.Locators;
import com.keystone.share.locations.ServiceLocation;
import com.keystone.share.locations.ServiceLocator;
import com.keystone.support.zkproxy.NodeChangedListener;
import com.keystone.support.zkproxy.SimpleZKProxy;
import com.keystone.support.zkproxy.ZKProxy;

/**
 * 
 * @author wuqq
 *
 */
public class ServiceLocator4RouterByZookeeper implements ServiceLocator, NodeChangedListener {

	/** */
	ConcurrentMap<String, RemotingServiceRouter<?>> remotingContainer ;
	
	/** */
	ZKProxy zkProxy ;
	
	
	
	
	public ServiceLocator4RouterByZookeeper(String zookeeperLocation, ConcurrentMap<String, RemotingServiceRouter<?>> remotingContainer) {
		if(zookeeperLocation!=null) {
			this.remotingContainer = remotingContainer ;
			zkProxy = new SimpleZKProxy(zookeeperLocation) ;			
		}
	}
	
	

	
	
	
	/**
	 * 
	 */
	@Override
	public List<ServiceLocation> locate(String business, String service) {
		return getLocationsFromZookeeper(business, service, 3) ;
	}



	
	
	
	/**
	 * 
	 * @param business
	 * @param service
	 * @param retryNTimes
	 * @return
	 */
	List<ServiceLocation> getLocationsFromZookeeper(String business, String service, int retryNTimes) {
		List<ServiceLocation> children = null ;
		if(zkProxy!=null)
		{
			String path = Locators.getServicePath(business, service) ; boolean successed = false ; RuntimeException ex = null ;
			for(int index=0; index<retryNTimes; index++) {
				try
				{
					children = zkProxy.getChildrenObject(path, ServiceLocation.class) ;
					zkProxy.subscribeChanges(path, ChangedType.BothChanged, this);
					successed = true ;
					break ;
				}catch(RuntimeException e) {
					ex = e ;
				}
			}
			if(!successed && ex!=null) throw ex ;
		}
		System.out.println("getLocationsFromZookeeper:" + children);
		return children ;
	}






	@Override
	public void onWatchedEvent(WatchedEvent event) {
		String path = event.getPath() ;
		String[] paths = Locators.spiltPath(path) ;
		String business = paths[0], service = paths[1] ;
		onLocationChanged(business, service);
	}

	

	
	
	
	void onLocationChanged(String business, String service) {
		if(remotingContainer!=null) {
			String key = Locators.getServiceLocationKey(business, service) ;
			remotingContainer.remove(key) ;
		}
	}



}
