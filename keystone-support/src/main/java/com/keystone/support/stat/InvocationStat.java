package com.keystone.support.stat;

import java.util.Arrays;

public class InvocationStat {

	private String business ;
	
	private String service ;
	
	private String name ;
	
	private String ip ;
	
	private int port ;
	
	private String context ;
	
	private long[] stat ;

	public InvocationStat(String business, String service, String name, String ip, int port, String context, long[] stat) {
		this.business = business ;
		this.service = service;
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.context = context ;
		this.stat = stat;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public long[] getStat() {
		return stat;
	}

	public void setStat(long[] stat) {
		this.stat = stat;
	}

	@Override
	public String toString() {
		return "MethodStat [business=" + business + ", service=" + service + ", name=" + name + ", ip=" + ip + ", port=" + port + ", context=" + context + ", stat=" + Arrays.toString(stat) + "]";
	}

	
}
