package com.keystone.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import com.keystone.transpot.api.IoSession;
import com.keystone.transpot.impl.DefaultIoWorker;

public class KeystoneIoWorker extends DefaultIoWorker {
	
	
	@Override
	public IoSession processAccept(SelectionKey key) throws IOException {
		IoSession session = super.processAccept(key) ;
		KeystoneSessionManager.getInstance().registerSession(session) ;
		return session ;
	}
	
	
	
	@Override
	public void processClose(IoSession session) {
		KeystoneSessionManager.getInstance().unregisterSession(session) ;
		super.processClose(session) ;
	}
}
