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
	 */
	public static Double calculate(String express, Map<String, Double> params){
        ScriptEngineManager manager = new ScriptEngineManager();  
        ScriptEngine engine = manager.getEngineByName("js");
        if(params == null){
        	params = new HashMap<String,Double>();
        }
        Iterator<Map.Entry<String,Double>> iter = params.entrySet().iterator();
        Map.Entry<String,Double> entry = null;
        while(iter.hasNext()){
        	entry = iter.next();
        	engine.put(entry.getKey(), entry.getValue());
        }
        Double result = null;
        try {
			result = (Double)engine.eval(express);
		} catch (ScriptException e) {
			e.printStackTrace();
		} 
        return result;
	}
	
}
