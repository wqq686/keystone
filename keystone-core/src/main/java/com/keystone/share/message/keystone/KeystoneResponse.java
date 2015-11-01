package com.keystone.share.message.keystone;


public class KeystoneResponse extends KeystoneMessage {

	/**
	 * 200代表调用成功, 500代表调用异常
	 */
	private int status = 200 ;
	
	/**
	 * 当status=200时为调用结果的返回值,当status=500时,则可能会调用的异常 
	 */
	private Object result ;
	
	public KeystoneResponse(){}
	
	
	public KeystoneResponse(byte version, int ticket) {
		this.setVersion(version) ; 
		this.setTicket(ticket) ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isOk() {
		return 200 == status ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	
	/**
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	
	/**
	 * 
	 * @return
	 */
	public Object getResult() {
		return result;
	}

	
	/**
	 * 
	 * @param result
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	
	/**
	 * 
	 * @param cause
	 */
	public void setThrowable(String cause) {
		this.status = 500 ;
		this.result = cause ;
	}
	
	@Override
	public String toString() {
		return "KeystoneResponse [status=" + status + ", result=" + result + "]";
	}
	
	
	
}
