package com.keystone.server.protocol.codec;

import com.keystone.share.message.keystone.KeystoneMessage;
import com.keystone.transpot.api.IoBuffer;
import com.keystone.transpot.api.codec.ProtocolDecoder;

/**
 * 
 * @author wuqq
 *
 */
public class MultiplexingProtocolDecoder implements ProtocolDecoder{

	
	/**
	 * 
	 */
	@Override
	public Object decode(IoBuffer buffer) {
		if (buffer.remaining() < 1) return null;
		
		buffer.mark();  byte magic = buffer.get();  buffer.reset();
		
		if(magic==KeystoneMessage.KEY_STONE_PROTOCOL_MAGIC_NUMBER)
		{
			return KeystoneProtocolCodec.decodeRequest(buffer) ;
		}
		else if(magic=='G' || magic == 'P')
		{
			return HttpProtocolCodec.decodeRequest(buffer) ;
		}
		else
		{
			throw new UnsupportedOperationException("UnSupportedProtocol:MagicNumber=["+magic+"]") ;
		}
	}

}
