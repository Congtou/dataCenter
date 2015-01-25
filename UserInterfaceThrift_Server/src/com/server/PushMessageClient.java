package com.server;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushMessageClient {
	private static final int Client_Port = 9002;
	private static String Host = "localhost";
	private TTransport c_transport;
	private TProtocol c_protocol;
	private service.thrift.PushMessage.Client pushClient;
	private final static Logger logger = LoggerFactory.getLogger(PushMessageClient.class); 
	
	public void openClient(){
		logger.info("PushMessageClient: begin open method.");
		c_transport = new TSocket(Host, Client_Port);
		c_protocol = new TBinaryProtocol(c_transport);
		setPushClient(new service.thrift.PushMessage.Client(c_protocol));
		try {
			cOpen();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("PushMessageClient: finish open method.");
	}
	
	public void cOpen() throws TTransportException{
		c_transport.open();
	}
	public void cClose(){
		c_transport.close();
	}

	public void setIpAddr(String ipAddr){
		logger.info("PushMessageClient: in setIpAddr method.");
		Host = ipAddr;
	}
	/**
	 * @return the pushClient
	 */
	public service.thrift.PushMessage.Client getPushClient()
	{
		logger.info("PushMessageClient: begin getPushClient method.");
		return pushClient;
	}

	
	/**
	 * @param pushClient the pushClient to set
	 */
	public void setPushClient(service.thrift.PushMessage.Client pushClient)
	{
		this.pushClient = pushClient;
	}
}
