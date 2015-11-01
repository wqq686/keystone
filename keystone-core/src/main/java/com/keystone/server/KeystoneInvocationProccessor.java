package com.keystone.server;

import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.message.http.KeystoneHttpServletRequest;
import com.keystone.server.message.http.KeystoneHttpServletResponse;
import com.keystone.server.processor.HTTPProccessor;
import com.keystone.server.processor.ServiceProccessor;
import com.keystone.share.distributed.DistributedContextFactory;
import com.keystone.share.message.keystone.KeystoneRequest;
import com.keystone.share.message.keystone.KeystoneResponse;
import com.keystone.support.common.ExceptionUtils;

public class KeystoneInvocationProccessor {

	
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static Object proccess(Object request) {
		KeystoneRequest keystoneRequest = null ; KeystoneResponse keystoneResponse = null ;
		KeystoneHttpServletRequest httpRequest = null ; KeystoneHttpServletResponse httpResponse = null ;
		if(request instanceof KeystoneRequest) {
			keystoneRequest = (KeystoneRequest) request ;
			keystoneResponse = new KeystoneResponse(keystoneRequest.getVersion(), keystoneRequest.getTicket()) ;
		} else if(request instanceof KeystoneHttpServletRequest) {
			httpRequest = (KeystoneHttpServletRequest) request ;
			httpResponse = new KeystoneHttpServletResponse() ;
		}
		
		ClassLoader currentClassLoader = null ;
		try
		{
			//1. found AppContext
			KeystoneAppContext appContext = foundAppContext(keystoneRequest, httpRequest) ;
			
			//2. is Waiting Too Long
			isWaitingTooLong(appContext, keystoneRequest, httpRequest);
			
			//3. switch ClassLoader
			currentClassLoader = Thread.currentThread().getContextClassLoader() ;
			Thread.currentThread().setContextClassLoader(appContext.getContextClassLoader());
			
			//4. proccess Request
			if(keystoneRequest!=null) {
				DistributedContextFactory.buildDistributedContext(keystoneRequest.getDistributed());
				ServiceProccessor.proccessRequest(keystoneRequest, keystoneResponse, appContext) ;
			} else if(httpRequest!=null) {
				HTTPProccessor.proccessRequest(httpRequest, httpResponse, appContext) ;
			}
		}
		catch(Throwable tw)
		{
			proccessThrowable(tw, keystoneRequest, keystoneResponse, httpRequest, httpResponse);
		}
		finally
		{
			DistributedContextFactory.clearDistributedContext();
			if(currentClassLoader!=null) Thread.currentThread().setContextClassLoader(currentClassLoader);
		}
		
		if(httpResponse!=null) {
			try{httpResponse.flushBuffer();}catch(Exception ignore){}
		}
		
		return keystoneResponse != null ? keystoneResponse : httpResponse ;
	}
	

	
	/**
	 * 
	 * @param keystoneRequest
	 * @param httpRequest
	 * @return
	 */
	private static KeystoneAppContext foundAppContext(KeystoneRequest keystoneRequest, KeystoneHttpServletRequest httpRequest) {
		String contextName = keystoneRequest != null ? keystoneRequest.getContextName() : httpRequest.getContextPath() ;
		KeystoneAppContext appContext = KeystoneResourcesManager.getAppContext(contextName, true) ;
		return appContext ;
	}
	
	
	
	
	
	/**
	 * 
	 * @param appContext
	 * @param keystoneRequest
	 * @param httpRequest
	 */
	private static void isWaitingTooLong(KeystoneAppContext appContext, KeystoneRequest keystoneRequest, KeystoneHttpServletRequest httpRequest) {
		if(appContext.getContextConfig().getWaitInQueue()<=0) return ;
		
		long bornTime = 0 ;
		if(keystoneRequest!=null) bornTime = keystoneRequest.getBornTime() ;
		else if(httpRequest!=null) bornTime = httpRequest.getBornTime() ;
		
		if(System.currentTimeMillis()-bornTime>appContext.getContextConfig().getWaitInQueue())
		{
			throw new IllegalStateException("Wait too long, server busy.");
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @param tw
	 * @param keystoneRequest
	 * @param keystoneResponse
	 * @param httpRequest
	 * @param httpResponse
	 */
	private static void proccessThrowable(Throwable tw, KeystoneRequest keystoneRequest, KeystoneResponse keystoneResponse, KeystoneHttpServletRequest httpRequest, KeystoneHttpServletResponse httpResponse) {
		try
		{
			Throwable real = ExceptionUtils.foundRealThrowable(tw) ;
			boolean print = true ;
			if(httpResponse!=null)
			{
				httpResponse.setCharacterEncoding("UTF-8");
				httpResponse.setContentType("text/plain");
				
				String message = real.getMessage() ;
				if(message!=null)
				{
					if(message.contains("NotFound") || message.contains("Not Found"))
					{
						httpResponse.setStatus(404);
						httpResponse.getWriter().write(message);
						print = false ;
					} else if(message.contains("Wait too long")) {
						httpResponse.setStatus(500);
						httpResponse.getWriter().write(message);
						print = false ;
					}
				}
			}
			
			
			if(print)
			{
				String cause = ExceptionUtils.formatThrowable(real) ;
				if(keystoneResponse!=null)
				{
					keystoneResponse.setThrowable(cause);
				}
				else if(httpResponse!=null)
				{
					httpResponse.setStatus(500);
					httpResponse.getWriter().write(cause);
				}
			}
			System.err.print("proccess "+(keystoneRequest!=null ? keystoneRequest : httpRequest)+ " with ex:" + ExceptionUtils.formatThrowable(real)) ;
			
		}
		catch(Throwable t)
		{
			t.printStackTrace(); 
		}
	}
	
	
}
