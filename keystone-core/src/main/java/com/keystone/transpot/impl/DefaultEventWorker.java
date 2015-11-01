package com.keystone.transpot.impl;

import java.nio.ByteBuffer;

import com.keystone.transpot.api.EventWorker;
import com.keystone.transpot.api.IoBuffer;
import com.keystone.transpot.api.IoSession;
import com.keystone.transpot.api.MessageHandler;
import com.keystone.transpot.api.codec.ProtocolFactory;

public class DefaultEventWorker implements EventWorker {

	
	/**
	 * 
	 */
	private ProtocolFactory protocolFactory ;
	
	
	/**
	 * 
	 */
	private MessageHandler messageHandler ;
	

	@Override
	public EventWorker setProtocolFactory(ProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory ;
		return this ;
	}

	@Override
	public EventWorker setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler ;
		return this ;
	}
	
	
	/**
	 * 
	 */
	@Override
	public void recvMessage(IoSession from) {
		try
		{
			IoBuffer recvBuffer = from.getRecvBuffer().duplicate().flip();
			while(true) 
			{
				Object message = null;
				recvBuffer.mark();  int remaining = recvBuffer.remaining() ;	
				if (remaining > 0)
				{
					message = protocolFactory.getDecoder().decode(recvBuffer);
					if(message!=null)
					{
						messageHandler.handleMessage(message, from) ;
					}
				}
				
				if(message==null)
				{
					recvBuffer.reset();
					recvBuffer = resetIoBuffer(recvBuffer);
					from.setRecvBuffer(recvBuffer) ;
					break;
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 */
	@Override
	public ByteBuffer sendMessage(Object message, IoSession to) {
		IoBuffer packet = protocolFactory.getEncoder().encode(message) ;
		ByteBuffer buf = packet.buf() ;
		buf.flip() ;
		return buf ;
	}

	
	
	/**
	 * 
	 * @param buffer
	 * @return
	 */
	protected IoBuffer resetIoBuffer(IoBuffer buffer) {
		IoBuffer newBuffer = null;
		if (buffer != null && buffer.remaining() > 0)
		{
			int len = buffer.remaining();
    		byte[] bb = new byte[len];
    		buffer.get(bb);
    		newBuffer = IoBuffer.wrap(bb);
    		newBuffer.position(len);
		}
		return newBuffer;
	}

}
