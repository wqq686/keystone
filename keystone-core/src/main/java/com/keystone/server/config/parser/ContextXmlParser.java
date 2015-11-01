package com.keystone.server.config.parser;

import java.util.List;

import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.KeystoneServiceAnalyzer;
import com.keystone.server.contexts.services.ServiceSkeleton;
import com.keystone.share.contexts.AppContextListener;
import com.keystone.share.services.InvokeAdvisor;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.IOUtils;
import com.keystone.support.common.StringUtils;
import com.keystone.support.utils.XmlElement;

public class ContextXmlParser {

	
	
	/**
	 * 
	 * @param appContext
	 */
	public static void parseContextXml(KeystoneAppContext appContext) {
		String path = IOUtils.mergePaths(appContext.getContextConfig().getContextRoot(), "WEB-INF", "context.xml") ;
		XmlElement context = XmlElement.read(path) ;
		//1. Listener
		parseAppContextListeners(context, appContext);
		//2. Service
		parseServices(context, appContext);
		//3. Advisor
		parseServiceAdvisors(context, appContext);
		//4. Task
		parseTasks(context, appContext);
	}
	
	

	/**
	 * 
	 * @param context
	 * @param appContext
	 */
	private static void parseAppContextListeners(XmlElement context, KeystoneAppContext appContext) {
		List<XmlElement> listenersList = context.elements("listener");
		for(XmlElement e : listenersList)
		{
			String className = e.getElementText() ;
			if(!StringUtils.isEmpty(className))
			{
				AppContextListener listener = CommonUtils.newInstance(className) ;
				appContext.getContextListeners().add(listener) ;
			}
		}
	}
	
	
	
	
	/**
	 * 
	 * @param context
	 * @param appContext
	 */
	private static void parseServices(XmlElement context, KeystoneAppContext appContext) {
		List<XmlElement> servicesList = context.elements("service");
		for(XmlElement node : servicesList)
		{
			String name = node.attributeValue("name") ;
			String apiName = node.elementText("home-api"), className = node.elementText("home-class") ;
			
			Class<?> apiClass = CommonUtils.classForName(apiName) ; Object service = CommonUtils.newInstance(className) ;
			ServiceSkeleton skeleton = KeystoneServiceAnalyzer.analyzeService(name, apiClass, service) ;
			appContext.getRemotingContainer().registerService(skeleton);
		}
	}
	
	
	/**
	 * 
	 * @param context
	 * @param appContext
	 */
	private static void parseServiceAdvisors(XmlElement context, KeystoneAppContext appContext) {
		List<XmlElement> advisorsList = context.elements("advisor");
		for(XmlElement e : advisorsList)
		{
			String className = e.elementText("class") ;
			String pattern = e.elementText("pattern", "*") ;
			if(!StringUtils.isEmpty(className))
			{
				
				InvokeAdvisor advisor = CommonUtils.newInstance(className) ;
				pattern = pattern.trim() ;
				if("*".equals(pattern))
				{
					for(ServiceSkeleton skeleton : appContext.getRemotingContainer().skeletons().values()) {
						skeleton.addAdvisors(advisor);
					}
				}
				else
				{
					String[] array = pattern.split("*") ;
					for(String name : array) {
						if(!StringUtils.isEmpty(name)) {
							name = name.trim() ; ServiceSkeleton skeleton = appContext.getServiceSkeleton(name) ;
							if(skeleton!=null) skeleton.addAdvisors(advisor);
						}
					}
				}
			}
		}
	}
	
	

	
	private static void parseTasks(XmlElement context, KeystoneAppContext appContext) {
		
	}
}
