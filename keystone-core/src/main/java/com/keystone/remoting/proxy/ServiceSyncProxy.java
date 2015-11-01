package com.keystone.remoting.proxy;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.keystone.remoting.locator.RemotingProxy;
import com.keystone.share.message.keystone.KeystoneMessage;


public class ServiceSyncProxy<T> extends ServiceAbstractProxy<T> {
	
	
	public ServiceSyncProxy(RemotingProxy remoting) {
		super(remoting);
	}


	@Override
	protected void beforeRemotingInvocation(RemotingTicket ticket) throws Throwable {
		ticket.setMsgType(KeystoneMessage.MSG_TYPE_SYNC);
		super.beforeRemotingInvocation(ticket);
	}

	
	@Override
	protected Object afterRemotingInvocation(RemotingTicket ticket) throws InterruptedException, IOException {
    	if(!ticket.await(getRemotingProxy().getLocation().getReadTimeout(), TimeUnit.MILLISECONDS)) {
    		ticket.operationTimeout();
    	}
    	
    	
    	if(!ticket.isOK()) {
    		throw ticket.getException() ;
    	}
    	
        return ticket.getResult() ;
	}






	
}
