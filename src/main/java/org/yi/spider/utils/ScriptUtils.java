package org.yi.spider.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(ScriptUtils.class);
	
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
			logger.warn("表达式执行异常： " + e.getMessage());
		} 
        return result;
	}
	
	public static <T> Integer evalInt(String express, Map<String, T> params) throws ScriptException{
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
        Integer result = null;
        try {
			result = new BigDecimal(String.valueOf(engine.eval(express))).intValue();
		} catch (ScriptException e) {
			logger.warn("表达式执行异常： " + e.getMessage());
		} 
        return result;
	}
	
	/**
	 * 解析字符串， 并将其当作表达式执行
	 * @param express
	 * @param params
	 * @return
	 * @throws ScriptException
	 */
	public static <T> Boolean evalBoolean(String express, Map<String, T> params) {
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
        Boolean result = null;
        try {
			result = (Boolean)engine.eval(express);
		} catch (ScriptException e) {
			result = false;
			logger.warn("表达式执行异常： " + e.getMessage());
		} 
        return result;
	}
	
}
