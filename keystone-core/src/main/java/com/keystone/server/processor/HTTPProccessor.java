package com.keystone.server.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.keystone.server.config.http.HttpServletConfig;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.server.message.http.HttpFilterChain;
import com.keystone.server.message.http.KeystoneHttpServletRequest;
import com.keystone.server.message.http.KeystoneHttpServletResponse;
import com.keystone.support.utils.Pair;

public class HTTPProccessor {

	/**
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param appContext
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void proccessRequest(KeystoneHttpServletRequest httpRequest, KeystoneHttpServletResponse httpResponse, KeystoneAppContext appContext) throws IOException, ServletException {
//		KeystoneAppContext appContext = KeystoneResourcesManager.getAppContext(httpRequest.getContextPath(), true) ;
		
        Pair<HttpServlet, HttpServletConfig> pair = appContext.getHttpContainer().getHttpServlet(httpRequest.getServletPath()) ;
        if(pair==null)
        {
        	//7. Static Resource
        	processStaticResource(appContext, httpRequest, httpResponse);
        }
        else
        {
        	//8. Start filter chain processing logic
        	HttpServlet servlet = pair.first;  HttpServletConfig config = pair.second ;
        	//9. Get HTTP filters according to requestURI
            Queue<Filter> filters = appContext.getHttpContainer().getHttpFilters(httpRequest.getServletPath()) ;
            HttpFilterChain chain = new HttpFilterChain(filters, servlet, config);
            chain.doFilter(httpRequest, httpResponse);
        }
	}
	
	
	
	/**
	 * 
	 * @param appContext
	 * @param httpRequest
	 * @param httpResponse
	 * @throws IOException
	 * @throws ServletException
	 */
	private static void processStaticResource(KeystoneAppContext appContext, KeystoneHttpServletRequest httpRequest, KeystoneHttpServletResponse httpResponse) throws IOException, ServletException {
		String path = httpRequest.getServletPath() ; 
		Pair<String, InputStream> res = appContext.getStaticResource(path) ;
		String namepath = res.first ; InputStream is = res.second; 
		if(is==null)
		{
			httpResponse.setStatus(404);
			httpResponse.setCharacterEncoding("UTF-8");
			httpResponse.setContentType("text/plain");
			httpResponse.getWriter().write("Resource=["+path+"] Not Found @ AppContext=["+appContext.getContextName()+"].") ;
		}
		else
		{
			httpResponse.setCharacterEncoding("UTF-8");
			String fileType = namepath.substring(namepath.lastIndexOf(".")+1) ;
			int end = fileType.indexOf("?") ;
			if(end>0) fileType = fileType.substring(0, end) ;
			String contentType = HTTPContentType.getContentType(fileType) ;
			if(contentType==null) contentType = "text/plain" ;
			httpResponse.setContentType(contentType);
			int len = 0 ; byte[] buffer = new byte[1024];
			while((len=is.read(buffer))!=-1)
			{
				httpResponse.getOutputStream().write(buffer, 0, len) ;
			}
		}
		
		appContext.releaseStaticResource(res);
		
		httpResponse.getOutputStream().flush() ;
	}
}
