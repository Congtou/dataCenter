package com.session;

import java.util.Hashtable;


public class ThreadObjectSession {
	private static Hashtable<String,Object> session =new Hashtable<String,Object>();

	public static void Set(String key,Object value){
		if(key==null){
			try {
				throw new Exception("key can not be null");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(value==null){
			try {
				throw new Exception("value can not be null");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		session.put(key, value);
	}
	
	public static Object get(String key){
		Object value =false;
		if(key ==null){
			try {
				throw new Exception("key can not be null");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(session.get(key)!=null){
			value = session.get(key);
		}
		return value;
	}
	
	public static void delete(String key){
		if(key ==null){
			try {
				throw new Exception("key can not be null");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		session.remove(key);
	}
}
