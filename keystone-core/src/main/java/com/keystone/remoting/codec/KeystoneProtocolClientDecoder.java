package com.keystone.remoting.codec;

import com.keystone.share.message.keystone.KeystoneMessage;
import com.keystone.share.message.keystone.KeystoneResponse;
import com.keystone.transpot.api.IoBuffer;
import com.keystone.transpot.api.codec.ProtocolDecoder;
import com.keystone.transpot.api.codec.ProtocolException;

public class KeystoneProtocolClientDecoder implements ProtocolDecoder{


	@Override
	public Object decode(IoBuffer buffer) {
		if (buffer.remaining() < 6) return null;
		if (buffer.get() != KeystoneMessage.KEY_STONE_PROTOCOL_MAGIC_NUMBER) throw new ProtocolException("Bad ks Response.");
		
		int len = buffer.getInt() ;
		if (buffer.remaining() < len) return null;
		
		byte version = buffer.get() ;
		int ticket = buffer.getInt(), status = buffer.getInt() ;
		int blen = len - 1 - 4 -4 ;
		byte[] data = null ;
		if(blen>0)
		{
			data = new byte[blen] ;
			buffer.get(data) ;
		}
		
		KeystoneResponse response = new KeystoneResponse() ;
		response.setVersion(version) ;
		response.setTicket(ticket) ;
		response.setStatus(status) ;
		response.setBody(data) ;
		return response ;
	}

}
