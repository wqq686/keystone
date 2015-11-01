package com.keystone.server.admin.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.keystone.server.KeystoneInvocationProccessor;
import com.keystone.server.KeystoneResourcesManager;
import com.keystone.server.contexts.services.MethodSkeleton;
import com.keystone.share.message.keystone.KeystoneRequest;
import com.keystone.share.message.keystone.KeystoneResponse;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.TypesUtils;

public class MethodInvokedServlet  extends AbstractJSONServlet {
	/** */
	private static final long serialVersionUID = -8934802875986554401L;

	/**
	 * 
	 */
	@Override
	protected Object proccessRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		String contextName = httpRequest.getParameter("contextName") ;
		String serviceName = httpRequest.getParameter("serviceName") ;
		String methodName = httpRequest.getParameter("methodName") ;
		
		Object[] parameters = takeInvokeParameters(contextName, serviceName, methodName, httpRequest) ;
		
		KeystoneRequest keystoneRequest = new KeystoneRequest() ;
		keystoneRequest.setContextName(contextName);
		keystoneRequest.setServiceName(serviceName);
		keystoneRequest.setMethodName(methodName);
		keystoneRequest.setParameters(parameters);
		
		KeystoneResponse keystoneResponse = (KeystoneResponse) KeystoneInvocationProccessor.proccess(keystoneRequest) ;
		return keystoneResponse.getResult() ;
	}
	
	
	
	
	/**
	 * 
	 * @param contextName
	 * @param serviceName
	 * @param methodName
	 * @param httpRequest
	 * @return
	 */
	private static Object[] takeInvokeParameters(String contextName, String serviceName, String methodName, HttpServletRequest httpRequest) {
		MethodSkeleton skeleton = KeystoneResourcesManager.getMethodSkeleton(contextName, serviceName, methodName, true) ;
		Object[] parameters = null ;
		String[] argsNames = skeleton.getArgsNames() ;
		if(!CommonUtils.isEmpty(argsNames)) {
			parameters = new Object[argsNames.length] ;
			for(int index=0; index<argsNames.length; index++) {
				String name = argsNames[index] ;
				Object marker = skeleton.getArgsMarkers()[index] ;
				parameters[index] = TypesUtils.cashTo(httpRequest.getParameter(name), marker) ;
			}
		}
		return parameters ;
	}

}
