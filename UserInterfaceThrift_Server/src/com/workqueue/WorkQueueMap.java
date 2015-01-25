package com.workqueue;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

public class WorkQueueMap {
	private static Map<String,LinkedList<Map<String,String>>> workQueue = null;
	private static Map<String,ByteBuffer> workBufferQueue = null;
	public static synchronized Map<String,LinkedList<Map<String,String>>> getWorkQueue(){
		if(workQueue == null){
//			workQueue = new Hashtable<String, LinkedList<Map<String,String>>>();
			workQueue = Collections.synchronizedMap(new Hashtable<String, LinkedList<Map<String,String>>>());
		}
		return workQueue;
	}
	
	public static synchronized Map<String,ByteBuffer> getWorkBufferQueue(){
		if(workBufferQueue == null){
//			workBufferQueue = new Hashtable<String, ByteBuffer>();
			workBufferQueue = Collections.synchronizedMap(new Hashtable<String, ByteBuffer>());
		}
		return workBufferQueue;
	}
}
