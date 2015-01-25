package com.client.subscription.test;

import java.util.List;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.access.DataAccessInterface;
import com.access.test.DataAccessInterfaceTest;
import com.client.Pusher;
import com.client.SubscriptionCallBack;
import com.util.TimeRecord;

public class SubscriptionClientTest implements SubscriptionCallBack
{
	Pusher						pusher	= Pusher.getInstance();

	private final static Logger	logger	= LoggerFactory.getLogger(DataAccessInterfaceTest.class);

	@Override
	public void receiveSubsription(List<String> result)
	{
		if (result == null)
			return;
		for (String string : result)
		{
			System.out.print(string + ";");
		}
		System.out.println();
	}

	public void getSubscription()
	{
		// System.out.println(pusher);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				pusher.getSubscription(SubscriptionClientTest.this);
				getSubscription();
			}
		}).start();
	}

	public static void main(String[] args) throws TTransportException
	{
		
		try
		{
			DataAccessInterface dai = new DataAccessInterface();
			//dai.start();
			logger.info("current time before statup dataBaseConn = " + TimeRecord.CurrentCompleteTime());

			// Subscribe
			dai.subscriptionRequest("ADD", "dili", 2);
			//dai.subscriptionRequest("ADD", "river", 0);
			//dai.subscriptionRequest("RMV", "mountains", 0);
			//dai.subscriptionRequest("ADD", "null", 0);
			
			SubscriptionClientTest clientTest = new SubscriptionClientTest();
			clientTest.getSubscription();
			
			String ID = dai.dataBaseConn("test", "1","11");
			
			System.out.println("result ID = " + ID);
			System.out.println("connection id = " + ID);
			dai.transactionBegin(ID);
			String SQLinfo1 = "insert into dili(name) values('324') ;";
			dai.dataOper(ID, SQLinfo1);
			String SQLinfo2 = "update dili set name ='name' where id = 2 ;";
			//dai.dataOper(ID, SQLinfo2);
			String SQLinfo3 = "delete from dili  where id = 9 ;";
			dai.dataOper(ID, SQLinfo3);
			dai.transactionCommit(ID);
			dai.dataBaseDisconn(ID);



			logger.info("current time after break dataBaseConn = " + TimeRecord.CurrentCompleteTime());

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger()
	{
		return logger;
	}
}
