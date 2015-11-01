package com.keystone.remoting;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.keystone.remoting.locator.RemotingProxy;
import com.keystone.remoting.proxy.ServiceAsyncProxy;
import com.keystone.remoting.proxy.ServiceFutureProxy;
import com.keystone.remoting.proxy.ServiceSingleProxy;
import com.keystone.remoting.proxy.ServiceSyncProxy;
import com.keystone.share.services.AsyncService;
import com.keystone.share.services.ParallelService;

/**
 * 
 * @author wuqq
 *
 */
public class RemotingServiceRouter<T> {
	
	/** */
	private Class<?> apiClass ;
	
	/** */
	private List<RemotingProxy> remotingProxys = new ArrayList<RemotingProxy>();

	/** */
	private AtomicInteger director = new AtomicInteger() ;

	/** */
	private ConcurrentMap<RemotingProxy, Object> syncProxyCache = new ConcurrentHashMap<>() ;
	
	
	public RemotingServiceRouter(Class<?> apiClass, List<RemotingProxy> proxys) {
		this.apiClass = apiClass;
		this.remotingProxys = proxys;
	}

	
	
	/**
	 * 
	 * @return
	 */
	public Class<?> getApiClass() {
		return apiClass;
	}

	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T getSyncProxy() {
		RemotingProxy remoting = getRemotingProxy() ;
		Object proxy = syncProxyCache.get(remoting) ;
		if(proxy==null) {
			proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ apiClass }, new ServiceSyncProxy<T>(remoting)) ;
			Object absent = syncProxyCache.putIfAbsent(remoting, proxy) ;
			if(absent!=null) proxy = absent ;
		}
		return (T) proxy ;
	}
	

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AsyncService<T> getAsyncProxy() {
		RemotingProxy location = getRemotingProxy() ;
		Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ apiClass, AsyncService.class }, 
				new ServiceAsyncProxy<T>(location)) ;
//		return (T) proxy ;
		return (AsyncService<T>) proxy ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ParallelService<T> getParallelProxy() {
		RemotingProxy location = getRemotingProxy() ;
		Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{apiClass, ParallelService.class}, 
				new ServiceFutureProxy<T>(location)) ;
		return (ParallelService<T>) proxy ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T getSingleProxy() {
		RemotingProxy remotingProxy = getRemotingProxy() ;
		Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ apiClass }, new ServiceSingleProxy<T>(remotingProxy)) ;
		return (T)proxy ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<RemotingProxy> getRemotingProxys() {
		return remotingProxys;
	}


	/**
	 * 
	 * @param remotingProxy
	 */
	public void addRemotingProxy(RemotingProxy remotingProxy) {
		remotingProxys.add(remotingProxy) ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	RemotingProxy getRemotingProxy() {
		RemotingProxy remotingProxy = null ;
		if(!remotingProxys.isEmpty()) {
			int index = director.getAndIncrement() ;
			if(index<0) {
				director.compareAndSet(index, 0) ;
				index = director.getAndIncrement() ;
			}
			index = index % remotingProxys.size() ;
			remotingProxy = remotingProxys.get(index) ;
		}
		return remotingProxy ;
	}
	
	
	

	@Override
	public String toString() {
		return "ServiceInfo [apiClass=" + apiClass.getName() + ", locations=" + remotingProxys + ", director=" + director + "]";
	}
	
	
	
	
}
