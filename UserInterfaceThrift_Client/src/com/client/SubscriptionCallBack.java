package com.client;

import java.util.List;

public interface SubscriptionCallBack
{
	// Result format
	// List<String> list = new ArrayList<String>();
	// list.add(sendList.get(i).getTableName());
	// list.add(sendList.get(i).getTitle());
	// list.add(sendList.get(i).getContent());
	// list.add(String.valueOf(sendList.get(i).getOper()));
	//
	public void receiveSubsription(List<String> result);
}
