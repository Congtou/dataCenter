package com.access;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.client.DAIClient;
import com.util.IPTest;
import com.util.getip;

public class DataAccessInterface implements DataAccess {

	private DAIClient daiClient = new DAIClient();

	private final String TransactionID = "transactionID";
	private final String TransactionOrder = "transactionOrder";

	// 定义事务,事务用一个唯一标示来区分
	private Stack<String> tranStack = new Stack<String>();
	// 定义事务中的操作顺序
	private Map<String, Integer> tranOrderMap = new HashMap<String, Integer>();
	// 定义事务与事务之间的包含关系，Key存储的是子节点，Value存储的是父节点
	private Map<String, String> tranRelationMap = new HashMap<String, String>();

	private static final org.slf4j.Logger logger = LoggerFactory
			.getLogger(DataAccessInterface.class);
	// LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();

	// 这里将redirectIP改为remoteIP
	private String remoteIP = DAIClient.getHost(); 
//	private String remoteIP = "192.168.213.235";
	Map<String, DAIClient> clientList = new HashMap<String, DAIClient>();
	Map<String, String> clientString = new HashMap<String, String>();

	public String localIP = getip.getRealIp();

	public DataAccessInterface() throws IOException {
		clientString = MapSerializer.deserialze(HashMap.class);
		if ((clientString != null) && (!clientString.isEmpty())) {
			Iterator<Entry<String, String>> it = clientString.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, String> obj = it.next();
				clientList.put(obj.getKey(), new DAIClient(obj.getValue()));
			}
		}
		if (clientString == null) {
			clientString = new HashMap<String, String>();
		}
	}

	public DAIClient getDAIClient(String ip) {
		logger.info("begining getDAIClient.");
		Iterator<Entry<String, DAIClient>> it = clientList.entrySet()
				.iterator();
		DAIClient daiClient = null;
		while (it.hasNext()) {
			Entry<String, DAIClient> obj = it.next();
			if (obj.getKey().equals(ip)) {
				logger.info("Has found DAIClient with " + ip + " IP Address.");
				daiClient = (DAIClient) obj.getValue();
			}
		}
		logger.info("finish getDaiClient.");
		return daiClient;
	}

	public void start() throws TTransportException {
		daiClient.open();
	}

	public void end() throws Exception {
		daiClient.close();
		if (!tranStack.isEmpty()) {
			throw new Exception("transaction end is not call");
		}
	}

	@Override
	public String dataBaseConn(String username, String dataType ,String dataBaseId) {
		logger.info("entering dataBaseConn");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;// = new HashMap<String,String>();
		String str_res = null;
		map.put("name", username);
		map.put("dataType", dataType);
		map.put("dataBaseId", dataBaseId);

		if (!tranStack.isEmpty()) {
			map.put(TransactionID, getTransactionIDs(tranStack.peek()));
			map.put(TransactionOrder,
					String.valueOf(getTransactionOrderbyID(tranStack.peek())));
		}

		try {
			String r_dataType = map.get("dataType");

			if (clientList.containsKey(r_dataType)) {
				logger.info("Has found existing DAIClient with dataType "
						+ r_dataType);
				daiClient = clientList.get(r_dataType);
				logger.info("Has found existing DAIClient remote ip = "
						+ daiClient.getHost());
			} else {
				logger.info("Not found existing DAIClient with dataType "
						+ r_dataType);
				logger.info("Connect to up node Server.");
				// daiClient = new DAIClient();
			}
			String ip=getip.getRealIp();
			System.out.println("myip:"+ip);
			map.put("ip", ip);
			//add by Yao
			daiClient.startReceiveSub();
			mapResult = daiClient.client.dataBaseConn(map);
			if(mapResult.containsKey("errorCode")){
				return mapResult.get("errorCode");
			}
			if (mapResult.containsKey("redirectIpAddress")) {
				remoteIP = mapResult.get("redirectIpAddress");
				logger.info("get redirect IP Address:" + remoteIP);
				logger.info("Create a new DaiClient proxy to IP Address: "
						+ remoteIP);
				daiClient = new DAIClient(remoteIP);
				clientList.put(r_dataType, daiClient);
				clientString.put(r_dataType, remoteIP);
				try {
					MapSerializer.serialize(clientString);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long rStartTime = System.currentTimeMillis();
				
				mapResult = daiClient.client.dataBaseConn(map);
				if(mapResult.containsKey("errorCode")){
					return mapResult.get("errorCode");
				}
				long rEndTime = System.currentTimeMillis();
			}
			str_res = mapResult.get("id");
			logger.info("finish DatabaseConn method.");

		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (str_res != null) {
			logger.info("InterfaceName=dataBaseConn;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
		} else {
			logger.info("InterfaceName=dataBaseConn;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
		}
		return  str_res;
	}

	@Override
	public String dataOper(String ID, String SQLinfo) {
		// TODO Auto-generated method stub
		logger.info("entering dataOper().");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;// = new HashMap<String,String>();
		String str_res = null;
		map.put("id", ID);
		map.put("SQLinfo", SQLinfo);

		placeTranIDAndOrder(map);

		try {
			mapResult = daiClient.client.dataOper(map);
			str_res = mapResult.get("res");
			System.out.println("in dataOper str_res = " + str_res);
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("finish dataOper().");
		if (str_res != null) {
			logger.info("InterfaceName=dataOper;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
		} else {
			logger.info("InterfaceName=dataOper;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
		}
		return str_res;
	}

	@Override
	public boolean transactionBegin(String ID) {
		// TODO Auto-generated method stub
		logger.info("entering transactionBegin.");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;// = new HashMap<String,String>();
		String str_res = null;
		String transactionID = null;
		map.put("id", ID);

		placeTranIDAndOrder(map);

		try {
			mapResult = daiClient.client.transactionBegin(map);
			str_res = mapResult.get("res");
			transactionID = mapResult.get(TransactionID);
			System.out.println("transactionID = " + transactionID);
			if (transactionID != null) {
				System.out.println("transactionID isn't null");
				if (!tranStack.isEmpty()) {
					tranRelationMap.put(transactionID, tranStack.peek());
				}

				tranStack.push(transactionID);
				tranOrderMap.put(transactionID, 0);
			} else
				System.out.println("transactionID == null");

		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ((str_res != null) && (str_res.equals("true"))) {
			logger.info("InterfaceName=transactionBegin;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
			return true;
		} else {
			logger.info("InterfaceName=transactionBegin;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
			return false;
		}
	}

	@Override
	public boolean transactionCommit(String ID) {
		logger.info("entering transactionCommit");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;// = new HashMap<String,String>();
		String str_res = null;
		map.put("id", ID);
		placeTranIDAndOrder(map);
		try {
			mapResult = daiClient.client.transactionCommit(map);
			str_res = mapResult.get("res");
			System.out.println("in transactionCommit res = " + str_res);
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tranRelationMap.remove(tranStack.peek());
		tranOrderMap.remove(tranStack.peek());
		tranStack.pop();
		if (str_res.equals("true")) {
			logger.info("InterfaceName=transactionCommit;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
			return true;
		} else {
			logger.info("InterfaceName=transactionCommit;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
			return false;
		}
	}

	@Override
	public boolean transactionRollBack(String ID) {
		// TODO Auto-generated method stub
		logger.info("entering transactionRollBack.");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;// = new HashMap<String,String>();
		String str_res = null;
		map.put("id", ID);
		placeTranIDAndOrder(map);
		try {
			mapResult = daiClient.client.transactionRollBack(map);
			str_res = mapResult.get("res");
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tranRelationMap.remove(tranStack.peek());
		tranOrderMap.remove(tranStack.peek());
		tranStack.pop();
		if (str_res.equals("true")) {
			logger.info("InterfaceName=transactionRollBack;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
			return true;
		} else {
			logger.info("InterfaceName=transactionRollBack;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
			return false;
		}
	}

	@Override
	public boolean transactionEnd(String ID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String dataSearchByTxt(String ID, String SQLinfo, String savaPath,
			String spaceMark) {
		// TODO Auto-generated method stub
		logger.info("entering dataSearchByTxt");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;
		String str_res = null;
		String transactionID = null;
		map.put("id", ID);
		map.put("SQLinfo", SQLinfo);
		map.put("spaceMark", spaceMark);

		placeTranIDAndOrder(map);

		BufferedWriter bw = null;
		try {
			// 这个地方是不是接收就应该是一个map，不然spaceMark没用
			// 或者这个地方要知道接收到的信息的格式
			mapResult = daiClient.client.dataSearchByTxt(map);
			str_res = mapResult.get("res");
			System.out.println("str_res = " + str_res);
			try {
				bw = new BufferedWriter(
						new FileWriter(savaPath + "\\result.txt"));
				bw.write(str_res);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bw.toString() != null) {
			logger.info("InterfaceName=dataSearchByTxt;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
			return "0";
		} else {
			logger.info("InterfaceName=dataSearchByTxt;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
			return "1";
		}
	}

	@Override
	public String dataSearchByMemory(String ID, String SQLinfo) {
		// TODO Auto-generated method stub
		logger.info("entering dataSearchByMemory.");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;// = new HashMap<String,String>();
		List<Map> list = null;
		String str_res = null;
		String str_success = null;
		map.put("id", ID);
		map.put("SQLinfo", SQLinfo);

		placeTranIDAndOrder(map);
		try {
			mapResult = daiClient.client.dataSearchByMemory(map);
			// 当发送的借口完成后可以直接将下面注释行去掉，然后注释掉写死的一行
			// str_res = mapResult.get("res");
			// str_res =
			// "{'SelectResponse':[{'gwcxxid':'1','spsl':'2'},{'gwcxxid':'1','spsl':'2'},{'gwcxxid':'3','spsl':'4'}]}";
			str_res = mapResult.get("res");
			str_success = mapResult.get("success");
			JSONObject jsonObject = new JSONObject().fromObject(str_res);

			SQLList sqllist = new SQLList();
			// 如果返回的消息格式有多个头部，这个地方要修改
			String tmp = jsonObject.getString("SelectResponse");
			//System.out.println(tmp);

			JSONArray jsonArray = JSONArray.fromObject(tmp);
			int iSize = jsonArray.size();
			for (int i = 0; i < iSize; i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				Iterator iterator = jsonObj.keys();
				String key = null;
				String value = null;
				Map result = new HashMap();
				while (iterator.hasNext()) {
					key = (String) iterator.next();
					value = jsonObj.getString(key);
					result.put(key, value);
					//System.out.println("key " + key + " value " + value);
				}
				sqllist.push(result);
				//System.out.println("result" + result);
			}
			// 只是测试
			//System.out.println("sqllist.size() " + sqllist.size());
			//for (int i = 0; i < sqllist.size(); i++) {
				//System.out.println(sqllist.get(i));
			//}

		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!tranStack.isEmpty()) {
			tranRelationMap.remove(tranStack.peek());
			tranOrderMap.remove(tranStack.peek());
			tranStack.pop();
		}
		if (str_success.equals("true")) {
			// 如果修改为返回一个自定义的SQLList格式的话，就返回sqllist
			// return sqllist;
			logger.info("InterfaceName=dataSearchByMemory;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
			return "1";
		} else {
			logger.info("InterfaceName=dataSearchByMemory;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
			return "-1";
		}
	}

	@Override
	public String lobSearch(String ID, String SQLinfo, String savaPath) {
		// TODO Auto-generated method stub
		logger.info("entering lobSearch.");
		// map为要发送的消息
		Map<String, String> map = new HashMap<String, String>();
		ByteBuffer result;// = new HashMap<String,String>();
		map.put("id", ID);
		map.put("SQLinfo", SQLinfo);
		map.put("savaPath", savaPath);
		File outFile = null;
		placeTranIDAndOrder(map);

		try {
			result = daiClient.client.lobSearch(map);
			outFile = new File(savaPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			boolean append = false;
			// If the second argument is true, then bytes will be written to the
			// end of the file rather than the beginning.
			// A new FileDescriptor object is created to represent this file
			// connection
			FileChannel fileChannel = new FileOutputStream(outFile, append)
					.getChannel();
			fileChannel.write(result);
			System.out.println( "in lob");
			fileChannel.close();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (outFile.exists()) {
			logger.info("InterfaceName=LobSearch;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
			return "1";
		} else {
			logger.info("InterfaceName=LobSearch;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
			return "-1";
		}
	}

	@Override
	public String lobInsert(String ID, String SQLinfo, String filePath) {
		// TODO Auto-generated method stub
		logger.info("entering lobInsert.");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;// = new HashMap<String,String>();
		String str_res = null;
		map.put("id", ID);
		map.put("SQLinfo", SQLinfo);

		File file = new File(filePath);
		if (!file.exists()) {
			try {
				throw new FileNotFoundException(filePath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileChannel channel = null;
		FileInputStream input = null;
		ByteBuffer byteBuffer = null;
		try {
			input = new FileInputStream(file);
			channel = input.getChannel();
			byteBuffer = ByteBuffer.allocate((int) channel.size());
			channel.read(byteBuffer);
			byteBuffer.flip();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				channel.close();
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		placeTranIDAndOrder(map);
		System.out.println("in lobInsert the transactionID = "
				+ map.get(TransactionID));

		try {
			// 发送map和bytesbuffer
			mapResult = daiClient.client.lobInsert(map, byteBuffer);
			str_res = mapResult.get("res");
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("finish lobInsert.");
		if (str_res != null) {
			logger.info("InterfaceName=LobInsert;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
		} else {
			logger.info("InterfaceName=dataSearchByTxt;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
		}
		return str_res;
	}

	@Override
	public String dataBaseDisconn(String ID) {
		// TODO Auto-generated method stub
		logger.info("entering dataBaseDisconn.");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;// = new HashMap<String,String>();
		String str_res = null;
		map.put("id", ID);

		placeTranIDAndOrder(map);

		try {
			mapResult = daiClient.client.dataBaseDisconn(map);
			str_res = mapResult.get("res");
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("finish dataBaseDisconn.");
		if (str_res != null) {
			logger.info("InterfaceName=dataBaseDisconn;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=success");
		} else {
			logger.info("InterfaceName=dataBaseDisconn;LocalIp=" + localIP
					+ ";RemoteIP=" + remoteIP + ";State=fail");
		}
		return str_res;
	}

	@Override
	public String errInfo(String errorCode) {
		// TODO Auto-generated method stub
		logger.info("entering errInfo.");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;// = new HashMap<String,String>();
		String str_res = null;
		//System.out.println("erroinfo: "+errorCode);
		map.put("errorCode", errorCode);

		placeTranIDAndOrder(map);

		try {
			mapResult = daiClient.client.errInfo(map);
			str_res = mapResult.get("res");
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("finish errInfo.");
		return str_res;
	}

	private String getTransactionIDs(String transactionID) {
		StringBuffer sb = new StringBuffer();
		sb.append(transactionID);
		String temp = transactionID;
		while (tranRelationMap.containsKey(temp)) {
			sb.append(";" + tranRelationMap.get(temp));
			temp = tranRelationMap.get(temp);
		}
		return sb.toString();
	}

	private int getTransactionOrderbyID(String transactionID) {
		if (!tranOrderMap.containsKey(transactionID)) {
			tranOrderMap.put(transactionID, 0);
			return 0;
		}
		int i = tranOrderMap.get(transactionID);
		i++;
		tranOrderMap.remove(transactionID);
		tranOrderMap.put(transactionID, i);
		return i;
	}

	private void placeTranIDAndOrder(Map<String, String> mappara) {
		if (!tranStack.isEmpty()) {
			System.out.println("in placeTranIDAndOrder trasactionID = "
					+ getTransactionIDs(tranStack.peek()));
			mappara.put(TransactionID, getTransactionIDs(tranStack.peek()));
			mappara.put(TransactionOrder,
					String.valueOf(getTransactionOrderbyID(tranStack.peek())));
		}
	}

	@Override
	public String subscriptionRequest(String Oper, String tableName, int mode) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapResult;
		String str_res = null;
		IPTest ipTest = new IPTest();
		String IP = ipTest.getIp();
		String content = IP + "," + tableName + "," + mode;
		map.put("Content", content);
		map.put("Mode", Oper);
		try {
			mapResult = daiClient.client.subscriptionRequest(map);
			str_res = mapResult.get("res");
			System.out.println("Subscribe: " + str_res);
		} catch (TException e) {
			e.printStackTrace();
		}
		return str_res;
	}
}
