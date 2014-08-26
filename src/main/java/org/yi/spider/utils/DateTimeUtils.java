package org.yi.spider.utils;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

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
    
}
