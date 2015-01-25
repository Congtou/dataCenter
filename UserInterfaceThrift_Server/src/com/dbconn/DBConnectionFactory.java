package com.dbconn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbconn.DBConnection.DBType;
import com.util.TimeRecord;
import com.util.getIP;



public class DBConnectionFactory {
	private final static Logger logger = LoggerFactory.getLogger(DBConnectionFactory.class); 
	private static IDBConnection instance=null;
	private static Object gate = new Object();
	public DBConnectionFactory() {
		// TODO Auto-generated constructor stub
	}
	public static String localIP = getIP.LocalIP;
	public static String remoteIP = getIP.RemoteIP;
	public static IDBConnection getInstance(DBType dbTypeStat) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
	logger.info("Begin:getInstance method;");
		if(instance==null){
			synchronized(gate){
				switch(dbTypeStat){
					case mySql:instance = (IDBConnection) Class.forName(DBConnectionConfig.mysql_classname).newInstance();break;
					case oracle:instance = (IDBConnection) Class.forName(DBConnectionConfig.oracle_classname).newInstance();break;
					case db2:instance = (IDBConnection) Class.forName(DBConnectionConfig.db2_classname).newInstance();break;
					case sybase:instance = (IDBConnection) Class.forName(DBConnectionConfig.sybase_classname).newInstance();break;
					case sqlserver:instance = (IDBConnection) Class.forName(DBConnectionConfig.sqlserver_classname).newInstance();break;
					default:break;
				}	
			}
		}	
		logger.info("End:getInstance method;");
		return instance;
	}
}
