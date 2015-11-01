package com.keystone.server.contexts;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * 
 * @author wuqq
 *
 */
public class KeystoneAppContextClassLoader extends URLClassLoader {

	/** */
	private String contextName ;
	
	/** */
	private URL[] urls ;
	
	
	/**
	 * 
	 * @param contextName
	 * @param urls
	 */
	public KeystoneAppContextClassLoader(String contextName, URL[] urls) {
		super(urls, getSystemClassLoader()) ;
		this.contextName = contextName ;
		this.urls = urls ;
	}

	
	/**
	 * 
	 * @return
	 */
	public String getContextName() {
		return contextName;
	}


	/**
	 * 
	 * @param name
	 * @param resolve
	 * @return
	 * @throws ClassNotFoundException
	 */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    	if("javax.servlet.http.HttpServlet".equals(name))
    	{
    		return super.loadClass(name, resolve) ;
    	}
    	else
    	{
    		synchronized (getClassLoadingLock(name))
            {
                Class<?> c = findLoadedClass(name); 
                
                ClassNotFoundException ex= null;            
                
                if (c == null)
                {
                    try
                    {
                        c= findClass(name);//load class from context's class path
                    }
                    catch (ClassNotFoundException e)
                    {
                        ex= e;
                    }
                }

                if (c == null)
                {
                    c = super.loadClass(name, resolve);
                }

                if (c == null && ex!=null)
                {
                    throw ex;
                }

                if (resolve)
                {
                	resolveClass(c);
                }
                
                return c;
            }
    	}
    }



    /**
     * 
     */
//    @Override
//    protected Class<?> findClass(final String name) throws ClassNotFoundException {
//        Class<?> klass = super.findClass(name) ;
//        if(klass!=null)
//        {
//        	return klass ;
//        }
//        
//        String path = name.replace('.', '/').concat(".class");
//        URL url = getResource(path);
//        
//        if (url==null) throw new ClassNotFoundException(name);
//        
//        InputStream is = null;
//        try
//        {
//            is = url.openStream();
//            byte[] bytes = IOUtils.readBytes(is);
//            klass = defineClass(name,bytes,0,bytes.length);
//        }
//        catch (IOException e)
//        {
//            throw new ClassNotFoundException(name,e);
//        }
//        finally
//        {
//        	IOUtils.close(is);
//        }
//        return klass;
//    }
    
    
    
    
	@Override
	public String toString() {
		return "KeystoneAppContextClassLoader [contextName=" + contextName +", classpath=" + Arrays.toString(urls)+ "]";
	}
	
	
}
