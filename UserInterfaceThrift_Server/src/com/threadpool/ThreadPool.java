package com.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	private static ExecutorService excutorService = null;
	private final static int POOL_SIZE = 4;
	public static ExecutorService getThreadPool(){
		synchronized (ThreadPool.class) {
			if(excutorService == null){
				excutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*POOL_SIZE);
			}
		}
		return excutorService;
	}
}
