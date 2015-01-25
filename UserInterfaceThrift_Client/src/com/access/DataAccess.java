package com.access;

public interface DataAccess {

	public String dataBaseConn(String username, String dataType ,String datBaseId);
	public String dataOper(String ID, String SQLinfo);

	public String dataSearchByTxt(String ID, String SQLinfo, String savaPath, String spaceMark);
	public String dataSearchByMemory(String ID, String SQLinfo);
	public String lobSearch(String ID, String SQLinfo, String savaPath);
	public String lobInsert(String ID, String SQLinfo, String filePath);
	
	public String dataBaseDisconn(String ID);
	public String errInfo(String errorCode);
	public String subscriptionRequest(String Oper, String tableName, int mode);
	
	boolean transactionBegin(String ID);
	boolean transactionCommit(String ID);
	boolean transactionRollBack(String ID);
	boolean transactionEnd(String ID);
}
