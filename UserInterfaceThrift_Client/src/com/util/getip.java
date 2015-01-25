package com.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class getip {

		    public static String getRealIp() throws SocketException {
		        String localip = null;// ����IP�����û����������IP�򷵻���
		        String netip = null;// ����IP
		        Enumeration<NetworkInterface> netInterfaces =
		            NetworkInterface.getNetworkInterfaces();
		        InetAddress ip = null;
		        boolean finded = false;// �Ƿ��ҵ�����IP
		        while (netInterfaces.hasMoreElements() && !finded) {
		            NetworkInterface ni = netInterfaces.nextElement();
		            Enumeration<InetAddress> address = ni.getInetAddresses();
		            while (address.hasMoreElements()) {
		                ip = address.nextElement();
		                if (!ip.isSiteLocalAddress()
		                        && !ip.isLoopbackAddress()
		                        && ip.getHostAddress().indexOf(":") == -1) {// ����IP
		                    netip = ip.getHostAddress();
		                    finded = true;
		                    break;
		                } else if (ip.isSiteLocalAddress()
		                        && !ip.isLoopbackAddress()
		                        && ip.getHostAddress().indexOf(":") == -1) {// ����IP
		                    localip = ip.getHostAddress();
		                }
		            }
		        }
		        if (netip != null && !"".equals(netip)) {
		            return netip;
		        } else {
		            return localip;
		        }
		    }
		
}
