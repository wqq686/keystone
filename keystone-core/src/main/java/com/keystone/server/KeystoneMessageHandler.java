package com.keystone.server;

import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.message.http.KeystoneHttpServletRequest;
import com.keystone.share.message.keystone.KeystoneRequest;
import com.keystone.transpot.api.IoSession;
import com.keystone.transpot.impl.DefaultMessageHandler;

public class KeystoneMessageHandler extends DefaultMessageHandler {

	
	
	/**
	 * 
	 */
	@Override
	public void handleMessage(Object request, IoSession session) {
		String contextName = getContextName(request) ;
		//TODO:暂不处理icon问题. if("favicon.ico".equals(httpRequest.getContextPath()))
		KeystoneAppContext appContext = KeystoneResourcesManager.getAppContext(contextName, false) ;
		KeystoneExecutors.execute(appContext, new InvocationWorker(request, session)) ;
	}
	
	
	
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	private String getContextName(Object request) {
		String contextName = null ;
		if(request instanceof KeystoneRequest)
		{
			contextName = ((KeystoneRequest) request).getContextName() ;
		}
		else if(request instanceof KeystoneHttpServletRequest)
		{
			contextName = ((KeystoneHttpServletRequest) request).getContextPath() ;
		}
		return contextName ;
	}
}
