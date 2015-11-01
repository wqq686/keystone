package com.keystone.server.contexts;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.keystone.server.KeystoneResourcesManager;
import com.keystone.server.config.AppContextConfig;
import com.keystone.server.config.parser.ContextXmlParser;
import com.keystone.server.config.parser.HttpWebAppParser;
import com.keystone.share.contexts.AppContextEvent;
import com.keystone.share.contexts.AppContextEvent.EventType;
import com.keystone.share.contexts.AppContextListener;
import com.keystone.support.common.ExceptionUtils;
import com.keystone.support.common.IOUtils;

public class KeystoneAppContextDefaultDeployer implements KeystoneAppContextDeployer {

	
	
	/**
	 * 
	 */
	@Override
	public KeystoneAppContext deployAppContext(AppContextConfig acc) {
		//1. load AppContext
		KeystoneAppContext appContext = loadAppContext(acc) ;
		
		//2. init context resource
		initAppContext(appContext) ;
		
		//3. register context
		KeystoneResourcesManager.registerAppContext(appContext) ;
		return appContext ;
	}
	
	

	

	/**
	 * 
	 */
	@Override
	public void releaseAppContext(KeystoneAppContext appContext) {
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader() ;
		try
		{
			//1. exchange class loader
			Thread.currentThread().setContextClassLoader(appContext.getContextClassLoader()) ;
			
			//2. notifty context listener
			notifyAppContextEvent(appContext, EventType.Destroyed) ;
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e) ;
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(currentClassLoader) ;
		}
	}


	
	
	
	/**
	 * 
	 * @param acc
	 * @return
	 */
	KeystoneAppContext loadAppContext(AppContextConfig acc) {
		//1. found classpath and instance classloader
		URL[] urls = foundContextClassPathURL(acc.getContextRoot()) ;
		KeystoneAppContextClassLoader contextClassLoader = new KeystoneAppContextClassLoader(acc.getContextName(), urls) ;
		
		//2. instance appcontext
		KeystoneAppContext appContext = new KeystoneAppContext() ;
		appContext.setContextConfig(acc) ;
		appContext.setContextClassLoader(contextClassLoader) ;
		return appContext ;
	}
	
	
	
	/**
	 * 
	 * @param appContext
	 */
	protected void initAppContext(KeystoneAppContext appContext) {
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader() ;
		try
		{
			//1. exchange class loader
			Thread.currentThread().setContextClassLoader(appContext.getContextClassLoader()) ;
			
			//2. parse context.xml
			ContextXmlParser.parseContextXml(appContext) ;
			
			//3. parse web.xml
			HttpWebAppParser.parseWebXml(appContext) ;
			
			//4. notifty context listener
			notifyAppContextEvent(appContext, EventType.Started) ;
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e) ;
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(currentClassLoader) ;
		}
	}
	
	
	
	/**
	 * 
	 * @param appContext
	 * @param eventType
	 */
	protected void notifyAppContextEvent(KeystoneAppContext appContext, EventType eventType) {
		AppContextEvent event = new KeystoneAppContextEvent(eventType, appContext) ;
		for(AppContextListener listener : appContext.getContextListeners()) {
			if(eventType == EventType.Started) {
				listener.contextStarted(event);
			} else if(eventType == EventType.Destroyed) {
				listener.contextDestroyed(event);
			}
		}
	}




	
	
	
	/**
	 * 
	 * @param contextPath
	 * @return
	 */
	private static URL[] foundContextClassPathURL(String contextPath) {
		final List<URL> urls = new ArrayList<URL>();
        try
        {
            // 1. Add the root path for this application
            urls.add(IOUtils.pathToURL(contextPath));
            // 2. Add URL for WEB-INF/classes
//            String classesPath = IOUtils.mergePaths(contextPath, "WEB-INF", "classes") ;
            urls.add(IOUtils.pathToURL(contextPath, "WEB-INF", "classes"));

            // 3. Add URL for WEB-INF/lib/*.jar
            String jarPath = IOUtils.mergePaths(contextPath, "WEB-INF", "lib") ;
            new File(jarPath).listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if(pathname.getName().endsWith(".jar"))
                    {
                        try
                        {
                        	urls.add(pathname.toURI().toURL());
                        } catch (MalformedURLException e) {
                        	ExceptionUtils.fullError("load file=["+pathname.getAbsolutePath()+"] with ex:", e);
                        }
                    }
                    return false;
                }
            });
        }
        catch (Exception e) 
        {
        	ExceptionUtils.fullError("load contextPath=["+contextPath+"] with ex:", e);
        }
        return urls.toArray(new URL[0]);
    }





}
