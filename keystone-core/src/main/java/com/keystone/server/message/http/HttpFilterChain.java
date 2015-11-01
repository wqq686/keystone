package com.keystone.server.message.http;

import java.io.IOException;
import java.util.Queue;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.keystone.server.config.http.HttpServletConfig;

public final class HttpFilterChain implements FilterChain {
    /**
	 * 
	 */
    private Queue<Filter> filters = null;
    
    /**
     * 
     */
    private HttpServlet servlet;
    
    /**
     * 
     */
    private HttpServletConfig config;

    /**
     * Constructs a HttpFilterChain object.
     * 
     * @param filters
     * @param servlet
     */
    public HttpFilterChain(Queue<Filter> filters, HttpServlet servlet, HttpServletConfig config) {
        this.filters = filters;
        this.servlet = servlet;
        this.config = config;
    }

    
    
    
    public HttpFilterChain(Queue<Filter> filters, HttpServlet servlet)
    {
    	this.filters = filters;
        this.servlet = servlet;
    }
    
    
    /**
     * 
     * @return
     */
    public HttpServletConfig getHttpServletConfig() {
    	return this.config ;
    }
    
    
    /**
     * 
     * @param req
     * @param res
     * @param chain
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
        if (!filters.isEmpty()) 
        {
            filters.poll().doFilter(req, res, this);
        }
        else if (servlet != null) 
        {
            servlet.service(req, res);
        }
        else
        {
            ((HttpServletResponse) res).sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
