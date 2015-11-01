package com.keystone.server.admin.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.keystone.server.KeystoneResourcesManager;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.services.MethodSkeleton;
import com.keystone.server.contexts.services.ServiceSkeleton;

public class ServiceInfomationServlet extends AbstractJSONServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8934802875986554401L;

	/**
	 * 
	 */
	@Override
	protected Object proccessRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		String contextName = httpRequest.getParameter("contextName") ;
		String serviceName = httpRequest.getParameter("serviceName") ;
		KeystoneAppContext kac = KeystoneResourcesManager.getAppContext(contextName, true) ;
		ServiceSkeleton skeleton = kac.getServiceSkeleton(serviceName) ;
		
		List<Map<String, Object>> list = new ArrayList<>() ;
		for(MethodSkeleton m : skeleton.getMethodContainer().values()) {
			String methodName = m.getMethod().getName() ;
			
			Map<String, Object> e = new HashMap<>() ;
			list.add(e) ;
			
			e.put("methodName", methodName) ;
			e.put("accessCount", m.getAccessCount()) ;
			e.put("successCount", m.getSuccessCount()) ;
			e.put("failedCount", m.getFailedCount()) ;
			e.put("avgCost", m.getAvgCost()) ;
			
			List<String> argsNames = AdminHelper.getMethodArgsName(contextName, serviceName, methodName) ;
			e.put("argsName", argsNames) ;
			
		}
		
		
		Map<String, Object> ret = new HashMap<>() ;
		ret.put("contextName", contextName) ;
		ret.put("serviceName", serviceName) ;
		ret.put("methods", list) ;
		
		return ret;
	}

}
