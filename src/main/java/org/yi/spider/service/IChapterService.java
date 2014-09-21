package org.yi.spider.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.NovelEntity;

public interface IChapterService {

	/**
	 * 
	 * <p>保存章节信息</p>
	 * @param chapter
	 * @return
	 * @throws SQLException 
	 */
    public Number save(ChapterEntity chapter) throws SQLException;

    /**
     * 
     * <p>获取当前小说的章节统计信息， 总字数、总章节数</p>
     * @param articleno
     * @return
     * @throws SQLException 
     */
	public Map<String, Object> getTotalInfo(Number novelno) throws SQLException;

	/**
	 * 
	 * <p>获取当前章节在排序中的序号</p>
	 * @param chapter
	 * @return
	 * @throws SQLException 
	 */
	public int getChapterOrder(ChapterEntity chapter) throws SQLException;

	/**
	 * 
	 * <p>章节是否存在</p>
	 * @param chapter
	 * @return
	 * @throws SQLException 
	 */
	public boolean exist(ChapterEntity chapter) throws SQLException;

	/**
	 * 
	 * <p>获取相邻章节编号</p>
	 * @param chapter	当前章节
	 * @param i			-1表示上一章	1表示下一章
	 * @return
	 * @throws SQLException 
	 */
	public ChapterEntity get(ChapterEntity chapter, int i) throws SQLException;

	/**
	 * 
	 * <p>获取当前小说的所有章节</p>
	 * @param novel
	 * @return
	 * @throws SQLException 
	 */
	public List<ChapterEntity> getChapterList(NovelEntity novel) throws SQLException;
							   

	/**
	 * 
	 * <p>根据章节号获取章节信息</p>
	 * @param chapterNo
	 * @return
	 * @throws SQLException 
	 */
	public ChapterEntity get(Number chapterNo) throws SQLException;

	/**
	 * 
	 * <p>获取小说的最后章节， 用于解决强行终止程序时article.getLasChapterEntity获取空值错误</p>
	 * @param chapter
	 * @return
	 * @throws SQLException 
	 */
	public ChapterEntity getLasChapter(ChapterEntity chapter) throws SQLException;

	/**
	 * 
	 * <p>获取重复章节</p>
	 * @return
	 * @throws SQLException 
	 */
	public List<ChapterEntity> getDuplicateChapter() throws SQLException;
	
	/**
	 * 
	 * <p>通过章节名和小说号获取具体的章节对象</p>
	 * @param cname
	 * @param article
	 * @return
	 * @throws SQLException 
	 */
	public ChapterEntity getChapterByChapterNameAndNovelNo(ChapterEntity chapter) throws SQLException;

	/**
	 * 
	 * <p>删除章节</p>
	 * @param chapters
	 * @throws SQLException 
	 */
	public void delete(List<ChapterEntity> chapters) throws SQLException;

	/**
	 * 更新章节字数
	 * @param chapter
	 * @return
	 * @throws SQLException 
	 */
	int updateSize(ChapterEntity chapter) throws SQLException;
	
}
