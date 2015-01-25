package com.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
public class getIP {
	 

	public static String getLocalIP() throws UnknownHostException
	    {
	        InetAddress inet = InetAddress.getLocalHost();
	        return inet.getHostAddress();

	    }
	public static String RemoteIP="192.168.1.111";
	public static String LocalIP="192.168.213.192";
}
