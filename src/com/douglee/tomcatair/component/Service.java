package com.douglee.tomcatair.component;

import java.util.List;

import com.douglee.tomcatair.util.ServerXMLUtil;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;

public class Service {
	 private String name;
	 private Engine engine;
	 private Server server;
	 
	    private List<Connector> connectors;
	    public Service(Server server){
	        this.server = server;
	        this.name = ServerXMLUtil.getServiceName();
	        this.engine = new Engine(this);
	        this.connectors = ServerXMLUtil.getConnectors(this);
	    }
	 
	    public Engine getEngine() {
	        return engine;
	    }
	 
	    public Server getServer() {
	        return server;
	    }
	 
	    public void start() {
	        init();
	    }
	 
	    private void init() {
	        TimeInterval timeInterval = DateUtil.timer();
	        for (Connector c : connectors)
	            c.init();
	        LogFactory.get().info("Initialization processed in {} ms",timeInterval.intervalMs());
	        for (Connector c : connectors)// 多端口启动
	            c.start();
	    }
}
