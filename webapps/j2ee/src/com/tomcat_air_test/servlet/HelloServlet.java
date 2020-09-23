package com.tomcat_air_test.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
public class HelloServlet extends HttpServlet{
 
    public void service(HttpServletRequest request, HttpServletResponse response){
         
        try {
        	Enumeration<String> attributeNames = request.getParameterNames();
        	String parameter = request.getParameter("name");
            response.getWriter().println("hello tomcat-air"+"所带参数："+attributeNames+"  " +parameter);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
     
}