package com.keystone.transpot.api;

public interface MessageHandler {

	
	
	void		handleMessage(Object message, IoSession session) ;
	
	
	
}
