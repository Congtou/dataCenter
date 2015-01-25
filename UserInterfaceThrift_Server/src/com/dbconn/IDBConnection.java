package com.dbconn;

import java.sql.Connection;

public interface IDBConnection {

	Connection getConnection();
	void closeConnection(Connection conn);
}
