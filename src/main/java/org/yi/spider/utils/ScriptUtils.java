package org.yi.spider.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptUtils {
	
	/**
	 * 
	 * <p>执行字符串计算</p>
	 * @param express
	 * @param params
	 * @return
	 * @throws ScriptException 
	 */
	@SuppressWarnings("unchecked")
	public static <T, E> E eval(String express, Map<String, T> params) throws ScriptException{
        ScriptEngineManager manager = new ScriptEngineManager();  
        ScriptEngine engine = manager.getEngineByName("js");
        if(params == null){
        	params = new HashMap<String,T>();
        }
        Iterator<Map.Entry<String, T>> iter = params.entrySet().iterator();
        Map.Entry<String, T> entry = null;
        while(iter.hasNext()){
        	entry = iter.next();
        	engine.put(entry.getKey(), entry.getValue());
        }
        E result = null;
        try {
			result = (E)engine.eval(express);
		} catch (ScriptException e) {
			throw new ScriptException(e);
		} 
        return result;
	}
	
}
