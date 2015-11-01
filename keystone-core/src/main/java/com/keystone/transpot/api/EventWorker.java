package com.keystone.transpot.api;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.keystone.transpot.api.codec.ProtocolFactory;


/**
 * 
 * @author wuqq
 *
 */
public interface EventWorker {
//
//	
//	
//	/**
//	 * 
//	 * @param ioWorker
//	 */
//	EventWorker				setIoWorker(IoWorker ioWorker) ;
//	
	
	
	/**
	 * 
	 * @param protocolFactory
	 * @return
	 */
	EventWorker				setProtocolFactory(ProtocolFactory protocolFactory) ;
	
	
	/**
	 * 
	 * @param messageHandler
	 * @return
	 */
	EventWorker 			setMessageHandler(MessageHandler messageHandler) ;
	
//	
//	
//	/**
//	 * 
//	 * @param messageProccessor
//	 */
//	void				setMessageProccessor(MessageProccessor messageProccessor) ;
	
	
	/**
	 * 
	 * @return
	 */
//	MessageProccessor	getMessageProccessor() ;
//	
//	
//	/**
//	 * 
//	 * @return
//	 */
//	List<MessageFilter> getFilters() ;
	
	
	/**
	 * 
	 * @param from
	 * @throws IOException
	 */
	void				recvMessage(IoSession from)  ;
	
	
	/**
	 * 
	 * @param message
	 * @param to
	 * @throws IOException
	 */
	ByteBuffer				sendMessage(Object message, IoSession to) ;
}
