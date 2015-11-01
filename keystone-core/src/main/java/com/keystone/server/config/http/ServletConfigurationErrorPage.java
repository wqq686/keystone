package com.keystone.server.config.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletConfigurationErrorPage extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 3214539855203065122L;
    private String msg;

    public ServletConfigurationErrorPage() {
        this("ServletConfigurationError, check the server logs for details.");
    }

    public ServletConfigurationErrorPage(String msg) {
        this.msg = msg;
    }

    
    /**
     * 
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.getWriter().println(msg);
        response.flushBuffer();
    }
}
