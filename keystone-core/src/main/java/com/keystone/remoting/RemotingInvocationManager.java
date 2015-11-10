package com.keystone.remoting;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import com.keystone.remoting.proxy.RemotingTicket;
import com.keystone.support.threadpool.TaskQueue;
import com.keystone.support.threadpool.TaskThreadFactory;
import com.keystone.support.threadpool.TaskThreadPoolExecutor;


/**
 * 
 * @author wuqq
 *
 */
public class RemotingInvocationManager {
	
	/** */
	private static ConcurrentMap<Integer, RemotingTicket> tickets = new ConcurrentHashMap<Integer, RemotingTicket>() ;
	
	/** */
	private static Executor remotingExecutor = null ;
	
	static { init();  }
	
	
	
	
	private static void init() {
		Thread t = new Thread("ksremoting-ticket-thread") {
			@Override
			public void run() {
				for(;;) 
				{
					try
					{
						//1. sleep
						TimeUnit.MILLISECONDS.sleep(500);
						
						//2. check
						Collection<RemotingTicket> values = tickets.values();
						long now = System.currentTimeMillis();
						for(RemotingTicket ticket : values) {
							long cost = now - ticket.getBornTime() ;
							if ( cost > ticket.getTimeout() ) {
								final RemotingTicket t = removeTicket(ticket.getTicket());
								if(t!=null) {
									t.countDown();
									if(t.getCallback()!=null) {
										getRemotingExecutor().execute(new Runnable() {
											@Override
											public void run() {
												t.getCallback().onExpired();
											}
										});								
									}
								}
							}
						}		
					}catch(Exception e) {e.printStackTrace();}
				}
				
			}
		} ;
		t.setDaemon(true);
		t.start();
	}
	
	
	
	
	
	/**
	 * 
	 * @return
	 */
	public static Executor getRemotingExecutor() {
		if(remotingExecutor==null) {
			synchronized (RemotingInvocationManager.class) {
				TaskQueue taskQueue = new TaskQueue(20000);
				TaskThreadPoolExecutor pool = new TaskThreadPoolExecutor(5, 20, 120, TimeUnit.SECONDS, taskQueue, new TaskThreadFactory("keystone-client-threadpool-exec-"));
				taskQueue.setParent(pool);
				remotingExecutor = pool ;
			}
		}
		return remotingExecutor ;
	}
	
	
	
	
	
	/**
	 * 
	 * @param ticket
	 */
	public static void registerTicket(RemotingTicket ticket) {
		tickets.put(ticket.getTicket(), ticket) ;
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static RemotingTicket removeTicket(Integer id) {
		return tickets.remove(id) ;
	}
}
