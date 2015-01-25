package com.util;

import java.util.Arrays;

public class SqlSimpleParser
{
	public SqlSimpleParser()
	{
		for (String item : returnStrings)
		{
			item = "";
		}
		clear();
	}

	private void clear()
	{
		tableName = "";
		title = "";
		content = "";
		oper = -1;
	}

	public String[] parseSql(String sql)
	{
		clear();
		String[] sqlStrings = sql.split(" ");
		if (sqlStrings[0].toLowerCase().equals("insert"))
			insertSqlParser(sql);
		else if (sqlStrings[0].toLowerCase().equals("delete"))
			deleteSqlParser(sql);
		else if (sqlStrings[0].toLowerCase().equals("update"))
			updateSqlParser(sql);
		returnStrings[0] = tableName;
		returnStrings[1] = title;
		returnStrings[2] = content;
		returnStrings[3] = String.valueOf(oper);
		return returnStrings;
	}

	private void insertSqlParser(String sql)
	{
		// sql="insert into 数据表 (字段1,字段2,字段3 …) valuess (值1,值2,值3 …)"

		// sql="insert into 数据表 valuess (值1,值2,值3 …)"
		// 不指定具体字段名表示将按照数据表中字段的顺序，依次添加

		// sql="insert into 目标数据表 select * from 源数据表"
		// 把源数据表的记录添加到目标数据表

		String[] sqlStrings = sql.split(" ");
		tableName = sqlStrings[2];
		oper = 1;
		String[] sqlParts = sql.split("VALUES|values");
		if (sqlParts.length > 1)
			content = sqlParts[1];
	}

	private void deleteSqlParser(String sql)
	{
		// Sql="delete from 数据表 where 条件表达式"
		//
		// Sql="delete from 数据表"
		// 没有条件将删除数据表中所有记录)
		String[] sqlStrings = sql.split(" ");
		if (sqlStrings[2].toLowerCase().equals("from"))
			tableName = sqlStrings[3];
		else
			tableName = sqlStrings[2];
		oper = 2;
		String[] sqlParts = sql.split("WHERE|where");
		if (sqlParts.length > 1)
			content = sqlParts[1];
		else
			content = "ALL";
	}

	private void updateSqlParser(String sql)
	{
		// Sql="update 数据表 set 字段名=字段值 where 条件表达式"
		//
		// Sql="update 数据表 set 字段1=值1,字段2=值2 …… 字段n=值n where 条件表达式"
		//
		// Sql="update 数据表 set 字段1=值1,字段2=值2 …… 字段n=值n "
		// 没有条件则更新整个数据表中的指定字段值
		String[] sqlStrings = sql.split(" ");
		tableName = sqlStrings[1];
		oper = 3;
		String[] sqlParts = sql.split("set|SET|WHERE|where");
		if (sqlParts.length == 2)
			content = sqlParts[1];
		else if (sqlParts.length == 3)
		{
			content = sqlParts[1];
			title = sqlParts[2];
		}
	}

	String		tableName;
	String		title;
	String		content;
	int			oper;
	String[]	returnStrings	= new String[4];

	public static void main(String[] args)
	{
		SqlSimpleParser simpleParser = new SqlSimpleParser();
		String[] Strings1 = simpleParser.parseSql("INSERT INTO Persons VALUES ('Gates', 'Bill', 'Xuanwumen 10', 'Beijing')");
		System.out.println(Arrays.toString(Strings1));
		String[] Strings2 = simpleParser.parseSql("INSERT INTO Persons (LastName, Address) VALUES ('Wilson', 'Champs-Elysees')");
		System.out.println(Arrays.toString(Strings2));

		String[] Strings3 = simpleParser.parseSql("DELETE FROM Person WHERE LastName = 'Wilson' ");
		System.out.println(Arrays.toString(Strings3));
		String[] Strings4 = simpleParser.parseSql("DELETE * FROM table_name");
		System.out.println(Arrays.toString(Strings4));
		String[] Strings5 = simpleParser.parseSql("DELETE  FROM table_name");
		System.out.println(Arrays.toString(Strings5));

		String[] Strings6 = simpleParser.parseSql("UPDATE Person SET FirstName = 'Fred' WHERE LastName = 'Wilson' ");
		System.out.println(Arrays.toString(Strings6));
		String[] Strings7 = simpleParser.parseSql("UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing' WHERE LastName = 'Wilson'");
		System.out.println(Arrays.toString(Strings7));
	}

}
