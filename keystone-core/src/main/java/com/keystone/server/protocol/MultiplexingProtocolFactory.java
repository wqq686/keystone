package com.keystone.server.protocol;

import com.keystone.server.protocol.codec.MultiplexingProtocolDecoder;
import com.keystone.server.protocol.codec.MultiplexingProtocolEncoder;
import com.keystone.transpot.api.codec.ProtocolDecoder;
import com.keystone.transpot.api.codec.ProtocolEncoder;
import com.keystone.transpot.api.codec.ProtocolFactory;

public class MultiplexingProtocolFactory implements ProtocolFactory {

	
	/**
	 * 
	 */
	private ProtocolEncoder encoder = new MultiplexingProtocolEncoder() ;
	
	
	/**
	 * 
	 */
	private ProtocolDecoder decoder = new MultiplexingProtocolDecoder() ;
	
	
	@Override
	public ProtocolEncoder getEncoder() {
		return encoder ;
	}

	@Override
	public ProtocolDecoder getDecoder() {
		return decoder ;
	}
	
	
//	MultiplexingProtocolDecoder
//	MultiplexingProtocolEncoder
}
