package com.keystone.server.config.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keystone.support.common.CommonUtils;

/**
 * The class is used to parse common configuration for HttpFitler and HttpServlet.
 * 
 * @author isaacdong
 * 
 */
public abstract class HttpBaseConfig {
    /**
     * The configuration name.
     */
    protected String name = null;

    /**
     * The implementation class name for the specified service.
     */
    private String clazzName = null;

    /**
     * The servlet aysnc supported flag.
     */
    private boolean asyncSupported = false;
    
    /**
     * The URL pattern.
     */
    private List<String> urlPatterns = new ArrayList<String>(4);

    /**
     * The initial parameters for the specified service.
     */
    protected Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Constructs a HttpBaseConfig object.
     * 
     * @param name
     * @param clazzName
     */
    HttpBaseConfig(String name, String clazzName) {
        this.name = name;
        this.clazzName = clazzName;
    }

    /**
     * add the URL patterns.
     * 
     * @param patterns
     */
    public void addUrlPatterns(Collection<String> patterns) {
    	if(!CommonUtils.isEmpty(patterns))
    	{
    		for (String pattern : patterns) {
                addUrlPattern(pattern);
            }
    	}
        
    }

    /**
     * add the URL patterns.
     * 
     * @param patterns
     */
    public void addUrlPatterns(String[] patterns) {
        if (patterns == null)
            return;
        for (String pattern : patterns) {
            addUrlPattern(pattern);
        }
    }

    /**
     * 
     * @param pattern
     */
    public final void addUrlPattern(String pattern) {
        if (pattern == null || pattern.isEmpty())
            return;
        this.urlPatterns.add(pattern);
    }
    
    /**
     * 
     * @param pattern
     */
    public final void removeUrlPattern(String pattern) {
        if (pattern == null || pattern.isEmpty())
            return;
        urlPatterns.remove(pattern);
    }

    public void clearUrlPatterns(String pattern) {
        this.urlPatterns.clear();
    }

    /**
     * Get the URL patterns.
     * 
     * @return
     */
    public List<String> getUrlPatterns() {
        return this.urlPatterns;
    }

    /**
     * Get the implementation class name of the specified service.
     * 
     * @return
     */
    public String getClassName() {
        return this.clazzName;
    }

    /**
     * Add a new initial parameter for the specified service.
     * 
     * @param name
     * @param value
     */
    public void putInitParameter(String name, String value) {
        this.parameters.put(name, value);
    }
    
    public void putInitParameters(Map<String, String> params) {
    	if(!CommonUtils.isEmpty(params))
    	{
    		this.parameters.putAll(params);
    	}
        
    }

    /**
     * Get the parameter value according to the parameter name.
     * 
     * @param name
     * @return
     */
    public String getInitParameter(String name) {
        return this.parameters.get(name);
    }

    /**
     * Get all parameter names.
     * 
     * @return
     */
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }
    
    /**
     * 
     * @return
     */
	public boolean isAsyncSupported() {
		return asyncSupported;
	}

	/**
	 * 
	 * @param asyncSupported
	 */
	public void setAsyncSupported(boolean asyncSupported) {
		this.asyncSupported = asyncSupported;
	}
}
