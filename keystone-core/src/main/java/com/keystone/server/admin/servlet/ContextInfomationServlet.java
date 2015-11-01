package com.keystone.server.admin.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.keystone.server.KeystoneResourcesManager;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.services.ServiceSkeleton;

public class ContextInfomationServlet extends AbstractJSONServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8934802875986554401L;

	
	@Override
	public Object proccessRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		String contextName = httpRequest.getParameter("contextName") ;
		KeystoneAppContext kac = KeystoneResourcesManager.getAppContext(contextName, true) ;
		
		Map<String, Object> context = new HashMap<>() ;
		context.put("contextName", contextName) ;
		context.put("path", kac.getContextConfig().getContextRoot()) ;
		long[] accouter = kac.getRemotingContainer().getContextAccessCount() ;
		long accessCount = accouter[0], successCount = accouter[1], failedCount = accouter[2], avgCost = accouter[3] ;
		
		Map<String, Object> runtime = new HashMap<>() ;
		runtime.put("accessCount", accessCount) ;
		runtime.put("successCount", successCount) ;
		runtime.put("failedCount", failedCount) ;
		runtime.put("avgCost", avgCost) ;
		
		List<Map<String, Object>> services = new ArrayList<>() ;
		for(ServiceSkeleton k : kac.getRemotingContainer().skeletons().values())
		{
			accouter = k.getServiceAccessCount() ;
			accessCount = accouter[0];
			successCount = accouter[1];
			failedCount = accouter[2];
			avgCost = accouter[3] ;
			
			Map<String, Object> e = new HashMap<>() ;
			e.put("serviceName", k.getName()) ;
			e.put("accessCount", accessCount) ;
			e.put("successCount", successCount) ;
			e.put("failedCount", failedCount) ;
			e.put("avgCost", avgCost) ;
			services.add(e) ;
		}
		
		Map<String, Object> result = new HashMap<>() ;
		result.put("context", context) ;
		result.put("runtime", runtime) ;
		result.put("services", services) ;
		
		return result ;
	}

	
//	@Override
//	public Object proccessRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
//		String contextName = httpRequest.getParameter("contextName") ;
//		KeystoneAppContext kac = KeystoneResourcesManager.getAppContext(contextName, true) ;
//		AppContextConfig acc = kac.getContextConfig() ;
//		
//		
//		Map<String, Object> config = new HashMap<>() ;
//		config.put("name", contextName) ;
//		config.put("path", kac.getContextConfig().getContextRoot()) ;
//		config.put("minThreadPoolSize", acc.getMinThreadPoolSize()) ;
//		config.put("maxThreadPoolSize", acc.getMaxThreadPoolSize()) ;
//		config.put("threadPoolQueueSize", acc.getThreadPoolQueueSize()) ;
//		config.put("waitInQueue", acc.getWaitInQueue()) ;
//		config.put("logRate", acc.getLogRate()) ;
//		
//		long[] accouter = kac.getRemotingContainer().getContextAccessCount() ;
//		long accessCount = accouter[0], successCount = accouter[1], failedCount = accouter[2], avgCost = accouter[3] ;
//		
//		Map<String, Object> runtime = new HashMap<>() ;
//		runtime.put("accessCount", accessCount) ;
//		runtime.put("successCount", successCount) ;
//		runtime.put("failedCount", failedCount) ;
//		runtime.put("avgCost", avgCost) ;
//		
//////		Map<String, Object> services = new HashMap<>() ;
////		List<Map<String, Object>> services = new ArrayList<>() ;
////		for(ServiceSkeleton ton : kac.getRemotingContainer().skeletons().values())
////		{
////			
////			Map<String, Object> e = new HashMap<>() ;
////			e.put("name", ton.getName()) ;
////			e.put("accessCount", kac.getAccessCount()) ;
////			e.put("successCount", kac.getSuccessCount()) ;
////			e.put("failedCount", kac.getFailedCount()) ;
////			e.put("avgCost", kac.getAvgCost()) ;
//////			services.put(ton.getName(), e) ;
////			services.add(e) ;
////		}
//		
//		Map<String, Object> result = new HashMap<>() ;
//		result.put("config", config) ;
//		result.put("runtime", runtime) ;
////		result.put("services", services) ;
//		
//		return result ;
//	}


}
