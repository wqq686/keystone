package com.keystone.server.admin.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.keystone.server.KeystoneResourcesManager;
import com.keystone.server.admin.KeystoneAdminContext;
import com.keystone.server.config.KeystoneServerConfig;
import com.keystone.server.contexts.KeystoneAppContext;

public class ServerInfomationServlet extends AbstractJSONServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8934802875986554401L;

	static final long BYTE_MB = 1024*1024 ;
	@Override
	public Object proccessRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		Map<String, Object> server = new HashMap<>(), config = new HashMap<>(), apps = new HashMap<>() ;

		Runtime runtime = Runtime.getRuntime() ;
		int cpus = runtime.availableProcessors() ;  
		long totalMemory = runtime.totalMemory()/BYTE_MB, freeMemory = runtime.freeMemory()/BYTE_MB, maxMemory = runtime.maxMemory()/BYTE_MB ;
		
		KeystoneServerConfig ksc = KeystoneResourcesManager.getServerConfig() ;

		server.put("os", ksc.getOSVersion()) ;
		server.put("jvm", ksc.getJVMVersion()) ;
		
		server.put("cpus", cpus) ;
		server.put("totalMemory", totalMemory) ;
		server.put("freeMemory", freeMemory) ;
		server.put("maxMemory", maxMemory) ;
		server.put("version", ksc.getVersion()) ;
		server.put("path", ksc.getServerRoot()) ;
		
		
		
		config.put("host", ksc.getHost()) ;
		config.put("port", ksc.getTcpPort()) ;
		config.put("isTcpNoDelay", ksc.isTcpNoDelay()) ;
		config.put("reactorCount", ksc.getReactorCount()) ;
		
		config.put("minThreadPoolSize", ksc.getMinThreadPoolSize()) ;
		config.put("maxThreadPoolSize", ksc.getMaxThreadPoolSize()) ;
		config.put("threadPoolQueueSize", ksc.getThreadPoolQueueSize()) ;
		config.put("waitInQueue", ksc.getWaitInQueue()) ;
		config.put("sessionTimeout", ksc.getSessionTimeout()) ;
		config.put("sessionCheckInterval", ksc.getSessionCheckInterval()) ;
		config.put("logRate", ksc.getLogRate()) ;
		
		
		
		ConcurrentMap<String, KeystoneAppContext> contexts = KeystoneResourcesManager.getAppcontexts() ;
		for(String contextName : contexts.keySet())
		{
			KeystoneAppContext kac = contexts.get(contextName) ;
			if(kac instanceof KeystoneAdminContext) continue ;
			
			
			long[] accouter = kac.getRemotingContainer().getContextAccessCount() ;
			long accessCount = accouter[0], successCount = accouter[1], failedCount = accouter[2], avgCost = accouter[3] ;
			
			Map<String, Object> app = new HashMap<>() ;
			app.put("contextName", contextName) ;
			app.put("accessCount", accessCount) ;
			app.put("successCount", successCount) ;
			app.put("failedCount", failedCount) ;
			app.put("avgCost", avgCost) ;
			app.put("path", kac.getContextConfig().getContextRoot()) ;			
			apps.put(contextName, app) ;
		}
		
		
		Map<String, Object> result = new HashMap<>() ;
		result.put("server", server) ;
		result.put("config", config) ;
		result.put("apps", apps) ;
		return result ;
	}
}
