package com.keystone.server.message.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.keystone.server.protocol.codec.HttpProtocolCodec;
import com.keystone.support.common.FastHttpDateFormat;
import com.keystone.support.utils.CaseInsensitiveLinkedMap;

public class KeystoneHttpServletResponse implements HttpServletResponse {
    /**
     * The status code from this response.
     */
    private int status = 200;

    /**
     * The status message from this response.
     */
    private String message = "OK";

    /**
     * All HTTP headers from this response.
     */
    private Map<String, String> headers = new CaseInsensitiveLinkedMap() ;

    /**
     * The response body
     */
    private HttpOutputStream body = new HttpOutputStream();

    /**
     * The HTTP context variable from this response.
     */
    @SuppressWarnings("unused")
	private HttpContext context = null;

    /**
     * The encoding from this response.
     */
    private String characterEncoding = "UTF-8";

    /**
     * All HTTP cookies from this response.
     */
    private List<Cookie> cookies = new ArrayList<Cookie>();

    /**
     * 
     */
//	@SuppressWarnings("unused")
//	private AppContextImpl appContext;

    /**
     * 
     */
    private PrintWriter printWriter;

    /**
     * Format for HTTP response header date field
     */
    private static final String HTTP_RESPONSE_DATE_HEADER = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     * SimpleDateFormat for HTTP response header date field
     */
    private static SimpleDateFormat format = null;
    
    
	@Override
	public void flushBuffer() throws IOException {
		getWriter().flush();
        getOutputStream().flush();
	}

	@Override
	public int getBufferSize() {
		return -1;//TODO must be implements ?
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding ;
	}

	
	@Override
	public String getContentType() {
		return headers.get("Content-Type");
	}

	@Override
	public Locale getLocale() {
		return Locale.CHINESE ;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.body;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (printWriter == null) 
        {
        	synchronized(this)
        	{
        		if (printWriter == null) //Double Check Locking
        		{
        			printWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
        		}
        	}
        }
        return printWriter;
	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public void reset() {
		try 
        {
            this.body.reset();
        } catch (Exception ignore) {}
	}

	@Override
	public void resetBuffer() {
		reset() ;
	}

	@Override
	public void setBufferSize(int arg0) {
//		throw new UnsupportedOperationException() ;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding ;
	}

	@Override
	public void setContentLength(int arg0) {}

	@Override
	public void setContentType(String contentType) {
        addHeader("Content-Type", contentType) ;
	}

	@Override
	public void setLocale(Locale locale) {}

	@Override
	public void addCookie(Cookie cookie) {
		this.cookies.add(cookie);
	}

	@Override
	public void addDateHeader(String name, long date) {
		addHeader(name, date2String(date)) ;
	}

	@Override
	public void addHeader(String name, String value) {
		actAddHeader(name, value) ;
	}

	@Override
	public void addIntHeader(String name, int value) {
		actAddHeader(name, value) ;
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.containsKey(name) ;
	}

	@Override
	public String encodeRedirectURL(String url) {
		return url ;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return url ;
	}

	@Override
	public String encodeURL(String url) {
		return url ;
	}

	@Override
	public String encodeUrl(String url) {
		return url ;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name) ;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet() ;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		List<String> list = null ;
		String value = headers.get(name) ;
		if(value!=null)
		{
			list = new ArrayList<String>(1) ;
			list.add(value) ;
		}
		return list ;
	}

	@Override
	public int getStatus() {
		return status ;
	}

	@Override
	public void sendError(int error) throws IOException {
		sendError(error, null) ;
	}

	@Override
	public void sendError(int error, String message) throws IOException {
		setStatus(error, message) ;
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		this.setStatus(SC_FOUND);
        String name = "Location";
        setHeader(name, location);
	}

	@Override
	public void setDateHeader(String name, long date) {
		addDateHeader(name, date) ;//is ok?
	}

	@Override
	public void setHeader(String name, String value) {
		actAddHeader(name, value) ;
	}

	@Override
	public void setIntHeader(String name, int value) {
		actAddHeader(name, value) ;
	}

	@Override
	public void setStatus(int status) {
		setStatus(status, null) ;
	}

	@Override
	public void setStatus(int status, String message) {
		this.status = status;

        if (status == SC_OK) 
        {
            this.message = HttpProtocolCodec.HTTP_200_MESSAGE;
        } 
        else if (status == SC_FOUND) 
        {
            this.message = HttpProtocolCodec.HTTP_302_MESSAGE;
            // Clear HTTP body
            this.body.reset();
            // Clear HTTP header
            this.headers.clear();
        } 
        else if (status == SC_BAD_REQUEST) 
        {
            this.message = HttpProtocolCodec.HTTP_400_MESSAGE;
            try 
            {
                this.body.reset();
                this.setCharacterEncoding("UTF-8");
                this.setContentType("text/html; charset=UTF-8");
                this.body.write(HttpProtocolCodec.HTTP_400_HTML);
            } 
            catch (Exception ex) {/** Do nothing */}
        } 
        else if (status == SC_NOT_FOUND) 
        {
            this.message = HttpProtocolCodec.HTTP_404_MESSAGE;
            
            if (!handleCustomErrorPage()) 
            {
                try 
                {
                    this.body.reset();
                    this.setCharacterEncoding("UTF-8");
                    this.setContentType("text/html; charset=UTF-8");
                    this.body.write(HttpProtocolCodec.HTTP_404_HTML);
                } 
                catch (Exception ex) {/** Do nothing */}
            }
        } 
        else if (status == SC_INTERNAL_SERVER_ERROR) 
        {
            this.message = HttpProtocolCodec.HTTP_500_MESSAGE;
            
            if (!handleCustomErrorPage()) 
            {
                try 
                {
                    this.body.reset();
                    this.setCharacterEncoding("UTF-8");
                    this.setContentType("text/html; charset=UTF-8");
                    this.body.write(HttpProtocolCodec.HTTP_500_HTML);
                } 
                catch (Exception ex) {/** Do nothing */}
            }
        }
        else
        {
            this.status = status;
            this.message = message;
        }
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public String getMessage() {
		return message ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> getHeaders() {
		return headers ;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Cookie> getCookies() {
		return cookies ;
	}
	
	
	/**
	 * 
	 * @param name
	 * @param value
	 */
	private void actAddHeader(String name, Object value) {
		headers.put(name, value.toString()) ;
	}
	
    
    /**
     * 
     * @param value
     * @return
     */
    private String date2String(long value) {
        if (format == null) 
        {
            format = new SimpleDateFormat(HTTP_RESPONSE_DATE_HEADER, Locale.US);//must be US ?
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        String time = FastHttpDateFormat.formatDate(value, format);
        return time;
    }

    
    /**
	 * 
	 */
    private boolean handleCustomErrorPage() 
    {
    	return true ;
//        HttpServlet servlet = null;
//        
////        if (getAppContext() == null) return false;
////        servlet = getAppContext().getErrorPage(status);
////        if (servlet == null) return false;
//        try 
//        {
//            servlet.service(context.getRequest(), context.getResponse());
//            return true;
//        } 
//        catch (Throwable ex) 
//        {
//            // 当响应错误页面的时候还出错，强制使用缺省的500页面
//            this.status = SC_INTERNAL_SERVER_ERROR;
//            this.message = HTTPCodecHelper.HTTP_500_MESSAGE;
//            try 
//            {
//                this.body.reset();
//                this.setCharacterEncoding("UTF-8");
//                this.setContentType("text/html; charset=UTF-8");
//                this.body.write(HTTPCodecHelper.HTTP_500_HTML);
//            }
//            catch (Exception e) {/** Do nothing */}
//            System.err.println("HttpResponse|handleErrorPage is error.");
//            ex.printStackTrace();
//        }
//        return true;
    }
    
    
}
