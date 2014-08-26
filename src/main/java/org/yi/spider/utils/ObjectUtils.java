package org.yi.spider.utils;

public class ObjectUtils {
	
	
	public static Integer obj2Int(Object obj){
		int i = 0;
		if(obj!=null) {
			i = Integer.parseInt(String.valueOf(obj));
		}
		return i;
	}

}