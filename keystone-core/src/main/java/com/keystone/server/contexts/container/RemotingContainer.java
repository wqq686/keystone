package com.keystone.server.contexts.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.keystone.server.contexts.services.ServiceSkeleton;
import com.keystone.share.services.Router;
import com.keystone.share.services.InvokeAdvisor;

public class RemotingContainer {
	
	/**
	 * 
	 */
	private Map<String, ServiceSkeleton> skeletons = new HashMap<>() ;

	/**
	 * 
	 */
	private Map<String, InvokeAdvisor> advisors = new HashMap<>() ;
	
	

	/**
	 * 
	 * @return
	 */
	public Map<String, ServiceSkeleton> skeletons() {
		return skeletons;
	}

	
	/**
	 * 
	 * @param skeleton
	 */
	public void registerService(ServiceSkeleton skeleton) {
		this.skeletons.put(skeleton.getName(), skeleton) ;
	}
	
	
	

	public Map<String, InvokeAdvisor> getAdvisors() {
		return advisors;
	}

	
	
	
	/**
	 * 
	 * @param name
	 * @param advisor
	 */
	public void addAdvisor(String name, InvokeAdvisor advisor) {
		this.advisors.put(name, advisor) ;
	}
	
	
	
	
	/**
	 * 
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> klass) {
		if(klass==null) throw new NullPointerException("klass=null") ;
		
		Router router = klass.getAnnotation(Router.class) ;
		if(router==null) throw new IllegalStateException("Annotation[Router]NotFound @ ["+klass.getName()+"]") ;
		
		String name = router.service() ;
		if(name==null) throw new IllegalStateException("Annotation[Router]Illegal @ ["+klass.getName()+"]:service=null.") ;
		
		ServiceSkeleton skeleton = getServiceSkeleton(name) ;
		if(skeleton==null) throw new IllegalStateException("ServiceNotFound[name="+name+"] class=["+klass.getName()+"]") ;
		
		return (T) skeleton.getService() ;
	}
	
	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public ServiceSkeleton getServiceSkeleton(String name) {
		return skeletons.get(name) ;
	}

	
	/**
	 * 
	 * @return
	 */
	public long[] getContextAccessCount() {
		Map<String, ServiceSkeleton> container = skeletons() ; 
		long accessCount = 0, successCount = 0, failedCount = 0, avgCost = 0 ;
		if(!container.isEmpty()) {
			Collection<ServiceSkeleton> services = container.values() ;
			for(ServiceSkeleton skeleton : services) {
				long[] counts = skeleton.getServiceAccessCount() ;
				
				accessCount  += counts[0] ;
				successCount += counts[1] ;
				failedCount  += counts[2] ;
				avgCost      += counts[3] ;
			}
			avgCost = avgCost/services.size() ;
		}
		return new long[]{ accessCount, successCount, failedCount, avgCost } ;
	}
	
}
