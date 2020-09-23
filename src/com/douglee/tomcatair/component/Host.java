package com.douglee.tomcatair.component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.douglee.tomcatair.domain.Constant;
import com.douglee.tomcatair.util.ServerXMLUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;

public class Host {
	private String name;
    private Map<String, Context> contextMap; // 应用uri 与 context
    private Engine engine;
    public Host(String name, Engine engine){
        this.contextMap = new HashMap<>();
        this.name =  name;
        this.engine = engine;
        new WarFileWatcher(this).start(); // 启动
        scanContextsOnWebAppsFolder();
        scanContextsInServerXML();
        scanWarOnWebAppsFolder(); // 加载war文件
 
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    private  void scanContextsInServerXML() {
        List<Context> contexts = ServerXMLUtil.getContexts(this);
        for (Context context : contexts) {
            contextMap.put(context.getPath(), context);
        }
    }
 
    private  void scanContextsOnWebAppsFolder() {
        File[] folders = Constant.webappsFolder.listFiles();
        for (File folder : folders) {
            if (!folder.isDirectory())
                continue;
            loadContext(folder);
        }
    }
    private  void loadContext(File folder) {
    	String path = folder.getName();
		if ("ROOT".equals(path))
			path = "/";
		else
			path = "/" + path;
		String docBase = folder.getAbsolutePath();
		Context context = new Context(path, docBase, this, false);
		contextMap.put(context.getPath(), context);	
    }
    public void reload(Context context) {
        LogFactory.get().info("Reloading Context with name [{}] has started", context.getPath());
        String path = context.getPath();
        String docBase = context.getDocBase();
        boolean reloadable = context.isReloadable();
        // stop
        context.stop();
        // remove
        contextMap.remove(path);
        // allocate new context
        Context newContext = new Context(path, docBase, this, reloadable);
        // assign it to map
        contextMap.put(newContext.getPath(), newContext);
        LogFactory.get().info("Reloading Context with name [{}] has completed", context.getPath());
 
    }
    public void loadWar(File warFile) {
		String fileName =warFile.getName();
		String folderName = StrUtil.subBefore(fileName,".",true);
		//看看是否已经有对应的 Context了
		Context context= getContext("/"+folderName);
		if(null!=context)
			return;
		//先看是否已经有对应的文件夹
		File folder = new File(Constant.webappsFolder,folderName);
		if(folder.exists())
			return;
		//移动war文件，因为jar 命令只支持解压到当前目录下
		File tempWarFile = FileUtil.file(Constant.webappsFolder, folderName, fileName);
		File contextFolder = tempWarFile.getParentFile();
		contextFolder.mkdir();
		FileUtil.copyFile(warFile, tempWarFile);
		//解压
		String command = "jar xvf " + fileName;
		Process p =RuntimeUtil.exec(null, contextFolder, command);
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//解压之后删除临时war
		tempWarFile.delete();
		//然后创建新的 Context
		loadContext(contextFolder);
	}
    private void scanWarOnWebAppsFolder() {
    	File folder = FileUtil.file(Constant.webappsFolder);
    	File[] files = folder.listFiles();
    	for (File file : files) {
    		if(!file.getName().toLowerCase().endsWith(".war")) 
    			continue;
    		loadWar(file);
    	}				
    }
    public Context getContext(String path) {
        return contextMap.get(path);
    }
}
