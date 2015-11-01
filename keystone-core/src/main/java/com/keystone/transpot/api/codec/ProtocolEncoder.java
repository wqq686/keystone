package com.keystone.transpot.api.codec;

import com.keystone.transpot.api.IoBuffer;

/**
 * 
 * @author wuqq
 *
 */
public interface ProtocolEncoder {

	public IoBuffer encode(Object object) ;
}
