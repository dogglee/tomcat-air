package com.douglee.tomcatair.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.douglee.tomcatair.component.Context;
import com.douglee.tomcatair.domain.Constant;
import com.douglee.tomcatair.domain.Request;
import com.douglee.tomcatair.domain.Response;

import cn.hutool.core.util.ReflectUtil;
/**
 * 加载servlet，并执行service方法 单例
 * @author doglee
 *
 */
public class InvokerServlet extends HttpServlet {
	private static final InvokerServlet INSTANCE = new InvokerServlet();
	private InvokerServlet() {}
	public static InvokerServlet getInstance() {
        return INSTANCE;
    }
	@Override
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException, ServletException {
		Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;

        String uri = request.getUri();
        Context context = request.getContext();
        String servletClassName = context.getServletClassName(uri);

        try {
            Class servletClass = context.getWebappClassLoader().loadClass(servletClassName);
            Object servletObject = context.getServlet(servletClass);
            ReflectUtil.invoke(servletObject, "service", request, response);

            if(null!=response.getRedirectPath())
                response.setStatus(Constant.CODE_302);
            else
                response.setStatus(Constant.CODE_200);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

}
}
