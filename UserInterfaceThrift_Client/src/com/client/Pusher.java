package com.client;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Pusher
{
	private static Pusher			pusher;
	private SubscriptionCallBack	callBack;
	private boolean					lock	= true;
	private List<String>			result;

	private Pusher()
	{
		try
		{
			Thread.sleep(5);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public static Pusher getInstance()
	{
		if (pusher == null)
			pusher = InnerClass.SINGLETON;
		return pusher;
	}

	private static class InnerClass
	{
		private static final Pusher	SINGLETON	= new Pusher();
	}

	public static class TestRun implements Runnable
	{
		@Override
		public void run()
		{
			System.out.println(Pusher.getInstance());
		}
	}

	/**
	 * @return the callBack
	 */
	public SubscriptionCallBack getCallBack()
	{
		return callBack;
	}

	/**
	 * @param callBack the callBack to set
	 */
	public void setCallBack(SubscriptionCallBack callBack)
	{
		this.callBack = callBack;
	}

	public void getSubscription(SubscriptionCallBack callBack)
	{
		this.callBack = callBack;
		System.out.println("wait");
		while (lock)
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		;
		// System.out.println("wait finish");
		callBack.receiveSubsription(result);
		// System.out.println("call back over");
		lock = true;
	}

	public void getSubscription()
	{
		if (callBack != null)
		{
			callBack.receiveSubsription(result);
			System.out.println("call back over");
		}
		else
			System.out.println("no client anymore");
	}

	public static void main(String[] args)
	{
		ExecutorService executorService = Executors.newCachedThreadPool();
		for (int c = 0; c < 20; c++)
		{
			executorService.execute(new TestRun());
		}

		executorService.shutdown();
		try
		{
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @return the lock
	 */
	public boolean isLock()
	{
		return lock;
	}

	/**
	 * @param lock the lock to set
	 */
	public void setLock(boolean lock)
	{
		this.lock = lock;
	}

	/**
	 * @return the messages
	 */
	public List<String> getResult()
	{
		return result;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setResult(List<String> messages)
	{
		this.result = messages;
	}
}