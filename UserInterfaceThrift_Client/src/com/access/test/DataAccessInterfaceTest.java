package com.access.test;

import java.io.IOException;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.access.DataAccessInterface;
import com.client.UpNodeConfig;
import com.client.subscription.test.SubscriptionClientTest;
import com.util.TimeRecord;
import com.util.getip;

public class DataAccessInterfaceTest {
	private final static Logger logger = LoggerFactory
			.getLogger(DataAccessInterfaceTest.class);

	public static void main(String[] args){
		
		UpNodeConfig con = new UpNodeConfig();
		String add = con.address;
		System.out.println("address="+add);
		

		
		try {
			DataAccessInterface dai;
			dai = new DataAccessInterface();
//			dai.start();
//			logger.info("current time before startup dataBaseConn = "+TimeRecord.CurrentCompleteTime()+"  IP="+getIP.getLocalIP());
			logger.info("Begin:dataBaseConn method;"+"  IP="+getip.getRealIp());
	//		String ID = dai.dataBaseConn("home", "2");
//			String ID = dai.dataBaseConn("usersysA", "0");
			String ID = dai.dataBaseConn("test", "1","1");//
			//String ID = dai.dataBaseConn("home", "0","167");//
			System.out.println("result ID = "+ID);
			//System.out.println("connection id = "+ID);
			//testLobInsert(ID,dai);
			testLobSearch(ID,dai);
		/*	
			System.out.println("dataoper");
			testDataOper(ID,dai);
			System.out.println("searchbymemory");
			testDataSearchByMemory(ID,dai);
			System.out.println("searchbytxt");
			testDataSearchByTxt(ID,dai);
			System.out.println("lobsearch");
			testLobSearch(ID,dai);
			System.out.println("lobinsert");
			testLobInsert(ID,dai);
			System.out.println("subscriptionRequest");
			testsubscriptionRequest2(dai);*/
			//System.out.println("Transaction1");
    		//testTransaction1(ID,dai);
			//System.out.println("Transaction2");
			//testTransaction2(ID,dai);
			//System.out.println("Transaction3");
			//testTransaction3(ID,dai);
			//System.out.println("Transaction4");
			//testTransaction4(ID,dai);
			System.out.println("scriptionResuest1");
			
			//testsubscriptionRequest1(ID,dai);
			
			
	//		dai.dataBaseDisconn(ID);
			logger.info("current time after break dataBaseConn = "+TimeRecord.CurrentCompleteTime()+"  IP="+getip.getRealIp());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	private static void testDataOper(String ID,DataAccessInterface dai){
		String SQLinfo = "update information set description='three' where information_id = 12 ;";
		dai.dataOper(ID, SQLinfo);
	}
	
	private static void testDataSearchByMemory(String ID,DataAccessInterface dai){
		String SQLinfo = "SELECT description "+
		"FROM  `information` WHERE information_id = 12 ";
		dai.dataSearchByMemory(ID, SQLinfo);
	}
	private static void testDataSearchByTxt(String ID,DataAccessInterface dai){
		//String SQLinfo = "SELECT information_id, description "+
			//	"FROM  `information` WHERE kind = 0 ";
		String SQLinfo = "select description from information  where information_id = 12; ";
		String spaceMark =" ";
		String savePath = System.getProperty("user.dir");//+"\\a.txt";
		dai.dataSearchByTxt(ID, SQLinfo, savePath, spaceMark);
	}
	private static void testLobSearch(String ID,DataAccessInterface dai){
		String savePath = System.getProperty("user.dir")+"/receive/imagereceive1.jpg";
		System.out.println(savePath);
		String SQLinfo = "select image from  information where information_id = 2; ";
//		String savaPath;
		dai.lobSearch(ID, SQLinfo, savePath);
	}
	private static void testLobInsert(String ID,DataAccessInterface dai){
		String SQLinfo = "insert into information (description , data ,image) "
				+" values('mountain12','extra data is null',?)";
		String filePath = System.getProperty("user.dir")+"/send/image1.jpg";
		dai.lobInsert(ID, SQLinfo, filePath);
	}
	
	private static void testTransaction1(String ID,DataAccessInterface dai) throws InterruptedException{
		//System.out.println("in transaction     ");
		dai.transactionBegin(ID);
		//System.out.println("in transaction1   ");
		//String SQLinfo1 = "update zhikong1 set description='1' where id = 1 ;";
		///dai.dataOper(ID, SQLinfo1);
		//String SQLinfo2 = "update zhikong1 set description = '2' where zhikid = 2 ;";
		//dai.dataOper(ID, SQLinfo2);
		//String SQLinfo3 = "update zhikong1 set description='1' where zhikong_id = 1 ;";
		//dai.dataOper(ID, SQLinfo3);
		//String SQLinfo4 = "update zhikong1 set description = '2' where zhikong_id = 2 ;";
		//dai.dataOper(ID, SQLinfo4);
		//String SQLinfo5 = "update zhikong1 set description='1' where zhikong_id = 1 ;";
		//dai.dataOper(ID, SQLinfo5);
		String SQLinfo6 = "update information set informations = '2' where id = 1 ;";
		dai.dataOper(ID, SQLinfo6);
		dai.transactionCommit(ID);
		
	}
	
	//本次事务没有提交transactionCommit
	private static void testTransaction2(String ID,DataAccessInterface dai){
		dai.transactionBegin(ID);
		String SQLinfo1 = "insert into dili1 (id) values (2)";
		dai.dataOper(ID, SQLinfo1);
		String SQLinfo2 = "insert into dili3(id) values (3)";
		dai.dataOper(ID, SQLinfo2);
		dai.transactionCommit(ID);
	}
	//本次事务里面既有dataOper，又有LobInsert,同时提交transactionCommit
	private static void testTransaction3(String ID,DataAccessInterface dai){
		dai.transactionBegin(ID);
		String SQLinfo1 = "insert into shuiwen1 (id) values (1)";
		dai.dataOper(ID, SQLinfo1);
		String SQLinfo2 = "insert into shuiwen2(id) values (1)";
		dai.dataOper(ID, SQLinfo2);
		dai.transactionCommit(ID);
	}
	private static void testTransaction4(String ID,DataAccessInterface dai){
		dai.transactionBegin(ID);
		String SQLinfo1 = "update information set kind = 3 where information_id = 1 ;";
		dai.dataOper(ID, SQLinfo1);
		testLobInsert(ID,dai);
	}
	
	private static void testsubscriptionRequest1(String ID,DataAccessInterface dai){
		String Oper = "ADD";
		String tableName = "information";
		int mode = 0;
		dai.subscriptionRequest(Oper, tableName, mode);
		//SubscriptionClientTest clientTest = new SubscriptionClientTest();
	//	clientTest.getSubscription();
		//dai.transactionBegin(ID);
		String SQLinfo1 = "update information set informations ='second' where id = 1 ;";
		//dai.dataOper(ID, SQLinfo1);
//		String SQLinfo2 = "update zhikong set description = 'first' where zhikong_id = 2 ;";
//		dai.dataOper(ID, SQLinfo2);
		//dai.transactionCommit(ID);
	}
	
	private static void testsubscriptionRequest2(DataAccessInterface dai){
		String Oper = "RMV";
		String tableName = "zhikong";
		int mode = 0;
		dai.subscriptionRequest(Oper, tableName, mode);
	}
}
