package com.douglee.tomcatair.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.douglee.tomcatair.component.Context;
import com.douglee.tomcatair.domain.Constant;
import com.douglee.tomcatair.domain.Request;
import com.douglee.tomcatair.domain.Response;
import com.douglee.tomcatair.util.WebXMLUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 
 * @author doglee
 *
 */
public class DefaultServlet extends HttpServlet{
	private static final DefaultServlet instance = new DefaultServlet();
	 
    public static  DefaultServlet getInstance() {
        return instance;
    }
 
    private DefaultServlet() {
 
    }
    @Override
    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException, ServletException {
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;
 
        Context context = request.getContext();
 
        String uri = request.getUri();
        if ("/500.html".equals(uri))
            throw new RuntimeException("this is a deliberately created exception");
 
        if ("/".equals(uri))
            uri = WebXMLUtil.getWelcomeFile(request.getContext());
 
        String fileName = StrUtil.removePrefix(uri, "/");
        File file = FileUtil.file(context.getDocBase(), fileName);
 
        if (file.exists()) {
            String extName = FileUtil.extName(file);
            String mimeType = WebXMLUtil.getMimeType(extName);
            response.setContentType(mimeType);
 
            byte body[] = FileUtil.readBytes(file);
            response.setBody(body);
 
            if (fileName.equals("timeConsume.html"))
                ThreadUtil.sleep(1000);
 
            response.setStatus(Constant.CODE_200);
        } else {
            response.setStatus(Constant.CODE_404);
        }
 
    }
}
