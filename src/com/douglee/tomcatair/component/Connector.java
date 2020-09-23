package com.douglee.tomcatair.component;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.douglee.tomcatair.domain.Constant;
import com.douglee.tomcatair.domain.Request;
import com.douglee.tomcatair.domain.Response;
import com.douglee.tomcatair.processor.HttpProcessor;
import com.douglee.tomcatair.util.ThreadPoolUtil;
import com.douglee.tomcatair.util.WebXMLUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
/**
 * 连接器，多端口
 * @author doglee
 *
 */
public class Connector implements Runnable {
	private int port;
    private Service service;
    private String compression;
	private int compressionMinSize;
	private String noCompressionUserAgents;
	private String compressableMimeType;
    public Connector(Service service) {
        this.service = service;
    }
  
    public Service getService() {
        return service;
    }
  
    public void setPort(int port) {
        this.port = port;
    }
    public String getCompression() {
		return compression;
	}
	public void setCompression(String compression) {
		this.compression = compression;
	}
	public int getCompressionMinSize() {
		return compressionMinSize;
	}
	public void setCompressionMinSize(int compressionMinSize) {
		this.compressionMinSize = compressionMinSize;
	}
	public String getNoCompressionUserAgents() {
		return noCompressionUserAgents;
	}
	public void setNoCompressionUserAgents(String noCompressionUserAgents) {
		this.noCompressionUserAgents = noCompressionUserAgents;
	}
	public String getCompressableMimeType() {
		return compressableMimeType;
	}
	public void setCompressableMimeType(String compressableMimeType) {
		this.compressableMimeType = compressableMimeType;
	}
    @Override
    public void run() {// 多端口
        try {
            ServerSocket ss = new ServerSocket(port);
            while(true) {
                Socket s = ss.accept();
                Runnable r = new Runnable() { // 一个连接多个线程
                    @Override
                    public void run() {
                        try {
                            Request request = new Request(s,Connector.this);
                            Response response = new Response();
                            HttpProcessor processor = new HttpProcessor();
                            processor.execute(s,request, response);
                        } finally {
                            if (!s.isClosed())
                            try {
                                s.close(); // 一个请求完后，把连接关闭
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                ThreadPoolUtil.run(r);
            }
 
        } catch (IOException e) {
            LogFactory.get().error(e);
            e.printStackTrace();
        }
    }
  
    public void init() {
        LogFactory.get().info("Initializing ProtocolHandler [http-bio-{}]",port);
    }
  
    public void start() {
        LogFactory.get().info("Starting ProtocolHandler [http-bio-{}]",port);
        new Thread(this).start();
    }
}