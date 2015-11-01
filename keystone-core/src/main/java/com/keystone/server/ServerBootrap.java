package com.keystone.server;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


public class ServerBootrap {

	/**
	 * 
	 */
	private static final AtomicBoolean started = new AtomicBoolean() ;
	
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 *  
	 */
	public static void main(String[] args) throws Exception {
		if(started.compareAndSet(false, true)) {
			new KeystoneServer().start();
		}
	}
	
}