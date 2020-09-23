package com.douglee.tomcatair.domain;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.douglee.tomcatair.processor.HttpProcessor;
/**
 * 服务端路由跳转
 * @author doglea
 *
 */
public class ApplicationRequestDispatcher implements RequestDispatcher{
	private String uri;
    public ApplicationRequestDispatcher(String uri) {
        if(!uri.startsWith("/"))
            uri = "/" + uri;
        this.uri = uri;
    }
	@Override
	public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
		Request request = (Request) servletRequest;
        Response response = (Response) servletResponse;
 
        request.setUri(uri);
         
        HttpProcessor processor = new HttpProcessor();
        processor.execute(request.getSocket(), request,response); // 这里就会返回forwarded之后的响应
        request.setForwarded(true);// 当这里Set为true之后，第一次的请求就没有响应了
	}

	@Override
	public void include(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

}
