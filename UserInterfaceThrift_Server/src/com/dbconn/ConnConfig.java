package com.dbconn;

import java.io.IOException;
import java.util.Properties;

public class ConnConfig {

	private static Properties prop = new Properties();
	static{
		try {
			prop.load(ConnConfig.class.getResourceAsStream("DBConfig.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String mysql_driver = prop.getProperty("mysql_driver");
	public static String mysql_url = prop.getProperty("mysql_url");
	public static String mysql_password = prop.getProperty("mysql_password");
	public static String mysql_user = prop.getProperty("mysql_user");

}
