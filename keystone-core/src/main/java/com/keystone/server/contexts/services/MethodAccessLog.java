package com.keystone.server.contexts.services;

public class MethodAccessLog {

	private long accessTime ;
	
	private long cost ;
	
	private int status ;

	
	
	public MethodAccessLog(long accessTime, long cost, int status) {
		this.accessTime = accessTime;
		this.cost = cost;
		this.status = status;
	}

	public long getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(long accessTime) {
		this.accessTime = accessTime;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "MethodAccessLog [accessTime=" + accessTime + ", cost=" + cost + ", status=" + status + "]";
	}
	
	
	
}
