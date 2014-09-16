package org.yi.spider.pool2;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.yi.spider.service.IChapterService;

public class ChapterObjectPool {
	
	private static GenericKeyedObjectPool<String, IChapterService> chapterPool;

	private ChapterObjectPool() {
		
	}
	
	public static GenericKeyedObjectPool<String, IChapterService> getPool() {
		if(chapterPool == null) {
			synchronized (ChapterObjectPool.class) {  
				if(chapterPool == null) { 
					chapterPool = new GenericKeyedObjectPool<String, IChapterService>(new ChapterPooledObjectFactory());
				}
			}
		}
		return chapterPool;
	}

}
