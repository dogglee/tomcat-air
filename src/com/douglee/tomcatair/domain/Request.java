package com.douglee.tomcatair.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.douglee.tomcatair.component.Connector;
import com.douglee.tomcatair.component.Context;
import com.douglee.tomcatair.component.Engine;
import com.douglee.tomcatair.component.Service;
import com.douglee.tomcatair.util.IOUtils;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

public class Request extends AbstractRequset {
	
	public Request(Socket is,Connector connector) {
		this.socket = is;
		this.connector=connector;
		this.service=connector.getService();
		this.parameterMap = new HashMap<>();
		this.headerMap = new HashMap<>();
		parseInit();
	}
	
	private void parseInit() {
		parseRequsetString();
		parseMethod();
		if(StrUtil.isBlank(requestString))
			return;
		parseUri();
		parseContext();
		if(!"/".equals(context.getPath())){
            uri = StrUtil.removePrefix(uri, context.getPath());
            if(StrUtil.isEmpty(uri))
                uri = "/";
        }
		parseParameters();
		parseHeaders();
		parseCookies();
	}
	// 解析context应用 path
	private void parseContext() {
        String path = StrUtil.subBetween(uri, "/", "/");// /号后的第一个
        if (null == path)
            path = "/";
        else
            path = "/" + path;
        Engine engine = service.getEngine();
        context = engine.getDefaultHost().getContext(path);
        if (null == context)
            context = engine.getDefaultHost().getContext("/");
    }
	//解析rui
	private void parseUri() {
		 String temp;
	     temp = StrUtil.subBetween(requestString, " ", " ");
	     if (!StrUtil.contains(temp, '?')) {
	            uri = temp;
	            return;
	        }
	      temp = StrUtil.subBefore(temp, '?', false);
	      uri = temp;
	}
	//解析请求头
	
	private void parseMethod() {
        method = StrUtil.subBefore(requestString, " ", false);
	}
	//解析请求内容
	private void parseRequsetString() {
		try {
			byte[] readBytes = IOUtils.readBytes(this.socket.getInputStream());
			requestString = new String(readBytes, "utf-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//解析参数
	private void parseParameters() {
		if ("GET".equals(this.getMethod())) {
			String url = StrUtil.subBetween(requestString, " ", " ");
			if (StrUtil.contains(url, '?')) {
				requestString = StrUtil.subAfter(url, '?', false);
			}
		}
		if ("POST".equals(this.getMethod())) {
			requestString = StrUtil.subAfter(requestString, "\r\n\r\n", false);
		}
		if (null == requestString)
			return;
		requestString = URLUtil.decode(requestString);// 转译
		String[] parameterValues = requestString.split("&");
		if (null != parameterValues) {
			for (String parameterValue : parameterValues) {
				String[] nameValues = parameterValue.split("=");
				String name = nameValues[0];
				String value = nameValues[1];
				String values[] = parameterMap.get(name);
				if (null == values) {
					values = new String[] { value };
					parameterMap.put(name, values);
				} else {
					values = ArrayUtil.append(values, value);
					parameterMap.put(name, values);
				}
			}
		}
	}
	// 请求头
	public void parseHeaders() {
		StringReader stringReader = new StringReader(requestString);
		List<String> lines = new ArrayList<>();
		IoUtil.readLines(stringReader, lines);
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			if (0 == line.length())
				break;
			String[] segs = line.split(":");
			String headerName = segs[0].toLowerCase();
			String headerValue = segs[1];
			headerMap.put(headerName, headerValue);
			// System.out.println(line);
		}
	}
	private void parseCookies() {
		List<Cookie> cookieList = new ArrayList<>();
		String cookies = headerMap.get("cookie");
		if (null != cookies) {
			String[] pairs = StrUtil.split(cookies, ";");
			for (String pair : pairs) {
				if (StrUtil.isBlank(pair))
					continue;
				// System.out.println(pair.length());
				// System.out.println("pair:"+pair);
				String[] segs = StrUtil.split(pair, "=");
				String name = segs[0].trim();
				String value = segs[1].trim();
				Cookie cookie = new Cookie(name, value);
				cookieList.add(cookie);
			}
		}
		this.cookies = ArrayUtil.toArray(cookieList, Cookie.class);
	}
	public String getParameter(String name) {
		String values[] = parameterMap.get(name);
		if (null != values && 0 != values.length)
			return values[0];
		return null;
	}
	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(parameterMap.keySet());
	}
	public String[] getParameterValues(String name) {
		return parameterMap.get(name);
	}
	public String getHeader(String name) {
		if(null==name)
			return null;
		name = name.toLowerCase();
		return headerMap.get(name);
	}
	public Enumeration getHeaderNames() {
		Set keys = headerMap.keySet();
		return Collections.enumeration(keys);
	}
	public int getIntHeader(String name) {
		String value = headerMap.get(name);
		return Convert.toInt(value, 0);
	}
	public Cookie[] getCookies() {
		return cookies;
	}
	@Override
	public ServletContext getServletContext() {
	        return context.getServletContext();
	    }
	@Override
	public String getRealPath(String path) {
	        return getServletContext().getRealPath(path);
	    }
	public String getRequestString() {
		return requestString;
	}
	public String getUri() {
		return uri;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public void setRequestString(String requestString) {
		this.requestString = requestString;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	public RequestDispatcher getRequestDispatcher(String uri) {
        return new ApplicationRequestDispatcher(uri);
    }
	@Override
	public String toString() {
		return "Request [requestString=" + requestString + ", uri=" + uri + ", socket=" + socket + ", context="
				+ context + "]";
	}

}
