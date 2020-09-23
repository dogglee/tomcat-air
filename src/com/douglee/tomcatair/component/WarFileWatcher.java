package com.douglee.tomcatair.component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

import com.douglee.tomcatair.domain.Constant;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;

/**
 * war文件监听，动态加载war
 * @author dogglee
 *
 */
public class WarFileWatcher {
	private WatchMonitor monitor;
	public WarFileWatcher(Host host) {
		// 1不监听子文件
        this.monitor = WatchUtil.createAll(Constant.webappsFolder, 1, new Watcher() {
            private void dealWith(WatchEvent<?> event, Path currentPath) {
                synchronized (WarFileWatcher.class) {
                    String fileName = event.context().toString();
                    if(fileName.toLowerCase().endsWith(".war")  && monitor.ENTRY_CREATE.equals(event.kind())) {
                        File warFile = FileUtil.file(Constant.webappsFolder, fileName);
                        host.loadWar(warFile);
                    }
                }
            }
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                dealWith(event, currentPath);
            }
 
            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                dealWith(event, currentPath);
 
            }
            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                dealWith(event, currentPath);
            }
            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                dealWith(event, currentPath);
            }
 
        });
    }
 
    public void start() {
        monitor.start();
    }
 
    public void stop() {
        monitor.interrupt();
    }
}
