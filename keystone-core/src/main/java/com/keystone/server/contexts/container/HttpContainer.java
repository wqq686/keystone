package com.keystone.server.contexts.container;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import com.keystone.server.config.http.HttpFilterConfig;
import com.keystone.server.config.http.HttpServletConfig;
import com.keystone.support.utils.Pair;

public class HttpContainer {
	
	
	/**
	 * 
	 */
	private Map<String, HttpServlet> servletContainer = new HashMap<String, HttpServlet>() ;
	
	
	/**
	 * 
	 */
	private Map<String, HttpServletConfig> servletConfigContainer = new HashMap<String, HttpServletConfig>() ;
	
	/**
	 * 
	 */
	private Map<String, Filter> filterContainer = new HashMap<String, Filter>() ;
	
	/**
	 * 
	 */
	private Map<String, HttpFilterConfig> filterConfigContainer = new HashMap<String, HttpFilterConfig>() ;
	
	/**
	 * 
	 */
	private Map<Integer, String> errorPages = new HashMap<Integer, String>() ;
	
	/**
	 * 
	 * @return
	 */
	public Map<String, Filter> getFilterContainer() {
		return filterContainer;
	}

	
	/**
	 * 
	 * @param filterContainer
	 */
	public void setFilterContainer(Map<String, Filter> filterContainer) {
		this.filterContainer = filterContainer;
	}


	/**
	 * 
	 * @return
	 */
	public Map<String, HttpFilterConfig> getFilterConfigContainer() {
		return filterConfigContainer;
	}

	
	/**
	 * 
	 * @param filterConfigContainer
	 */
	public void setFilterConfigContainer(Map<String, HttpFilterConfig> filterConfigContainer) {
		this.filterConfigContainer = filterConfigContainer;
	}


	/**
	 * 
	 * @return
	 */
	public Map<String, HttpServlet> getServletContainer() {
		return servletContainer;
	}


	/**
	 * 
	 * @param servletContainer
	 */
	public void setServletContainer(Map<String, HttpServlet> servletContainer) {
		this.servletContainer = servletContainer;
	}


	/**
	 * 
	 * @return
	 */
	public Map<String, HttpServletConfig> getServletConfigContainer() {
		return servletConfigContainer;
	}


	/**
	 * 
	 * @param servletConfigContainer
	 */
	public void setServletConfigContainer(Map<String, HttpServletConfig> servletConfigContainer) {
		this.servletConfigContainer = servletConfigContainer;
	}

	
	/**
	 * 
	 * @return
	 */
	public Map<Integer, String> getErrorPages() {
		return errorPages;
	}
	
	
	/**
	 * 
	 * @param errorPages
	 */
	public void setErrorPages(Map<Integer, String> errorPages) {
		this.errorPages = errorPages;
	}


	
	
	/**
	 * 
	 * @param name
	 * @param servlet
	 * @param config
	 */
	public void addServlet(String name, HttpServlet servlet, HttpServletConfig config) {
		if(name!=null && servlet!=null)
		{
			this.servletContainer.put(name, servlet) ;
			if(config!=null) this.servletConfigContainer.put(name, config) ;
		}
	}
	
	
	
	/**
	 * 
	 * @param name
	 * @param filter
	 * @param config
	 * @return
	 */
	public HttpContainer addFilter(String name, Filter filter, HttpFilterConfig config) {
		if(name!=null && filter!=null)
		{
			filterContainer.put(name, filter) ;
			if(config!=null) filterConfigContainer.put(name, config) ;
		}
		return this ;
	}
	
	
	
	
	
	/**
	 * 
	 * @param servletPath
	 * @return
	 */
	public Pair<HttpServlet, HttpServletConfig> getHttpServlet(String servletPath) {
    	HttpServlet servlet = null;
        for (HttpServletConfig config : servletConfigContainer.values()) {
            List<String> patterns = config.getUrlPatterns();
            for (String pattern : patterns) {
                if (match(pattern, servletPath)) {
                    servlet = servletContainer.get(config.getServletName());
                    if (servlet != null) 
                    {
                    	Pair<HttpServlet, HttpServletConfig> pair = new Pair<HttpServlet, HttpServletConfig>();
                    	pair.first = servlet;
                    	pair.second = config;
                        return pair;
                    }
                }
            }
        }
        return null;
	}
	
	
    /**
     * 
     * @param pattern
     * @param servletPath
     * @return
     */
    private boolean match(String pattern, String servletPath) {
        if (pattern == null || pattern.trim().length() == 0)
            return false;

        if (pattern.endsWith("*")) {
            pattern = pattern.substring(0, pattern.length() - 1);
            if (servletPath.startsWith(pattern)) {
                return true;
            }
        } else if (pattern.startsWith("*")) {
            pattern = pattern.substring(1, pattern.length());
            if (servletPath.endsWith(pattern)) {
                return true;
            }
        } else {
            if (servletPath.equals(pattern)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 
     * @param requestURI
     * @return
     */
    public Queue<Filter> getHttpFilters(String servletPath) {
        Queue<Filter> queue = new LinkedList<Filter>();
        for (HttpFilterConfig filterConfig : filterConfigContainer.values()) {
            List<String> patterns = filterConfig.getUrlPatterns();
            for (String pattern : patterns) {
                if (match(pattern, servletPath)) {
                    queue.add(filterContainer.get(filterConfig.getFilterName()));
                }
            }
        }
        return queue;
    }

    

	
	
}
