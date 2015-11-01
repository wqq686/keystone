package com.keystone.server.config.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * 
 * @author oscarhuang
 * 
 */
public class HttpServletConfig extends HttpBaseConfig implements ServletConfig {
    /**
     * Constructs a HTTP servlet configuration.
     * 
     * @param name
     * @param className
     */
    public HttpServletConfig(String name, String className) {
        super(name, className);
    }

    /**
     * Get the servlet context.
     */
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the servlet name.
     * 
     */
    public String getServletName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HttpServletConfig [getServletName()=" + getServletName() + ", getClassName()=" + getClassName()
                        + ", getUrlPatterns()=" + getUrlPatterns() + ", getInitParameterNames()="
                        + getInitParameterNames() + "]";
    }

}
