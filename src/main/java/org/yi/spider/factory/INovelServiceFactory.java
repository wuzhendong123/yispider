package org.yi.spider.factory;

import org.yi.spider.service.IChapterService;
import org.yi.spider.service.INovelService;

public interface INovelServiceFactory {
	
	public INovelService createNovelService(String key);
	
	public IChapterService createChapterService(String key);

}
