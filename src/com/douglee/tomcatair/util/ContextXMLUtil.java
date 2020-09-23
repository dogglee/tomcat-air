package com.douglee.tomcatair.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.douglee.tomcatair.domain.Constant;

import cn.hutool.core.io.FileUtil;

public class ContextXMLUtil {
	//应用的web.xml路径
	public static String getWatchedResource() {
        try {
            String xml = FileUtil.readUtf8String(Constant.contextXmlFile);
            Document d = Jsoup.parse(xml);
            Element e = d.select("WatchedResource").first();
            return e.text();
        } catch (Exception e) {
            e.printStackTrace();
            return "WEB-INF/web.xml";
        }
    }
}
