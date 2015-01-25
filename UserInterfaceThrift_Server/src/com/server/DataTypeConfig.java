package com.server;

import java.io.IOException;
import java.util.Properties;


public class DataTypeConfig {

	private static Properties prop = new Properties();
	static{
		try {
			prop.load(DataTypeConfig.class.getResourceAsStream("DataType.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String dataType = prop.getProperty("server_type");
}
