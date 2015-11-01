package com.keystone.transpot.impl;

import com.keystone.transpot.api.NIOConfig;

public class DefaultNIOConfg implements NIOConfig {

	/**
	 * 
	 */
	private int reactorCount = 4;
	
	/**
	 * 
	 */
	private String reactorNamePrefix = "keystone-reactor";
	
	/**
	 * 
	 */
	private String reactorClassName = "com.keystone.transpot.impl.DefaultReactor" ;
	
	/**
	 * 
	 */
	private String reactorManagerClassName = "com.keystone.transpot.impl.DefaultReactorManager";
	
	/**
	 * 
	 */
	private String ioWorkerClassName = "com.keystone.transpot.impl.DefaultIoWorker";
	
	/**
	 * 
	 */
	private String eventWorkerClassName = "com.keystone.transpot.impl.DefaultEventWorker";
	
	/**
	 * 
	 */
	private String protocolFactoryClassName = "com.keystone.protocol.multiplexing.MultiplexingProtocolFactory";
	
	/**
	 * 
	 */
	private String messageHandlerClassName = "com.keystone.transpot.impl.DefaultMessageHandler";

	/**
	 * 
	 */
	private boolean isTcpNoDelay = true ;
	
	/**
	 * 
	 */
	private int backlog = 1024 ;
	
	/**
	 * 
	 */
	private int minThreadPoolSize = 20;
	
	/**
	 * 
	 */
	private int maxThreadPoolSize = 512 ;
	
	/**
	 * 
	 */
	private int threadPoolQueueSize = 2000 ;

	/**
	 * 
	 */
	private int recvBufferSize = 1024*4 ;
	
	@Override
	public boolean isTcpNoDelay() {
		return isTcpNoDelay;
	}

	@Override
	public void setTcpNoDelay(boolean isTcpNoDelay) {
		this.isTcpNoDelay = isTcpNoDelay;
	}

	
	@Override
	public int getReactorCount() {
		return reactorCount;
	}

	@Override
	public void setReactorCount(int reactorCount) {
		this.reactorCount = reactorCount;
	}

	@Override
	public String getReactorNamePrefix() {
		return reactorNamePrefix;
	}

	@Override
	public void setReactorNamePrefix(String reactorNamePrefix) {
		this.reactorNamePrefix = reactorNamePrefix;
	}

	@Override
	public String getReactorClassName() {
		return reactorClassName;
	}

	@Override
	public void setReactorClassName(String reactorClassName) {
		this.reactorClassName = reactorClassName;
	}

	
	@Override
	public String getIoWorkerClassName() {
		return ioWorkerClassName;
	}

	@Override
	public void setIoWorkerClassName(String ioWorkerClassName) {
		this.ioWorkerClassName = ioWorkerClassName;
	}

	@Override
	public String getEventWorkerClassName() {
		return eventWorkerClassName;
	}

	@Override
	public void setEventWorkerClassName(String eventWorkerClassName) {
		this.eventWorkerClassName = eventWorkerClassName;
	}

	@Override
	public String getProtocolFactoryClassName() {
		return protocolFactoryClassName;
	}

	
	@Override
	public void setProtocolFactoryClassName(String protocolFactoryClassName) {
		this.protocolFactoryClassName = protocolFactoryClassName;
	}

	@Override
	public String getMessageHandlerClassName() {
		return messageHandlerClassName;
	}

	@Override
	public void setMessageHandlerClassName(String messageHandlerClassName) {
		this.messageHandlerClassName = messageHandlerClassName;
	}


	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}


	public String getReactorManagerClassName() {
		return reactorManagerClassName;
	}

	public void setReactorManagerClassName(String reactorManagerClassName) {
		this.reactorManagerClassName = reactorManagerClassName;
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

	@Override
	public int getRecvBufferSize() {
		return recvBufferSize;
	}

	@Override
	public NIOConfig setRecvBufferSize(int recvBufferSize) {
		this.recvBufferSize = recvBufferSize ;
		return this ;
	}
	
	
	
}
