package com.access;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class TestFile {
	private static final String FILENAME = "test.txt";
	private static final int COMMA = 0x2c;
	private static final int RIGHTBRACKET = 0x5d;
	
	public static void serialize(Map<?,?> map) throws IOException{
		File file = new File(FILENAME);
		if(!file.exists())
			file.createNewFile();
		
		FileWriter writer = new FileWriter(file);
		
		writer.write(JSON.toJSONString(map));
		writer.close();
	}
	
	public static <T> T deserialze(Class<T> type) throws IOException{
		File file = new File(FILENAME);
		if(!file.exists())
			return null;
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		
		String line = reader.readLine();
		while(line != null){
			sb.append(line);
			line = reader.readLine();
		}
		reader.close();
		return JSON.parseObject(sb.toString(), type);
	}
	

	
	public static void update(Object o) throws IOException{
		String json = JSON.toJSONString(o);
		File file = new File(FILENAME);
		if(!file.exists())
			throw new FileNotFoundException();
		long len = file.length();
		RandomAccessFile writer = new RandomAccessFile(file, "rw");
		writer.seek(len - 1);
		writer.writeByte(COMMA);
		writer.write(json.getBytes());
		writer.writeByte(RIGHTBRACKET);
		
		writer.close();
	}
	public static void main(String[] args){
		TestFile tf = new TestFile();
		Map<String,String> mappara = new HashMap<String,String>();
		mappara.put("a", "test-a");
		mappara.put("b", "test-b");
		mappara.put("c", "test-c");
		/*
		try {
			tf.serialize(mappara);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		try {
			//tf.update(mappara);
			tf.update("AAA");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		System.out.println("after update the file");
		
		
		try {
			HashMap<String,String> tMap = tf.deserialze(HashMap.class);
			for(String str : tMap.keySet()){
				System.out.println("key = "+str+" value = "+tMap.get(str));
			}
			tMap.keySet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("after read the file");
		
		
	}

}
