package com.keystone.server.protocol.codec;

import java.lang.reflect.Type;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.keystone.server.contexts.KeystoneServiceAnalyzer;
import com.keystone.share.message.keystone.KeystoneMessage;
import com.keystone.share.message.keystone.KeystoneRequest;
import com.keystone.share.message.keystone.KeystoneResponse;
import com.keystone.support.common.CommonUtils;
import com.keystone.transpot.api.IoBuffer;
import com.keystone.transpot.api.codec.ProtocolException;

public class KeystoneProtocolCodec {

	/**
	 * 
	 */
	private static Object[] __EMPTY__OBJECT__ARRAY__ = new Object[0] ;
	
	
	/**
	 * request protocol: [magic(1)] + [len(4)] + [version(1)] + [ticket(4)] + [msgType(1)] + [header length(4)] + [header body] + [parameter length(4)] + [parameter body] + [distribute length(4)] + [distribute body]
	 * 
	 */
	public static KeystoneRequest decodeRequest(IoBuffer buffer) {
		if (buffer.remaining() < 6) return null;
		byte magic = buffer.get() ;
		if (magic!= KeystoneMessage.KEY_STONE_PROTOCOL_MAGIC_NUMBER) throw new ProtocolException("Bad Keystone Request.");
		
		//1. read protocol length
		int sum_length = buffer.getInt() ;
		if (buffer.remaining() < sum_length) return null;
		KeystoneRequest request = new KeystoneRequest() ;
		
		//2. read protocol version
		byte version = buffer.get() ;
		if (version != KeystoneMessage.KEY_STONE_PROTOCOL_VERSION_1_2) throw new ProtocolException("Invalid Keystone Protocol Version.");
		sum_length -= 1 ;// skip version(1)
		request.setVersion(version) ;
		
		int ticket = buffer.getInt() ;
		sum_length -= 4 ;// skip ticket(4)
		request.setTicket(ticket) ;
		
		byte msgType = buffer.get() ;
		sum_length -= 1 ;// skip msgType(1)
		request.setMsgType(msgType);
		
		int head_length = buffer.getInt() ; //head length
		sum_length -= 4 ;// skip head length(4)
		byte[] header_data = new byte[head_length] ;
		buffer.get(header_data) ;
		sum_length -= head_length ;
		String[] header = JSON.parseObject(header_data, String[].class) ;
		request.setContextName(header[0]) ;
		request.setServiceName(header[1]) ;
		request.setMethodName(header[2]) ;		

		Object[] parameters = __EMPTY__OBJECT__ARRAY__ ;
		if(sum_length>0)
		{
			int parameters_length = buffer.getInt() ;
			sum_length -= 4 ;
			byte[] parameters_data = new byte[parameters_length] ;
			buffer.get(parameters_data) ;
			Type[] types = KeystoneServiceAnalyzer.getMethodParameterTypes(request.getContextName(), request.getServiceName(), request.getMethodName()) ;
			parameters = JSON.parseArray(new String(parameters_data), types).toArray() ;
			sum_length -= parameters_length ;
		}
		request.setParameters(parameters) ;
		
		Map<String, String> distribute = CommonUtils.emptyMap() ;
		if(sum_length>0)
		{
			int distribute_length = buffer.getInt() ;
			byte[] distribute_data = new byte[distribute_length] ;
			buffer.get(distribute_data) ;
			distribute = JSON.parseObject(new String(distribute_data), new TypeReference<Map<String, String>>(){}) ;
		}
		request.setDistributed(distribute);
		
		return request ;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * response protocol: [magic(1)] + [len(4)] + [version(1)] + [ticket(4)] + [status(4)] + [body]
	 * @param response
	 * @return
	 */
	public static IoBuffer encodeResponse(KeystoneResponse response) {
		IoBuffer buffer = IoBuffer.allocate(1024);
		
		int len = 0 ;
		buffer.put(KeystoneMessage.KEY_STONE_PROTOCOL_MAGIC_NUMBER);
		buffer.put(len) ;//length
		
		buffer.put(KeystoneMessage.KEY_STONE_PROTOCOL_VERSION_1_2) ; //version
		buffer.put(response.getTicket()) ;//ticket
		buffer.put(response.getStatus()) ;//status
		len = 1 + 4 + 4 ;//version(1) + ticket(4) + status(4) + data length
		byte[] result_data = null ;
		if(response.getResult()!=null)
		{
			result_data = JSON.toJSONBytes(response.getResult(), SerializerFeature.WriteClassName, SerializerFeature.WriteDateUseDateFormat) ;
			buffer.put(result_data) ;
			len += result_data.length ;
		}
		
		int position = buffer.position() ;
		buffer.position(1).put(len).position(position) ;
		return buffer;
	}
	

	
}
