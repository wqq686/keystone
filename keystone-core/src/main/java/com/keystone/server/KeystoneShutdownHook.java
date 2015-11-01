package com.keystone.server;

public class KeystoneShutdownHook extends Thread {

	public KeystoneShutdownHook() {
		super("keystone-shutdown-thread") ;
	}
	
	
	@Override
	public void run() {
		KeystoneResourcesManager.getAppcontexts() ;//TODO
	}
}
