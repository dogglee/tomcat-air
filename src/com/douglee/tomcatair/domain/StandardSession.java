package com.douglee.tomcatair.domain;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class StandardSession implements HttpSession{
	private Map<String, Object> attributesMap;
	 
    private String id;// sessionId
    private long creationTime; // 创建时间
    private long lastAccessedTime; // 最后访问时间
    private ServletContext servletContext; // servletContext
    private int maxInactiveInterval; // 最大生存时间
    public StandardSession(String jsessionid, ServletContext servletContext) {
        this.attributesMap = new HashMap<>();
        this.id = jsessionid;
        this.creationTime = System.currentTimeMillis();
        this.servletContext = servletContext;
    }
    @Override
    public void removeAttribute(String name) {
        attributesMap.remove(name);
 
    }
    @Override
    public void setAttribute(String name, Object value) {
        attributesMap.put(name, value);
    }
    @Override
    public Object getAttribute(String name) {
        return attributesMap.get(name);
    }
    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> keys = attributesMap.keySet();
        return Collections.enumeration(keys);
    }
    @Override
    public long getCreationTime() {
 
        return this.creationTime;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }
    
    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }
    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }
    @Override
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }
    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
    @Override
    public HttpSessionContext getSessionContext() {
 
        return null;
    }
    @Override
    public Object getValue(String arg0) {
 
        return null;
    }
    @Override
    public String[] getValueNames() {
 
        return null;
    }
    @Override
    public void invalidate() {
        attributesMap.clear();
 
    }
    @Override
    public boolean isNew() {
        return creationTime == lastAccessedTime;
    }
    @Override
    public void putValue(String arg0, Object arg1) {
 
    }
    @Override
    public void removeValue(String arg0) {
 
    }

}
