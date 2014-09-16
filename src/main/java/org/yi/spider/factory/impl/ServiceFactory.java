package org.yi.spider.factory.impl;

import org.yi.spider.enums.ProgramEnum;
import org.yi.spider.factory.IServiceFactory;
import org.yi.spider.service.IChapterService;
import org.yi.spider.service.IHtmlBuilder;
import org.yi.spider.service.INovelService;

public class ServiceFactory implements IServiceFactory {

	/**
	 * 
	 * <p>抽象工厂， 产生NovelService对象</p>
	 * @param key
	 * @return
	 * @see org.yi.spider.factory.IServiceFactory#createNovelService(java.lang.String)
	 */
	@Override
	public INovelService createNovelService(String key) {
		
		INovelService novelService = null;
		
		if(key.equalsIgnoreCase(ProgramEnum.JIEQI.getName())) {
			novelService = new org.yi.spider.service.jieqi.NovelServiceImpl();
		} else {
			novelService = new org.yi.spider.service.yidu.NovelServiceImpl();
		}
		return novelService;
	}

	/**
	 * 
	 * <p>抽象工厂， 产生ChapterService对象</p>
	 * @param key
	 * @return
	 * @see org.yi.spider.factory.IServiceFactory#createChapterService(java.lang.String)
	 */
	@Override
	public IChapterService createChapterService(String key) {
		IChapterService chapterService = null;
		if(key.equalsIgnoreCase(ProgramEnum.JIEQI.getName())) {
			chapterService = new org.yi.spider.service.jieqi.ChapterServiceImpl();
		} else {
			chapterService = new org.yi.spider.service.yidu.ChapterServiceImpl();
		}
		return chapterService;
	}

	@Override
	public IHtmlBuilder createHtmlBuilder(String key) {
		IHtmlBuilder htmlBuilder = null;
		if(key.equalsIgnoreCase(ProgramEnum.JIEQI.getName())) {
			htmlBuilder = new org.yi.spider.service.jieqi.HtmlBuilderImpl();
		} else {
//			htmlBuilder = new org.yi.spider.service.yidu.ChapterServiceImpl();
		}
		return htmlBuilder;
	}

}
