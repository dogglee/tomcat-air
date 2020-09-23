# tomcat-air设计实现

**tomcat-air是模仿tomcat的一个项目，提供servlet容器的一些基本功能**
提供了servlet容器的一些基			    本常用的功能。比如常规的http get和post请求响应静态资源的访问、			    cookie和session、客户端跳转和服务端跳转、filter过滤器等


## 类加载体系实现资源隔离

	把启动环境和依赖环境分开来，启动环境启动时使用APPClassLoader,然后通过自定义commoClassLoader,去加载项目中依赖的其它类，为每个context实现自定义ApplicationClassLoader类加载器，
使用bat脚本启动如下：

> del /q bootstrap.jar
jar cvf0 bootstrap.jar -C bin com/douglee/tomcatair/Bootstrap.class -C bin com/douglee/tomcatair/classloader/CommonClassLoader.class
del /q lib/tomcat-air.jar
cd bin
jar cvf0 ../lib/tomcat-air.jar *
cd ..
java -cp bootstrap.jar com.douglee.tomcatair.Bootstrap
pause


## 多端口启动
	配置多connector实现多端口启动

## 线程池实现多连接
	使用线程池实现多请求的处理,bio下一个连接使用一条线程处理

## servletSingletons
	servlet单俐池实现servlet的单俐

## HttpProcessor
	讲请求交给servlet，filter的执行，io流的处理关闭

## SessionManager
	负责session的管理，比如session的新增，获取session，通过session生成cookie和session的过期销毁

##ApplicationRequestDispatcher
	实现内部request的转发

##ApplicationFilterChain
	责任链模式，filterChain实现过滤器

##ContextFileChangeWatcher
	实现context应用文件路径监听，当jar包或class文件发生变化时，reload context实现热加载

