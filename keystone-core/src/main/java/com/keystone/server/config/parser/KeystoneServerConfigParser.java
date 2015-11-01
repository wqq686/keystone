package com.keystone.server.config.parser;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.keystone.server.config.AppContextConfig;
import com.keystone.server.config.KeystoneServerConfig;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.IOUtils;
import com.keystone.support.common.StringUtils;
import com.keystone.support.utils.XmlElement;
import com.keystone.transpot.ReactorUtils;

public class KeystoneServerConfigParser {

	
	
	/**
	 * 
	 * @param serverXml
	 * @return
	 */
	public static KeystoneServerConfig parseServerXml(String serverXml) {
		XmlElement server = XmlElement.read(serverXml) ;
		XmlElement connector = server.element("connector") ;
		//1. global config
		String host = connector.elementText("host", "localhost,127.0.0.1") ;
		int port = connector.elementInt("port", 19800) ;
		int backlog = connector.elementInt("backlog", 1024) ;
		int reactorCount = connector.elementInt("reactorCount", ReactorUtils.calRactorCount()) ;
		int readBufferSize = connector.elementInt("readBufferSize", 1024) ;
		long sessionTimeout = connector.elementLong("sessionTimeout", TimeUnit.MILLISECONDS.convert(3, TimeUnit.MINUTES)) ;
		long sessionCheckInterval = connector.elementLong("sessionCheckInterval", TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)) ;
		
		
		String ioWorkerClassName        = StringUtils.isEmpty(connector.elementText("ioWorker"), "com.keystone.transpot.impl.DefaultIoWorker") ;
		String eventWorkerClassName     = StringUtils.isEmpty(connector.elementText("eventWorker"), "com.keystone.transpot.impl.DefaultEventWorker") ;
		String protocolFactoryClass     = StringUtils.isEmpty(connector.elementText("protocolFactory"), "com.keystone.server.protocol.MultiplexingProtocolFactory") ;
		String messageHandlerClassName  = StringUtils.isEmpty(connector.elementText("messageHandler"), "com.keystone.server.KeystoneMessageHandler") ;
		int defMinThreadPoolSize = connector.elementInt("minThreadPoolSize", 20) ;
		int defMaxThreadPoolSize = connector.elementInt("maxThreadPoolSize", 512) ;
		int defThreadPoolQueueSize = connector.elementInt("threadPoolQueueSize", 2000) ;
		int logRate = connector.elementInt("accessLogRate", 0) ;
		String appsRoot = connector.elementText("appsRoot", null) ;
		XmlElement zookeeper = connector.element("zookeeper") ;
		String zookeeperAddresses = null ;
		if(zookeeper!=null) {
			zookeeperAddresses = zookeeper.attributeValue("location") ;
		}
		
		
		KeystoneServerConfig serverConf = new KeystoneServerConfig() ;
		String osVersion = CommonUtils.getOSVersion() ;
		String jvmVersion = CommonUtils.getJvmVersion() ;
		
		serverConf.setOSVersion(osVersion);
		serverConf.setJVMVersion(jvmVersion);
		serverConf.setReactorCount(reactorCount) ;
		serverConf.setHost(host);
		serverConf.setTcpPort(port);
		serverConf.setBacklog(backlog) ;
		serverConf.setRecvBufferSize(readBufferSize) ;
		serverConf.setSessionTimeout(sessionTimeout);
		serverConf.setSessionCheckInterval(sessionCheckInterval);
		serverConf.setIoWorkerClassName(ioWorkerClassName) ;
		serverConf.setEventWorkerClassName(eventWorkerClassName) ;
		serverConf.setProtocolFactoryClassName(protocolFactoryClass) ;
		serverConf.setMessageHandlerClassName(messageHandlerClassName) ;
		//TODO:services.xml
		serverConf.setMinThreadPoolSize(defMinThreadPoolSize);
		serverConf.setMaxThreadPoolSize(defMaxThreadPoolSize);
		serverConf.setThreadPoolQueueSize(defThreadPoolQueueSize) ;
		serverConf.setLogRate(logRate);
		serverConf.setAppsRoot(appsRoot);
		
		serverConf.setZookeeperAddresses(zookeeperAddresses);
		
		XmlElement contexts = server.element("contexts") ;
		if(contexts!=null) {
			List<XmlElement> contextList = contexts.elements("context") ;
			for(XmlElement c : contextList) {
				parseAppContextConf(null, null, serverConf, c) ;
			}
		}
		
		return serverConf ;
	}
	
	
	
	
	/**
	 * 
	 * @param serverConf
	 * @param app
	 * @return
	 */
	public static void parseContextConf(KeystoneServerConfig serverConf, File app) {
		if(app!=null && app.isDirectory())
		{
			String contextName = app.getName();
			String contextRoot = IOUtils.getCanonicalPath(app) ;
			String contextXml = IOUtils.mergePaths(contextRoot, "WEB-INF", "context.xml") ;
			XmlElement context = XmlElement.read(contextXml) ;
			parseAppContextConf(contextName, contextRoot, serverConf, context) ;
		}
	}
	
	
	
	
	
	/**
	 * 
	 * @param contextName
	 * @param contextRoot
	 * @param serverConf
	 * @param acc
	 */
	public static void parseAppContextConf(String contextName, String contextRoot, KeystoneServerConfig serverConf, XmlElement acc) {
		String status = acc.attributeValueOrElementText("status", "open") ;
		if(!"close".equals(status))
		{
			String name = acc.attributeValueOrElementText("name") ;
			String root = acc.attributeValueOrElementText("path") ;
			
			if(name!=null) contextName = name ;
			if(root!=null) contextRoot = root ;
			if(contextName==null || contextRoot==null) throw new NullPointerException("context=" + acc.getTagName() + "'s contextName=" + contextName + ", contextRoot=" + contextRoot + " is null.") ;
			
			long waitInQueue = acc.attributeLongOrElementLong("waitInQueue", serverConf.getWaitInQueue()) ;
			int logRate = acc.attributeIntOrElementInt("logRate", serverConf.getLogRate()) ;
			int flow = acc.attributeIntOrElementInt("flow", serverConf.getLogRate()) ;
			
			AppContextConfig conf = new AppContextConfig(contextName, contextRoot, waitInQueue, logRate, flow) ;
			serverConf.getContextList().add(conf) ;
			
			
		}
		
		/**
		int minThreadPoolSize = acc.elementInt("minThreadPoolSize", serverConf.getMinThreadPoolSize()) ;
		int maxThreadPoolSize = acc.elementInt("maxThreadPoolSize", serverConf.getMaxThreadPoolSize()) ;
		int threadPoolQueueSize = acc.elementInt("threadPoolQueueSize", serverConf.getThreadPoolQueueSize()) ;
		int logRate = acc.elementInt("logRate", serverConf.getLogRate()) ;
		AppContextConfig conf = new AppContextConfig(contextName, contextRoot, minThreadPoolSize, maxThreadPoolSize, threadPoolQueueSize, logRate) ;
		serverConf.getContextList().add(conf) ;
		*/
	}
	
}
