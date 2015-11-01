package com.keystone.server.message.http;


/**
 * 
 * @author oscarhuang
 *
 */
public class HttpContext 
{
	/**
	 * The HTTP request object.
	 */
	private KeystoneHttpServletRequest request = null;
	
	/**
	 * The HTTP response object.
	 */
	private KeystoneHttpServletResponse response = null;
	
	/**
	 * Constructs a new HTTP context with the request and response.
	 * 
	 */
	public HttpContext(KeystoneHttpServletRequest request, KeystoneHttpServletResponse response)
	{
		this.request = request;
		this.response = response;
	}

	/**
	 * Get the HTTP request from this context.
	 * 
	 * @return
	 */
	public KeystoneHttpServletRequest getRequest() 
	{
		return request;
	}

	/**
	 * Get the HTTP respose from this context.
	 * @return
	 */
	public KeystoneHttpServletResponse getResponse() 
	{
		return response;
	}
}
