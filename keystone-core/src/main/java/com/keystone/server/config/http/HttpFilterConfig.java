package com.keystone.server.config.http;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * 
 * @author oscarhuang
 * 
 */
public class HttpFilterConfig extends HttpBaseConfig implements FilterConfig {
    /**
     * Constructs a HTTP filter configuration.
     * 
     * @param name
     * @param clazzName
     */
    public HttpFilterConfig(String name, String clazzName) {
        super(name, clazzName);
    }

    /**
     * Get the filter name.
     * 
     */
    public String getFilterName() {
        return this.name;
    }

    /**
	 * 
	 */
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

}
