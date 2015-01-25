package com.subscription;

import java.io.Serializable;

public class SubMsg implements Serializable
{
	public SubMsg()
	{

	}

	public void print()
	{
		System.out.printf("id=%d, IP=%s, port=%d, dataBase=%s, table=%s, mode=%d, title=%s, content=%s, opera=%d, failCount=%d\n", id, IP, port, dataBase, tableName, mode, title, content, oper,
				failCount);
	}

	private static final long	serialVersionUID	= 2965199283911651786L;
	private int					id;
	private String				IP;
	private int					port;
	private String				dataBase;
	private String				tableName;
	private int					mode;
	private String				title;
	private String				content;
	private int					failCount;
	private int					oper;
	private String				defID;

	/**
	 * @return the iP
	 */
	public String getIP()
	{
		return IP;
	}

	/**
	 * @param iP the iP to set
	 */
	public void setIP(String iP)
	{
		IP = iP;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * @return the failCount
	 */
	public int getFailCount()
	{
		return failCount;
	}

	/**
	 * @param failCount the failCount to set
	 */
	public void setFailCount(int failCount)
	{
		this.failCount = failCount;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * @return the token
	 */
	public String getDataBase()
	{
		return dataBase;
	}

	/**
	 * @param token the token to set
	 */
	public void setDataBase(String token)
	{
		this.dataBase = token;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 * @return the mode
	 */
	public int getMode()
	{
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode)
	{
		if (mode < 0 || mode > 3)
			this.mode = 0;
		else
			this.mode = mode;
	}

	/**
	 * @return the oper
	 */
	public int getOper()
	{
		return oper;
	}

	/**
	 * @param oper the oper to set
	 */
	public void setOper(int oper)
	{
		this.oper = oper;
	}

	/**
	 * @return the defIDString
	 */
	public String getDefIDString()
	{
		return defID;
	}

	/**
	 * @param defIDString the defIDString to set
	 */
	public void setDefIDString(String defIDString)
	{
		this.defID = defIDString;
	}
}
