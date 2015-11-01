package com.keystone.server.config.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.contexts.container.HttpContainer;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.IOUtils;
import com.keystone.support.common.NumberUtils;
import com.keystone.support.common.StringUtils;
import com.keystone.support.utils.XmlElement;


/**
 * 
 * @author wuqq
 *
 */
public class HttpWebAppParser {

	
	/**
	 * 
	 * @param httpContainer
	 * @throws FileNotFoundException 
	 * @throws ServletException 
	 */
	public static void parseWebXml(KeystoneAppContext appContext) throws FileNotFoundException, ServletException {
		String namepath = IOUtils.mergePaths(appContext.getContextConfig().getContextRoot(), "WEB-INF", "web.xml") ;
    	File file = new File(namepath) ;
    	if(file.exists())
    	{
    		XmlElement webXml = XmlElement.read(new FileInputStream(file)) ;
    		HttpContainer httpContainer = appContext.getHttpContainer() ;
    		//1. parse filter
    		HttpFilterParser.parse(webXml, httpContainer) ;
    		
    		//2. parse servlet
    		HttpServletParser.parse(webXml, httpContainer) ;
    		
    		//3. parse error page
    		parseErrorPages(webXml, httpContainer) ;
    	}
	}
	
	
	
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	public static Map<String, String> parseInitParams(XmlElement element) {
		Map<String, String> params = new HashMap<String, String>() ;
		
		List<XmlElement> paramList = element.elements("init-param") ;
        if(!CommonUtils.isEmpty(paramList))
        {
        	for(XmlElement param : paramList) {
                String paramName = param.element("param-name").getElementText() ;
                String paramValue = param.element("param-value").getElementText();
                params.put(paramName, paramValue) ;
        	}
        }
        return params ;
    }
	
	/**
	 * 
	 * @param webXml
	 * @param httpContainer
	 */
	private static void parseErrorPages(XmlElement webXml, HttpContainer httpContainer) {
    	List<XmlElement> list = webXml.elements("error-page") ;
    	if(!CommonUtils.isEmpty(list))
    	{
    		for(XmlElement n : list)
    		{
    			int errorCode = NumberUtils.parseInt(n.elementText("error-code"), -1);
    			String location = n.elementText("location") ;
    			if(errorCode > 0 && !StringUtils.isEmpty(location))
    			{
    				httpContainer.getErrorPages().put(errorCode, location);
    			}
    		}
    	}
    }
}
