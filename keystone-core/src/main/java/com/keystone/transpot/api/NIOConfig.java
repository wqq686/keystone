package com.keystone.transpot.api;



/**
 * 
 * @author wuqq
 *
 */
public interface NIOConfig {
	
	
	/**
	 * 
	 * @return
	 */
	boolean 		isTcpNoDelay() ;

	
	/**
	 * 
	 * @param isTcpNoDelay
	 */
	void 			setTcpNoDelay(boolean isTcpNoDelay) ;

	
	/**
	 * 
	 * @return
	 */
	int 			getReactorCount() ;

	
	/**
	 * 
	 * @param reactorCount
	 */
	void 			setReactorCount(int reactorCount) ;

	
	/**
	 * 
	 * @return
	 */
	String 			getReactorNamePrefix() ;

	
	/**
	 * 
	 * @param reactorNamePrefix
	 */
	void 			setReactorNamePrefix(String reactorNamePrefix) ;

	
	/**
	 * 
	 * @return
	 */
	String 			getReactorClassName() ;

	
	/**
	 * 
	 * @param reactorClassName
	 */
	void 			setReactorClassName(String reactorClassName) ;

	
	
	/**
	 * 
	 * @return
	 */
	String 			getIoWorkerClassName() ;

	
	/**
	 * 
	 * @param ioWorkerClassName
	 */
	void 			setIoWorkerClassName(String ioWorkerClassName) ;

	
	/**
	 * 
	 * @return
	 */
	String 			getEventWorkerClassName() ;

	
	/**
	 * 
	 * @param eventWorkerClassName
	 */
	void 			setEventWorkerClassName(String eventWorkerClassName) ;

	
	/**
	 * 
	 * @return
	 */
	String 			getProtocolFactoryClassName() ;

	
	/**
	 * 
	 * @param protocolFactoryClassName
	 */
	void 			setProtocolFactoryClassName(String protocolFactoryClassName) ;

	
	/**
	 * 
	 * @return
	 */
	String 			getMessageHandlerClassName() ;

	
	/**
	 * 
	 * @param messageHandlerClassName
	 */
	void 			setMessageHandlerClassName(String messageHandlerClassName) ;


	/**
	 * 
	 * @return
	 */
	int 			getRecvBufferSize() ;
	
	
	/**
	 * 
	 * @param recvBufferSize
	 * @return
	 */
	NIOConfig 		setRecvBufferSize(int recvBufferSize) ;
}
