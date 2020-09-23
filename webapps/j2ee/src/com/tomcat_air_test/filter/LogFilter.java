package com.tomcat_air_test.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LogFilter implements Filter{

	@Override
    public void destroy() {
         
    }
 
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
         
        HttpServletRequest request =  (HttpServletRequest) servletRequest;
        
        filterChain.doFilter(servletRequest, servletResponse);
        System.out.println("im filter logging request:"+request.toString());
    }
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("LogFilter 的初始化参数：");
        Enumeration<String>  e = filterConfig.getInitParameterNames();
        while(e.hasMoreElements()){
            String name = e.nextElement();
            String value = filterConfig.getInitParameter(name);
            System.out.println("name:" + name);
            System.out.println("value:" + value);
        }
    }

}
