package com.threadpool;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serverAgent.ServerAgent;

public class WorkThreadLob implements Callable<ByteBuffer>{
	private Map<String, String> mappara;
	private ByteBuffer buffer;
	ServerAgent serverAgent;
	private int interfaceType;
	private final int LOB_SEARCH = 8;
	private final static Logger logger = LoggerFactory.getLogger(WorkThread.class); 
	public WorkThreadLob(int interfaceType,Map<String, String> mappara) {
		// TODO Auto-generated constructor stub
		this.mappara = mappara;
		this.interfaceType = interfaceType;
		serverAgent = new ServerAgent();
	}
	public ByteBuffer call() throws Exception {
		if(interfaceType!=LOB_SEARCH){
			throw new Exception("interfaceType is wrong");
		}
		else{
			buffer = serverAgent.dealWithLobSearch(mappara);
		}
		return buffer;
	}
}
