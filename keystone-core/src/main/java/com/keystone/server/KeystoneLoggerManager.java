package com.keystone.server;

import java.io.PrintStream;
import java.util.Random;

import com.keystone.server.contexts.services.MethodSkeleton;
import com.keystone.server.message.http.HttpOutputStream;
import com.keystone.server.message.http.KeystoneHttpServletRequest;
import com.keystone.server.message.http.KeystoneHttpServletResponse;
import com.keystone.share.message.keystone.KeystoneRequest;
import com.keystone.share.message.keystone.KeystoneResponse;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.logger.Logger;
import com.keystone.support.logger.Logger.Level;
import com.keystone.support.logger.LoggerFactory;
import com.keystone.support.logger.LoggingOutputStream;
import com.keystone.transpot.api.IoSession;



/**
 * 
 * @author wuqq
 *
 */
public final class KeystoneLoggerManager {
	
	private KeystoneLoggerManager(){}
	
	/** */
	private static boolean started = false ;
	
	/** */
	private static final Random logRandom = new Random(System.currentTimeMillis());
	
	/** */
	public static Logger stdout ;
	
	/** */
	public static Logger stderr ;
	
	/** */
	public static Logger access ;
	
	
	/**
	 * 
	 * @param logPath
	 */
	static synchronized void start(String logPath) {
		if(!started)
		{
			stdout   = LoggerFactory.createLogger("stdout.log", Level.INFO, logPath);
			stderr   = LoggerFactory.createLogger("stderr.log", Level.INFO, logPath);
			access = LoggerFactory.createLogger("access.log", Level.INFO, logPath);
			if(!CommonUtils.isWindowsOS())
			{
				System.setOut(new PrintStream(new LoggingOutputStream(stdout), true));
				System.setErr(new PrintStream(new LoggingOutputStream(stderr), true));			
			}
			started = true ;
		}
	}
	
	

	
	/**
	 * 
	 * @return
	 */
    public static boolean enableLog() {
    	//TODO: change to context log rate
        return KeystoneResourcesManager.getServerConfig().getLogRate() - logRandom.nextInt(100) > 0;
    }
    
    
    
    
    
    
    /**
     * 
     * @param request
     * @param response
     * @param session
     * @param cost
     */
    public static void printAccessLog(Object request, Object response, IoSession session, long cost) {
    	if(request instanceof KeystoneRequest) {
    		printKeystoneAccessLog((KeystoneRequest)request, (KeystoneResponse)response, session, cost) ;
    	} else if(request instanceof KeystoneHttpServletRequest) {
    		printHTTPAccessLog((KeystoneHttpServletRequest)request, (KeystoneHttpServletResponse)response, session, cost) ;
    	}
    }
    
    
    
    
    
    
    
	/**
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param cost
	 */
	static void printHTTPAccessLog(KeystoneHttpServletRequest request, KeystoneHttpServletResponse response, IoSession session, long cost) {
		// 只对正常调用抽样，非正常调用则全量输出
		if (response.getStatus() == 200 && !enableLog()) return;
        
        long contextLength = 0;
        try
        {
	        HttpOutputStream body = null;
	        body = (HttpOutputStream) response.getOutputStream();
	        contextLength = body.size();
        } catch(Exception ignore) {}
        
        String log = new StringBuilder()
        .append(session.getRemoteSocketAddress()).append("\t")
        .append(request.getMethod()).append("\t")
        .append(request.getRequestURL()).append("\t")
        .append(request.getProtocol()).append("\t")
        .append(response.getStatus()).append("\t")
        .append(contextLength).append("\t")
        .append(cost).toString();
        
        access.info(log);
	}
	
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param cost
	 */
	static void printKeystoneAccessLog(KeystoneRequest request, KeystoneResponse response, IoSession session, long cost) {
		 // 只对正常调用抽样，非正常调用则全量输出
        if (response.isOk() && !enableLog()) return;
        
		
		Object args[] = request.getParameters() ;
		
		//TODO: socketaddress is ok?
		StringBuilder builder = new StringBuilder()
		.append(session.getRemoteSocketAddress()).append(" ")
		.append(request.getMethodName()).append("(");
    	
    	if ( null != args ) 
    	{
    		StringBuilder sbArgs = new StringBuilder();
    		for (int i = 0; i < args.length; i++)
    		{
    			if(i!=0) sbArgs.append(",") ;
    			
    			if(args[i] == null)
        			sbArgs.append("NULL");
        		else if(args[i] instanceof Number || args[i] instanceof Boolean)
        			sbArgs.append(args[i]);
        		else
        			sbArgs.append(encodeParameter(args[i]));
    		}
        	builder.append(sbArgs);
    	}
    	
    	builder.append(")").append("	")
    	.append(response.getStatus()).append("	").append(cost);
    	
    	access.info(builder.toString());
    	
    	MethodSkeleton skeleton = KeystoneResourcesManager.getMethodSkeleton(request.getContextName(), request.getServiceName(), request.getMethodName(), false) ;
    	if(skeleton!=null) {
    		skeleton.access(request.getBornTime(), response.getStatus(), cost);
    	}
	}
	
	
	
	
	/**
	 * 
	 */
	private static int encodeParameterLength = 25 ;
	
	
	
	/**
	 * 
	 * @param args
	 * @return
	 */
	private static String encodeParameter(Object args) {
		String parameter = args.toString() ;
		if(parameter.isEmpty()) return "" ;
		String shortParam = parameter.length() > encodeParameterLength ? parameter.substring(0, encodeParameterLength) + "..("+ parameter.length() +")" : parameter ;
		return shortParam.replaceAll(" ","_").replaceAll("	","_").replaceAll("\n","+").replace(',', '，').replace('(', '（').replace(')', '）');
		
	}

    
	/**
	 * 
	 */
//	Logger ksclient = LoggerFactory.getLogger("ksclient.log") ;

}
