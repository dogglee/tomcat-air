package com.douglee.tomcatair.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {
	 private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 100, 60, TimeUnit.SECONDS,
	            new LinkedBlockingQueue<Runnable>(10));
	     
	    public static void run(Runnable r) {
	        threadPool.execute(r);
	    }
	    public static <T> Future<T> call(Callable<T> call){
	    	return threadPool.submit(call);
	    }
}
