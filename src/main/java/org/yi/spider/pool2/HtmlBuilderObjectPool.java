package org.yi.spider.pool2;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.yi.spider.service.IHtmlBuilder;

public class HtmlBuilderObjectPool {

	private static GenericKeyedObjectPool<String, IHtmlBuilder> htmlBuilderPool;

	private HtmlBuilderObjectPool() {
		
	}
	
	public static GenericKeyedObjectPool<String, IHtmlBuilder> getPool() {
		if(htmlBuilderPool == null) {
			synchronized (HtmlBuilderObjectPool.class) {  
				if(htmlBuilderPool == null) { 
					htmlBuilderPool = new GenericKeyedObjectPool<String, IHtmlBuilder>(new HtmlBuilderPooledObjectFactory());
				}
			}
		}
		return htmlBuilderPool;
	}
	
}
