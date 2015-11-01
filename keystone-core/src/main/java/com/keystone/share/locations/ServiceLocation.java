package com.keystone.share.locations;


public class ServiceLocation {

	/** */
	private String business ;
	
	/** */
	private String serviceName ;

	/** */
	private String host ;
	
	/** */
	private int port ;
	
	/** */
	private String contextName ;
	
	/** */
	private int connectTimeout = 1000 ;
	
	/** */
	private int readTimeout = 1000 ;
	
	public ServiceLocation(){}
	
	public ServiceLocation(String business, String serviceName, String host, int port, String contextName, int connectTimeout, int readTimeout) {
		this.business = business ;
		this.serviceName = serviceName ;
		this.host = host ;
		this.port = port ;
		this.contextName = contextName;
		this.connectTimeout = connectTimeout ;
		this.readTimeout = readTimeout ;
	}



	public String getBusiness() {
		return business;
	}



	public void setBusiness(String business) {
		this.business = business;
	}



	public String getServiceName() {
		return serviceName;
	}



	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}



	public String getHost() {
		return host;
	}



	public void setHost(String host) {
		this.host = host;
	}



	public int getPort() {
		return port;
	}



	public void setPort(int port) {
		this.port = port;
	}



	public String getContextName() {
		return contextName;
	}



	public void setContextName(String contextName) {
		this.contextName = contextName;
	}



	public int getConnectTimeout() {
		return connectTimeout;
	}



	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}



	public int getReadTimeout() {
		return readTimeout;
	}



	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}



	@Override
	public String toString() {
		return "ServiceLocation:{business=" + business + ", serviceName=" + serviceName + ", host=" + host + ", port=" + port + ", contextName=" + contextName + ", connectTimeout=" + connectTimeout + ", readTimeout=" + readTimeout + "}";
	}
	
	


	

	
	
}
