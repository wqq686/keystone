package com.keystone.share.contexts;

/**
 * 
 * @author wuqq
 *
 */
public interface AppContextEvent {

	
	/**
	 * 
	 * @author wuqq
	 *
	 */
	public static enum EventType{ Started, Destroyed }
	
	
	/**
	 * 
	 * @return
	 */
	public EventType getEventType() ;
	
	
	/**
	 * 
	 * @return
	 */
	public AppContext getAppContext() ;
	
}
