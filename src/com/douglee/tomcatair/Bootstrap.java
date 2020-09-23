package com.douglee.tomcatair;

import java.lang.reflect.Method;

import com.douglee.tomcatair.classloader.CommonClassLoader;

/**
 * Hello world!
 *
 */
public class Bootstrap 
{
	 public static void main(String[] args) throws Exception {
	        CommonClassLoader commonClassLoader = new CommonClassLoader();
	 
	        Thread.currentThread().setContextClassLoader(commonClassLoader);
	 
	        String serverClassName = "com.douglee.tomcatair.component.Server";
	        /**
	         *  这里双亲委派机制会 把server类交给父类加载器加载，发现可以加载成功，此时拿到server对像类加载器是app类加载器
	         *  解决方法：
	         *  	把启动环境和依赖环境分离开来，使得当appclassload想加载server时没有发现这个类
	         *  	启动类只需要包含 commonClassLoader和Bootstrap
	         *  	然后让commonClassLoader加载server类以及lib目录下的类
	         *  	webClassLoader加载webapps下面的
	         */
	        Class<?> serverClazz = commonClassLoader.loadClass(serverClassName);
	 
	        Object serverObject = serverClazz.newInstance();
	 
	        Method m = serverClazz.getMethod("start");
	 
	        m.invoke(serverObject);
	        System.out.println(serverObject.getClass().getClassLoader());
	        // 不能关闭，否则后续就不能使用啦
	        // commonClassLoader.close();
	 
	    }
   
}
