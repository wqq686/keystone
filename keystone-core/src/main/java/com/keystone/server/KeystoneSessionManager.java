package com.keystone.server;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.keystone.transpot.api.IoSession;

public class KeystoneSessionManager extends Thread {
	
	/**
	 * 
	 */
	private static final KeystoneSessionManager instance = new KeystoneSessionManager() ;
	
	
	/**
	 * 
	 */
	private long sessionTimeout = TimeUnit.MILLISECONDS.convert(3, TimeUnit.MINUTES) ;
	
	/**
	 * 
	 */
	private long sessionCheckInterval = TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);
	
	/**
	 * 
	 */
	private Set<IoSession> sessionSet = Collections.newSetFromMap(new ConcurrentHashMap<IoSession, Boolean>()); 
	
	
	
	/**
	 * 
	 * @return
	 */
	public static KeystoneSessionManager getInstance() {
		return instance ;
	}
	
	
	
	private KeystoneSessionManager() {
		setName("keystone-session-manager") ;
	}
	
	
	
	public KeystoneSessionManager setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout ;
		return this ;
	}

	
	public KeystoneSessionManager setSessionCheckInterval(long sessionCheckInterval) {
		this.sessionCheckInterval = sessionCheckInterval ;
		return this ;
	}

	
	@Override
	public void run() {
		System.out.println("[KEYSTONE] The session manager started...");
		long lastUpdateOperationTime = -1;
		for(;!Thread.interrupted();) 
		{
			try 					
			{
				Thread.sleep(sessionCheckInterval);
				long now = System.currentTimeMillis() ;
				for(IoSession session : sessionSet) 
				{
					lastUpdateOperationTime = session.getLastOperationTime();							
                    if ((now - lastUpdateOperationTime) > sessionTimeout) 
                    {
                      clearUpSession(session) ;   
                    }
				}
			} catch (Exception ignore){}
		}
	}

	
	
	
	/**
	 * 
	 * @param session
	 */
	protected void clearUpSession(IoSession session) {
		session.close();
        unregisterSession(session);
		InetSocketAddress from = session.getRemoteSocketAddress() ;
	    String message = "The session has timed out. [from : " + from.getHostName()  + ":" + from.getPort() + "]";
	    KeystoneLoggerManager.access.info(message) ;
	}
	
	
	
	/**
	 * 
	 */
	public KeystoneSessionManager registerSession(IoSession session) {
		sessionSet.add(session);
		return this ;
	}
	
	
	
	/**
	 * 
	 */
	public KeystoneSessionManager unregisterSession(IoSession session) {
		sessionSet.remove(session);
		return this ;
	}
}
