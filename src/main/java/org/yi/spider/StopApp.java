package org.yi.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.pool2.NovelObjectPool;

public class StopApp {
	
	private static final Logger logger = LoggerFactory.getLogger(StopApp.class);
	
	public static void stop(){
		try {
			NovelObjectPool.getNovelPool().close();
			System.exit(0);
		} catch (Exception e) {
			logger.error("程序异常退出", e);
			System.exit(-1);
		}
	}

}
