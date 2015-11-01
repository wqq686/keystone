package com.keystone.server.config;

import java.io.File;

public class AppContextConfig {


	/**
	 * 
	 */
	private String contextName ;
	
	/**
	 * 
	 */
	private String contextKey ;
	
	/**
	 * 
	 */
	private String contextRoot ;
	
	/**
	 * 
	 */
	private int minThreadPoolSize = 20 ;
	
	/**
	 * 
	 */
	private int maxThreadPoolSize = 200;
	
	/**
	 * 
	 */
	private int threadPoolQueueSize = 2000;
	
	/**
	 * 
	 */
	private long waitInQueue ;
	
	/**
	 * 
	 */
	private int logRate ;

	/**
	 * 
	 */
	private int flow ;
	
	
	/**
	 * 
	 * @param contextName
	 * @param contextRoot
	 * @param waitInQueue
	 * @param logRate
	 * @param flow
	 */
	public AppContextConfig(String contextName, String contextRoot, long waitInQueue, int logRate, int flow) {
		this(contextName, contextRoot, 20, 200, 2000, waitInQueue, logRate, flow);
	}
	
	
	
	/**
	 * 
	 * @param contextName
	 * @param contextRoot
	 * @param minThreadPoolSize
	 * @param maxThreadPoolSize
	 * @param threadPoolQueueSize
	 * @param waitInQueue
	 * @param logRate
	 * @param flow
	 */
	public AppContextConfig(String contextName, String contextRoot, int minThreadPoolSize, int maxThreadPoolSize, int threadPoolQueueSize, long waitInQueue, int logRate, int flow) {
		this.contextName = contextName ;
		this.contextRoot = contextRoot ;
		this.contextKey = contextRoot.replace(":", "-").replace(File.separator, "-") ;
		this.minThreadPoolSize = minThreadPoolSize ;
		this.maxThreadPoolSize = maxThreadPoolSize ;
		this.threadPoolQueueSize = threadPoolQueueSize ;
		this.waitInQueue = waitInQueue ;
		this.logRate = logRate ;
		this.flow = flow ;
	}
	
	

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public String getContextKey() {
		return contextKey;
	}

	public void setContextKey(String contextKey) {
		this.contextKey = contextKey;
	}

	public String getContextRoot() {
		return contextRoot;
	}

	public void setContextRoot(String contextRoot) {
		this.contextRoot = contextRoot;
	}

	public int getMinThreadPoolSize() {
		return minThreadPoolSize;
	}

	public void setMinThreadPoolSize(int minThreadPoolSize) {
		this.minThreadPoolSize = minThreadPoolSize;
	}

	public int getMaxThreadPoolSize() {
		return maxThreadPoolSize;
	}

	public void setMaxThreadPoolSize(int maxThreadPoolSize) {
		this.maxThreadPoolSize = maxThreadPoolSize;
	}

	public int getThreadPoolQueueSize() {
		return threadPoolQueueSize;
	}

	public void setThreadPoolQueueSize(int threadPoolQueueSize) {
		this.threadPoolQueueSize = threadPoolQueueSize;
	}

	public long getWaitInQueue() {
		return waitInQueue;
	}

	public void setWaitInQueue(long waitInQueue) {
		this.waitInQueue = waitInQueue;
	}

	public int getLogRate() {
		return logRate;
	}

	public void setLogRate(int logRate) {
		this.logRate = logRate;
	}

	public int getFlow() {
		return flow;
	}

	public void setFlow(int flow) {
		this.flow = flow;
	}



	@Override
	public String toString() {
		return "AppContextConfig [contextName=" + contextName + ", contextKey=" + contextKey + ", contextRoot=" + contextRoot + ", minThreadPoolSize=" + minThreadPoolSize + ", maxThreadPoolSize=" + maxThreadPoolSize + ", threadPoolQueueSize=" + threadPoolQueueSize + ", waitInQueue=" + waitInQueue + ", logRate=" + logRate + ", flow=" + flow + "]";
	}
	
	
	
}
