package com.keystone.server.contexts.services;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.keystone.share.services.InvokeAdvisor;

/**
 * 
 * @author wuqq
 *
 */
public class ServiceSkeleton {
	
	/**
	 * 
	 */
	private final AtomicInteger invokeCount = new AtomicInteger(0);
	
	/**
	 * The service name.
	 */
	private final String name;

	/**
	 * The service interface class.
	 */
	private final Class<?> apiClass;

	/**
	 * The service implementation object.
	 */
	private final Object service;

	/**
	 * 
	 */
	private final Map<String, MethodSkeleton> methodContainer ;
	
	/**
	 * 
	 */
	private List<InvokeAdvisor> advisors = new ArrayList<>() ;
	
	
	
	
	/**
	 * 
	 * @param name
	 * @param apiClass
	 * @param service
	 * @param methodContainer
	 */
	public ServiceSkeleton(String name, Class<?> apiClass, Object service, Map<String, MethodSkeleton> methodContainer) {
		this.name = name;
		this.apiClass = apiClass;
		this.service = service;
		if(methodContainer==null) methodContainer = new HashMap<>() ;
		this.methodContainer = methodContainer;
	}


	/**
	 * 
	 * @return
	 */
    public String getName() {
        return this.name;
    }
    
    
	/**
	 * 
	 * @return
	 */
	public Class<?> getApiClass() {
		return this.apiClass;
	}
	
	/**
	 * 
	 * @return
	 */
	public Object getService() {
		return service;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public AtomicInteger getInvokeCount() {
		return invokeCount;
	}

	
	/**
	 * 
	 * @return
	 */
	public Map<String, MethodSkeleton> getMethodContainer() {
		return methodContainer;
	}

	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public MethodSkeleton getMethodSkeleton(String name) {
		return this.methodContainer.get(name) ;
	}
	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Method getMethod(String name) {
		MethodSkeleton skeleton = getMethodSkeleton(name) ;
		return skeleton==null ? null : skeleton.getMethod() ;
	}

	
	public List<InvokeAdvisor> getAdvisors() {
		return advisors;
	}


	/**
	 * 
	 * @param advisor
	 */
	public void addAdvisors(InvokeAdvisor advisor) {
		if(advisor!=null) this.advisors.add(advisor) ;
	}

	
	
	/**
	 * 
	 * @return
	 */
	public long[] getServiceAccessCount() {
		Map<String, MethodSkeleton> container = getMethodContainer() ;
		long accessCount = 0, successCount = 0, failedCount = 0, avgCost = 0 ;
		if(!container.isEmpty()) {
			Collection<MethodSkeleton> methods = container.values() ;
			for(MethodSkeleton skeleton : methods) {
				accessCount  += skeleton.getAccessCount() ;
				successCount += skeleton.getSuccessCount() ;
				failedCount  += skeleton.getFailedCount() ;
				avgCost      += skeleton.getAvgCost() ;
			}
			avgCost = avgCost/methods.size() ;
		}
		return new long[]{ accessCount, successCount, failedCount, avgCost } ;
	}



	
	@Override
	public String toString() {
		return "ServiceSkeleton [invokeCount=" + invokeCount + ", name=" + name + ", apiClass=" + apiClass + ", service=" + service + ", methodContainer=" + methodContainer + "]";
	}
	
	
	

}
