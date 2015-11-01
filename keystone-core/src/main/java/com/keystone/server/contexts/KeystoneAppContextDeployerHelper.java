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
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.ExceptionUtils;
import com.keystone.support.common.IOUtils;


/**
 * 
 * @author wuqq
 *
 */
@Deprecated
public class KeystoneAppContextDeployerHelper {
	
	
	/**
	 * 
	 * @param acc
	 */
	public static KeystoneAppContext deployAppContext(AppContextConfig acc) {
		//1. load AppContext
		KeystoneAppContext appContext = loadAppContext(acc) ;
		
		//2. init context resource
		initAppContext(appContext) ;
		
		//4. notify filter wapper
		notifyAppContextFilter(appContext) ;
		
		//5. register context
		KeystoneResourcesManager.registerAppContext(appContext) ;
		return appContext ;
	}
	

	
	
	/**
	 * 
	 * @param acc
	 */
	public static void shutdownAppContext(AppContextConfig acc) {
		
	}
	
	
	
	
	
	/**
	 * 
	 * @param acc
	 * @return
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings({ "unchecked"})
	private static KeystoneAppContext loadAppContext(AppContextConfig acc) {
		//1. instance  classloader
		URL[] urls = foundContextClassPathURL(acc.getContextRoot()) ;
		KeystoneAppContextClassLoader contextClassLoader = new KeystoneAppContextClassLoader(acc.getContextName(), urls) ;
		
		//2. instance appcontext
		Class<KeystoneAppContext> contextClass = (Class<KeystoneAppContext>) CommonUtils.loadClass("com.keystone.server.contexts.KeystoneAppContext", contextClassLoader) ;
		KeystoneAppContext appContext = CommonUtils.newInstance(contextClass) ;
		
		appContext.setContextConfig(acc) ;
		appContext.setContextClassLoader(contextClassLoader) ;
		return appContext ;
	}
	
	
	
	
	
	/**
	 * 
	 * @param appContext
	 */
	private static void notifyAppContextFilter(KeystoneAppContext appContext) {
//		AppContextConfig acc = appContext.getContextConfig() ;
		
		//1. init context resource.
		
		//1. init thread pool
//		KeystoneResourcesManager.initAppContextThreadPool(acc.getContextName(), acc.getMinThreadPoolSize(), acc.getMaxThreadPoolSize(), acc.getThreadPoolQueueSize()) ;
		
		//2. 
//		KeystoneResourcesManager.registerAppContext(acc, appContext) ;
	}

	
	
	/**
	 * 
	 * @param appContext
	 */
	private static void initAppContext(KeystoneAppContext appContext) {
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
	private static void notifyAppContextEvent(KeystoneAppContext appContext, EventType eventType) {
		AppContextEvent event = new KeystoneAppContextEvent(eventType, appContext) ;
		for(AppContextListener listener : appContext.getContextListeners())
		{
			listener.contextStarted(event);
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
                        } 
                        catch (MalformedURLException e) 
                        {
                        	System.out.println("load file=["+pathname.getAbsolutePath()+"] with ex:" +ExceptionUtils.formatThrowable(e));
                        	System.err.println("load file=["+pathname.getAbsolutePath()+"] with ex:" +ExceptionUtils.formatThrowable(e));
                        }
                    }
                    return false;
                }
            });
        }
        catch (Exception e) 
        {
        	System.out.println("load contextPath=["+contextPath+"] with ex:" +ExceptionUtils.formatThrowable(e));
        	System.err.println("load contextPath=["+contextPath+"] with ex:" +ExceptionUtils.formatThrowable(e));
        }
        return urls.toArray(new URL[0]);
    }

	
}
