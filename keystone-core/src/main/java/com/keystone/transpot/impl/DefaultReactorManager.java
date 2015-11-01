package com.keystone.transpot.impl;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicInteger;

import com.keystone.support.common.CommonUtils;
import com.keystone.transpot.api.EventWorker;
import com.keystone.transpot.api.IoSession;
import com.keystone.transpot.api.IoWorker;
import com.keystone.transpot.api.NIOConfig;
import com.keystone.transpot.api.Reactor;
import com.keystone.transpot.api.ReactorManager;

public class DefaultReactorManager implements ReactorManager {
	
	/**
	 * 
	 */
	private final AtomicInteger next = new AtomicInteger() ;
	
	/**
	 * 
	 */
	private boolean started = false ;
	
	/**
	 * Read and Write Reactors Count
	 */
	private int rwReactorCount ;
	
	/**
	 * 
	 */
	private Reactor[] reactors ;
	
	/**
	 * 
	 */
	private NIOConfig nioConfig ;
	
	/**
	 * 
	 */
	private IoWorker ioWorker ;
	
	/**
	 * 
	 */
	private EventWorker eventWorker ;
	
	/**
	 * 
	 */
	@Override
	public ReactorManager setNIOConfig(NIOConfig conf) {
		this.nioConfig = conf ;
		return this ;
	}
	

	@Override
	public NIOConfig getNIOConfig() {
		return this.nioConfig ;
	}

	/**
	 * 
	 */
	@Override
	public ReactorManager setIoWorker(IoWorker ioWorker) {
		this.ioWorker = ioWorker ;
		return this ;
	}
	
	
	@Override
	public EventWorker getEventWorker() {
		return eventWorker;
	}


	@Override
	public ReactorManager setEventWorker(EventWorker eventWorker) {
		this.eventWorker = eventWorker;
		return this ;
	}



	/**
	 * 
	 */
	@Override
	public synchronized void start() {
		if(!started)
		{
			//1. Start IoWorker
			
			//2. Start Reacotr
			startReactor() ;
			
			started = true ;
		}
	}

	
	
	
	@Override
	public synchronized void stop() {
		for(Reactor r : reactors)
		{
			r.stop() ;
		}
	}

	


	/**
	 * 
	 * @param channel
	 * @param ops
	 * @param attach
	 * @throws IOException 
	 */
	@Override
	public void registerSession(IoSession session, int ops) throws IOException {
		getReactor(ops).registerSession(session, ops) ;
	}
	
	@Override
	public void registerChannel(SelectableChannel channel, int ops, Object attach) throws IOException {
		getReactor(ops).registerChannel(channel, ops, attach) ;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Reactor nextReactor() {
		return rwReactorCount>0 ? reactors[next.incrementAndGet()%rwReactorCount+1] : reactors[0] ;
	}

	
	/**
	 * 
	 */
	@Override
	public Reactor foundReactor(SelectionKey key) {
		if(key!=null)
		{
			Selector selector = key.selector();
			for(Reactor r : reactors)
			{
				if(r.getSelector()==selector) return r ;
			}
		}
		return null;
	}
	
	
	
	
	/**
	 * 
	 */
	private void startReactor() {
		reactors = new Reactor[nioConfig.getReactorCount()] ;
		for(int index=0; index<nioConfig.getReactorCount(); index++)
		{
			String name = nioConfig.getReactorNamePrefix() + "-" + index ;
			Reactor reactor = CommonUtils.newInstance(nioConfig.getReactorClassName()) ;
			reactor.setName(name) ;
			reactor.setIoWorker(ioWorker) ;
			reactors[index] = reactor ;
		}
		rwReactorCount = reactors.length-1;
		
    	for (Reactor reactor : reactors) {
        	reactor.start();
        }
	}
	
	
	/**
	 * 
	 * @param ops
	 * @return
	 */
	private Reactor getReactor(int ops) {
		return (ops==SelectionKey.OP_ACCEPT || ops==SelectionKey.OP_CONNECT) ? reactors[0] : nextReactor() ;
	}







}
