package com.access;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SQLList {
//	List<Map<String,String>> SQLLine ;


    LinkedList<Map> SQLList;  
      
    public int size(){
		return SQLList.size();
    }
    
    public Map get(int index){
    	return SQLList.get(index);
    }
    
    public SQLList()  
    {  
        this.SQLList = new LinkedList<Map>();  
    }  
    public void push(Map str) //Èë¶Ó  
    {  
    	SQLList.add(str);  
    }  
    public Map top()  
    {  
        return SQLList.getFirst();  
    }  
    public Map pop() //³öÕ»  
    {  
        return SQLList.removeFirst();  
    }  
    public boolean isEmpty()  
    {  
        return SQLList.isEmpty();  
    }  
      
    public static void main(String[] args)  
    {  
    	SQLList stack = new SQLList();  
        Scanner sc = new Scanner(System.in);  
          
        while(!stack.isEmpty())  
            System.out.println(stack.pop());  
    }  

}
