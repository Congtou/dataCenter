package com.serverProxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbconn.DBConnection;
import com.metadata.entity.MetaDataNode;
import com.server.DIFServiceImpl;
import com.server.DataTypeConfig;
import com.server.PushMessageClient;
import com.serverAgent.ServerAgent;
import com.session.Session;
import com.subscription.Subscriber;
import com.threadpool.ThreadPool;
import com.threadpool.WorkThread;
import com.util.IdGenerator;
import com.util.MapSerializer;
import com.util.SqlSimpleParser;
import com.util.TimeRecord;
import com.util.getIP;
import com.rest.server.RestServer;

import service.thrift.DataInterfaceForward.Client;
import service.thrift.DataInterfaceForward.Iface;

public class DAIServerProxy implements Iface {

	DIFServiceImpl localServer = new DIFServiceImpl();
	IdGenerator idGenerator = new IdGenerator(0);
	private final String TransactionID = "transactionID";
	private final static Logger logger = LoggerFactory
			.getLogger(DAIServerProxy.class);
	ProxyDataRecord proxyDataRecord = new ProxyDataRecord();
	List<MetaDataNode> metaList = new ArrayList<MetaDataNode>();
	private String dataType = "0";
	Map<String, Client> clientList = new HashMap<String, Client>();	
	
	private Subscriber			subscriber				= Subscriber.getInstance();
	
	ServerAgent nodeServer = new ServerAgent();
	private static final int Proxy_PORT = 9000;
	public static String localIP = getIP.LocalIP;
	public static String remoteIP = getIP.RemoteIP;
	
	//add by cong
	public  RestServer restServer = new RestServer();
	
	public DAIServerProxy(){
		
	}

	@Override
	public Map<String, String> dataBaseConn(Map<String, String> mappara, String ipaddr)
			 {
		logger.info("Begin:dataBaseConn method;");
		Map<String,String> resMap = new HashMap<String, String>();
		if(ipaddr == null || ipaddr.equals("")){
			resMap.put("errorCode", "-1");//-1 represents client ip is null
			return resMap;
		}
		ArrayList<String> clientIp = proxyDataRecord.getAllNodeIPAddress("0");
		System.out.println(clientIp);
		String name = mappara.get("name");
		if(!clientIp.contains(ipaddr)){
			resMap.put("errorCode", "0");//0 represents client ip is not authentic
			return resMap;
		}
		//modify by cong 0531 begin
		else if(!proxyDataRecord.checkUserExist(name)){
			resMap.put("errorCode", "021");
			resMap.put("errorInformation", "user, "+name+" has not been accepted");
			return resMap;
		}
		//modify by cong 0531 end
		String type = mappara.get("dataType");
		System.out.println("type="+type);
		if(dataType == null){
			dataType = DataTypeConfig.dataType;
		}
		System.out.println("type = "+type+" dataType = "+dataType);
		if(!type.equals(dataType)){			
			String forwardIpAddr = proxyDataRecord.getIpAddr(type);
	
			resMap.put("redirectIpAddress", forwardIpAddr);
		}else{
			resMap = nodeServer.dealWithDbconn(mappara);
			
			//add by cong ,如果该用户已经被审批而且，本地存待通知的ip地址中包含该ip
			//则给该ip发送一条审批通过的通知，并将该ip地址从本地文件中移除
			try {
				Map<String,String> map =  MapSerializer.deserialze(HashMap.class);
				if(map.containsKey(ipaddr)){
					
					PushMessageClient pushMessageClient = new PushMessageClient();
					pushMessageClient.setIpAddr(ipaddr);

					pushMessageClient.openClient();
					Map<String ,String> mapNotify = new HashMap<String,String>();
					mapNotify.put("applicationNotify", "success");
					mapNotify.put(ipaddr, map.get(ipaddr));
					try {
						pushMessageClient.getPushClient().registerCompleteNotify(mapNotify);
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.info("in:dataBaseConn method notify "+ipaddr+" register fail");
					}
					map.remove(ipaddr);
					MapSerializer.serialize(map);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("in:dataBaseConn method read/write notifyIPRecord.txt fail");
			}

		}
		logger.info("End:dataBaseConn method;");		
		return resMap;
	}
	
	public Map<String, String> dataOper(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
//		logger.info("Begin dataOper method, current time is: LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
		resMap = localServer.dataOper(mappara);
		logger.info("End dataOper method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}
	public Map<String, String> transactionBegin(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
		logger.info("Begin:transactionBegin method;");
		Map<String, String> resMap = null;
			resMap = localServer.transactionBegin(mappara);
			logger.info("End:transactionBegin method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

	@Override
	public Map<String, String> transactionCommit(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
//		logger.info("Begin transactionCommit method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
			resMap = localServer.transactionCommit(mappara);
			logger.info("End transactionCommit method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

	@Override
	public Map<String, String> transactionRollBack(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
//		logger.info("begin:transactionRollBack method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
			resMap = localServer.transactionRollBack(mappara);
			logger.info("End:transactionRollBack method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

	@Override
	public Map<String, String> transactionEnd(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
//		logger.info("begin:transactionEnd method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
		String ID = mappara.get("ID");
			resMap = localServer.transactionEnd(mappara);
			logger.info("End:transactionEnd method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

	@Override
	public Map<String, String> dataSearchByTxt(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
//		logger.info("begin dataSearchByTxt method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
			resMap = localServer.dataSearchByTxt(mappara);
			logger.info("End:dataSearchByTxt method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

	@Override
	public Map<String, String> dataSearchByMemory(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
//		logger.info("begin:dataSearchByMemory method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
			resMap = localServer.dataSearchByMemory(mappara);
			logger.info("End:dataSearchByMemory method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

	@Override
	public ByteBuffer lobSearch(Map<String, String> mappara) throws TException {
		// TODO Auto-generated method stub
//		logger.info("begin lobSearch method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		ByteBuffer buffer = null;
			buffer = localServer.lobSearch(mappara);
			logger.info("End:lobSearch method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return buffer;
	}

	@Override
	public Map<String, String> lobInsert(Map<String, String> mappara,
			ByteBuffer bytes) throws TException {
		// TODO Auto-generated method stub
//		logger.info("begin lobInsert method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
			resMap = localServer.lobInsert(mappara, bytes);
			logger.info("Begin:lobInsert method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

	@Override
	public Map<String, String> dataBaseDisconn(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
//		logger.info("begin dataBaseDisconn method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
			resMap = localServer.dataBaseDisconn(mappara);
			logger.info("End:dataBaseDisconn method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

	@Override
	public Map<String, String> errInfo(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
//		logger.info("begin:errInfo method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
			resMap = localServer.errInfo(mappara);
			logger.info("End:errInfo method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

	@Override
	public Map<String, String> subscriptionRequest(Map<String, String> mappara)
			throws TException {
		// TODO Auto-generated method stub
//		logger.info("begin:subscriptionRequest method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		Map<String, String> resMap = null;
		resMap = localServer.subscriptionRequest(mappara);
		logger.info("begin:subscriptionRequest method;LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return resMap;
	}

}
