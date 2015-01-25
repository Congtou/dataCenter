package com.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import service.thrift.PushMessage.Iface;

public class PushMessageImpl implements Iface
{
	@Override
	public List<String> push(List<String> messages) throws TException
	{
		Pusher pusher = Pusher.getInstance();
		// System.out.println(pusher);
		// System.out.println("Get new subscription. Push to client.");
		pusher.setResult(messages);
		pusher.setLock(false);
		// System.out.println("push return");
		return messages;
	}

	@Override
	public Map<String, String> registerCompleteNotify(
			Map<String, String> mappara) throws TException {
		System.out.println(" in registerCompleteNotify receive Message");
		// TODO Auto-generated method stub
		for(String key : mappara.keySet()){
			System.out.println("key = "+key+" value = "+mappara.get(key));
		}
		
		Map<String,String> resMap = new HashMap<String,String>();
		resMap.put("res", "success");
		return resMap;
	}
}
