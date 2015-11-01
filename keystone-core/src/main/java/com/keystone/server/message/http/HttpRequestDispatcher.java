package com.keystone.server.message.http;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @author oscarhuang
 *
 */
public class HttpRequestDispatcher implements RequestDispatcher {
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private String forwardURI = null;
	
	/**
	 * 
	 * @param URI
	 */
	HttpRequestDispatcher(String URI)
	{
	    if(URI == null || !URI.startsWith("/")) {
	        throw new IllegalArgumentException("forward path MUST start with /.");
	    }
		this.forwardURI = URI;
	}

    /**
	 * 
	 */
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
    	//TODO:
//        KSHttpServletRequestWapper req = HTTPCodecHelper.getRequest(request);
//        req.setForwardURL(this.forwardURI);
//        AppContextImpl appContext = req.getAppContext();
//        Pair<HttpServlet, HttpServletConfig> pair = appContext.getHttpServlet(req.getServletPath());
//        if (pair == null || pair.first == null) {
//            response.reset();
//            ((HttpServletResponse) response).sendError(404);
//            return;
//        }
//        pair.first.service(request, response);
    }

	/**
	 * 
	 */
	public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException 
	{
		throw new UnsupportedOperationException();
	}
}
