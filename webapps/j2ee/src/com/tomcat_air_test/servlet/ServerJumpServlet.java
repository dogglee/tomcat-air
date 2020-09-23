package com.tomcat_air_test.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerJumpServlet extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
        	response.getWriter().append(" im from serverJumpServlet");
            request.getRequestDispatcher("/hello").forward(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        }
 
    }
}
