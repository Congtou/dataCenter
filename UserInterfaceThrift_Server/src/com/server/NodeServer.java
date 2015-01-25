package com.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class NodeServer {
	public Map<String, String> dataBaseConn(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		Map<String,String> ret = new HashMap<String, String>();
		ret.put("id", "123456");
		return ret;
	}
	public Map<String, String> dataOper(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	public Map<String, String> affairBegin(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	public Map<String, String> affairCommit(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	public Map<String, String> affairRollBack(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	public Map<String, String> affairEnd(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	public Map<String, String> dataSearchByTxt(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	public Map<String, String> dataSearchByMemory(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	public ByteBuffer lobSearch(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return ByteBuffer.allocate(256);
	}
	public Map<String, String> lobInsert(Map<String, String> mappara,ByteBuffer bytes){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	public Map<String, String> dataBaseDisconn(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	public Map<String, String> errInfo(Map<String, String> mappara){
		System.out.println("in NodeServer Class.");
		return new HashMap<String,String>();
	}
	
}
