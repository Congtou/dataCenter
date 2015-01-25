package com.client;

 
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.thrift.PushMessage;
import service.thrift.DataInterfaceForward.Client;


public class DAIClient {

	private TTransport transport;
	private TProtocol protocol;
	public Client client;
//    private static String Host = "192.168.213.235";	
	private static String Host = UpNodeConfig.address;
//    private static String Host = "localhost";	
	private static final int Thrift_PORT = 9000;
	
	public static String getHost(){
		return Host;
	}
	public DAIClient(){
		System.out.println("Host : "+Host);
		transport = new TSocket(Host, Thrift_PORT);
		try {
			transport.open();
			//modified by Yao
			//startReceiveSub();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		protocol = new TBinaryProtocol(transport);
		client = new Client(protocol);
	}
	
	public DAIClient(String ip){
		transport = new TSocket(ip, Thrift_PORT);
		try {
			transport.open();
			//modified by yao
			//startReceiveSub();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		protocol = new TBinaryProtocol(transport);
		client = new Client(protocol);
	}
	
	//客户端中的服务器
	private TServer server = null;
	private Thread thread1 = new Thread(){
		public void run() {
			PushMessageImpl pushMessage = new PushMessageImpl();
			PushMessage.Processor<PushMessageImpl> processor =
					new PushMessage.Processor<PushMessageImpl>(pushMessage);
			TServerTransport serverTransport;
			try {
				serverTransport = new TServerSocket(Client_PORT);
				Factory portFactory =new TBinaryProtocol.Factory(true, true);
				Args arg=new Args(serverTransport);
				arg.processor(processor);
				arg.protocolFactory(portFactory);
				TServer server =new TThreadPoolServer(arg);
				logger.info("server start.");
				server.serve();
			} catch (TTransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private Thread thread2 = new Thread(){
		public void run(){
			if(server != null && server.isServing())
				server.stop();
		}
	};
	
	//客户端端口号
	private static final int Client_PORT = 9002;
	private final static Logger logger = LoggerFactory.getLogger(DAIClient.class); 
	public void open() throws TTransportException{
		transport.open();
		startReceiveSub();
	}
	public void close(){
		transport.close();
		stopReceiveSub();
	}
	
	public void startReceiveSub(){
		thread1.start();
	}
	public void stopReceiveSub(){
		thread2.start();
	}
}
