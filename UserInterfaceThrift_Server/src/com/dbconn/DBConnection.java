package com.dbconn;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.TimeRecord;
import com.util.getIP;

public class DBConnection {
	private final static Logger logger = LoggerFactory.getLogger(DBConnection.class); 
	public DBConnection() {
		// TODO Auto-generated constructor stub
	}
	public enum DBType{
		mySql,
		oracle,
		db2,
		sybase,
		sqlserver
	}
	public static String localIP = getIP.LocalIP;
	public static String remoteIP = getIP.RemoteIP;
	public static Connection getConnection(int dbType) {
		logger.info("Begin:getConnection method;");
		DBType dbTypeStat;
		Connection conn = null;
		if(dbType == 0){
			dbTypeStat = DBType.mySql;
		}
		else if(dbType == 1){
			dbTypeStat = DBType.oracle;
		}
		else if(dbType == 2){
			dbTypeStat = DBType.db2;
		}
		else if(dbType == 3){
			dbTypeStat = DBType.sybase;
		}
		else{
			dbTypeStat = DBType.sqlserver;
		}
		try {
			IDBConnection instance = DBConnectionFactory.getInstance(dbTypeStat);
			conn = instance.getConnection();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("End:getConnection method;");
		return conn;	
	}
	public static void closeConnection(Connection conn,DBType dbTypeStat){
	logger.info("Begin:closeConnection method;");
		IDBConnection service;
		try {
			service = DBConnectionFactory.getInstance(dbTypeStat);
			service.closeConnection(conn);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("End:closeConnection method;");
	}
}
