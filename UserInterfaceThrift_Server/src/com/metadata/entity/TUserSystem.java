package com.metadata.entity;

import java.sql.Date;

//add by cong
public class TUserSystem {

	private String id;
	private String userSysName;
	private String organization;
	private String phyAddr;
	private String ipAddr;
	private String upperDataAccessNode;
	private String state;
	private String sysType;
	private String createTime;
	private String uComment;
	
	public TUserSystem(){
		
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getCreateTime() {
		return createTime;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}


	public String getUserSysName() {
		return userSysName;
	}

	public void setUserSysName(String userSysName) {
		this.userSysName = userSysName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getPhyAddr() {
		return phyAddr;
	}

	public void setPhyAddr(String phyAddr) {
		this.phyAddr = phyAddr;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getUpperDataAccessNode() {
		return upperDataAccessNode;
	}

	public void setUpperDataAccessNode(String upperDataAccessNode) {
		this.upperDataAccessNode = upperDataAccessNode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSysType() {
		return sysType;
	}

	public void setSysType(String sysType) {
		this.sysType = sysType;
	}

	public String getuComment() {
		return uComment;
	}

	public void setuComment(String uComment) {
		this.uComment = uComment;
	}
}
