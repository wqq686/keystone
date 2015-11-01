package com.keystone.server.protocol.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.keystone.server.message.http.HttpOutputStream;
import com.keystone.server.message.http.KeystoneHttpServletRequest;
import com.keystone.server.message.http.KeystoneHttpServletResponse;
import com.keystone.support.common.ExceptionUtils;
import com.keystone.support.common.FastHttpDateFormat;
import com.keystone.transpot.api.IoBuffer;


/**
 * 
 * @author wuqq
 *
 */
public class HttpProtocolCodec {
	/**
	 * 
	 */
	private static final byte[] CLRF = new byte[] {0x0D, 0x0A};
	
	/**
	 * 
	 */
	public static final String HTT_SERVER_VERSION = "KEYSTONE-SERVER beta 1.0";
	
	/**
	 * 
	 */
	public static final String HTT_CONTENT_LENGTH = "Content-Length";
	
	/**
	 * 
	 */
	public static final String HTTP_200_MESSAGE = "OK";
	
	/**
	 * 
	 */
	public static final String HTTP_302_MESSAGE = "Found";
	
	/**
	 * 
	 */
	public static final String HTTP_400_MESSAGE = "Bad Request";
	
	/**
	 * 
	 */
	public static final String HTTP_404_MESSAGE = "Not Found";
	
	/**
	 * 
	 */
	public static final String HTTP_500_MESSAGE = "Server Internal Error";
	
	/**
	 * 
	 */
	public static final byte[] HTTP_400_HTML = "<html><head><title>400 Bad Request</title></head><body><h3>Bad Request</h3></body></html>\r\n".getBytes();
	
	/**
	 * 
	 */
	public static final byte[] HTTP_404_HTML = "<html><head><title>404 Page Not Found</title></head><body><h3>Page Not Found</h3></body></html>\r\n".getBytes();
	
	/**
	 * 
	 */
	public static final byte[] HTTP_500_HTML = "<html><head><title>500 Server Internal Error</title></head><body><h3>Server Internal Error</h3></body></html>\r\n".getBytes();
	
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static IoBuffer encodeRequest(HttpServletRequest request) {
		throw ExceptionUtils.unsupportedMethodException() ;
	}
	
	
	/**
	 * 
	 * @param buffer
	 * @return
	 */
	public static KeystoneHttpServletRequest decodeRequest(IoBuffer buffer) {
		if (buffer.remaining() < 4) return null ;
		
//		HTTP请求行 
//		（请求）头 
//		空行 
//		可选的消息体
//		Request中除了Host以外，其他都是可选的
		//HTTP请求行 （请求）头 ==> HTTP Header
		//可选的消息体 ==> HTTP Body
//		HttpRequestWrapper request = new HttpRequestWrapper(session);
		KeystoneHttpServletRequest request = new KeystoneHttpServletRequest();
		String headerLine = null, requestLine = null ;
		while(true)
		{
			headerLine = readHttpLine(buffer);
			if (headerLine == null)
			{
				return null ;
			}
			else if(headerLine.isEmpty())// End Of HTTP Header
			{
				//So, Read HTTP Body
				int contentLength = request.getContentLength() ;
				if(contentLength>0)
				{
					byte[] body = new byte[contentLength] ;
					buffer.get(body) ;
					request.setRequestBody(body) ;
				}
				
				return request ;
			}
			else if(requestLine==null) //First, so it's the Request Line
			{
				requestLine = headerLine ;
				request.parseRequestLine(requestLine) ;
			}
			else //It's a Header Line
			{
				int index = headerLine.indexOf(":");
				if(index < 0)
				{
					return null;
				}
				
				String name = headerLine.substring(0, index).trim();
				String value = index<headerLine.length() ? headerLine.substring(index+1).trim() : "" ;
				
				request.addHeader(name, value) ;
			}
		}
		
	}
	
	
	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private static String readHttpLine(IoBuffer buffer) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean isLineEnd = false; byte b;
		
		while(true && buffer.remaining() > 0) 
		{
			if ((b = buffer.get()) == '\r') 
			{
				buffer.get(); // skip '\n'
				isLineEnd = true;
				break;
			}
			else
			{
				baos.write(b);
			}
		}
		
		if (!isLineEnd) return null;
		return new String(baos.toByteArray());
	}

	
	
	/**
	 * Write HTTP response to client.
	 *  
	 * @param response
	 * @return
	 */
	public static IoBuffer encodeResponse(HttpServletResponse res) {
		KeystoneHttpServletResponse response = (KeystoneHttpServletResponse) res;
		IoBuffer buffer = IoBuffer.allocate(1024);
		
		//0. Need to flush
		try {response.flushBuffer();} catch (Exception ex) {ex.printStackTrace();}
		
		//1. Write HTTP status line.
		StringBuilder statusLine = new StringBuilder("HTTP/1.1 ").append(response.getStatus()).append(" ").append(response.getMessage());
		
		buffer.put(statusLine.toString().getBytes());
		buffer.put(CLRF);
		
		//2. Write HTTP content length.
		HttpOutputStream body = null;
        try { body = (HttpOutputStream) response.getOutputStream(); } catch (IOException e) {/*this will NOT happen.*/e.printStackTrace();}
		StringBuilder header = new StringBuilder();
		header.append("Content-Length: ").append(body.size());
		buffer.put(header.toString().getBytes());
		buffer.put(CLRF);
		
		//3. Write HTTP resoponse headers.
        for (Map.Entry<String, String> e: response.getHeaders().entrySet())
		{	
			header.setLength(0);
			header.append(e.getKey()).append(": ").append(e.getValue());
			buffer.put(header.toString().getBytes());
			buffer.put(CLRF);
		}
		
	    //4. Write HTTP response cookies.
        List<Cookie> cookies = response.getCookies();
        for(Cookie cookie : cookies)
        {
            String value = generateCookieString(cookie);
            if (value == null || value.length() == 0) continue;
            
            header.setLength(0);
            header.append("Set-Cookie: ").append(value);
            
            buffer.put(header.toString().getBytes());
            buffer.put(CLRF);
        }
		buffer.put(CLRF);
		
		//4. Write HTTP response body.
		if (body.size() > 0) buffer.put(body.toByteArray());
//		buffer.flip();
		return buffer;
	}

	
    /**
     * 
     * @param cookie
     * @return
     */
    private static String generateCookieString(Cookie cookie) {
        StringBuilder builder = new StringBuilder(128) ;

        builder.append(cookie.getName()).append("=").append(cookie.getValue());

        if (cookie.getDomain() != null) 
        {
            builder.append("; Domain=").append(cookie.getDomain());
        }

        if (cookie.getMaxAge() > 0) {
            builder.append("; Expires=").append(FastHttpDateFormat.formatMaxAge(cookie.getMaxAge()));
        }

        if (cookie.getPath() != null) {
            builder.append("; Path=").append(cookie.getPath());
        }

        if (cookie.getSecure()) {
            builder.append("; Secure");
        }
        
        return builder.toString();
    }
    
    
    public static KeystoneHttpServletRequest getRequest(ServletRequest request) {
        if (request instanceof KeystoneHttpServletRequest)
            return (KeystoneHttpServletRequest) request;

        while ((request instanceof ServletRequestWrapper)) {
            request = ((ServletRequestWrapper) request).getRequest();
            if (request instanceof KeystoneHttpServletRequest)
                return (KeystoneHttpServletRequest) request;
        }
        throw new RuntimeException("get request failed. This may be a bug,fire a report,please.");
    }

    public static KeystoneHttpServletResponse getResponse(ServletResponse response) {
        if (response instanceof KeystoneHttpServletResponse)
            return (KeystoneHttpServletResponse) response;

        while (response instanceof ServletResponseWrapper) 
        {
            response = ((ServletResponseWrapper) response).getResponse();
            if (response instanceof KeystoneHttpServletResponse)
                return (KeystoneHttpServletResponse) response;
        }
        throw new RuntimeException("get response failed. This may be a bug,fire a report,please.");
    }


	public static HttpServletResponse decodeResponse(IoBuffer buffer) {
		throw ExceptionUtils.unsupportedMethodException() ;
	}
}
