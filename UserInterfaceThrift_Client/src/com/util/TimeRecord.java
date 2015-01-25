package com.util;

import java.util.Calendar;

public class TimeRecord {

	public static String CurrentCompleteTime(){
		Calendar now = Calendar.getInstance();
		int month = now.get(Calendar.MONTH) + 1;
        String m = "";
         
        if(month < 10) {
            m = "0"+month;
        } else 
        	m = String.valueOf(month);
        String str_ms = "";
        int ms = now.get(Calendar.MILLISECOND);
//        System.out.println("ms = "+ms);
        if(ms < 1000) {
        	str_ms = "0"+ms;
//        	System.out.println("test str_ms = "+str_ms);
        } else
        	str_ms = String.valueOf(ms);
        
        String m_ms = "";
        
		String time = now.get(Calendar.YEAR)+"/"+ m +"/"+ now.get(Calendar.DAY_OF_MONTH) + 
				"/"+now.get(Calendar.HOUR_OF_DAY)+"/"+now.get(Calendar.MINUTE)+
                "/"+now.get(Calendar.SECOND)+"/"+str_ms;
		return time;

	}
}
