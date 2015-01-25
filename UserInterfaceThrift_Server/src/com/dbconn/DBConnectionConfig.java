package com.dbconn;

import java.io.IOException;
import java.util.Properties;

public class DBConnectionConfig {
	private static Properties prop = new Properties();
	static{
		try {
			prop.load(DBConnectionConfig.class.getResourceAsStream("DBConn_ClassNameConfig.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String mysql_classname = prop.getProperty("mysql_classname");
	public static String oracle_classname = prop.getProperty("oracle_classnameey");
	public static String db2_classname = prop.getProperty("db2_classname");
	public static String sybase_classname = prop.getProperty("sybase_classname");
	public static String sqlserver_classname = prop.getProperty("sqlserver_classname");

}
