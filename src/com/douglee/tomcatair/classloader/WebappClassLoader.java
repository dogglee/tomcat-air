package com.douglee.tomcatair.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import cn.hutool.core.io.FileUtil;
/**
 * 每个web应用应该有自己的类加载器，防止不同应用有相同的类
 * @author doglee
 *
 */
public class WebappClassLoader extends URLClassLoader{
	public WebappClassLoader(String docBase, ClassLoader commonClassLoader) {
        super(new URL[] {}, commonClassLoader);
 
        try {
        	// WEB-INF/classes 为字节码文件，lib为jar
            File webinfFolder = new File(docBase, "WEB-INF");
            File classesFolder = new File(webinfFolder, "classes");
            File libFolder = new File(webinfFolder, "lib");
            URL url;
            url = new URL("file:" + classesFolder.getAbsolutePath() + "/");
            this.addURL(url);
            List<File> jarFiles = FileUtil.loopFiles(libFolder);
            for (File file : jarFiles) {
                url = new URL("file:" + file.getAbsolutePath());
                this.addURL(url);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
 
    public void stop() {
        try {
            close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
