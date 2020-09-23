package com.tomcat_air_test.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetSessionServlet extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            String name = (String)request.getSession().getAttribute("name");
            String password = (String)request.getSession().getAttribute("name");
            response.getWriter().println("name:"+name+"\r\n"+"password:"+password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
