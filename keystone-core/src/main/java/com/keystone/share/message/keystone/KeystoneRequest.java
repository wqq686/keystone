package com.keystone.share.message.keystone;

import java.util.Arrays;
import java.util.Map;

public class KeystoneRequest extends KeystoneMessage {
	
	/**
	 * 
	 */
	private String contextName ;
	
	/**
	 * 
	 */
	private String serviceName ;
	
	/**
	 * 
	 */
	private String methodName ;
	
	/**
	 * 
	 */
	private Object[] parameters ;
	
	/**
	 * 
	 */
	private Map<String, String> distributed ;
	
	/**
	 * 
	 */
	private long bornTime ;
	
	
	
	public KeystoneRequest() {
		this.bornTime = System.currentTimeMillis() ;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getContextName() {
		return contextName;
	}

	
	/**
	 * 
	 * @param contextName
	 */
	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	
	/**
	 * 
	 * @return
	 */
	public String getServiceName() {
		return serviceName;
	}

	
	/**
	 * 
	 * @param serviceName
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	
	/**
	 * 
	 * @return
	 */
	public String getMethodName() {
		return methodName;
	}

	
	/**
	 * 
	 * @param methodName
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	
	/**
	 * 
	 * @return
	 */
	public Object[] getParameters() {
		return parameters;
	}

	
	/**
	 * 
	 * @param parameters
	 */
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> getDistributed() {
		return distributed;
	}

	
	/**
	 * 
	 * @param distributed
	 */
	public void setDistributed(Map<String, String> distributed) {
		this.distributed = distributed;
	}

	/**
	 * 
	 * @return
	 */
	public long getBornTime() {
		return bornTime;
	}


	/**
	 * 
	 * @param bornTime
	 */
	public void setBornTime(long bornTime) {
		this.bornTime = bornTime;
	}

	@Override
	public String toString() {
		return "KeystoneRequest [contextName=" + contextName + ", serviceName=" + serviceName + ", methodName=" + methodName + ", parameters=" + Arrays.toString(parameters) + ", distribute=" + distributed + ", bornTime=" + bornTime + "]";
	}



	
}
