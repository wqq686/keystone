package com.keystone.share.contexts;

public interface AppContextListener {

	
	/**
	 * 
	 * @param event
	 */
	public void contextStarted(AppContextEvent event) ;
	
	
	/**
	 * 
	 * @param event
	 */
	public void contextDestroyed(AppContextEvent event) ;
	
}
