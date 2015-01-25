package com.subscription;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.events.StartDocument;

import org.apache.thrift.TException;

import com.metadata.entity.MetaDataDef;
import com.metadata.entity.MetaDataStore;
import com.metadata.entity.MetaDataSub;
import com.server.PushMessageClient;

public class Subscriber
{
	// private static final Subscriber subscriber = null;
	private static Subscriber	subscriber	= null;
	private static int			ID			= 0;

	private Subscriber()
	{
		try
		{
			Thread.sleep(5);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		//modify by cong 暂时注释掉，因为数据库没有数据，方便调试
		//syncSubDatabase();
	}

	public static Subscriber getInstance()
	{
		if (subscriber == null)
			subscriber = InnerClass.SINGLETON;
		return subscriber;
	}

	private void syncSubDatabase()
	{
		String sql = "select * from t_metadata_sub";
//		System.out.println(sql);
		ArrayList<MetaDataSub> subList = MetaDataReader.readSubDatabase(sql);
		messageArrayList.clear();
		for (MetaDataSub metaDataSub : subList)
		{
			String defID = metaDataSub.getDefID();
			String IP = metaDataSub.getTokenList();
			int id = Integer.parseInt(metaDataSub.getId());
			if (id > ID)
				ID = id;
			int mode = Integer.parseInt(metaDataSub.getSubMode());
			String sql1 = "select * from t_accusation_def where id='" + metaDataSub.getDefID() + "'";
			System.out.println(sql1);
			ArrayList<MetaDataDef> return1 = MetaDataReader.readDefDatabase(sql1);
			if (!return1.isEmpty())
			{
				String tableName = return1.get(0).getTableName();
				String dataKind = return1.get(0).getDataKind();
				sql1 = "select * from t_accusation_store where dataKind='" + dataKind + "'";
				System.out.println(sql);
				ArrayList<MetaDataStore> return2 = MetaDataReader.readStoreDatabase(sql1);
				if (!return2.isEmpty())
				{
					String dataBase = return2.get(0).getDatabaseName();
					// String link = return2.get(0).getDatabaseLink();
					SubMsg msg = new SubMsg();
					msg.setDefIDString(defID);
					msg.setId(id);
					msg.setIP(IP);
					msg.setMode(mode);
					msg.setTableName(tableName);
					msg.setDataBase(dataBase);
					messageArrayList.add(msg);
				}
			}
		}
		printSubList();
	}

	private void printSubList()
	{
//		System.out.println("***** Sub List Start*****");
		for (SubMsg msg : messageArrayList)
		{
			msg.print();
		}
//		System.out.println("***** Sub List End*****");
	}

	private static class InnerClass
	{
		private static final Subscriber	SINGLETON	= new Subscriber();
	}

	public static class TestRun implements Runnable
	{
		@Override
		public void run()
		{
			System.out.println(Subscriber.getInstance());
		}
	}

	public ArrayList<SubMsg>	messageArrayList	= new ArrayList<SubMsg>();

	public boolean addSubfromClient(String IP, String tableName, int mode)
	{
		String sql1 = "select * from t_accusation_def where tableName='" + tableName + "'";
		System.out.println(sql1);
		ArrayList<MetaDataDef> return1 = MetaDataReader.readDefDatabase(sql1);
		if (!return1.isEmpty())
		{
			String defID = return1.get(0).getId();
			String dataKind = return1.get(0).getDataKind();
			sql1 = "select * from t_accusation_store where dataKind='" + dataKind + "'";
			System.out.println(sql1);
			ArrayList<MetaDataStore> return2 = MetaDataReader.readStoreDatabase(sql1);
			if (!return2.isEmpty())
			{
				sql1 = "INSERT INTO `t_metadata_sub` (`id`, `subMode`, `defID`, `tokenList`, `createTime`) VALUES ('" + ++ID + "','" + mode + "','" + defID + "','" + IP + "','2014-3-26');";
				System.out.println(sql1);
				try
				{
					MetaDataReader.writeSubDataBase(sql1);
					syncSubDatabase();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean removeSubfromClient(String IP, String tableName, int mode)
	{
		String sql1 = "select * from t_accusation_def where tableName='" + tableName + "'";
		System.out.println(sql1);
		ArrayList<MetaDataDef> return1 = MetaDataReader.readDefDatabase(sql1);
		if (!return1.isEmpty())
		{
			String defID = return1.get(0).getId();
			sql1 = "delete from t_metadata_sub where defID='" + defID + "' AND tokenList ='" + IP + "' AND subMode ='" + mode + "';";
			System.out.println(sql1);
			try
			{
				boolean result = MetaDataReader.writeSubDataBase(sql1);
				syncSubDatabase();
				return result;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean removeSub(int id)
	{
		if (id == -1)
			return false;
		String sql1 = "delete from t_metadata_sub where id='" + id + "';";
		System.out.println(sql1);
		try
		{
			return MetaDataReader.writeSubDataBase(sql1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	private ArrayList<SubMsg> findSubMsgs(String tableName, int oper)
	{
		ArrayList<SubMsg> returnList = new ArrayList<SubMsg>();
		for (int i = 0; i < messageArrayList.size(); i++)
		{
			if (messageArrayList.get(i).getTableName().compareTo(tableName) == 0 && (messageArrayList.get(i).getMode() == 0 || messageArrayList.get(i).getMode() == oper))
			{
				returnList.add(messageArrayList.get(i));
			}
		}
		return returnList;
	}

	private void sendSub(ArrayList<SubMsg> sendList)
	{
		PushMessageClient pushMessageClient = new PushMessageClient();
		for (int i = 0; i < sendList.size(); i++)
		{
			List<String> list = new ArrayList<String>();
			list.add(sendList.get(i).getTableName());
			list.add(sendList.get(i).getTitle());
			list.add(sendList.get(i).getContent());
			list.add(String.valueOf(sendList.get(i).getOper()));
			try
			{
				pushMessageClient.setIpAddr(sendList.get(i).getIP());
				// pushMessageClient.setIpAddr("localhost");
				// pushMessageClient.setIpAddr("192.168.1.151");
				pushMessageClient.openClient();
				pushMessageClient.getPushClient().push(list);
			}
			catch (TException e)
			{
				e.printStackTrace();
				setFailCount(sendList.get(i).getId());
				System.out.println("Failcount + 1: ID = " + sendList.get(i).getId());
			}
		}
	}

	private ArrayList<Integer> getDeadSub()
	{
		ArrayList<Integer> deadList = new ArrayList<Integer>();
		for (int i = 0; i < messageArrayList.size(); i++)
		{
			if (messageArrayList.get(i).getFailCount() > 1)
				deadList.add(messageArrayList.get(i).getId());
		}
		return deadList;
	}

	private void clearDeadSub()
	{
		ArrayList<Integer> deadList = getDeadSub();
		for (int i = 0; i < deadList.size(); i++)
		{
			removeSub(deadList.get(i));
			syncSubDatabase();
		}
	}

	private void setFailCount(int id)
	{
		for (int i = 0; i < messageArrayList.size(); i++)
		{
			if (messageArrayList.get(i).getId() == id)
			{
				int failCount = messageArrayList.get(i).getFailCount() + 1;
				messageArrayList.get(i).setFailCount(failCount);
				break;
			}
		}
		clearDeadSub();
	}

	private boolean	sendLock	= false;

	public void receiveDBChange(String tableName, String title, String content, int oper)
	{
		ArrayList<SubMsg> sendList = findSubMsgs(tableName, oper);
		for (int i = 0; i < sendList.size(); i++)
		{
			sendList.get(i).setTitle(title);
			sendList.get(i).setContent(content);
			sendList.get(i).setOper(oper);
		}

		sendSub(sendList);
	}

	public static void main(String[] args)
	{
		Subscriber subscriber = Subscriber.getInstance();
		// subscriber.Test1();
		subscriber.Test4();
	}

	public void Test1()
	{

		System.out.println("send result:");
		subscriber.addSubfromClient("192.168.213.53", "Weather", 2);
		subscriber.receiveDBChange("Weather", "Test Title 1", "Test Content 1", 1);
		subscriber.receiveDBChange("Weather", "Test Title 2", "Test Content 2", 2);
		subscriber.receiveDBChange("Weather", "Test Title 3", "Test Content 3", 3);
	}

	public void Test2()
	{
		String dataTable = "rivers";
		String sql1 = "select * from t_metadata_def where tableName='" + dataTable + "'";
		ArrayList<MetaDataDef> return1 = MetaDataReader.readDefDatabase(sql1);
		if (!return1.isEmpty())
		{
			String defID = return1.get(0).getId();
			String dataKind = return1.get(0).getDataKind();
			sql1 = "select * from t_metadata_store where dataKind='" + dataKind + "'";
			ArrayList<MetaDataStore> return2 = MetaDataReader.readStoreDatabase(sql1);
			if (!return2.isEmpty())
			{
				String dataBase = return2.get(0).getDatabaseName();
				String link = return2.get(0).getDatabaseLink();
				System.out.println("defID = " + defID);
				System.out.println("link = " + link);
				System.out.println("database = " + dataBase);
			}
		}
	}

	public void Test3()
	{
		addSubfromClient("192.168.1.199", "rivers", 0);
		addSubfromClient("192.168.1.199", "mounts", 0);
		// removeSubfromClient("localhost", "mounts", 0);
		setFailCount(1);
		setFailCount(1);
		setFailCount(1);
		setFailCount(1);

	}

	public void Test4()
	{
		System.out.println("send result:");
		subscriber.receiveDBChange("rivers", "Test Title 3", "Test Content 3", 3);
	}
}
