package com.rest.servicebean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.thrift.TException;

import com.server.PushMessageClient;
import com.util.MapSerializer;

public class NotifyServiceImpl implements NotifyService {

	//private PushMessageClient pushMessageClient = new PushMessageClient();
	
	public String notifyUserSystem( String ip,String userSysName) {
		// TODO Auto-generated method stub
		System.out.println(" ip = "+ip+" userSysName = "+userSysName);
		PushMessageClient pushMessageClient = new PushMessageClient();
		pushMessageClient.setIpAddr(ip);

		pushMessageClient.openClient();
		Map<String ,String> mappara = new HashMap<String,String>();
		mappara.put("applicationNotify", "success");
		mappara.put(ip, userSysName);
		try {
			pushMessageClient.getPushClient().registerCompleteNotify(mappara);
			return "success";
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {
				System.out.println("============================================ begin deserialze");
				Map<String,String> map =  MapSerializer.deserialze(HashMap.class);
				System.out.println("ip = "+ip+"userSysName = "+userSysName);
				map.put(ip, userSysName);
				MapSerializer.serialize(map);
				System.out.println("============================================ after serialize");
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "fail";
		}
	}


	public String test(String id) {
		// TODO Auto-generated method stub
		System.out.println("in NotifyServiceImpl test()");
		return id+"success";
	}
	
	public static void main(String args[])
	{
		Map<String, String> map;
		try
		{
			map = MapSerializer.deserialze(HashMap.class);
			System.out.println(System.getProperty("user.dir"));
			//map = new HashMap<String, String>();
			map.put("a1", "test-a");
			MapSerializer.serialize(map);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
