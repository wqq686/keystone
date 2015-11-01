package com.keystone.transpot.api;

import java.io.IOException;
import java.nio.channels.SelectionKey;


public interface IoWorker {

	
	/**
	 * 
	 * @param reactorManager
	 */
	void 			setReactorManager(ReactorManager reactorManager) ;


	
	/**
	 * Accept one connection from client.
	 * 
	 * @param key
	 * @throws IOException
	 */
	IoSession 			processAccept(SelectionKey key) throws IOException ;

	
	
	/**
	 * Establish one connection.
	 * 
	 * @param key
	 * @throws IOException
	 */
	void 			processConnect(SelectionKey key) throws IOException ;
	
	
	/**
	 * 
	 * @param key
	 * @throws IOException
	 */
	void 			processClose(IoSession session) ;
	
	
	
	/**
	 * 
	 * @param session
	 * @return
	 * @throws IOException
	 */
	int 			processRead(IoSession session) throws IOException ;

	
	
	/**
	 * 
	 * @param session
	 * @return write message count
	 * @throws IOException
	 */
	int 			processWrite(IoSession session) throws IOException ;
	
	/**
	 * 
	 * @param session
	 * @throws IOException
	 */
	void 			interestRead(IoSession session) throws IOException ;
	
	
	/**
	 * 
	 * @param session
	 * @throws IOException
	 */
	void 			interestWrite(IoSession session) throws IOException ;
	
}
