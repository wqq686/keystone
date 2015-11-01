package com.keystone.remoting.proxy;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.keystone.share.message.keystone.KeystoneRequest;
import com.keystone.share.services.AsyncCallback;


/**
 * 
 * @author wuqq
 *
 */
public class RemotingTicket extends KeystoneRequest {

	/** */
	private CountDownLatch latcher = new CountDownLatch(1) ;
	
	/** */
	private int timeout ;
	
	/** */
	private Class<?> returnType ;
	
	/** */
	private Object result ;
	
	/** */
	private boolean isOK ;
	
	/** */
	private AsyncCallback<?> callback ;
	
	/** */
	private RuntimeException exception ;
	
	
	/**
	 * 
	 * @throws InterruptedException
	 */
	public void await() throws InterruptedException {
		latcher.await() ;
	}
	
	
	
	/**
	 * 
	 * @param timeout
	 * @param unit
	 * @throws InterruptedException
	 */
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return latcher.await(timeout, unit);
	}
	
	
	
	/**
	 * 
	 */
	public void countDown() {
		latcher.countDown() ;
	}


	public int getTimeout() {
		return timeout;
	}


	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


	public Class<?> getReturnType() {
		return returnType;
	}


	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}


	public Object getResult() {
		return result;
	}


	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isOK() {
		return isOK;
	}

	
	/**
	 * 
	 * @param isOK
	 */
	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

	
	/**
	 * 
	 * @return
	 */
	public boolean isDone() {
		return latcher.getCount() == 0 ;
	}

	
	/**
	 * 
	 * @return
	 */
	public AsyncCallback<?> getCallback() {
		return callback;
	}


	/**
	 * 
	 * @param callback
	 */
	public void setCallback(AsyncCallback<?> callback) {
		this.callback = callback;
	}

	
	public RuntimeException getException() {
		return exception;
	}


	public void setException(RuntimeException exception) {
		this.exception = exception;
	}



	/**
	 * 
	 * @throws IOException
	 */
	public void operationTimeout() throws IOException {
		throw new IOException("The operation has timed out.");
	}
	

	/**
	 * 
	 * @return
	 */
	public boolean isTimeout() {
		return System.currentTimeMillis() - getBornTime() > getTimeout() ;
	}
	
	@Override
	public String toString() {
		return "RemotingTicket [ticket="+this.getTicket()+", latcher=" + latcher + ", timeout=" + timeout + ", returnType=" + returnType + ", result=" + result + ", isOK=" + isOK + ", callback=" + callback + "]";
	}




	
}
