package com.keystone.remoting.proxy;

import java.lang.reflect.Method;

import com.keystone.remoting.locator.RemotingProxy;
import com.keystone.share.message.keystone.KeystoneMessage;
import com.keystone.share.services.AsyncCallback;
import com.keystone.share.services.AsyncService;


public class ServiceAsyncProxy<T> extends ServiceAbstractProxy<T> implements AsyncService<T> {
		
	/** */
	private AsyncCallback<?> callback ;


	public ServiceAsyncProxy(RemotingProxy remoting) {
		super(remoting) ;
	}
	
	@Override
	public void setCallback(AsyncCallback<?> callback) {
		this.callback = callback ;
	}

	@Override
	public AsyncCallback<?> getCallback() {
		return this.callback;
	}
	
	@Override
	public T getService() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	RemotingTicket createRemotingTicket(RemotingProxy remoting, Method method, Object[] args) {
    	RemotingTicket ticket = super.createRemotingTicket(remoting, method, args) ;
    	ticket.setCallback(callback);
    	return ticket ;
	}

	@Override
	protected void beforeRemotingInvocation(RemotingTicket ticket) throws Throwable {
		ticket.setMsgType(KeystoneMessage.MSG_TYPE_ASYNC);
		super.beforeRemotingInvocation(ticket);
	}
	
	@Override
	protected Object afterRemotingInvocation(RemotingTicket ticket) throws Throwable {
		return getPrimitiveTypeDefaultValue(ticket.getReturnType()) ;
	}
	

	
	
	@SuppressWarnings("unchecked")
	@Override
	Object handleLocalMethod(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();
		if ("getService".equals(methodName)) {
			return (T) proxy;
		} else if ("getCallback".equals(methodName)) {
			return getCallback();
		} else if ("setCallback".equals(methodName)) {
			setCallback((AsyncCallback<?>) args[0]);
			return null;
		}
		return REMOTE_METHOD_INVOKE;
	}


}
