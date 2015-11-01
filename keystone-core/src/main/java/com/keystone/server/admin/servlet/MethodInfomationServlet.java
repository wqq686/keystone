package com.keystone.server.admin.servlet;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.keystone.server.KeystoneResourcesManager;
import com.keystone.server.contexts.services.MethodAccessLog;
import com.keystone.server.contexts.services.MethodSkeleton;
import com.keystone.support.common.TimeUtils;

public class MethodInfomationServlet extends AbstractJSONServlet {

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
		String methodName  = httpRequest.getParameter("methodName") ;
		
		MethodSkeleton skeleton = KeystoneResourcesManager.getMethodSkeleton(contextName, serviceName, methodName, true) ;
		
		Map<String, Object> stat = new HashMap<>() ;
		stat.put("methodName", methodName) ;
		stat.put("accessCount", skeleton.getAccessCount()) ;
		stat.put("successCount", skeleton.getSuccessCount()) ;
		stat.put("failedCount", skeleton.getFailedCount()) ;
		stat.put("avgCost", skeleton.getAvgCost()) ;
		
		List<String> argsNames = AdminHelper.getMethodArgsName(contextName, serviceName, methodName) ;
		stat.put("argsName", argsNames) ;
		
		Queue<MethodAccessLog> accesses = skeleton.getLastAccesses() ;
		/**
		if(accesses.isEmpty()){
			long initTime = System.currentTimeMillis() ;
			for(int index=0; index<50; index++)
			{
				initTime += 1000; 
				accesses.add(new MethodAccessLog(initTime, 10, 200)) ;
			}
		}*/
		
		List<Map<String, Object>> list = new ArrayList<>() ;
		Iterator<MethodAccessLog> itor = accesses.iterator() ;
		DateFormat formatter = TimeUtils.dateFormat4yyyyMMddHHmmss() ;
		while(itor.hasNext())
		{
			MethodAccessLog e = itor.next() ; Map<String, Object> m = new HashMap<>() ;
			String accTime = formatter.format(new Date(e.getAccessTime())) ;
			m.put("accessTime", accTime) ;
			m.put("status", e.getStatus()) ;
			m.put("cost", e.getCost()) ;
			list.add(m) ;
		}
		
		Collections.reverse(list) ;
		
		Map<String, Object> result = new HashMap<>() ;
		
		result.put("contextName", contextName) ;
		result.put("serviceName", serviceName) ;
		result.put("methodName", methodName) ;
		result.put("stat", stat) ;
		result.put("accesses", list) ;
		
		return result ;
	}
}
