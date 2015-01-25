package com.metadata.entity;

public class MetaDataStore {
	private String phyAddr;
	private String ipAddr;
	private String storeKind;
	private String dataKind;
	private String caseName;
	private String databaseName;
	private String databaseLink; 
	private String createTime;
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
	public String getStoreKind() {
		return storeKind;
	}
	public void setStoreKind(String storeKind) {
		this.storeKind = storeKind;
	}
	public String getDataKind() {
		return dataKind;
	}
	public void setDataKind(String dataKind) {
		this.dataKind = dataKind;
	}
	public String getCaseName() {
		return caseName;
	}
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getDatabaseLink() {
		return databaseLink;
	}
	public void setDatabaseLink(String databaseLink) {
		this.databaseLink = databaseLink;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
