package com.keystone.transpot.api;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * 
 * @author wuqq
 *
 */
public interface ReactorManager {

	
	
	/**
	 * 
	 */
	void		start() ;
	
	
	
	/**
	 * 
	 */
	void		stop() ;

	
	/**
	 * 
	 * @param conf
	 * @return
	 */
	ReactorManager setNIOConfig(NIOConfig conf) ;
	
	/**
	 * 
	 * @param ioWorker
	 * @return
	 */
	ReactorManager setIoWorker(IoWorker ioWorker) ;
	
	
	/**
	 * 
	 * @return
	 */
	EventWorker		getEventWorker() ;
	
	/**
	 * 
	 * @param eventWorker
	 * @return
	 */
	ReactorManager 	setEventWorker(EventWorker eventWorker) ;
	
	
	/**
	 * 
	 * @return
	 */
	NIOConfig		getNIOConfig() ;
	
	/**
	 * 
	 * @param session
	 * @param ops
	 * @throws IOException
	 */
	void 	registerSession(IoSession session, int ops) throws IOException ;
	
	
	/**
	 * 
	 * @param channel
	 * @param ops
	 * @param attach
	 * @throws IOException
	 */
	void 	registerChannel(SelectableChannel channel, int ops, Object attach) throws IOException ;
	
	/**
	 * 
	 * @return
	 */
	Reactor 	nextReactor() ;
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	Reactor 	foundReactor(SelectionKey key);
}
