package com.metadata.entity;

public class MetaDataNode {
	private String phyAddr;
	private String ipAddr;
    private String nodeKind;
	private String dataKind;
    private String upIPAddr;
	private String author;
	private String createTime;
	public String getPhyAddr() {
		return phyAddr;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public String getNodeKind() {
		return nodeKind;
	}
	public String getDataKind() {
		return dataKind;
	}
	public String getUpIPAddr() {
		return upIPAddr;
	}
	public String getAuthor() {
		return author;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setPhyAddr(String phyAddr) {
		this.phyAddr = phyAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public void setNodeKind(String nodeKind) {
		this.nodeKind = nodeKind;
	}
	public void setDataKind(String dataKind) {
		this.dataKind = dataKind;
	}
	public void setUpIPAddr(String upIPAddr) {
		this.upIPAddr = upIPAddr;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
