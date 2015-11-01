package com.keystone.server.message.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * 
 * @author oscarhuang
 *
 */
public class HttpOutputStream extends ServletOutputStream
{
	/**
	 * 
	 */
	private ByteArrayOutputStream baos = null; 
	
	/**
	 * 
	 */
	protected HttpOutputStream()
	{
		baos = new ByteArrayOutputStream();
	}
	
	@Override
	public void write(int b) throws IOException 
	{
		baos.write(b);
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void reset()
	{
		baos.reset();
	}
	
	/**
	 * 
	 * @return
	 */
	public int size()
	{
		return baos.size();
	}
	
	/**
	 * 
	 * @return
	 */
	public byte[] toByteArray()
	{
		return baos.toByteArray();
	}
	
    public void flush() throws IOException {
        baos.flush();
    }
}
