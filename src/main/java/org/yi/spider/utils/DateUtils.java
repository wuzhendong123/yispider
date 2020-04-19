package org.yi.spider.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH24:mm:ss";

	/** 
     * @return 获得当前Calendar 
     */  
    public static Calendar getCalendar(){  
        return Calendar.getInstance();  
    }  
    /** 
     * @return 获得今年 
     */  
    public static int getThisYear(){  
        return getCalendar().get(Calendar.YEAR);  
    }  
    /** 
     * @return 获得本月 
     */  
    public static int getThisMonth(){  
        return getCalendar().get(Calendar. MONTH)+1;  
    }
    /**
     * @return 获得当前时间 
     */  
    public static Date getNow(){  
        return getCalendar().getTime(); 
    }
    /**
     * @return 获得日期
     */
    public static Date getDate(String date,String pattern){
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 获取当前时间， 格式为yyyy-MM-dd HH24:mm:ss
     * @return
     */
    public static String getNowStr(){
    	return getDateStr(getNow(), DEFAULT_PATTERN);
    }
    
    /**
     * 获取当前时间
     * @return
     */
    public static String getDateStr(Date date, String pattern){
    	return new SimpleDateFormat(pattern).format(date);
    }

    
}
