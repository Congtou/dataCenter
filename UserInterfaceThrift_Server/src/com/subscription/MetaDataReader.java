package com.subscription;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.dbconn.DBConnection;
import com.metadata.entity.MetaDataDef;
import com.metadata.entity.MetaDataStore;
import com.metadata.entity.MetaDataSub;

public class MetaDataReader
{
	// Database operation fields
	private static ResultSet	rs				= null;
	private static Statement	stat			= null;
	private final static int	DBTYPE_MYSQL	= 0;
	private static Connection	conn			= null;

	private static ArrayList<MetaDataStore> getDataStoreList(String sql)
	{
		ArrayList<MetaDataStore> metaDataStoreList = new ArrayList<MetaDataStore>();
		try
		{
			rs = stat.executeQuery(sql);
			while (rs.next())
			{
				MetaDataStore metaData = new MetaDataStore();
				metaData.setPhyAddr(rs.getString("phyAddr"));
				metaData.setIpAddr(rs.getString("ipAddr"));
				metaData.setDataKind(rs.getString("dataKind"));
				metaData.setStoreKind(rs.getString("storeKind"));
				metaData.setDatabaseName(rs.getString("databaseName"));
				metaData.setDatabaseLink(rs.getString("databaseLink"));
				metaData.setCaseName(rs.getString("caseName"));
				metaData.setCreateTime(rs.getString("createTime"));
				metaDataStoreList.add(metaData);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return metaDataStoreList;
	}

	private static ArrayList<MetaDataDef> getDataDefList(String sql)
	{
		ArrayList<MetaDataDef> metaDataDefList = new ArrayList<MetaDataDef>();
		try
		{
			rs = stat.executeQuery(sql);
			while (rs.next())
			{
				MetaDataDef metaData = new MetaDataDef();
				metaData.setCreateKind(rs.getString("createKind"));
				metaData.setCreateTime(rs.getString("createTime"));
				metaData.setAuthor(rs.getString("author"));
				metaData.setDataKind(rs.getString("dataKind"));
				metaData.setDataName(rs.getString("dataName"));
				metaData.setDataSource(rs.getString("dataSource"));
				metaData.setDefine(rs.getString("define"));
				metaData.setFrequency(rs.getString("frequency"));
				metaData.setHostAddr(rs.getString("hostAddr"));
				metaData.setTableName(rs.getString("tableName"));
				metaData.setId(rs.getString("id"));
				metaDataDefList.add(metaData);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return metaDataDefList;
	}

	private static ArrayList<MetaDataSub> getDataSubList(String sql)
	{
		ArrayList<MetaDataSub> metaDataSubList = new ArrayList<MetaDataSub>();
		try
		{
			rs = stat.executeQuery(sql);
			while (rs.next())
			{
				MetaDataSub metaData = new MetaDataSub();
				metaData.setCreateTime(rs.getString("CreateTime"));
				metaData.setDefID(rs.getString("defID"));
				metaData.setId(rs.getString("id"));
				metaData.setSubMode(rs.getString("subMode"));
				metaData.setTokenList(rs.getString("tokenList"));
				metaDataSubList.add(metaData);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return metaDataSubList;
	}

	public static ArrayList<MetaDataStore> readStoreDatabase(String sql)
	{
		conn = DBConnection.getConnection(DBTYPE_MYSQL);
		try
		{
			stat = conn.createStatement();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		ArrayList<MetaDataStore> storeList = getDataStoreList(sql);

		// for (MetaDataStore entity : storeList)
		// {
		// System.out.println("in getConn entity.getDatabaseLink = " +
		// entity.getDatabaseLink());
		// System.out.println("Get database =" + entity.getDatabaseName());
		// }
		releaseDatabaseConnection();
		return storeList;
	}

	public static ArrayList<MetaDataDef> readDefDatabase(String sql)
	{
		conn = DBConnection.getConnection(DBTYPE_MYSQL);
		try
		{
			stat = conn.createStatement();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		ArrayList<MetaDataDef> defList = getDataDefList(sql);
		releaseDatabaseConnection();
		return defList;
	}

	public static ArrayList<MetaDataSub> readSubDatabase(String sql)
	{
		conn = DBConnection.getConnection(DBTYPE_MYSQL);
		try
		{
			stat = conn.createStatement();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		ArrayList<MetaDataSub> subList = getDataSubList(sql);
		releaseDatabaseConnection();
		return subList;
	}

	public static boolean writeSubDataBase(String sql) throws SQLException
	{
		conn = DBConnection.getConnection(DBTYPE_MYSQL);
		try
		{
			stat = conn.createStatement();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		boolean result;
		if (stat.executeUpdate(sql) != 0)
			result = true;
		else
			result = false;
		releaseDatabaseConnection();
		return result;
	}

	private static void releaseDatabaseConnection()
	{

		try
		{
			rs.close();
			stat.close();
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}
}
