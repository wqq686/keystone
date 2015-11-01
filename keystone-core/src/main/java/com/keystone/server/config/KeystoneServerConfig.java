package com.keystone.server.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.keystone.transpot.impl.DefaultNIOConfg;


public class KeystoneServerConfig extends DefaultNIOConfg {
	
	/** */
	private final String version = "1.0.0" ;
	
	/** */
	private String host ;
	
	/** */
	private int tcpPort ;
	
	/** */
	private long sessionTimeout;
	
	/** */
	private long sessionCheckInterval;
	
	/** */
	private long waitInQueue = TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS) ;
	
	/** */
	private int logRate;

	/** */
	private String appsRoot;
	
	/** */
	private List<AppContextConfig> contextList = new ArrayList<>();
	
	/** */
	private String serverRoot ;
	
	/** */
	private String jvmVersion ;
	
	/** */
	private String osVersion ;
	
	/** */
	private String adminContextName = "admin" ;

	private String zookeeperAddresses ;
	
	public String getVersion() {
		return version;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getTcpPort() {
		return tcpPort;
	}

	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public long getSessionCheckInterval() {
		return sessionCheckInterval;
	}

	public void setSessionCheckInterval(long sessionCheckInterval) {
		this.sessionCheckInterval = sessionCheckInterval;
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

	public String getAppsRoot() {
		return appsRoot;
	}

	public void setAppsRoot(String appsRoot) {
		this.appsRoot = appsRoot;
	}

	public List<AppContextConfig> getContextList() {
		return contextList;
	}

	public void setContextList(List<AppContextConfig> contextList) {
		this.contextList = contextList;
	}

	public String getServerRoot() {
		return serverRoot;
	}

	public void setServerRoot(String serverRoot) {
		this.serverRoot = serverRoot;
	}

	public String getJVMVersion() {
		return jvmVersion;
	}

	public void setJVMVersion(String jvmVersion) {
		this.jvmVersion = jvmVersion;
	}

	public String getOSVersion() {
		return osVersion;
	}

	public void setOSVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getAdminContextName() {
		return adminContextName;
	}

	public void setAdminContextName(String adminContextName) {
		this.adminContextName = adminContextName;
	}

	public String getZookeeperAddresses() {
		return zookeeperAddresses;
	}

	public void setZookeeperAddresses(String zookeeperAddresses) {
		this.zookeeperAddresses = zookeeperAddresses;
	}



	
}
