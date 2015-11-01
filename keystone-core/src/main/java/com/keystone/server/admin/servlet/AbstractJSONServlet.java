package com.keystone.server.admin.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.keystone.support.common.ExceptionUtils;

public abstract class AbstractJSONServlet extends HttpServlet {
	
	private static final long serialVersionUID = -7738099101070784223L;


	@Override
	public void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		Map<String, Object> response = new HashMap<>() ;
		response.put("code", 200) ;
		try
		{
			Object result = proccessRequest(httpRequest, httpResponse) ;
			if(result!=null) response.put("result", result) ;
		}
		catch(Throwable t)
		{
			t.printStackTrace() ;
			response.put("code", 500) ;
			String desc = ExceptionUtils.formatThrowable(t) ;
			response.put("desc", desc) ;
		}
		
		String text = JSON.toJSONString(response) ;
		httpResponse.getWriter().write(text) ;
		httpResponse.flushBuffer();
	}
	
	
	protected abstract Object proccessRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse)  ;
}
