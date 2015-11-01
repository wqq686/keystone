package com.keystone.server.contexts;

import com.keystone.share.contexts.AppContext;
import com.keystone.share.contexts.AppContextEvent;

public class KeystoneAppContextEvent implements AppContextEvent {
	
	
	/**
	 * 
	 */
	private final EventType eventType ;
	
	
	/**
	 * 
	 */
	private final AppContext appContext ;
	
	
	
	/**
	 * 
	 * @param eventType
	 * @param appContext
	 */
	KeystoneAppContextEvent(EventType eventType, AppContext appContext) {
		this.eventType = eventType ;
		this.appContext = appContext ;
	}
	
	
	
	/**
	 * 
	 */
	@Override
	public EventType getEventType() {
		return eventType ;
	}

	
	/**
	 * 
	 */
	@Override
	public AppContext getAppContext() {
		return appContext ;
	}

	
	
}
