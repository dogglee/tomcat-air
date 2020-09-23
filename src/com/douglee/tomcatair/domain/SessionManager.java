package com.douglee.tomcatair.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;

public class SessionManager {
	private static Map<String, StandardSession> sessionMap = new ConcurrentHashMap<>(); //Concurrent
    private static int defaultTimeout = getTimeout();
    static {
        startSessionOutdateCheckThread();
    }
 
    public static HttpSession getSession(String jsessionid, Request request, Response response) {
    	// 如果浏览器没有传sessionId过来，创建一个
        if (null == jsessionid) {
            return newSession(request, response);
        } else {
            StandardSession currentSession = sessionMap.get(jsessionid);
            // 传过来的无效也创建一个
            if (null == currentSession) {
                return newSession(request, response);
            } else {
                currentSession.setLastAccessedTime(System.currentTimeMillis()); // 设置最后访问时间
                createCookieBySession(currentSession, request, response);// 创建cookie
                return currentSession;
            }
        }
    }
 
    private static void createCookieBySession(HttpSession session, Request request, Response response) {
        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setMaxAge(session.getMaxInactiveInterval());
        cookie.setPath(request.getContext().getPath());
        response.addCookie(cookie);
    }
 
    private static HttpSession newSession(Request request, Response response) {
        ServletContext servletContext = request.getServletContext();
        String sid = generateSessionId();
        StandardSession session = new StandardSession(sid, servletContext);
        session.setMaxInactiveInterval(defaultTimeout);
        session.setLastAccessedTime(System.currentTimeMillis());
        sessionMap.put(sid, session);
        createCookieBySession(session, request, response);
        return session;
    }
 
    private static int getTimeout() {
        int defaultResult = 30;
        try {
            Document d = Jsoup.parse(Constant.webXmlFile, "utf-8");
            Elements es = d.select("session-config session-timeout");
            if (es.isEmpty())
                return defaultResult;
            return Convert.toInt(es.get(0).text());
        } catch (IOException e) {
            return defaultResult;
        }
    }
    // 这个地方第一次出现了concurrentmodify,使用entrySet效率更高
    private static void checkOutDateSession() {
        Set<Entry<String, StandardSession>> entrySet = sessionMap.entrySet();
        Iterator<Entry<String, StandardSession>> iterator = entrySet.iterator();
        while(iterator.hasNext()){
        	Entry<String, StandardSession> entry=iterator.next();
        	StandardSession session=entry.getValue();
        	long interval = System.currentTimeMillis() -  session.getLastAccessedTime();
            if (interval > session.getMaxInactiveInterval() * 1000)
            	iterator.remove();
        }
    }
 
    private static void startSessionOutdateCheckThread() {
        new Thread() {
            public void run() {
                while (true) {
                    checkOutDateSession();
                    ThreadUtil.sleep(1000 * 30);// 30秒检测一次
                }
            }
 
        }.start();
 
    }
    // 使用HashMap的话，这里要加锁，这里不加锁每次一个请求都生成一个cookie
    public static  String generateSessionId() {
        String result = null;
        byte[] bytes = RandomUtil.randomBytes(16);
        result = new String(bytes);
        result = SecureUtil.md5(result);
        result = result.toUpperCase();
        return result;
    }
 
}
