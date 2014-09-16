package org.yi.spider.service;

import java.util.List;

import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.enums.CategoryGradeEnum;
import org.yi.spider.model.Category;
import org.yi.spider.model.PreNextChapter;

/**
 * html建造器工厂
 * @author lenovo
 *
 */
public interface IHtmlBuilder {

	/**
	 * 生成章节目录页HTML
	 * @param novel
	 * @param chapterList
	 */
	public void buildChapterListHtml(NovelEntity novel, List<ChapterEntity> chapterList);
	
	/**
	 * 生成内容页HTML
	 * @param article
	 * @param chapter
	 * @param content
	 * @param preNext
	 */
	public void buildChapterCntHtml(NovelEntity article, ChapterEntity chapter, String content, PreNextChapter preNext);
	
	
	/**
	 * 根据章节号获取章节TXT内容
	 * @param chapter 章节实例
	 * @return
	 */
	public String loadChapterContent(ChapterEntity chapter);

	/**
	 * 通过分类id获取分类对象
	 * @param id
	 * @param grade
	 * @return
	 */
	Category getCategoryById(String id, CategoryGradeEnum grade);

}
