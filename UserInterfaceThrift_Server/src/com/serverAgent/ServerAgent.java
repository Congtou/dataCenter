package com.serverAgent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbconn.DBConnection;
import com.metadata.entity.MetaDataDef;
import com.metadata.entity.MetaDataStore;
import com.server.DIFServiceImpl;
import com.session.Session;
import com.session.ThreadObjectSession;
import com.subscription.Subscriber;
import com.util.SqlSimpleParser;
import com.util.TimeRecord;
import com.util.getIP;
import com.workqueue.WorkQueueMap;

public class ServerAgent
{
	private Connection			conn					= null;
	private Statement			stat					= null;
	private ResultSet			rs						= null;
	private Map<String, String>	result;
	private final static int	DBTYPE_MYSQL			= 0;
	private final static String	TransactionID			= "transactionID";
	private final static Logger	logger					= LoggerFactory.getLogger(ServerAgent.class);
	private final String		TransactionByteBufferID	= "transactionByteBufferID";

	// Yingnan
	// Subs
	private Subscriber			subscriber				= Subscriber.getInstance();
	public static String localIP = getIP.LocalIP;
	public static String remoteIP = getIP.RemoteIP;
	public ServerAgent()
	{
		// TODO Auto-generated constructor stub
//		logger.info("Begin:ServerAgent method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		result = new HashMap<String, String>();
		conn = DBConnection.getConnection(DBTYPE_MYSQL);
		try
		{
			stat = conn.createStatement();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("End:ServerAgent method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
	}

	public Map<String, String> dealWithDbconn(Map<String, String> mappara)
	{
		
		
		
		// TODO Auto-generated method stub
		logger.info("Begin:dealWithDbconn method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		String sql = "select id from t_user_system where userSysName ='" + mappara.get("name") + "'";
		String id = null;
		try
		{
			rs = stat.executeQuery(sql);
			while (rs.next())
			{
				id = rs.getString("id");
				result.put("id", id);
				System.out.println("id=" + id);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Session.Set(id, getConn(mappara.get("dataType")));
		Session.Set(id, getConnBydataBaseId(mappara.get("dataBaseId")));
		logger.info("End:dealWithDbconn method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return result;
	}

	//add by cong
	private Object getConnBydataBaseId(String dataBaseId)
	{
		Connection con = null;
		String sql = "select * from t_accusation_store where id =" + dataBaseId + "";
		ArrayList<MetaDataStore> storeList = getDataStoreList(sql);
		for (MetaDataStore entity : storeList)
		{
			// 0:oracle
			if (entity.getStoreKind().equals("1"))
			{

			}
			// 1:mysql
			else if (entity.getStoreKind().equals("2"))
			{
				try
				{
					Class.forName("com.mysql.jdbc.Driver");
				}
				catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try
				{
					System.out.println("in getConn entity.getDatabaseLink = " + entity.getDatabaseLink());
					con = DriverManager.getConnection(entity.getDatabaseLink());
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{

			}
		}
		logger.info("End:getConn method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return con;
	}
	
	private Object getConn(String dataType)
	{
		// TODO Auto-generated method stub
//		logger.info("Begin:getConn method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Connection con = null;
		String sql = "select * from t_accusation_store where dataKind='" + dataType + "'";
		ArrayList<MetaDataStore> storeList = getDataStoreList(sql);
		for (MetaDataStore entity : storeList)
		{
			// 0:oracle
			if (entity.getStoreKind().equals("1"))
			{

			}
			// 1:mysql
			else if (entity.getStoreKind().equals("2"))
			{
				try
				{
					Class.forName("com.mysql.jdbc.Driver");
				}
				catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try
				{
					System.out.println("in getConn entity.getDatabaseLink = " + entity.getDatabaseLink());
					con = DriverManager.getConnection(entity.getDatabaseLink());
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{

			}
		}
		logger.info("End:getConn method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return con;
	}

	private ArrayList<MetaDataStore> getDataStoreList(String sql)
	{
		// TODO Auto-generated method stub
//		logger.info("Begin:getDataStoreList method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		ArrayList<MetaDataStore> metaDataStoreList = new ArrayList<MetaDataStore>();
		try
		{
			rs = stat.executeQuery(sql);
			while (rs.next())
			{
				MetaDataStore metaData = new MetaDataStore();
				metaData.setPhyAddr(rs.getString("phyAddr"));
				metaData.setIpAddr(rs.getString("ipAddr"));
				metaData.setDataKind(rs.getString("dataKind"));
				metaData.setStoreKind(rs.getString("storeKind"));
				metaData.setDatabaseName(rs.getString("databaseName"));
				metaData.setDatabaseLink(rs.getString("databaseLink"));
				metaData.setCaseName(rs.getString("caseName"));
				metaData.setCreateTime(rs.getString("createTime"));
				metaDataStoreList.add(metaData);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("End:getDataStoreList method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return metaDataStoreList;
	}

	public Map<String, String> dealWithDataOper(Map<String, String> mappara)
	{
		// TODO Auto-generated method stub
		//add by cong debug
		Set<String> sets = mappara.keySet();
		for(String string : sets){
			System.out.println("key = "+string);
			System.out.println("value = "+mappara.get(string));
		}
		System.out.println("mappara dealWithDataOper end");	
		
//		logger.info("Begin:dealWithDataOper method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		String ID = mappara.get("id");
		String SQL = mappara.get("SQLinfo");
		String TransactionID = mappara.get("transactionID");
		Map<String, String> dataOperMap = new HashMap<String, String>();
		if (TransactionID == null)
		{
			Connection conn = (Connection) Session.get(ID);
			try
			{
				System.out.println("IP ");
				PreparedStatement st = conn.prepareStatement(SQL);
				st.executeUpdate();
				dataOperMap.put("res", "0");
				SqlSimpleParser simpleParser = new SqlSimpleParser();
				String[] sqlStrings = simpleParser.parseSql(SQL);
				String tableName = sqlStrings[0];
				String title = sqlStrings[1];
				String content = sqlStrings[2];
				int oper = Integer.parseInt(sqlStrings[3]);
//				System.out.println("IP " + subscriber.messageArrayList.get(0).getIP());
				subscriber.receiveDBChange(tableName, title, content, oper);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				dataOperMap.put("res", "1");
			}
		}
		logger.info("End:dealWithDataOper method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return dataOperMap;
	}

	public Map<String, String> dealWithtransactionBegin(Map<String, String> mappara)
	{
//		logger.info("Begin:dealWithtransactionBegin method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> result = new HashMap<String, String>();
		Map<String, String> task;
		int tasktimes = 0;
		while (true)
		{
			try
			{
				tasktimes++;
				System.out.println("tasktimes = " + tasktimes);
				task = getTask(mappara.get(TransactionID));
				logger.info("get one task");
				System.out.println(task.get(TransactionID));
				System.out.println(task.get("commit"));
				if (task.get("commit") != null)
				{
					logger.info("transaction commit-------------");
					dealWithTaskCommit(task);
					result.put("res", "success");
					return result;
				}
				else if (task.get("rollback") != null)
				{
					dealWithTaskRollBack(task);
					result.put("res", "success");
					return result;
				}
				else if (task.get("lobInsert") != null)
				{
					String ByteBufferID = task.get(TransactionByteBufferID);
					System.out.println("ByteBufferiD = " + ByteBufferID);
					ByteBuffer buffer = getTaskByteBuffer(ByteBufferID);
					dealWithTaskLobInsert(task, buffer);
				}
				else
				{
					logger.info("dealWithTask---------------");
					dealWithTask(task);
					logger.info("End:dealWithtransactionBegin method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}

	}

	private boolean					taskLock	= false;

	ArrayList<Map<String, String>>	taskList	= new ArrayList<Map<String, String>>();

	private void dealWithTaskCommit(Map<String, String> task)
	{
		taskLock = true;
//		logger.info("Begin:dealWithTaskCommit method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Connection conn = (Connection) Session.get(task.get("id"));
		try
		{
			conn.commit();
			for (Map<String, String> t : taskList)
			{
				SqlSimpleParser simpleParser = new SqlSimpleParser();
				String[] sqlStrings = simpleParser.parseSql(t.get("SQLinfo"));
				String tableName = sqlStrings[0];
				String title = sqlStrings[1];
				String content = sqlStrings[2];
				int oper = Integer.parseInt(sqlStrings[3]);
				subscriber.receiveDBChange(tableName, title, content, oper);
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			taskList.clear();
			logger.info("End:dealWithTaskCommit method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void dealWithTaskRollBack(Map<String, String> task)
	{
//		logger.info("Begin:dealWithTaskRollBack method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Connection conn = (Connection) Session.get(task.get("id"));
		try
		{
			conn.rollback();
			logger.info("End:dealWithTaskRollBack method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void dealWithTask(Map<String, String> task)
	{
//		logger.info("Begin:dealWithTask method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		logger.info("sql is " + task.get("SQLinfo"));
		Connection conn = (Connection) Session.get(task.get("id"));
		// Connection conn = (Connection) Session.get(task.get(TransactionID));

		try
		{
			Statement stat = conn.createStatement();
			int flag = stat.executeUpdate(task.get("SQLinfo"));
			stat.close();
			taskList.add(task);
			logger.info("End:dealWithTask method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");

			// SqlSimpleParser simpleParser = new SqlSimpleParser();
			// String[] sqlStrings = simpleParser.parseSql(task.get("SQLinfo"));
			// String tableName = sqlStrings[0];
			// String title = sqlStrings[1];
			// String content = sqlStrings[2];
			// int oper = Integer.parseInt(sqlStrings[3]);
			// System.out.println("IP " + tableName);
			// subscriber.receiveDBChange(tableName, title, content, oper);
			// System.out.println("Sent");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	private void dealWithTaskLobInsert(Map<String, String> task, ByteBuffer buffer)
	{
//		logger.info("Begin:dealWithTaskLobInsert method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Connection conn = (Connection) Session.get(task.get("ID"));
		System.out.println("in dealWithTaskLobInsert before inputStream");
		InputStream inputStream = new ByteArrayInputStream(buffer.array());
		System.out.println("in dealWithTaskLobInsert after inputStream");
		PreparedStatement st;
		System.out.println("in dealWithTaskLobInsert before try blocks");
		try
		{
			st = conn.prepareStatement(task.get("SQLinfo"));
			st.setBinaryStream(1, inputStream);
			System.out.println("in dealWithTaskLobInsert before executeUpdate blocks");
			st.executeUpdate();
			System.out.println("in dealWithTaskLobInsert after executeUpdate blocks");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		logger.info("End:dealWithTaskLobInsert method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
	}

	private synchronized Map<String, String> getTask(String id) throws InterruptedException
	{
//		logger.info("Begin:getTask method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		LinkedList<Map<String, String>> queue = WorkQueueMap.getWorkQueue().get(id);
		if (queue.size() == 0)
		{
			synchronized (ThreadObjectSession.get(id))
			{
				logger.trace("getTask-------task TransactionID is " + id);
				ThreadObjectSession.get(id).wait();
			}
		}
		logger.info("End:getTask method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return queue.removeFirst();
	}

	private synchronized ByteBuffer getTaskByteBuffer(String id)
	{
		return WorkQueueMap.getWorkBufferQueue().get(id);
	}

	public Map<String, String> dealWithtransactionCommit(Map<String, String> mappara)
	{
		Session.get(mappara.get("id"));
		return null;
	}

	public Map<String, String> dealWithtransactionRollBack(Map<String, String> mappara)
	{
		return null;
	}

	public Map<String, String> dealWithtransactionEnd(Map<String, String> mappara)
	{
		return null;
	}

	public Map<String, String> dealWithDataSearchByTxt(Map<String, String> mappara)
	{
		String ID = mappara.get("id");
		String SQL = mappara.get("SQLinfo");
		String spaceMark = mappara.get("spaceMark");
		String TransactionID = mappara.get("transactionID");
		Map<String, String> DataSearchByTxtMap = new HashMap<String, String>();
		ResultSet rs = null;
		if (TransactionID == null)
		{
			Connection conn = (Connection) Session.get(ID);
			PreparedStatement st;
			try
			{
				st = conn.prepareStatement(SQL);
				rs = st.executeQuery();
				DataSearchByTxtMap.put("res", ConvertToTxt(rs, spaceMark));
				DataSearchByTxtMap.put("success", "0");
				rs.close();
				st.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				DataSearchByTxtMap.put("res", "1");
				DataSearchByTxtMap.put("success", "0");
			}

		}
		return DataSearchByTxtMap;
	}

	public Map<String, String> dealWithDataSearchByMemory(Map<String, String> mappara)
	{
//		logger.info("Begin:dealWithDataSearchByMemory method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		String ID = mappara.get("id");
		String SQL = mappara.get("SQLinfo");
		String TransactionID = mappara.get("transactionID");
		Map<String, String> DataSearchByMemoryMap = new HashMap<String, String>();
		ResultSet rs = null;
		if (TransactionID == null)
		{
			Connection conn = (Connection) Session.get(ID);
			try
			{
				PreparedStatement st = conn.prepareStatement(SQL);
				rs = st.executeQuery();
				DataSearchByMemoryMap.put("res", ConvertToJson(rs));
				DataSearchByMemoryMap.put("success", "0");
				rs.close();
				st.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				DataSearchByMemoryMap.put("res", "1");
				DataSearchByMemoryMap.put("success", "1");
			}
		}
		logger.info("End:dealWithDataSearchByMemory method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return DataSearchByMemoryMap;
	}

	public ByteBuffer dealWithLobSearch(Map<String, String> mappara)
	{
//		logger.info("Begin:dealWithLobSearch method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		String ID = mappara.get("id");
		String SQL = mappara.get("SQLinfo");
		String TransactionID = mappara.get("transactionID");
		Connection conn = (Connection) Session.get(ID);
		ResultSet rs = null;
		InputStream is = null;
		ByteBuffer buff = null;
		Blob b;
		try
		{
			PreparedStatement st = conn.prepareStatement(SQL);
			rs = st.executeQuery();
			rs.next();
			b = rs.getBlob(1);
			is = b.getBinaryStream();
			buff = InputStreamToByteBuffer(is);
			is.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			buff = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			buff = null;
		}
		logger.info("End:dealWithLobSearch method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return buff;
	}

	public Map<String, String> dealWithLobInsert(Map<String, String> mappara, ByteBuffer buffer)
	{
//		logger.info("Begin:dealWithLobInsert method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		String ID = mappara.get("id");
		String SQL = mappara.get("SQLinfo");
		String TransactionID = mappara.get("transactionID");
		Map<String, String> LobInsertMap = new HashMap<String, String>();
		System.out.println("buffer.capacity() = " + buffer.capacity());
		System.out.println("in dealWithLobInsert before inputStream blocks");
		InputStream inputStream = new ByteArrayInputStream(buffer.array());
		System.out.println("in dealWithLobInsert after inputStream blocks");
		if (TransactionID == null)
		{
			Connection conn = (Connection) Session.get(ID);
			try
			{
				conn.setAutoCommit(false);
				PreparedStatement st = conn.prepareStatement(SQL);
				st.setBinaryStream(1, inputStream);
				st.executeUpdate();
				conn.commit();
				LobInsertMap.put("res", "0");
				st.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				LobInsertMap.put("res", "1");
			}
		}
		logger.info("End:dealWithLobInsert method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return LobInsertMap;
	}

	public Map<String, String> dealWithDbClose(Map<String, String> mappara)
	{
		String ID = mappara.get("id");
		Map<String, String> DbCloseMap = new HashMap<String, String>();
		Connection conn = (Connection) Session.get(ID);
		try
		{
			conn.close();
			DbCloseMap.put("success", "0");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			DbCloseMap.put("success", "1");
		}
		Session.delete(ID);
		return DbCloseMap;
	}

	public Map<String, String> dealWithErrInfo(Map<String, String> mappara)
	{
		return null;
	}

	private String ConvertToJson(ResultSet rs)
	{
		int rowCount = 0;
		int columnCount = 0;
		String[][] QuerySet = null;
		String[] columnName = null;
		try
		{
			rs.last();
			rowCount = rs.getRow();
			System.out.println("1 rs.getRow() = " + rs.getRow());
			rs.first();
			System.out.println("2 rs.getRow() = " + rs.getRow());
			ResultSetMetaData rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();
			columnName = new String[columnCount];
			QuerySet = new String[rowCount][columnCount];
			System.out.println("rowCount = " + rowCount + " columnCount = " + columnCount);
			for (int i = 1; i <= columnCount; i++)
			{
				System.out.println("ColumnName = " + rsmd.getColumnName(i));
				columnName[i - 1] = rsmd.getColumnName(i);
			}
			int i = 0;
			do
			{
				for (int j = 1; j <= columnCount; j++)
				{
					System.out.println("i = " + i + " j = " + j);
					QuerySet[i][j - 1] = rs.getString(j);
				}
				i++;
			} while (rs.next());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		System.out.println("2 rowCount = " + rowCount + " columnCount = " + columnCount);
		for (int k = 0; k < rowCount; k++)
		{
			for (int i = 0; i < columnCount; i++)
			{
				JSONObject temp = new JSONObject();
				temp.accumulate(columnName[i], QuerySet[k][i]);
				jsonArray.add(temp);
			}
		}
		jsonObj.accumulate("SelectResponse", jsonArray);
		return jsonObj.toString();
	}

	private String ConvertToTxt(ResultSet rs, String spaceMark)
	{
		int rowCount = 0;
		int columnCount = 0;
		String[][] QuerySet = null;
		String[] columnName = null;
		try
		{
			rs.last();
			rowCount = rs.getRow();
			rs.first();

			ResultSetMetaData rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();
			columnName = new String[columnCount];
			QuerySet = new String[rowCount][columnCount];
			for (int i = 1; i <= columnCount; i++)
			{
				columnName[i - 1] = rsmd.getColumnName(i);
			}
			int i = 0;
			do
			{
				for (int j = 1; j <= columnCount; j++)
				{
					System.out.println("i = " + i + " j = " + j);
					QuerySet[i][j - 1] = rs.getString(j);
				}
				i++;
			} while (rs.next());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int k = 0; k < columnCount; k++)
			sb.append(columnName[k] + spaceMark);
		sb.append("\r\n");
		for (int i = 0; i < rowCount; i++)
		{
			for (int j = 0; j < columnCount; j++)
				sb.append(QuerySet[i][j] + spaceMark);
			sb.append("\r\n");
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	private ByteBuffer InputStreamToByteBuffer(InputStream is) throws IOException
	{
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int ch;
		while ((ch = is.read()) != -1)
		{
			bytestream.write(ch);
		}
		byte bytes[] = bytestream.toByteArray();
		bytestream.close();
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		return byteBuffer;
	}

}
