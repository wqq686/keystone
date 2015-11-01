package com.keystone.remoting.proxy;

import java.lang.reflect.Method;

import com.keystone.remoting.locator.RemotingProxy;
import com.keystone.share.message.keystone.KeystoneMessage;
import com.keystone.share.services.ParallelFuture;
import com.keystone.share.services.ParallelService;

public class ServiceFutureProxy<T> extends ServiceAbstractProxy<T> implements ParallelService<T> {
	
	/** */
	private ParallelFuture<?> future ;

	
	public ServiceFutureProxy(RemotingProxy remoting) {
		super(remoting) ;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> ParallelFuture<E> getFuture() {
		return (ParallelFuture<E>) future ;
	}

	@Override
	public void setFuture(ParallelFuture<?> future) {
		this.future = future ;
	}

	@Override
	public T getService() { throw new UnsupportedOperationException() ; }
	
	@Override
	protected void beforeRemotingInvocation(RemotingTicket ticket) throws Throwable {
		ticket.setMsgType(KeystoneMessage.MSG_TYPE_ASYNC);
		super.beforeRemotingInvocation(ticket);
	}
	
	@Override
	protected Object afterRemotingInvocation(RemotingTicket ticket) throws Throwable {
		future = new RemotingFutrue<>(ticket) ;
		return getPrimitiveTypeDefaultValue(ticket.getReturnType()) ;
	}
	

	
	
	@SuppressWarnings("unchecked")
	@Override
	Object handleLocalMethod(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();
		if ("getService".equals(methodName)) {
			return (T) proxy;
		} else if ("getFuture".equals(methodName)) {
			return getFuture();
		} else if ("setFuture".equals(methodName)) {
			setFuture((ParallelFuture<?>) args[0]);
			return null;
		}
		return REMOTE_METHOD_INVOKE;
	}


}
