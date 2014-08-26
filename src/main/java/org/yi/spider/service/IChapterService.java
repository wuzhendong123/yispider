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
    public int save(ChapterEntity chapter) throws SQLException;

    /**
     * 
     * <p>获取当前小说的章节统计信息， 总字数、总章节数</p>
     * @param articleno
     * @return
     * @throws SQLException 
     */
	public Map<String, Object> getTotalInfo(Integer novelno) throws SQLException;

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
	 */
	public Integer get(ChapterEntity chapter, int i);

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
	public ChapterEntity get(Integer chapterNo) throws SQLException;

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
	 * <p>获取txt文件的绝对路径</p>
	 * @param chapter
	 * @return
	 */
	public String getTxtFilePath(ChapterEntity chapter);
	
	/**
	 * 
	 * <p>获取html文件的绝对路径</p>
	 * @param chapter
	 * @return
	 */
	public String getHtmlFilePath(ChapterEntity chapter);
	
	/**
	 * 
	 * <p>获取html文件的url地址</p>
	 * @param articleNo
	 * @param chapterNo
	 * @return
	 */
	public String getStaticUrl(Integer articleNo, String chapterNo);

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
	
}
