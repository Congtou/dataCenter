package com.serverProxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbconn.DBConnection;
import com.metadata.entity.MetaDataNode;
import com.metadata.entity.MetaDataStore;
import com.metadata.entity.TUserSystem;
import com.serverAgent.ServerAgent;
import com.util.TimeRecord;
import com.util.getIP;

public class ProxyDataRecord {
	
	private Connection conn=null;
	private Statement stat =null;
	private final static int DBTYPE_MYSQL = 0;
	private ResultSet rs = null;
	private final static Logger logger = LoggerFactory.getLogger(ProxyDataRecord.class); 
	public static String localIP = getIP.LocalIP;
	public static String remoteIP = getIP.RemoteIP;
	public String getIpAddr(String dataType){
		logger.info("Begin getIPAddr method.LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		String ip = null;
		if(conn == null){
			conn = DBConnection.getConnection(DBTYPE_MYSQL);
			try{
				stat = conn.createStatement();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		String sql = "select * from t_metadata_node where dataKind='" + dataType + "'";
		ArrayList<MetaDataNode> nodeList = getDataNodeList(sql);
		for(MetaDataNode node:nodeList){
			if(node.getDataKind().equals(dataType))
				ip = node.getIpAddr();
			break;
		}
		logger.info("End getIPAddr method." + ip+"   LocalIp=" + localIP+ ";RemoteIP=" + remoteIP + ";State=success");
		return ip;
		
	}
	
	private ArrayList<MetaDataNode> getDataNodeList(String sql) {
		// TODO Auto-generated method stub
		logger.info("Begin getDataNodeList");
		ArrayList<MetaDataNode> metaDataNodeList = new ArrayList<MetaDataNode>();
		try {
			rs = stat.executeQuery(sql);
			while(rs.next()){
				MetaDataNode metaData = new MetaDataNode();
				metaData.setPhyAddr(rs.getString("phyAddr"));
				metaData.setIpAddr(rs.getString("ipAddr"));
				metaData.setDataKind(rs.getString("dataKind"));
				metaData.setAuthor(rs.getString("author"));
				metaData.setCreateTime(rs.getString("createTime"));
				metaData.setNodeKind(rs.getString("nodeKind"));
				metaData.setUpIPAddr(rs.getString("upIpAddr"));
				metaDataNodeList.add(metaData);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		logger.info("End getDataNodeList");
		return metaDataNodeList;
	}
	public ArrayList<String> getAllNodeIPAddress(String nodeKind){
		logger.info("Begin getAllNodeIPAddress method.");
		if(conn == null){
			conn = DBConnection.getConnection(DBTYPE_MYSQL);
			try{
				stat = conn.createStatement();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		ArrayList<String> ipList = new ArrayList<String>();
		try {
			rs = stat.executeQuery("select ipAddr from t_metadata_node where nodeKind='" + nodeKind + "'");
			while(rs.next()){
				ipList.add(rs.getString("ipAddr"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		logger.info("finish getAllNodeIPAddress method.");
		return ipList;
	}
	
	//add by cong 0531
	public boolean checkUserExist(String name){
		logger.info("Begin checkUserExist method name = "+name);
		boolean judge = false;
		if(conn == null){
			conn = DBConnection.getConnection(DBTYPE_MYSQL);
			try{
				stat = conn.createStatement();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		String sql = "select * from t_user_system where userSysName = '"+name+"'; ";
		ArrayList<TUserSystem> tUserSystemList = getTUserSystemList(sql);
		for(TUserSystem tUserSystem : tUserSystemList){
			if(tUserSystem.getUserSysName().equals(name)){
				System.out.println("name :"+tUserSystem.getUserSysName());
				judge = true;
//				tus.setCreateTime(tUserSystem.getCreateTime());
//				tus.setId(tUserSystem.getId());
//				tus.setIpAddr(tUserSystem.getIpAddr());
//				tus.setOrganization(tUserSystem.getOrganization());
//				tus.setPhyAddr(tUserSystem.getPhyAddr());
//				tus.setState(tUserSystem.getState());
//				tus.setSysType(tUserSystem.getSysType());
//				tus.setuComment(tUserSystem.getuComment());
//				tus.setUpperDataAccessNode(tUserSystem.getUpperDataAccessNode());
//				tus.setUserSysName(name);
				break;
			}
		}
		logger.info("End checkUserExist method name = "+name);
//		System.out.println("judge = "+judge);
		return judge;
	}
	//add by cong 0531
	public ArrayList<TUserSystem> getTUserSystemList(String sql){
		logger.info("Begin getTUserSystemList");
		if(conn == null){
			conn = DBConnection.getConnection(DBTYPE_MYSQL);
			try{
				stat = conn.createStatement();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		ArrayList<TUserSystem> tUserSystemList = new ArrayList<TUserSystem>();
		try {
			rs = stat.executeQuery(sql);
			while(rs.next()){
				TUserSystem tUserSystem = new TUserSystem();
				tUserSystem.setId(rs.getString("id"));
				tUserSystem.setIpAddr(rs.getString("ipAddr"));
				tUserSystem.setCreateTime(rs.getString("createTime"));
				tUserSystem.setOrganization(rs.getString("organization"));
				tUserSystem.setPhyAddr(rs.getString("phyAddr"));
				tUserSystem.setState(rs.getString("state"));
				tUserSystem.setSysType(rs.getString("sysType"));
				tUserSystem.setuComment(rs.getString("uComment"));
				tUserSystem.setUpperDataAccessNode(rs.getString("upperDataAccessNode"));
				tUserSystem.setUserSysName(rs.getString("userSysName"));
				
				tUserSystemList.add(tUserSystem);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("End getTUserSystemList");
//		System.out.println("tUserSystemList size() = "+tUserSystemList.size());
		return tUserSystemList;
	}
}
