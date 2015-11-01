package com.keystone.server.admin;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;

import com.keystone.server.config.http.HttpServletConfig;
import com.keystone.server.contexts.KeystoneAppContext;
import com.keystone.support.common.CommonUtils;
import com.keystone.support.common.StringUtils;
import com.keystone.support.utils.Pair;

public class KeystoneAdminContext extends KeystoneAppContext {
	
	
	/**
	 * 
	 */
	public KeystoneAdminContext() {
		addAdminServlet("ServerInfomationServlet", "/server.api", "com.keystone.server.admin.servlet.ServerInfomationServlet");
		addAdminServlet("ContextInfomationServlet", "/context.api", "com.keystone.server.admin.servlet.ContextInfomationServlet");
		addAdminServlet("ServiceInfomationServlet", "/service.api", "com.keystone.server.admin.servlet.ServiceInfomationServlet");
		addAdminServlet("MethodInfomationServlet", "/method.api", "com.keystone.server.admin.servlet.MethodInfomationServlet");
		addAdminServlet("MethodInvokedServlet", "/invoke.api", "com.keystone.server.admin.servlet.MethodInvokedServlet");
	}
	
	
	
	
	/**
	 * 
	 */
	private void addAdminServlet(String name, String pattern, String className) {
		HttpServlet servlet = CommonUtils.newInstance(className) ;
		HttpServletConfig config = new HttpServletConfig(name, className);
		config.addUrlPattern(pattern) ;
		getHttpContainer().addServlet(name, servlet, config);
	}
	
	
	/**
	 * 
	 */
	@Override
	public Pair<String, InputStream> getStaticResource(String filename) throws IOException {
		filename = StringUtils.emptyOrTrim(filename) ;
		if(StringUtils.isEmpty(filename) || "/".equals(filename)) filename = "/admin-index.html" ;
		if(!filename.startsWith("/")) filename = "/" + filename ;
		
		filename = "resources" + filename ;
		
		InputStream is =  KeystoneAdminContext.class.getResourceAsStream(filename) ;
		
		return new Pair<String, InputStream>(filename, is) ;
	}

}
