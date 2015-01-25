package com.rest.server;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean; 

import com.rest.servicebean.NotifyServiceImpl;

public class RestServer {

	private JAXRSServerFactoryBean restServer = new JAXRSServerFactoryBean();
	private Server server;
	private Thread thread1 = new Thread(){
		public void run() {
			if(server != null && !server.isStarted()){
				server.start();
			}
		}
	};  //start server
	private Thread thread2 = new Thread(){
		public void run() {
			if(server != null && !server.isStarted()){
				server.stop();
			}
		}
	};//stop server
	private Thread thread3 = new Thread(){
		public void run() {
			if(server != null){
				server.destroy();
			}
		}
	};  //destroy server
	public RestServer(){
		init();
	}
	
	public void init(){
		NotifyServiceImpl notifyService = new NotifyServiceImpl();
        restServer.setServiceBean(notifyService);  
        restServer.setAddress("http://localhost:9999/");  
        server = restServer.create();
	}
	
	public void start(){
		thread1.start();
	}
	
	public void stop(){
		thread2.start();
	}
	public void destroy(){
		thread3.start();
	}
}
