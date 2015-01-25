package com.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class UpNodeConfig {

	private static Properties prop = new Properties();
	static{
		try {
			prop.load(System.getProperty("os.name").toLowerCase().indexOf("linux")>-1?
					new FileInputStream(new File(("/home/UpNodeAddress.properties"))):
					new FileInputStream(new File(("C:\\UpNodeAddress.properties"))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String address = prop.getProperty("up_address");
}
