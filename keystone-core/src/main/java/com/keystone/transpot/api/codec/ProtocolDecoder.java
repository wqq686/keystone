package com.keystone.transpot.api.codec;

import com.keystone.transpot.api.IoBuffer;

/**
 * 
 * @author wuqq
 *
 */
public interface ProtocolDecoder {

	
	public Object decode(IoBuffer buffer) ;
	
}
