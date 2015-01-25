package com.server;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serverAgent.LocalDBOperator;
import com.session.Session;
import com.session.ThreadObjectSession;
import com.subscription.Subscriber;
import com.threadpool.ThreadPool;
import com.threadpool.WorkThread;
import com.threadpool.WorkThreadLob;
import com.threadpool.WorkThreadLobInsert;
import com.util.IdGenerator;
import com.util.TimeRecord;
import com.util.getIP;
import com.workqueue.WorkQueueMap;

import service.thrift.DataInterfaceForward.Iface;

public class DIFServiceImpl implements Iface {
	private final static Logger logger = LoggerFactory
			.getLogger(DIFServiceImpl.class);
	List<String> tranList = new ArrayList<String>();
	List<Long> tranIDList = new ArrayList<Long>();
	Stack<Long> tranIDStack = new Stack<Long>();
	IdGenerator idGenerator = new IdGenerator(0);
	Map<String, String> resMap = new HashMap<String, String>();
	Future<Map<String, String>> result;
	private final int DBCONN = 0;
	private final int DATA_OPER = 1;
	private final int transaction_BEGIN = 2;
	private final int DATE_SEARCH_BY_TEXT = 6;
	private final int DATA_SEARCH_BY_MEMORY = 7;
	private final int LOB_SEARCH = 8;
	private final int LOB_INSERT = 9;
	private final int DB_CLOSE = 10;
	private final int ERR_INFO = 11;
	// add by cong
	private final String TransactionID = "transactionID";
	private final String TransactionOrder = "transactionOrder";
	private final String TransactionByteBufferID = "transactionByteBufferID";
	LocalDBOperator localDBOperator = new LocalDBOperator();
	NodeServer nodeServer = new NodeServer(); 
	// private Object result;
	private String dataType = DataTypeConfig.dataType;

	// subscrition
	private Subscriber subscriber = Subscriber.getInstance();
	public static String localIP = getIP.LocalIP;
	public static String remoteIP = getIP.RemoteIP;
	@Override
	public Map<String, String> dataBaseConn(Map<String, String> mappara, String ipaddr)
			throws TException {
//		logger.info("Begin dataBaseConn method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		String type = mappara.get("dataType");
		Map<String,String> resMap = new HashMap<String, String>();
		if(dataType == null){
			dataType = DataTypeConfig.dataType;
		}
		if(!type.equals(dataType)){			
			String forwardIpAddr = localDBOperator.getIpAddr(type);
			
			resMap.put("redirectIpAddress", forwardIpAddr);
		}else{
			resMap = nodeServer.dataBaseConn(mappara);
		}
		logger.info("End:dataBaseConn method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");		
		return resMap;
	}

	public void dataOpertransaction(Map<String, String> mappara) {
		addTask(mappara);
	}

	private synchronized void addTask(Map<String, String> mappara) {
		// TODO Auto-generated method stub
//		logger.info("Begin:addTask method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		System.out
				.println("come into function addTask mappara.get(TransactionID) = "
						+ mappara.get(TransactionID));

		// add cong
		/*
		 * 目前存在一个bug，当transactionbegin开启一共线程后，执行call方法时，
		 * 还没执行到WorkQueueMap.getWorkQueue().put(mappara.get(TransactionID),
		 * list); 这时候WorkQueueMap.getWorkQueue()中存储的内容为空，而这个时候再调用dataoper时，
		 * 主线程就会进入addtask这个方法 在addtask中执行以下语句 LinkedList<Map<String,String>>
		 * list = WorkQueueMap.getWorkQueue().get(mappara.get(TransactionID));
		 * 此时获得的list为空，再执行list.add(mappara)就会报空指针错误;
		 */
		int i = 0;
		while (WorkQueueMap.getWorkQueue().size() == 0) {
			System.out.print(i + " ");
			if ((i + 1) % 20 == 0)
				System.out.println();
		}

		LinkedList<Map<String, String>> list = WorkQueueMap.getWorkQueue().get(
				mappara.get(TransactionID));
		if (list == null)
			System.out.println("come into function addTask list is null");
		list.add(mappara);

		WorkQueueMap.getWorkQueue().put(mappara.get(TransactionID), list);
		synchronized (ThreadObjectSession.get(mappara.get(TransactionID))) {
			ThreadObjectSession.get(mappara.get(TransactionID)).notify();
		}
		logger.info("End:addTask method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
	}

	private synchronized void addByteBuffer(String byteBufferID,
			ByteBuffer buffer) {
//		logger.info("come into function addByteBuffer of class DIFServiceImpl");
		System.out.println("buffer.capacity() = " + buffer.capacity());
		WorkQueueMap.getWorkBufferQueue().put(byteBufferID, buffer);
//		logger.info("leave off function addByteBuffer of class DIFServiceImpl");
	}

	@Override
	public Map<String, String> dataOper(Map<String, String> mappara)
			throws TException {
//		logger.info("come into function dataOper of class DIFServiceImpl");
		// TODO Auto-generated method stub
		// System.out.println("in method dataOper");
		Map<String, String> resMap = new HashMap<String, String>();
		// System.out.println("mappara ID = "+mappara.get("ID"));
		// System.out.println("mappara SQLinfo = "+mappara.get("SQLinfo"));
		// resMap.put("res", "dataBaseOperID123456");
		// printTranIDandOrder(mappara);
		if (mappara.get(TransactionID) != null
				&& ThreadObjectSession.get(mappara.get(TransactionID)) != null) {
			logger.info("task id is " + mappara.get(TransactionID));

			addTask(mappara);
			resMap.put("res", "事务添加到队列");
			logger.info("add one task");
		} else {
			result = ThreadPool.getThreadPool().submit(
					new WorkThread(DATA_OPER, mappara));
			try {
				resMap = result.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		logger.info("leave off function dataOper of class DIFServiceImpl");

		return resMap;
	}

	@Override
	public Map<String, String> transactionBegin(Map<String, String> mappara)
			throws TException {
		
		Set<String> sets = mappara.keySet();
		for(String string : sets){
			System.out.println("key = "+string);
			System.out.println("value = "+mappara.get(string));
		}
		System.out.println("mappara transactionBegin end");	
		// TODO Auto-generated method stub
//		logger.info("come into function transactionBegin of class DIFServiceImpl");
		long transactionIdNum = idGenerator.nextId();
		String transactionIdStr = Long.toString(transactionIdNum);
		mappara.put(TransactionID, transactionIdStr);
		Connection conn = (Connection) Session.get(mappara.get("id"));
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = ThreadPool.getThreadPool().submit(
				new WorkThread(transaction_BEGIN, mappara));
		resMap.put(TransactionID, transactionIdStr);
		resMap.put("res", "success");

//		logger.info("leave off function transactionBegin of class DIFServiceImpl");
		return resMap;
	}

	@Override
	public Map<String, String> transactionCommit(Map<String, String> mappara)
			throws TException {
//		logger.info("come into function transactionCommit of class DIFServiceImpl");
		// TODO Auto-generated method stub
		// System.out.println("in method transactionCommit");
		// System.out.println("mappara ID = "+mappara.get("ID"));
		// printTranIDandOrder(mappara);
		// Map<String,String> resMap = new HashMap<String,String>();
		// resMap.put("res", "transactionCommit123456");
		// System.out.println("pop stack ");
		// tranIDStack.pop();
		// printTranIDandOrder(mappara);
		mappara.put("commit", "commit");
		addTask(mappara);
		return resMap;
	}

	@Override
	public Map<String, String> transactionRollBack(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
		mappara.put("rollback", "rollback");
		addTask(mappara);
		try {
			resMap = this.result.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resMap;
	}

	@Override
	public Map<String, String> transactionEnd(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> dataSearchByTxt(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
		result = ThreadPool.getThreadPool().submit(
				new WorkThread(DATE_SEARCH_BY_TEXT, mappara));
		try {
			resMap = this.result.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resMap;
	}

	@Override
	public Map<String, String> dataSearchByMemory(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
		result = ThreadPool.getThreadPool().submit(
				new WorkThread(DATA_SEARCH_BY_MEMORY, mappara));
		try {
			resMap = this.result.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resMap;
	}

	@Override
	public ByteBuffer lobSearch(Map<String, String> mappara) throws TException {
		// TODO Auto-generated method stub
		ByteBuffer buffer = null;
		Future<ByteBuffer> result = ThreadPool.getThreadPool().submit(
				new WorkThreadLob(LOB_SEARCH, mappara));
		try {
			buffer = result.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer;
	}

	@Override
	public Map<String, String> lobInsert(Map<String, String> mappara,
			ByteBuffer bytes) throws TException {
		// TODO Auto-generated method stub
		if (mappara.get(TransactionID) != null
				&& ThreadObjectSession.get(mappara.get(TransactionID)) != null) {
			logger.info("task id is " + mappara.get(TransactionID));
			mappara.put("lobInsert", "lobInsert");
			String ByteBufferID = mappara.get(TransactionID)
					+ mappara.get(TransactionOrder) + "buffer";
			mappara.put(TransactionByteBufferID, ByteBufferID);
			addByteBuffer(ByteBufferID, bytes);
			addTask(mappara);
			resMap.put("res", "0");
			logger.info("add one task");
		} else {
			result = ThreadPool.getThreadPool().submit(
					new WorkThreadLobInsert(LOB_INSERT, mappara, bytes));
			try {
				resMap = this.result.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resMap;
	}

	@Override
	public Map<String, String> dataBaseDisconn(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
		result = ThreadPool.getThreadPool().submit(
				new WorkThread(DB_CLOSE, mappara));
		try {
			resMap = this.result.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resMap;
	}

	@Override
	public Map<String, String> errInfo(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
		result = ThreadPool.getThreadPool().submit(
				new WorkThread(ERR_INFO, mappara));
		try {
			resMap = this.result.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resMap;
	}

	private void printTranIDandOrder(Map<String, String> mappara) {
		if (mappara.containsKey("transactionID")) {
			System.out.println("transactionID = "
					+ mappara.get("transactionID"));
		}
		if (mappara.containsKey("transactionOrder")) {
			System.out.println("transactionOrder = "
					+ mappara.get("transactionOrder"));
		}
		if (!tranIDStack.isEmpty()) {
			System.out.println("current TransactionID = " + tranIDStack.peek());
		}
	}

	@Override
	public Map<String, String> subscriptionRequest(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
		String[] strings = String.valueOf(
				mappara.get("Content")).split(",");
		String IP = strings[0];
		String tableName = strings[1];
		int mode = Integer.parseInt(strings[2]);
		if (mappara.get("Mode").equals( "ADD")) {
			subscriber.addSubfromClient(IP, tableName, mode);
		} else if (mappara.get("Mode").equals( "RMV")) {
			subscriber.removeSubfromClient(IP, tableName, mode);
		}
		// TODO Auto-generated method stub
		Map<String,String> res = new HashMap<String,String>();
		res.put("res", "0");
		return res;
	}
}
