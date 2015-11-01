package com.keystone.server.contexts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.keystone.server.config.AppContextConfig;
import com.keystone.server.contexts.container.HttpContainer;
import com.keystone.server.contexts.container.RemotingContainer;
import com.keystone.server.contexts.services.ServiceSkeleton;
import com.keystone.share.contexts.AppContext;
import com.keystone.share.contexts.AppContextListener;
import com.keystone.support.common.IOUtils;
import com.keystone.support.utils.Pair;

public class KeystoneAppContext implements AppContext {

	/** */
	private AppContextConfig contextConfig ;
	
	/** */
	private ClassLoader contextClassLoader ;
	
	/** */
	private List<AppContextListener> contextListeners = new ArrayList<AppContextListener>() ;
	
	/** */
	private RemotingContainer remotingContainer = new RemotingContainer() ;
	
	/** */
	private HttpContainer httpContainer = new HttpContainer() ;
	
	/**
	 * 
	 * @return
	 */
	public String getContextName() {
		return this.contextConfig.getContextName() ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public AppContextConfig getContextConfig() {
		return contextConfig;
	}

	
	/**
	 * 
	 * @param contextConfig
	 */
	public void setContextConfig(AppContextConfig contextConfig) {
		this.contextConfig = contextConfig;
	}

	
	/**
	 * 
	 * @return
	 */
	public ClassLoader getContextClassLoader() {
		return contextClassLoader;
	}

	
	/**
	 * 
	 * @param contextClassLoader
	 */
	public void setContextClassLoader(ClassLoader contextClassLoader) {
		this.contextClassLoader = contextClassLoader;
	}

	
	/**
	 * 
	 * @return
	 */
	public List<AppContextListener> getContextListeners() {
		return contextListeners;
	}


	/**
	 * 
	 * @param contextListeners
	 */
	public void setContextListeners(List<AppContextListener> contextListeners) {
		this.contextListeners = contextListeners;
	}

	
	/**
	 * 
	 * @return
	 */
	public HttpContainer getHttpContainer() {
		return httpContainer;
	}


	/**
	 * 
	 * @param httpContainer
	 */
	public void setHttpContainer(HttpContainer httpContainer) {
		this.httpContainer = httpContainer;
	}

	
	/**
	 * 
	 * @return
	 */
	public RemotingContainer getRemotingContainer() {
		return remotingContainer;
	}

	
	/**
	 * 
	 * @param remotingContainer
	 */
	public void setRemotingContainer(RemotingContainer remotingContainer) {
		this.remotingContainer = remotingContainer;
	}


	/**
	 * 
	 */
	@Override
	public <T> T getService(Class<T> klass) {
		return this.remotingContainer.getService(klass) ;
	}
	
	/**
	 * 
	 * @param serviceName
	 * @return
	 */
	public ServiceSkeleton getServiceSkeleton(String serviceName) {
		return this.remotingContainer.getServiceSkeleton(serviceName) ;
	}



	/**
	 * 
	 * @param path
	 * @return <File's abstract path, InputStream>
	 * @throws IOException
	 */
	public Pair<String, InputStream> getStaticResource(String path) throws IOException {
		Pair<String, InputStream> ret = null ;
		String namepath = IOUtils.mergePaths(getContextConfig().getContextRoot(), path) ;
		File file = new File(namepath) ;
		if(file.exists()) {
			InputStream is = new FileInputStream(file);
			ret = new Pair<String, InputStream>(namepath, is) ;
		}
		return ret ;
	}
	
	
	
	/**
	 * 
	 * @param res <File's abstract path, InputStream>
	 * @throws IOException
	 */
	public void releaseStaticResource(Pair<String, InputStream> res) throws IOException {
		
	}
}
