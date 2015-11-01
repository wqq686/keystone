package com.keystone.remoting;

import com.alibaba.fastjson.JSON;
import com.keystone.remoting.proxy.RemotingTicket;
import com.keystone.share.message.keystone.KeystoneResponse;
import com.keystone.share.services.AsyncCallback;
import com.keystone.transpot.api.IoSession;
import com.keystone.transpot.api.MessageHandler;

public class RemotingMessageHandler implements MessageHandler {

	
	@Override
	public void handleMessage(final Object message, IoSession session) {
		RemotingInvocationManager.getRemotingExecutor().execute(new Runnable() {
			@Override
			public void run() {
				KeystoneResponse response = (KeystoneResponse) message ;
				RemotingTicket ticket = RemotingInvocationManager.removeTicket(response.getTicket()) ;
				if(ticket!=null)
				{
					//1. 
					response2Ticket(response, ticket);
					
					//2. 
					ticket.countDown() ;
					
					//3. 
					processCallback(ticket);
				}
			}
		});
	}
	
	
	
	
	
	static void response2Ticket(KeystoneResponse response, RemotingTicket ticket) {
		byte[] body = response.getBody() ; 
		if(body!=null) {
			boolean isOk = response.isOk() ;
			ticket.setOK(isOk) ;
			if(isOk) {
				Object result = JSON.parseObject(body, ticket.getReturnType()) ;
				ticket.setResult(result) ;
			} else {
				String cause = (String)JSON.parse(body) ;
				RuntimeException exception = new IllegalStateException(cause) ;
				ticket.setException(exception);
			}
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static void processCallback(RemotingTicket ticket) {
		AsyncCallback callback = ticket.getCallback() ;
		if(callback!=null) {
			if(ticket.isOK()) {
				callback.onCompleted(ticket.getResult());
			} else {
				callback.onException(ticket.getException());
			}
		}
	}
	
//	@Override
//	public void handleMessage(final Object message, IoSession session) {
//		RemotingInvocationManager.getRemotingExecutor().execute(new Runnable() {
//			@Override
//			public void run() {
//				KeystoneResponse response = (KeystoneResponse) message ;
//				RemotingTicket ticket = RemotingInvocationManager.removeTicket(response.getTicket()) ;
//				if(ticket!=null)
//				{
//					byte[] body = response.getBody() ;
//					Object result = null ;
//					if(body!=null)
//					{
//						if(response.isOk())
//						{
//							result = JSON.parseObject(body, ticket.getReturnType()) ;
//						}
//						else
//						{
//							result =  JSON.parse(body) ;
//						}
//					}
//					ticket.setOK(response.isOk()) ;
//					ticket.setResult(result) ;
//					ticket.countDown() ;
//				}
//			}
//		});
//	}

	
	
	
}
