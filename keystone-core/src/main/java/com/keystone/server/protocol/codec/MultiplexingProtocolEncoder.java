package com.keystone.server.protocol.codec;

import javax.servlet.http.HttpServletResponse;

import com.keystone.server.message.http.KeystoneHttpServletResponse;
import com.keystone.share.message.keystone.KeystoneResponse;
import com.keystone.transpot.api.IoBuffer;
import com.keystone.transpot.api.codec.ProtocolEncoder;

public class MultiplexingProtocolEncoder implements ProtocolEncoder{

	@Override
	public IoBuffer encode(Object message) {
		if(message instanceof KeystoneResponse)
		{
			return KeystoneProtocolCodec.encodeResponse((KeystoneResponse)message) ;
		}
		else if(message instanceof KeystoneHttpServletResponse)
		{
			return HttpProtocolCodec.encodeResponse((HttpServletResponse) message) ;
		}
		else
		{
			throw new UnsupportedOperationException("UnSupportedProtocol:Message=["+message+"]") ;
		}
	}

}
