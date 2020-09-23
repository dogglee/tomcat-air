package com.douglee.tomcatair.component;

import java.util.List;

import com.douglee.tomcatair.util.ServerXMLUtil;
/**
 * 引擎
 * @author doglee
 *
 */
public class Engine {
	private String defaultHost;
    private List<Host> hosts;
    private Service service;
    public Engine(Service service){
        this.defaultHost = ServerXMLUtil.getEngineDefaultHost();
        this.hosts = ServerXMLUtil.getHosts(this);
        this.service=service;
        checkDefault();
    }
 
    private void checkDefault() {
        if(null==getDefaultHost())
            throw new RuntimeException("the defaultHost" + defaultHost + " does not exist!");
    }
 
    public Host getDefaultHost(){
        for (Host host : hosts) {
            if(host.getName().equals(defaultHost))
                return host;
        }
        return null;
    }
}
