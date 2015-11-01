package com.keystone.remoting.codec;

import com.keystone.transpot.api.codec.ProtocolDecoder;
import com.keystone.transpot.api.codec.ProtocolEncoder;
import com.keystone.transpot.api.codec.ProtocolFactory;

public class KeystoneClientProtocolFactory implements ProtocolFactory {

	/**
	 * 
	 */
	private ProtocolEncoder encoder = new KeystoneProtocolClientEncoder() ;
	
	
	/**
	 * 
	 */
	private ProtocolDecoder decoder = new KeystoneProtocolClientDecoder() ;
	
	
	/**
	 * 
	 */
	@Override
	public ProtocolEncoder getEncoder() {
		return encoder ;
	}

	/**
	 * 
	 */
	@Override
	public ProtocolDecoder getDecoder() {
		return decoder ;
	}

}
