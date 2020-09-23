package com.douglee.tomcatair.domain;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class StandardServletConfig implements ServletConfig{
	private static final Enumeration<String> Collect = null;
	private ServletContext servletContext;
    private Map<String, String> initParameters;
    private String servletName;
 
    public StandardServletConfig(ServletContext servletContext, String servletName,
            Map<String, String> initParameters) {
        this.servletContext = servletContext;
        this.servletName = servletName;
        this.initParameters = initParameters;
        if (null == this.initParameters)
            this.initParameters = new HashMap<>();
    }
 
    @Override
    public String getInitParameter(String name) {
        // TODO Auto-generated method stub
        return initParameters.get(name);
    }
 
    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }
 
    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
 
    @Override
    public String getServletName() {
        return servletName;
    }
}
