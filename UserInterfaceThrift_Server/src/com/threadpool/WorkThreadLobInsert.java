package com.threadpool;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serverAgent.ServerAgent;

public class WorkThreadLobInsert implements Callable<Map<String, String>>{
	private Map<String, String> mappara;
	private ByteBuffer buffer;
	private Map<String, String> result;
	ServerAgent serverAgent;
	private int interfaceType;
	private final int LOB_INSERT = 9;
	
	private final static Logger logger = LoggerFactory.getLogger(WorkThread.class); 
	public WorkThreadLobInsert(int interfaceType,Map<String, String> mappara,ByteBuffer buffer) {
		// TODO Auto-generated constructor stub
		this.mappara = mappara;
		this.interfaceType = interfaceType;
		this.buffer = buffer;
		serverAgent = new ServerAgent();
	}
	public Map<String, String> call() throws Exception {
		if(interfaceType!=LOB_INSERT){
			throw new Exception("interfaceType is wrong");
		}
		else{
			result = serverAgent.dealWithLobInsert(mappara,buffer);
		}
		return result;
	}
}
