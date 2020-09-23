package com.douglee.tomcatair.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public abstract class AbstractResponse implements HttpServletResponse{
	protected StringWriter stringWriter;
	protected PrintWriter writer;
	protected String contentType;
	protected Map<String,String> header;
	protected int status;
	protected byte[] body;
	protected List<Cookie> cookies;
	protected String redirectPath;
	@Override
    public void addCookie(Cookie cookie) {
 
    }
 
    @Override
    public boolean containsHeader(String s) {
        return false;
    }
 
    @Override
    public String encodeURL(String s) {
        return null;
    }
 
    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }
 
    @Override
    public String encodeUrl(String s) {
        return null;
    }
 
    @Override
    public String encodeRedirectUrl(String s) {
        return null;
    }
 
    @Override
    public void sendError(int i, String s) throws IOException {
 
    }
 
    @Override
    public void sendError(int i) throws IOException {
 
    }
 
    @Override
    public void sendRedirect(String s) throws IOException {
 
    }
 
    @Override
    public void setDateHeader(String s, long l) {
 
    }
 
    @Override
    public void addDateHeader(String s, long l) {
 
    }
 
    @Override
    public void setHeader(String s, String s1) {
    	header.put(s, s1);
    }
 
    @Override
    public void addHeader(String s, String s1) {
    	header.put(s, s1);
    }
 
    @Override
    public void setIntHeader(String s, int i) {
 
    }
 
    @Override
    public void addIntHeader(String s, int i) {
 
    }
 
    @Override
    public void setStatus(int i) {
 
    }
 
    @Override
    public void setStatus(int i, String s) {
 
    }
 
    @Override
    public int getStatus() {
        return 0;
    }
 
    @Override
    public String getHeader(String s) {
        return null;
    }
 
    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }
 
    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }
 
    @Override
    public String getCharacterEncoding() {
        return null;
    }
 
    @Override
    public String getContentType() {
        return null;
    }
 
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
 
    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }
 
    @Override
    public void setCharacterEncoding(String s) {
    	header.put("Content-type", "text/html;charset=UTF-8");
    }
 
    @Override
    public void setContentLength(int i) {
 
    }
 
    @Override
    public void setContentType(String s) {
 
    }
 
    @Override
    public void setBufferSize(int i) {
 
    }
 
    @Override
    public int getBufferSize() {
        return 0;
    }
 
    @Override
    public void flushBuffer() throws IOException {
 
    }
 
    @Override
    public void resetBuffer() {
 
    }
 
    @Override
    public boolean isCommitted() {
        return false;
    }
 
    @Override
    public void reset() {
 
    }
 
    @Override
    public void setLocale(Locale locale) {
 
    }
 
    @Override
    public Locale getLocale() {
        return null;
    }
}
