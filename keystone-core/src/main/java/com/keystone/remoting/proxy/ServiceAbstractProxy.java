package com.keystone.remoting.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.keystone.remoting.RemotingInvocationManager;
import com.keystone.remoting.locator.RemotingProxy;
import com.keystone.share.distributed.DistributedContextFactory;

public abstract class ServiceAbstractProxy<T> implements InvocationHandler {
	
	static final Object REMOTE_METHOD_INVOKE = new Object() ;
	
	/** */
	private static AtomicInteger ticketer = new AtomicInteger() ;
	
	/** */
	RemotingProxy remoting ;
	
	
	ServiceAbstractProxy(RemotingProxy remoting) {
		this.remoting = remoting ;
	}
	
	
	
	public RemotingProxy getRemotingProxy() {
		return remoting;
	}



	@Override
	public Object invoke(Object service, Method method, Object[] args) throws Throwable {
		//1. handle local method
		Object ret = handleLocalMethod(service, method, args) ;
		if(ret!=REMOTE_METHOD_INVOKE) return ret ;
		
		//2. create remote ticket
		RemotingTicket ticket = createRemotingTicket(remoting, method, args) ;
		Map<String, String> distribute = DistributedContextFactory.exportDistributedContext() ;
		ticket.setDistributed(distribute);
		//3. process invoke
		beforeRemotingInvocation(ticket) ;
		
		remoting.getClient().invoke(ticket);
		
		return afterRemotingInvocation(ticket) ;
		
	}
	
	
	
	/**
	 * 
	 * @param ticket
	 * @return
	 * @throws Throwable
	 */
	protected void beforeRemotingInvocation(RemotingTicket ticket) throws Throwable {
		RemotingInvocationManager.registerTicket(ticket) ;
	}
	
	
	
	/**
	 * 
	 * @param ticket
	 * @return
	 * @throws Throwable
	 */
	protected abstract Object afterRemotingInvocation(RemotingTicket ticket) throws Throwable ;
	
	
	
	
	
	RemotingTicket createRemotingTicket(RemotingProxy proxy, Method method, Object[] args) {
    	RemotingTicket ticket = new RemotingTicket() ;
    	ticket.setTicket(getTicket()) ;
    	ticket.setContextName(proxy.getLocation().getContextName()) ;
    	ticket.setServiceName(proxy.getLocation().getServiceName()) ;
    	ticket.setMethodName(method.getName()) ;
    	ticket.setParameters(args) ;
    	ticket.setReturnType(method.getReturnType());
    	ticket.setTimeout(proxy.getLocation().getReadTimeout());
    	return ticket ;
	}
	
	

	Object handleLocalMethod(Object service, Method method, Object[] args) throws Throwable {
		return REMOTE_METHOD_INVOKE ;
	}
	
	
	private int getTicket() {
		int ticket = ticketer.getAndIncrement() ;
		if(ticket<0)
		{
			ticketer.compareAndSet(ticket, 0) ;
			ticket = ticketer.getAndIncrement() ;
		}
		return ticket ;
	}
	
	
	
	/**
	 * 
	 * @param primitive
	 * @return
	 */
	static Object getPrimitiveTypeDefaultValue(Class<?> primitive) {
		if (primitive.equals(java.lang.Byte.TYPE) ) {
			return (byte) 0;
		} else if (primitive.equals(java.lang.Character.TYPE)) {
			return (char) 0;
		} else if (primitive.equals(java.lang.Boolean.TYPE)) {
			return (boolean)false;
		} else if (primitive.equals(java.lang.Short.TYPE)) {
			return (short) 0;
		} else if (primitive.equals(java.lang.Integer.TYPE)) {
			return (int) 0;
		} else if (primitive.equals(java.lang.Long.TYPE)) {
			return (long) 0;
		} else if (primitive.equals(java.lang.Float.TYPE)) {
			return (float) 0;
		} else if (primitive.equals(java.lang.Double.TYPE)) {
			return (double) 0;
		}
		
		return null;
	}


	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [location=" + remoting + "]";
	}
	
	
	
}
