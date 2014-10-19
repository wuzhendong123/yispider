package org.yi.spider.loader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.loader.BaseLoader;
import org.yi.spider.loader.ILoader;

public class YiDuConfigLoader extends BaseLoader implements ILoader {
	
	private static final Logger logger = LoggerFactory.getLogger(YiDuConfigLoader.class);

	@Override
	public void load() {
		logger.debug("开始加载采集参数...");
		loadInitParam();
	}
	

}
