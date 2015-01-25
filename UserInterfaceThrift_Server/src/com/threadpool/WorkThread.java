package com.threadpool;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.DIFServiceImpl;
import com.serverAgent.ServerAgent;
import com.session.ThreadObjectSession;
import com.workqueue.WorkQueueMap;

public class WorkThread implements Callable<Map<String, String>> {
	private Map<String, String> mappara;
	private Map<String, String> result;
	ServerAgent serverAgent;
	private int interfaceType;
	private final static Logger logger = LoggerFactory.getLogger(WorkThread.class); 
	
	//add by cong
	private final String TransactionID = "transactionID";
	
	public WorkThread(int interfaceType,Map<String, String> mappara) {
		// TODO Auto-generated constructor stub
		this.mappara = mappara;
		this.interfaceType = interfaceType;
		serverAgent = new ServerAgent();
	}
	@Override
	public Map<String, String> call() throws Exception {
		// TODO Auto-generated method stub
		logger.info("come into function call of class WorkThread");
		switch(interfaceType){
		case 0:
			result = serverAgent.dealWithDbconn(mappara);
		break;
		case 1: result = serverAgent.dealWithDataOper(mappara);
		break;
		case 2: 
			Object object = new Object();
//			ThreadObjectSession.Set(mappara.get("ID"), object);
			ThreadObjectSession.Set(mappara.get(TransactionID), object);
			LinkedList<Map<String, String>> list = new LinkedList<Map<String,String>>();
			System.out.println("in WorkThread the transactionID = "+mappara.get(TransactionID));
			WorkQueueMap.getWorkQueue().put(mappara.get(TransactionID), list);
			result = serverAgent.dealWithtransactionBegin(mappara);
		break;
		case 3: result = serverAgent.dealWithtransactionCommit(mappara);
		break;
		case 4: result = serverAgent.dealWithtransactionRollBack(mappara);
		break;
		case 5: result = serverAgent.dealWithtransactionEnd(mappara);
		break;
		case 6: result = serverAgent.dealWithDataSearchByTxt(mappara);
		break;
		case 7: result = serverAgent.dealWithDataSearchByMemory(mappara);
		break;
		case 10: result = serverAgent.dealWithDbClose(mappara);
		break;
		case 11: result = serverAgent.dealWithErrInfo(mappara);
		default: break;
		}
		logger.info("leave off function call of class WorkThread");
		return result;
	}

	
}
