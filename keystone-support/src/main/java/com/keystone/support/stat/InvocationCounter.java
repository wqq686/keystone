package com.keystone.support.stat;

import java.util.concurrent.atomic.AtomicLongArray;

public class InvocationCounter {
	
	/**
	 * 
	 */
	private final String name ;
	
	/**
	 * 
	 */
	private AtomicLongArray counter = new AtomicLongArray(4);//0-tatal,1-succ,2-timeout,3-totalCostTime
	
	
	/**
	 * 
	 * @param name
	 */
	public InvocationCounter(String name) {
		this.name = name ;
	}
	
	
	public String getName() {
		return name;
	}


	/**
	 * 
	 * @param ok
	 * @param cost
	 */
	public void count(boolean ok, long cost) {
		int index = ok ? 1 : 2 ;
		counter.incrementAndGet(0) ;
		counter.incrementAndGet(index) ;
		counter.addAndGet(3, cost) ;
	}


	@Override
	public String toString() {
		return "InvocationCounter [name=" + name + ", counter=" + counter + "]";
	}
	
	
	
	
	public long[] reset() {
		long[] data = new long[4] ;
		for(int index=0; index<4; index++)
		{
			data[index] = reset(index) ;
		}
		return data ;
	}
	
	
	private long reset(int index) {
		for(;;)
		{
			long current = counter.get(index) ;
			if(counter.compareAndSet(index, current, 0)) {
				return current ;
			}
		}
	}
}
