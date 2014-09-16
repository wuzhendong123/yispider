package org.yi.spider.factory;

import org.yi.spider.service.IChapterService;
import org.yi.spider.service.IHtmlBuilder;
import org.yi.spider.service.INovelService;

public interface IServiceFactory {
	
	public INovelService createNovelService(String key);
	
	public IChapterService createChapterService(String key);
	
	public IHtmlBuilder createHtmlBuilder(String key);

}
