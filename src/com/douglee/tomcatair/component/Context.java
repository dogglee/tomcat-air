package com.douglee.tomcatair.component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.douglee.tomcatair.classloader.WebappClassLoader;
import com.douglee.tomcatair.domain.ApplicationContext;
import com.douglee.tomcatair.domain.StandardFilterConfig;
import com.douglee.tomcatair.domain.StandardServletConfig;
import com.douglee.tomcatair.exception.WebConfigDuplicatedException;
import com.douglee.tomcatair.util.ContextXMLUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
/**
 * web应用
 * @author doglee
 *
 */
public class Context {
	private String path;
    private String docBase;
    private File contextWebXmlFile;
    private WebappClassLoader webappClassLoader;// web应用类加载器
    private ServletContext servletContext; // 
    private List<ServletContextListener> listeners;
    private Map<String, String> url_servletClassName;
    private Map<String, String> url_servletName;
    private Map<String, String> servletName_className;
    private Map<String, String> className_servletName;
    private Map<String, Map<String, String>> servlet_className_init_params;
    // filter
    private Map<String, List<String>> url_filterClassName;
    private Map<String, List<String>> url_FilterNames;
    private Map<String, String> filterName_className;
    private Map<String, String> className_filterName;
    private Map<String, Map<String, String>> filter_className_init_params;
    
    private Map<String, Filter> filterPool; // filter池
    private Map<Class<?>, HttpServlet> servletSingletons; // servlet单例
    // 热加载
    private Host host;
    private boolean reloadable;
    private ContextFileChangeWatcher contextFileChangeWatcher;
    public Context(String path, String docBase, Host host, boolean reloadable) {
        this.path = path;
        this.docBase = docBase;
        this.host = host;
        this.reloadable = reloadable;
        this.contextWebXmlFile = new File(docBase, ContextXMLUtil.getWatchedResource());
        listeners=new ArrayList<ServletContextListener>();
        this.url_servletClassName = new HashMap<>();
        this.url_servletName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();
        this.servletSingletons=new HashMap<>();
        this.servlet_className_init_params = new HashMap<>();
        
        this.url_filterClassName = new HashMap<>();
    	this.url_FilterNames = new HashMap<>();
    	this.filterName_className = new HashMap<>();
    	this.className_filterName = new HashMap<>();
    	this.filter_className_init_params = new HashMap<>();
    	this.filterPool = new HashMap<>();
        this.servletContext = new ApplicationContext(this);
        // 初始化类加载器
        ClassLoader commonClassLoader = Thread.currentThread().getContextClassLoader();
        this.webappClassLoader = new WebappClassLoader(docBase, commonClassLoader);
        
        deploy();// 检查并将servlet配置信息包装
    }
    
    public void reload() {
        host.reload(this);
    }
    private void deploy() {
    	loadListeners(); // 先创建监听器
        TimeInterval timeInterval = DateUtil.timer();
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        init();// 检查并将servlet配置信息包装
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",this.getDocBase(),timeInterval.intervalMs());
        // 如果是热加载
        if(reloadable){
            contextFileChangeWatcher = new ContextFileChangeWatcher(this);
            contextFileChangeWatcher.start();
        }
    }
 
    private void init() {
        if (!contextWebXmlFile.exists()) //web.xml 不存在就返回了
            return;
 
        try {
            checkDuplicated(); // 检查配置文件正不正确，不正确抛出异常
        } catch (WebConfigDuplicatedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
 
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);
        parseServletMapping(d); // 开始装载
        parseServletInitParams(d); // 初始化启动参数
        parseFilterMapping(d); // 装载filter
        parseFilterInitParams(d); // filter启动参数
        initFilter(); // 启动filter
      //  parseLoadOnStartup(d);
      //  handleLoadOnStartup();
        publishEvent("init");
    }

    private void parseServletMapping(Document d) {
        // url_ServletName
        Elements mappingurlElements = d.select("servlet-mapping url-pattern");
        for (Element mappingurlElement : mappingurlElements) {
            String urlPattern = mappingurlElement.text();
            String servletName = mappingurlElement.parent().select("servlet-name").first().text();
            url_servletName.put(urlPattern, servletName);
        }
        // servletName_className / className_servletName
        Elements servletNameElements = d.select("servlet servlet-name");
        for (Element servletNameElement : servletNameElements) {
            String servletName = servletNameElement.text();
            String servletClass = servletNameElement.parent().select("servlet-class").first().text();
            servletName_className.put(servletName, servletClass);
            className_servletName.put(servletClass, servletName);
        }
        // url_servletClassName
        Set<String> urls = url_servletName.keySet();
        for (String url : urls) {
            String servletName = url_servletName.get(url);
            String servletClassName = servletName_className.get(servletName);
            url_servletClassName.put(url, servletClassName);
        }
    }
 
    private void checkDuplicated(Document d, String mapping, String desc) throws WebConfigDuplicatedException {
        Elements elements = d.select(mapping);
        // 判断逻辑是放入一个集合，然后把集合排序之后看两临两个元素是否相同
        List<String> contents = new ArrayList<>();
        for (Element e : elements) {
            contents.add(e.text());
        }
 
        Collections.sort(contents);
 
        for (int i = 0; i < contents.size() - 1; i++) {
            String contentPre = contents.get(i);
            String contentNext = contents.get(i + 1);
            if (contentPre.equals(contentNext)) {
                throw new WebConfigDuplicatedException(StrUtil.format(desc, contentPre));
            }
        }
    }
 
    private void checkDuplicated() throws WebConfigDuplicatedException {
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);
 
        checkDuplicated(d, "servlet-mapping url-pattern", "servlet url 重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-name", "servlet 名称重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-class", "servlet 类名重复,请保持其唯一性:{} ");
    }
    
    public synchronized  HttpServlet getServlet(Class<?> clazz)
            throws InstantiationException, IllegalAccessException, ServletException {
        HttpServlet servlet = servletSingletons.get(clazz);
        if (null == servlet) {
        	servlet = (HttpServlet) clazz.newInstance();
            ServletContext servletContext = this.getServletContext();
            String className = clazz.getName();
            String servletName = className_servletName.get(className);
            Map<String, String> initParameters = servlet_className_init_params.get(className);
            ServletConfig servletConfig = new StandardServletConfig(servletContext, servletName, initParameters);
            servlet.init(servletConfig);
            servletSingletons.put(clazz, servlet);
        }
        return servlet;
    }
    
    private void parseServletInitParams(Document d) {
        Elements servletClassNameElements = d.select("servlet-class");
        for (Element servletClassNameElement : servletClassNameElements) {
            String servletClassName = servletClassNameElement.text();
 
            Elements initElements = servletClassNameElement.parent().select("init-param");
            if (initElements.isEmpty())
                continue;
 
            Map<String, String> initParams = new HashMap<>();
 
            for (Element element : initElements) {
                String name = element.select("param-name").get(0).text();
                String value = element.select("param-value").get(0).text();
                initParams.put(name, value);
            }
 
            servlet_className_init_params.put(servletClassName, initParams);
        }
    }
    // 初始化
    private void initFilter() {
        Set<String> classNames = className_filterName.keySet();
        for (String className : classNames) {
            try {
                Class clazz =  this.getWebappClassLoader().loadClass(className);
                Map<String,String> initParameters = filter_className_init_params.get(className);
                String filterName = className_filterName.get(className);
                FilterConfig filterConfig = new StandardFilterConfig(servletContext, filterName, initParameters);
                Filter filter = filterPool.get(clazz);
                if(null==filter) {
                    filter = (Filter) ReflectUtil.newInstance(clazz);
                    filter.init(filterConfig);
                    filterPool.put(className, filter);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    //解析filter
    public void parseFilterMapping(Document d) {
    	// filter_url_name
    	Elements mappingurlElements = d.select("filter-mapping url-pattern");
    	for (Element mappingurlElement : mappingurlElements) {
    		String urlPattern = mappingurlElement.text();
    		String filterName = mappingurlElement.parent().select("filter-name").first().text();
    		
    		List<String> filterNames= url_FilterNames.get(urlPattern);
    		if(null==filterNames) { // 不存在再添加
    			filterNames = new ArrayList<>();
    			url_FilterNames.put(urlPattern, filterNames);
    		}
    		filterNames.add(filterName);
    	}
    	// class_name_filter_name
    	Elements filterNameElements = d.select("filter filter-name");
    	for (Element filterNameElement : filterNameElements) {
    		String filterName = filterNameElement.text();
    		String filterClass = filterNameElement.parent().select("filter-class").first().text();
    		filterName_className.put(filterName, filterClass);
    		className_filterName.put(filterClass, filterName);
    	}
    	// url_filterClassName
    	
    	Set<String> urls = url_FilterNames.keySet();
    	for (String url : urls) {
    		List<String> filterNames = url_FilterNames.get(url);
    		if(null == filterNames) {
    			filterNames = new ArrayList<>();
    			url_FilterNames.put(url, filterNames);
    		}
    		for (String filterName : filterNames) {
    			String filterClassName = filterName_className.get(filterName);
    			List<String> filterClassNames = url_filterClassName.get(url);
    			if(null==filterClassNames) {
    				filterClassNames = new ArrayList<>();
    				url_filterClassName.put(url, filterClassNames);
    			}
    			filterClassNames.add(filterClassName);
    		}
    	}
    }
    //解析filter参数
    private void parseFilterInitParams(Document d) {
    	Elements filterClassNameElements = d.select("filter-class");
    	for (Element filterClassNameElement : filterClassNameElements) {
    		String filterClassName = filterClassNameElement.text();
    		
    		Elements initElements = filterClassNameElement.parent().select("init-param");
    		if (initElements.isEmpty())
    			continue;
    		
    		
    		Map<String, String> initParams = new HashMap<>();
    		
    		for (Element element : initElements) {
    			String name = element.select("param-name").get(0).text();
    			String value = element.select("param-value").get(0).text();
    			initParams.put(name, value);
    		}
    		
    		filter_className_init_params.put(filterClassName, initParams);
    		
    	}		
    }
    // 匹配filter
    private boolean match(String pattern, String uri) {
        // 完全匹配
        if(StrUtil.equals(pattern, uri))
            return true;
        // /* 模式
        if(StrUtil.equals(pattern, "/*"))
            return true;

        return false;
    }
    // 获取该uri匹配到的过滤器集合
    public List<Filter> getMatchedFilters(String uri) {
    	List<Filter> filterList=new ArrayList<>();
    	Set<Entry<String, List<String>>> entrySet = url_filterClassName.entrySet();
    	Iterator<Entry<String, List<String>>> iterator = entrySet.iterator();
    	while(iterator.hasNext()){
    		Entry<String, List<String>> next = iterator.next();
    		if(match(next.getKey(),uri)){
    			List<String> value = next.getValue();
    			for(String className:value){
    				filterList.add(filterPool.get(className));
    			}
    		}
    			
    	}
		return filterList;
    }
    // 扫描listener
    private void loadListeners()  {
    	try {
    		if(!contextWebXmlFile.exists())
    			return;
    		String xml = FileUtil.readUtf8String(contextWebXmlFile);
    		Document d = Jsoup.parse(xml);
    		
    		Elements es = d.select("listener listener-class");
    		for (Element e : es) {
    			String listenerClassName = e.text();
    			
    			Class<?> clazz= this.getWebappClassLoader().loadClass(listenerClassName);
    			ServletContextListener listener = (ServletContextListener) clazz.newInstance();
    			addListener(listener);
    			
    		}
    	} catch (IORuntimeException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
    		throw new RuntimeException(e);
    	}
    }
    public void addListener(ServletContextListener listener){
    	listeners.add(listener);
    }
    private void publishEvent(String type) {
    	ServletContextEvent event = new ServletContextEvent(servletContext);
    	for (ServletContextListener servletContextListener : listeners) {
    		if("init".equals(type)) 
    			servletContextListener.contextInitialized(event);
    		if("destroy".equals(type)) 
    			servletContextListener.contextDestroyed(event);
    	}
    }
    // 生命周期相关
    public void stop() {
        webappClassLoader.stop();
        destroyServlets();
        contextFileChangeWatcher.stop();
        publishEvent("destroy");
    }
    // 生命周期相关
    private void destroyServlets() {
        Collection<HttpServlet> servlets = servletSingletons.values();
        for (HttpServlet servlet : servlets) {
            servlet.destroy();
        }
    }
    
    public ServletContext getServletContext() {
        return servletContext;
    }
 
    public String getServletClassName(String uri) {
        return url_servletClassName.get(uri);
    }
 
    public String getPath() {
        return path;
    }
 
    public void setPath(String path) {
        this.path = path;
    }
 
    public String getDocBase() {
        return docBase;
    }
 
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }
    public WebappClassLoader getWebappClassLoader() {
        return webappClassLoader;
    }
    public boolean isReloadable() {
        return reloadable;
    }
 
    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }
		@Override
		public String toString() {
			return "Context [path=" + path + ", docBase=" + docBase + "]";
		}
	    
}
