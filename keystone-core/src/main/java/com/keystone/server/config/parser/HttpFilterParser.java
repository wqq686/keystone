package com.keystone.server.config.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.ServletException;

import com.keystone.server.config.http.HttpFilterConfig;
import com.keystone.server.contexts.container.HttpContainer;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.StringUtils;
import com.keystone.support.utils.XmlElement;

public class HttpFilterParser {

	
	
	/**
	 * 
	 * @param webXml
	 * @param httpContainer
	 * @throws ServletException 
	 */
	public static void parse(XmlElement webXml, HttpContainer httpContainer) throws ServletException {
		List<XmlElement> filters = webXml.elements("filter");
		Map<String, List<String>> patterns = parseFilterMapping(webXml) ;
    	if(!CommonUtils.isEmpty(filters) && !CommonUtils.isEmpty(patterns)) {
        	for (XmlElement element : filters)
        	{
        		String name = element.elementText("filter-name");
        		String className = element.elementText("filter-class") ;
        		
        		//1. is ok
        		isOkFilter(name, className, httpContainer) ;
        		
        		//2. parse filter config
        		HttpFilterConfig config = parseFilterConfig(element, name, className, patterns) ;
        		
        		//3. init filter class
        		Filter filter = CommonUtils.newInstance(className) ;
                filter.init(config);
        		
        		//4. add filter
                httpContainer.addFilter(name, filter, config) ;
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
	private static void isOkFilter(String name, String className, HttpContainer httpContainer) throws ServletException {
		Filter oldFilter = httpContainer.getFilterContainer().remove(name) ;
		if(oldFilter!=null)
		{
    		String msg = "HTTPFilterError:duplicate Filter[name=" + name + "] found at [" + className + "] and [" + oldFilter.getClass().getName() +"]";
            throw new ServletException(msg);
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
	private static HttpFilterConfig parseFilterConfig(XmlElement element, String name, String className, Map<String, List<String>> urlPatterns) {
        HttpFilterConfig config = new HttpFilterConfig(name, className);
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
	private static Map<String, List<String>> parseFilterMapping(XmlElement webXml) {
		List<XmlElement> mappings = webXml.elements("filter-mapping") ;
		Map<String, List<String>> container = new HashMap<String, List<String>>() ;
		if(!CommonUtils.isEmpty(mappings))
		{
			for(XmlElement map : mappings)
			{
				String name = map.element("filter-name").getElementText() ;
				String pattern = map.element("url-pattern").getElementText() ;
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
		}
		return container ;
	}
}
