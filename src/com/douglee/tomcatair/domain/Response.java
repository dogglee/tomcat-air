package com.douglee.tomcatair.domain;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

public class Response extends AbstractResponse{
	 	
	    public Response(){
	        this.stringWriter = new StringWriter();
	        this.writer = new PrintWriter(stringWriter);
	        this.contentType = "text/html;charset=UTF-8";
	        this.cookies = new ArrayList<>();
	        this.header = new HashMap<String, String>();
	    }
	
	    public String getRedirectPath() {
	        return this.redirectPath;
	    }
	    @Override
	    public void sendRedirect(String redirect) throws IOException {
	        this.redirectPath = redirect;
	    }
	    @Override
	    public String getContentType() {
	        return contentType;
	    }
	    @Override
		public PrintWriter getWriter() {
			return writer;
		}

		public StringWriter getStringWriter() {
			return stringWriter;
		}
		public void setStringWriter(StringWriter stringWriter) {
			this.stringWriter = stringWriter;
		}
		public void setWriter(PrintWriter writer) {
			this.writer = writer;
		}
		public void setContentType(String contentType) {
			this.contentType = contentType;
		}
		public void setBody(byte[] body) {
		        this.body = body;
		}
		
		public byte[] getBody() throws UnsupportedEncodingException {
	        if(null==body) {
	            String content = stringWriter.toString();
	            body = content.getBytes("utf-8");
	        }
	        return body;
	    }
		
		@Override
		public String toString() {
			return "Response [stringWriter=" + stringWriter + ", writer=" + writer + ", contentType=" + contentType
					+ ", body=" + Arrays.toString(body) + "]";
		}
		@Override
	    public void setStatus(int status) {
	        this.status = status;
	    }
		@Override
	    public int getStatus() {
	        return status;
	    }
		@Override
		public void addCookie(Cookie cookie) {
		        cookies.add(cookie);
		    }
	
		public List<Cookie> getCookies() {
		        return this.cookies;
		    }
		public String getCookiesHeader() {
		        if(null==cookies)
		            return "";
		 
		        String pattern = "EEE, d MMM yyyy HH:mm:ss 'GMT'";
		        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
		 
		        StringBuffer sb = new StringBuffer();
		        for (Cookie cookie : getCookies()) {
		            sb.append("\r\n");
		            sb.append("Set-Cookie: ");
		            sb.append(cookie.getName() + "=" + cookie.getValue() + "; ");
		            if (-1 != cookie.getMaxAge()) { //-1 永不过期
		                sb.append("Expires=");
		                Date now = new Date();
		                Date expire = DateUtil.offset(now, DateField.MINUTE, cookie.getMaxAge());
		                sb.append(sdf.format(expire));
		                sb.append("; ");
		            }
		            if (null != cookie.getPath()) {
		                sb.append("Path=" + cookie.getPath());
		            }
		        }
		 
		        return sb.toString();
		    }
}
