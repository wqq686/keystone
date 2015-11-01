package com.keystone.server.contexts.services;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class MethodSkeleton {
	
	/**
	 * 
	 */
	private Method method ;
	
	/**
	 * 
	 */
	private Type[] argsTypes ;
	
	/**
	 * 
	 */
	private String[] argsNames ;
	
	/**
	 * 
	 */
	private Object[] argsMarkers ;
	
	/**
	 * 
	 */
	private Type returnType ;
	
	
	private ArrayBlockingQueue<MethodAccessLog> lastAccesses = new ArrayBlockingQueue<>(100) ;
	
	private AtomicLong accessCount = new AtomicLong() ;
	
	private AtomicLong successCount = new AtomicLong() ;
	
	private AtomicLong failedCount = new AtomicLong() ;
	
	private AtomicLong avgCost = new AtomicLong() ;
	
	/**
	 * 
	 * @param method
	 * @param argsTypes
	 * @param argsNames
	 * @param argsMarkers
	 * @param returnType
	 */
	public MethodSkeleton(Method method, Type[] argsTypes, String[] argsNames, Object[] argsMarkers, Type returnType) {
		this.method = method;
		this.argsTypes = argsTypes ;
		this.argsNames = argsNames;
		this.argsMarkers = argsMarkers ;
		this.returnType = returnType ;
	}


	/**
	 * 
	 * @return
	 */
	public Method getMethod() {
		return method ;
	}
	

	/**
	 * 
	 * @return
	 */
	public Type[] getArgsTypes() {
		return argsTypes;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getArgsNames() {
		return argsNames ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Object[] getArgsMarkers() {
		return argsMarkers;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isReturnVoid() {
		return returnType==Void.class || returnType == void.class ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Type getReturnType() {
		return returnType ;
	}
	
	
	/**
	 * 
	 * @param access
	 * @param success
	 * @param failed
	 * @param avg
	 */
	public void initStatCount(long access, long success, long failed, long avg) {
		accessCount.set(access); 
		successCount.set(success); 
		failedCount.set(failed);
		avgCost.set(avg);
	}
	
	
	/**
	 * 
	 * @param accessTime
	 * @param status
	 * @param cost
	 */
	public void access(long accessTime, int status, long cost) {
		MethodAccessLog log = new MethodAccessLog(accessTime, cost, status) ; boolean ok = false ;
		for(int i=0; i<3; i++) {
			ok = lastAccesses.offer(log) ;
			if(!ok) lastAccesses.remove() ;
			else break ;
		}
		
		//不追求绝对的精准
		long count = accessCount.incrementAndGet() ;
		if(count<0) accessCount.set(1);
		
		if(status==200) {
			count = successCount.incrementAndGet() ;
			if(count<0) successCount.set(1);
		}
		else {
			count = failedCount.incrementAndGet() ;
			if(count<0) failedCount.set(1);
		}
		
		
		long avg = avgCost.get() ;
		if(avg==0) avgCost.set(cost);
		else {
			avg =(avg + cost)/2 ;
			avgCost.set(avg);
		}
	}


	/**
	 * 
	 * @return
	 */
	public ArrayBlockingQueue<MethodAccessLog> getLastAccesses() {
		return lastAccesses;
	}


	public long getAccessCount() {
		return accessCount.get();
	}

	public long getSuccessCount() {
		return successCount.get();
	}

	public long getFailedCount() {
		return failedCount.get();
	}

	public long getAvgCost() {
		return avgCost.get();
	}


}
