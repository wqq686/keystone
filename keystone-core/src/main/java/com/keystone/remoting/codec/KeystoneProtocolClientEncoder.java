package com.keystone.remoting.codec;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.keystone.share.message.keystone.KeystoneMessage;
import com.keystone.share.message.keystone.KeystoneRequest;
import com.keystone.transpot.api.IoBuffer;
import com.keystone.transpot.api.codec.ProtocolEncoder;

public class KeystoneProtocolClientEncoder implements ProtocolEncoder {

	
	
	/**
	 * request protocol: [magic(1)] + [len(4)] + [version(1)] + [ticket(4)] + [msgType(1)] + [header length(4)] + [header body] + [parameter length(4)] + [parameter body] + [distribute length(4)] + [distribute body]
	 * 
	 */
	@Override
	public IoBuffer encode(Object message) {
		if(message instanceof KeystoneRequest)
		{
			KeystoneRequest request = (KeystoneRequest) message ;
			IoBuffer buffer = IoBuffer.allocate(1024);
			buffer.put(KeystoneMessage.KEY_STONE_PROTOCOL_MAGIC_NUMBER) ;
			buffer.buf().putInt(0) ;
			buffer.put(KeystoneMessage.KEY_STONE_PROTOCOL_VERSION_1_2) ;
			buffer.put(request.getTicket()) ;
			buffer.put(request.getMsgType()) ;
			//version + ticket + msgType
			int len = 1 + 4 + 1;
			
			String[] header = new String[]{ request.getContextName(), request.getServiceName(), request.getMethodName() } ;
			byte[] header_body = JSON.toJSONBytes(header) ;
			buffer.put(header_body.length).put(header_body) ;
			len += 4 + header_body.length ;
			
			Object[] parameters = request.getParameters() ;
			if(parameters!=null)
			{
				byte[] body = JSON.toJSONBytes(parameters) ;
				buffer.put(body.length).put(body) ;
				len += 4 + body.length ;
			}
			
			Map<String, String> distributed = request.getDistributed() ;
			if(distributed!=null)
			{
				byte[] body = JSON.toJSONBytes(distributed) ;
				buffer.put(body.length).put(body) ;
				len += 4 + body.length ;
			}
			
			int position = buffer.position() ;
			buffer.position(1).put(len).position(position) ;
			return buffer ;
		}
		return null;
	}

}
