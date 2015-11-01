package com.keystone.remoting.proxy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.keystone.share.services.ParallelFuture;

public class RemotingFutrue<V> implements ParallelFuture<V> {
	
	private RemotingTicket ticket ;
	
	RemotingFutrue(RemotingTicket ticket) {
		this.ticket = ticket ;
	}
	
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	
	
	@Override
	public boolean isDone() {
		return ticket.isDone() ;
	}

	@Override
	public boolean isExpire() {
		return ticket.isTimeout() ;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public V get() throws InterruptedException, ExecutionException {
		if(!isDone()) {
			ticket.await();
		}
		return (V) ticket.getResult() ;
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if(!isDone()) {
			ticket.await(timeout, unit);
		}
		return (V) ticket.getResult() ;
	}


	

	
	
}
