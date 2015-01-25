package com.access.test;

import java.io.IOException;

import com.access.DataAccessInterface;

public class LjtTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DataAccessInterface dai;
		dai = new DataAccessInterface();
		String ID = dai.dataBaseConn("usersysA", "0","1");//
		System.out.println("result ID = "+ID);
		System.out.println("connection id = "+ID);
		long start = System.currentTimeMillis();
		String SQLinfo = "select * from testinsert limit 1,5";
		dai.dataSearchByMemory(ID, SQLinfo);
		System.out.println(System.currentTimeMillis()-start);
	}

}
