package com.keystone.server.message.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;

/**
 * 
 * @author oscarhuang
 *
 */
public class HttpInputStream extends ServletInputStream
{

	/**
	 * 
	 */
	private ByteArrayInputStream input = null;
	
	/**
	 * 
	 */
	private byte[] data = null; 
	
	/**
	 * 
	 * @param data
	 */
	public HttpInputStream(byte[] data)
	{
		this.input = new ByteArrayInputStream(data);
		this.data = data;
	}
	
	@Override
	public int read() throws IOException 
	{
		return input.read();
	}
	
	/**
	 * 
	 * @return
	 */
	public byte[] toByteArray()
	{
		return this.data;
	}
}
