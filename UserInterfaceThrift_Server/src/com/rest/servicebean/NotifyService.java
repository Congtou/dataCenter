package com.rest.servicebean;

import javax.ws.rs.Consumes;  
import javax.ws.rs.DELETE;  
import javax.ws.rs.GET;  
import javax.ws.rs.POST;  
import javax.ws.rs.PUT;  
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  
import javax.ws.rs.QueryParam;

import com.server.PushMessageClient;

@Path("/notifyservice")  
public interface NotifyService {

	
	@GET  
    @Path("/usersystem") 
	public String notifyUserSystem(@QueryParam("ip")String ip,
			@QueryParam("userSysName")String userSysName);
	
	@GET
	@Path("/test")
	public String test(@QueryParam("id")String id);
}
