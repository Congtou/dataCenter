package com.serverAgent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metadata.entity.MetaDataNode;

public class LocalDBOperator {

	private Connection conn=null;
	private Statement stat =null;
	private ResultSet rs = null;
	private String driver = "com.mysql.jdbc.Driver";
	//private String url = "jdbc:mysql://localhost:3306/mydb";
	private String url = "jdbc:mysql://192.168.213.235:3306/mydb";
	private String user = "root";
	private String pwd = "";
	private final static Logger logger = LoggerFactory.getLogger(LocalDBOperator.class); 
	
	public LocalDBOperator(){
		try {
				Class.forName(driver);
				conn = DriverManager.getConnection(url,user,pwd);
				stat = conn.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getIpAddr(String dataType){
		logger.info("LocalDBOperator: begin getIpAddr method.");
		String ip = null;
		if(conn == null){
			try {
				Class.forName(driver);
				conn = DriverManager.getConnection(url,user,pwd);
				stat = conn.createStatement();
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
		logger.info("LocalDBOperator: finish getIpAddr method.");
		return ip;
	}
	
	
	private ArrayList<MetaDataNode> getDataNodeList(String sql) {
		// TODO Auto-generated method stub
		logger.info("LocalDBOperator: begin getDataNodeList method.");
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
		logger.info("LocalDBOperator: finish getDataNodeList method.");
		return metaDataNodeList;
	}
}
