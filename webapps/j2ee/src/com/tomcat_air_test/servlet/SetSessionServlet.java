package com.tomcat_air_test.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SetSessionServlet extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            request.getSession().setAttribute("name", "dogglee");
            request.getSession().setAttribute("password", "123");
            response.getWriter().println("your sessionId:"+request.getSession().getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
