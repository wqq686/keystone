package com.keystone.server.config.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.keystone.server.config.http.HttpServletConfig;
import com.keystone.server.contexts.container.HttpContainer;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.StringUtils;
import com.keystone.support.utils.XmlElement;

public class HttpServletParser {

	
	
	
	
	/**
	 * 
	 * @param webXml
	 * @param httpContainer
	 * @throws ServletException
	 */
	public static void parse(XmlElement webXml, HttpContainer httpContainer) throws ServletException {
		List<XmlElement> servlets = webXml.elements("servlet") ;
		Map<String, List<String>> urlPatterns = parseServletMapping(webXml) ;
		if(!CommonUtils.isEmpty(servlets) && !CommonUtils.isEmpty(urlPatterns)) {
			for(XmlElement element : servlets) 
			{
				//1. name and class
				String name = element.elementText("servlet-name", null), className = element.elementText("servlet-class", null);
				
				//2. is ok
				isOkServlet(name, className, httpContainer) ;
				
				//3. parse servlet config
				HttpServletConfig config = parseHttpServletConfig(element, name, className, urlPatterns) ;
				
				//4. is ok config
				isOkServletConfig(config, httpContainer) ;
				
				HttpServlet servlet = CommonUtils.newInstance(className) ;
				
				servlet.init(config) ;
				
				//6. add servlet
				httpContainer.addServlet(name, servlet, config);
        	}
    	}
	}
	

	
	
	/**
	 * 
	 * @param config
	 * @param httpContainer
	 * @throws ServletException 
	 */
	private static void isOkServletConfig(HttpServletConfig config, HttpContainer httpContainer) throws ServletException {
		for(HttpServletConfig current : httpContainer.getServletConfigContainer().values())
		{
			for(String url : current.getUrlPatterns())
			{
				if(config.getUrlPatterns().contains(url))
				{
					String message = "HTTPServletURLMappingError: url=[" + url + "] Mapping To Servlet[name=" + config.getServletName() + "] and Servlet[name="+current.getServletName()+"]";
					throw new ServletException(message) ;
				}
			}
		}
	}


	/**
	 * 
	 * @param name
	 * @param className
	 * @param httpContainer
	 * @throws ServletException 
	 */
	private static void isOkServlet(String name, String className, HttpContainer httpContainer) throws ServletException {
		Servlet oldServlet = httpContainer.getServletContainer().remove(name) ;
		if(oldServlet!=null)
		{
    		String message = "HTTPServletConfigError:duplicate Servlet[name=" + name + "] found at [" + className + "] and [" + oldServlet.getClass().getName() +"]";
            throw new ServletException(message);
		}
	}
	
	
	
	
	/**
	 * 
	 * @param element 
	 * @param name
	 * @param className
	 * @param urlPatterns
	 * @return
	 */
	private static HttpServletConfig parseHttpServletConfig(XmlElement element, String name, String className, Map<String, List<String>> urlPatterns) {
		HttpServletConfig config = new HttpServletConfig(name, className);
        Map<String, String> params = HttpWebAppParser.parseInitParams(element) ;
        List<String> filterPatterns = urlPatterns.get(name) ;
        config.putInitParameters(params);
        config.addUrlPatterns(filterPatterns);
        return config ;
	}
	
	
	/**
	 * 
	 * @param webXml
	 * @return
	 */
    private static Map<String, List<String>> parseServletMapping(XmlElement webXml) {
    	List<XmlElement> mappings = webXml.elements("servlet-mapping") ;
    	Map<String, List<String>> container = new HashMap<String, List<String>>() ;
		for(XmlElement e : mappings)
		{
			String name = e.elementText("servlet-name", null), pattern = e.elementText("url-pattern", null) ;
			if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(pattern))
			{
				List<String> patterns = container.get(name) ;
				if(patterns==null)
				{
					patterns = new ArrayList<String>() ;
					container.put(name, patterns) ;
				}
				patterns.add(pattern) ;
			}
		}
		return container ;
	}
	

}
