package com.keystone.server;

import com.keystone.share.message.keystone.KeystoneMessage;
import com.keystone.share.message.keystone.KeystoneRequest;
import com.keystone.transpot.api.IoSession;

public class InvocationWorker implements Runnable {

	/**
	 * 
	 */
	private Object request ;
	
	/**
	 * 
	 */
	private IoSession session ;
	
	
	
	public InvocationWorker(Object request, IoSession session) {
		this.request = request;
		this.session = session;
	}



	@Override
	public void run() {
		long start = System.currentTimeMillis() ; Object response = null ;
		try
		{
			response = KeystoneInvocationProccessor.proccess(request) ;
			boolean write = true ;
			if(request instanceof KeystoneRequest && ((KeystoneRequest)request).getMsgType() == KeystoneMessage.MSG_TYPE_SINGLE) {
				write = false ;
			}
			
			if(write) {
				session.asyncWrite(response) ;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
		finally
		{
			KeystoneLoggerManager.printAccessLog(request, response, session, System.currentTimeMillis() - start) ;
		}
	}

}
