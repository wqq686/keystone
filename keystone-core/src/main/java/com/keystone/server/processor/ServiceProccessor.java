package com.keystone.server.processor;

import java.lang.reflect.Method;
import java.util.List;

import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.services.ServiceSkeleton;
import com.keystone.share.message.keystone.KeystoneRequest;
import com.keystone.share.message.keystone.KeystoneResponse;
import com.keystone.share.services.InvokeAdvisor;

public class ServiceProccessor {


	
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param appContext
	 * @throws Throwable
	 */
	public static void proccessRequest(KeystoneRequest request, KeystoneResponse response, KeystoneAppContext appContext) throws Throwable {
		Object invokeResult = null ; Throwable tw = null ;
		try
		{
//			KeystoneAppContext appContext = KeystoneResourcesManager.getAppContext(request.getContextName(), true) ;
			ServiceSkeleton skeleton = appContext.getServiceSkeleton(request.getServiceName()) ;
			List<InvokeAdvisor> advisors = skeleton.getAdvisors() ;
			
			Object target = skeleton.getService() ;
			Method method = skeleton.getMethod(request.getMethodName()) ;
			Object[] args = request.getParameters() ;
			try
			{
				for(InvokeAdvisor advisor : advisors) {
					advisor.before(method, args, target);
				}
				
				invokeResult = method.invoke(target, args) ;
				
				for(InvokeAdvisor advisor : advisors) {
					advisor.after(invokeResult, method, args, target);
				}
			}
			catch(Throwable t)
			{
				tw = t ;
				for(InvokeAdvisor advisor : advisors) {
					advisor.onThrowable(t, method, args, target);
				}
			}
			finally
			{
				for(InvokeAdvisor advisor : advisors) {
					try{ advisor.onComplete(invokeResult, method, args, target); } catch(Throwable tx){tx.printStackTrace();}
				}
			}
		}
		catch(Throwable t)
		{
			if(tw==null) tw = t ;
		}
		
		if(tw!=null)
		{
			throw tw ;
		}
		else
		{
			response.setResult(invokeResult) ;
		}
		
	}
	
	
}
