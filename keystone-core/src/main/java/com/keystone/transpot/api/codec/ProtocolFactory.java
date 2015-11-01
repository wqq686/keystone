package com.keystone.transpot.api.codec;


/**
 * 
 * @author wuqq
 *
 */
public interface ProtocolFactory {
	
	/**
	 * 
	 * @return
	 */
	public ProtocolEncoder getEncoder();
	
	/**
	 * 
	 * @return
	 */
	public ProtocolDecoder getDecoder();
	
}
