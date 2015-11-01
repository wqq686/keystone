package com.keystone.threadpool;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author oscarhuang
 *
 */
public final class QueueStatusCounter 
{
	/**
	 * 
	 */
	private String counterName;
	
	/**
	 * 
	 */
	private AtomicLong totalNumber = new AtomicLong(0);

	/**
	 * 
	 */
	private AtomicLong totalCostTime = new AtomicLong(0);
	
	/**
	 * 
	 */
	private AtomicLong successNumber = new AtomicLong(0);
	
	/**
	 * 
	 */
	private AtomicLong failedNumber = new AtomicLong(0);
	
	/**
	 * 
	 * @param couterName
	 */
	public QueueStatusCounter(String counterName)
	{
		this.counterName = counterName;
	}
	
	/**
	 * 
	 * @param cost
	 */
	public void addSuccess(int cost)
	{
		this.totalNumber.incrementAndGet();
		this.successNumber.incrementAndGet();
		computeTotalCostTime(cost);
	}
	
	/**
	 * 
	 * @param cost
	 */
	public void addFailed(int cost)
	{
		this.totalNumber.incrementAndGet();
		this.failedNumber.incrementAndGet();
		computeTotalCostTime(cost);
	}
	
	/**
	 * 
	 * @param cost
	 */
	private void computeTotalCostTime(int cost)
	{
		totalCostTime.addAndGet(cost);
	}
	
	/**
	 * 
	 */
	public void reset()
	{
		totalNumber.set(0);
		totalCostTime.set(0);
		successNumber.set(0);
		failedNumber.set(0);
	}

	public String getCounterName() {
		return counterName;
	}

	public long getTotalNumber() {
		return totalNumber.get();
	}

	public long getTotalCostTime() {
		return totalCostTime.get();
	}

	public long getSuccessNumber() {
		return successNumber.get();
	}

	public long getFailedNumber() {
		return failedNumber.get();
	}

	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("QueueStatusCounter [name=");
		sb.append(this.counterName).append(",");
		sb.append("totalNumber=").append(this.totalNumber).append(",");
		sb.append("successNumber=").append(this.successNumber).append(",");
		sb.append("failedNumber=").append(this.failedNumber).append(",");
		sb.append("totalCostTime=").append(this.totalCostTime).append("]");
		
		return sb.toString();
	}
	
	
}
