package com.keystone.server.message.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.keystone.support.common.FastHttpDateFormat;
import com.keystone.support.common.StringUtils;
import com.keystone.support.utils.CaseInsensitiveHashMap;


public class KeystoneHttpServletRequest implements HttpServletRequest {

	/** */
	private static final Cookie[] __EMPTY__COOKIE__ARRAY__ = new Cookie[0] ;
	
	/**
	 * The HTTP request method.
	 */
	private String method = "GET";

	/**
	 * The HTTP request URI.
	 */
	private String requestURI = null;
	
	/**
	 * The query URL from this request.
	 */
	private String requestURL = null;
	
	/**
	 * The remote address from this request.
	 */
	private String remoteAddr = null;
	
	/**
	 * The query string from this request.
	 */
	private String queryString = null;
	
	/**
	 * The HTTP version information.
	 */
	private String httpVersion = null;
	
	/**
	 * All HTTP headers from this request.
	 */
    private Map<String, String> headers = new CaseInsensitiveHashMap() ;
    
	/**
	 * All HTTP parameters from this request.
	 */
	private Map<String, String> parameters = new HashMap<String,String>();
	
	/**
	 * All attributes from this request.
	 */
	private Map<String, Object> attributes = new HashMap<String,Object>();

	/**
	 * The input stream from this request.
	 */
	private HttpInputStream input = null;
	
	/**
	 * The encoding from this request.
	 * 1. default to encode url query string
	 * 2. after setcharacterEncoding, decode for post body.
	 */
	private String characterEncoding = "UTF-8";
	
//	/**
//	 * The HTTP request context variable.
//	 */
//	private HttpContext context = null;
	
	/**
	 * All HTTP cookies from this request.
	 */
	private List<Cookie> cookies = new ArrayList<Cookie>();

    /**
     * 
     */
    private byte[] body ;
    
    /**
     * 
     */
    private boolean needParseBody = false ;
    

    /**
     * 
     */
    private String contextPath;

    /**
     * 
     */
    private String servletPath;

    /**
     * 
     */
    private int remotePort;
    
    /**
     * 
     */
    private long bornTime ;
    
    
    
    public KeystoneHttpServletRequest() {
    	bornTime = System.currentTimeMillis() ;
    }
    
	public long getBornTime() {
		return bornTime;
	}	
	
	@Override
	public Object getAttribute(String key) {
		return this.attributes.get(key) ;
	}

	
	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(this.attributes.keySet());
	}

	
	@Override
	public String getCharacterEncoding() {
		return this.characterEncoding ;
	}

	@Override
	public int getContentLength() {
		String len = getHeader("Content-Length") ;
		return len==null ? 0 : Integer.valueOf(len) ;
	}

	@Override
	public String getContentType() {
		return headers.get("Content-Type") ;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new HttpInputStream(body) ;
	}
	


	@Override
	public Locale getLocale() {
		return Locale.CHINESE;
	}


	@Override
	public String getParameter(String name) {
		return getParameters().get(name) ;
	}

	
	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(getParameters().keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return new String[]{getParameter(name)} ;
	}

	@Override
	public String getProtocol() {
		return this.httpVersion ;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.input));
	}

	@Override
	public String getRealPath(String path) {
		return path;
	}

	@Override
	public String getRemoteAddr() {
		return this.remoteAddr ;
	}


	@Override
	public int getRemotePort() {
		return this.remotePort ;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String URI) {
		return new HttpRequestDispatcher(URI);
	}

	@Override
	public String getScheme() {
		return this.httpVersion;
	}

	@Override
	public String getServerName() {
		return this.headers.get("Host");
	}

	@Override
	public boolean isAsyncStarted() {
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		return false;
	}

	@Override
	public boolean isSecure() {
		throw new UnsupportedOperationException() ;
	}

	@Override
	public void removeAttribute(String key) {
		this.attributes.remove(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value) ;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
		//指定后可以通过getParameter()则直接获得正确的字符串，如果不指定，则默认使用iso8859-1编码。
		//值得注意的是在执行setCharacterEncoding()之前，不能执行任何getParameter()。
		//而且，该指定只对POST方法有效，对GET方法无效。
		//应该是在执行第一个getParameter()的时候
		//java将会按照编码分析所有的提交内容，而后续的getParameter()不再进行分析，
		//所以setCharacterEncoding()无效。
		//而对于GET方法提交表单是，提交的内容在URL中，一开始就已经按照编码分析提交内容，
		//setCharacterEncoding()自然就无效。
		this.characterEncoding = characterEncoding ;
		setNeedParseBody(true) ;
	}


	@Override
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public Cookie[] getCookies() {
		return cookies.toArray(__EMPTY__COOKIE__ARRAY__);
	}

	@Override
	public long getDateHeader(String name) {
		String value = getHeader(name);
        if (value == null) return -1L;
        
        // Attempt to convert the date header in a variety of formats
        SimpleDateFormat formats[] = 
        {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)
        };
        
        long result = FastHttpDateFormat.parseDate(value, formats);
        
        if (-1L != result) 
        {
            return result;
        }
        throw new IllegalArgumentException(value);
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name) ;
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(headers.keySet());
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		return Collections.enumeration(Arrays.asList(new String[]{headers.get(name)}));
	}

	@Override
	public int getIntHeader(String key) {
		return Integer.valueOf(headers.get(key)) ;//it's ok?
	}

	@Override
	public String getMethod() {
		return this.method ;
	}


	@Override
	public String getPathInfo() {
		return this.requestURI;
	}

	@Override
	public String getQueryString() {
		return this.queryString ;
	}


	@Override
	public String getRequestURI() {
		return this.requestURI ;
	}

	@Override
	public StringBuffer getRequestURL() {
		return new StringBuffer(this.requestURL);
	}


	@Override
	public String getServletPath() {
		return this.servletPath; //No context
	}

	
	
	
	
	

	@Override
	public AsyncContext getAsyncContext() {throw new UnsupportedOperationException() ;}
	
	@Override
	public DispatcherType getDispatcherType() { throw new UnsupportedOperationException() ; }
	
	@Override
	public String getLocalAddr() { throw new UnsupportedOperationException() ; }
	
	@Override
	public String getLocalName() { throw new UnsupportedOperationException() ; }

	
	@Override
	public int getLocalPort() { throw new UnsupportedOperationException() ; }
	
	@Override
	public Enumeration<Locale> getLocales() { throw new UnsupportedOperationException(); }
	
	@Override
	public Map<String, String[]> getParameterMap() { throw new UnsupportedOperationException(); }

	@Override
	public String getRemoteHost() { throw new UnsupportedOperationException() ; }

	@Override
	public int getServerPort() { throw new UnsupportedOperationException() ; }

	@Override
	public ServletContext getServletContext() { throw new UnsupportedOperationException() ; }
	
	@Override
	public AsyncContext startAsync() { throw new UnsupportedOperationException() ; }

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException { throw new UnsupportedOperationException() ; }

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException { throw new UnsupportedOperationException() ; }

	@Override
	public String getAuthType() { throw new UnsupportedOperationException() ; }
	
	@Override
	public Part getPart(String arg0) throws IOException, ServletException { throw new UnsupportedOperationException() ; }

	@Override
	public Collection<Part> getParts() throws IOException, ServletException { throw new UnsupportedOperationException() ; }

	@Override
	public String getRemoteUser() { throw new UnsupportedOperationException() ; }
	
	@Override
	public String getPathTranslated() { throw new UnsupportedOperationException() ; }
	
	@Override
	public String getRequestedSessionId() { throw new UnsupportedOperationException() ; }
	
	@Override
	public HttpSession getSession() { throw new UnsupportedOperationException() ; }

	@Override
	public HttpSession getSession(boolean flag) { throw new UnsupportedOperationException() ; }

	@Override
	public Principal getUserPrincipal() { throw new UnsupportedOperationException() ; }

	@Override
	public boolean isRequestedSessionIdFromCookie() { throw new UnsupportedOperationException() ; }

	@Override
	public boolean isRequestedSessionIdFromURL() { throw new UnsupportedOperationException() ; }

	@Override
	public boolean isRequestedSessionIdFromUrl() { throw new UnsupportedOperationException() ; }

	@Override
	public boolean isRequestedSessionIdValid() { throw new UnsupportedOperationException() ; }

	@Override
	public boolean isUserInRole(String arg0) { throw new UnsupportedOperationException() ; }

	@Override
	public void login(String arg0, String arg1) throws ServletException { throw new UnsupportedOperationException() ; }

	@Override
	public void logout() throws ServletException { throw new UnsupportedOperationException() ; }

	
	
	
	
	
	
	
	
	
	
	
	
	//--------------------implements for http protocol by wuqq. 2014-04-30 ----------------------------

	/**
	 * 
	 * @param requestLine
	 */
	public void parseRequestLine(String requestLine) {
		if(requestLine!=null)
		{
			String[] array = requestLine.split(" ");
			this.method = array[0].trim() ;
			setRequestURL(array[1].trim()) ;
			this.httpVersion = array[2].trim() ;
		}
	}
	
	
	
	public void addHeader(String name, String value) {
		if ("Cookie".equals(name)) {
			parseCookie(value);
		}
		
		this.headers.put(name, value);
	}
	
	
	private boolean isNeedParseBody() {
		return needParseBody;
	}
	
	private void setNeedParseBody(boolean needParseBody) {
		if(needParseBody)
		{
			needParseBody = body!=null && body.length>0 ;
			if(needParseBody)
			{
				String contentType = getContentType() ;
				needParseBody = contentType != null && contentType.contains("application/x-www-form-urlencoded") ;
			}
		}
		this.needParseBody = needParseBody;
	}

	public void setRequestBody(byte[] body) {
		this.body = body ;
		setNeedParseBody(true) ;
	}
	
	
	private Map<String, String> getParameters() {
		if(isNeedParseBody())
		{
			parseQueryString(new String(body), true) ;
			setNeedParseBody(false) ;
		}
		return this.parameters ;
	}
	
	
	private void parseQueryString(String queryString, boolean isURLDecode) {
		//1. java string is unicode
		if(!StringUtils.isEmpty(queryString))
		{
			String[] array = queryString.split("&") ;
			String key = null, value = null ;
			for(String one : array)
			{
				int flag = one.indexOf("=") ;
				key = one.substring(0, flag) ; value = one.substring(flag+1) ;
				if(isURLDecode)
				{
					try
					{
						key = URLDecoder.decode(key, characterEncoding);
	                    value = URLDecoder.decode(value, characterEncoding);
					}
					catch(Exception e)
					{
						e.printStackTrace() ;
					}
				}
                this.parameters.put(key, value);
			}
		}
	}
	
	
	
	private static final Pattern uniqSlashPattern = Pattern.compile("/+");
	
	
    public void setRequestURL(String requestURL) {
        // 1. get request url
        if (requestURL.length() > 7 && requestURL.substring(0, 7).equalsIgnoreCase("http://")) {
            // http://xxx.xxx.xxx/path.html?queryStrings
            // http://xxx.xxx.xxx/?queryStrings
            // http://xxx.xxx.xxx/?
            // http://xxx.xxx.xxx/
            // slash index
            int index = requestURL.indexOf('/', 7);
            if (index < 0) {
                // http://xxx.xxx.xxx?queryStrings
                // http://xxx.xxx.xxx?
                index = requestURL.indexOf('?', 7); /* question mark index */
            }
            
            if (index < 0) {
                // http://xxx.xxx.xxx
                requestURL = "/";
            }else {
                requestURL = requestURL.substring(index);
            }
        }
        if(!requestURL.startsWith("/")) requestURL ='/'+requestURL;
        requestURL = uniqSlashPattern.matcher(requestURL).replaceAll("/");
        this.requestURL = requestURL;
        
        // 2. get request URI and query string
        int index = requestURL.indexOf("?");
        if (index >= 0) {
            requestURI = requestURL.substring(0, index);
            if (index < requestURL.length() - 1) {
                queryString = requestURL.substring(index + 1);
                parseQueryString(queryString, true);
            }
        } else {
            requestURI = requestURL;
        }
        
        // 3. get context and serveltPath
        index = requestURI.indexOf("/",1);
        if (index > 0) 
        {
        	this.contextPath = requestURI.substring(1, index);
            this.servletPath = requestURI.substring(index);
        }
        else 
        {
            this.contextPath = requestURI.substring(1);
            this.servletPath = "";
//            this.servletPath = this.requestURI;
        }
    }
	
    
    
    /**
	 * Parse cookie from this request. 
	 * 
	 * @param value
	 */
	private void parseCookie(String cookieString) {
		if (cookieString == null || cookieString.length() == 0) return ;
		try 
		{
			String cookieArray[] = cookieString.split(";");
			if (cookieArray == null || cookieArray.length == 0) return ;
			
			Cookie lastCookie = null; int pos = -1;
			String key = null, value = null;
			for(String cookie : cookieArray)
			{
				if (cookie.length() > 0)
				{
					if (cookie.charAt(0) == ' ') cookie = cookie.substring(1);
					if (cookie.startsWith("HttpOnly,")) cookie = cookie.substring(9);
					
					pos = cookie.indexOf("=");
					
					if (pos > 0)
					{
						key = cookie.substring(0, pos);
						value = cookie.substring(pos + 1);
						
						if (!isReservedToken(key)) {
							lastCookie = new Cookie(key, value);
							this.cookies.add(lastCookie);
						} else if (lastCookie != null) {
							setCookieValue(lastCookie, key, value);
						}
					}
//					else
//					{
//						if ("HttpOnly".equals(cookie) || lastCookie != null) lastCookie.setSecure(true); 
//					}
				}
			}
		}
		catch(IllegalArgumentException ie) {
		    System.err.println("Can't parse cookie: " + cookieString + " \tCased by :" + ie.getMessage());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	private boolean isReservedToken(String name)
	{
		if (name == null || name.length() <= 0 ) return false;
		if (name.startsWith("$")) name = name.substring(1);
		
		if (name.equalsIgnoreCase("Comment") ||
				name.equalsIgnoreCase("Domain") ||
				name.equalsIgnoreCase("Path") ||
				name.equalsIgnoreCase("Expires") ||
				name.equalsIgnoreCase("Max-Age") ||
				name.equalsIgnoreCase("Version")) return true;
		
		return false;
	}
	
	/**
	 * 
	 * @param cookie
	 * @param name
	 * @param value
	 */
	private void setCookieValue(Cookie cookie, String name, String value)
	{
		if (cookie == null || name == null || name.length() <= 0) return ;
		
		if (name.startsWith("$")) name = name.substring(1);
		
		if ("Comment".equalsIgnoreCase(name)) {
			cookie.setComment(value);
		} else if ("Domain".equalsIgnoreCase(name)) {
			cookie.setDomain(value);
		}  else if ("Path".equalsIgnoreCase(name)) {
			cookie.setPath(value);
		}  else if ("Expires".equalsIgnoreCase(name)) {
			
		}  else if ("Max-Age".equalsIgnoreCase(name)) {
			cookie.setMaxAge(Integer.valueOf(value.trim()));
		} else if ("Version".equalsIgnoreCase(name)) {
			//cookie.setVersion(Integer.valueOf(value.trim()));
		}
	}
	
}
