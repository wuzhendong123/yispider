package org.yi.spider.service;

import java.sql.SQLException;

import org.yi.spider.entity.NovelEntity;

public interface INovelService {
	
	/**
	 * 
	 * <p>更新书籍信息， 最后更新日期， 最后分卷ID， 分卷名， 最后章节，章节数， 书籍大小</p>
	 * @param article
	 * @return
	 * @throws SQLException 
	 */
    public int update(NovelEntity article) throws SQLException;
    
    /**
	 * 
	 * <p>修复小说介绍、</p>
	 * @param novel
     * @param newNovel 
	 * @return
	 * @throws SQLException 
	 */
    public void repair(NovelEntity novel, NovelEntity newNovel) throws SQLException;

    /**
     * 
     * <p>判断书籍是否已经存在，  存在返回true， 不存在返回false</p>
     * @param name
     * @return
     * @throws SQLException 
     */
	boolean exist(String name) throws SQLException;

	/**
	 * 
	 * <p>保存小说信息</p>
	 * @param novel
	 * @return
	 * @throws SQLException 
	 */
	public Integer saveNovel(NovelEntity novel) throws SQLException;

	/**
	 * 
	 * <p>通过小说名获取小说</p>
	 * @param novelName
	 * @return
	 * @throws SQLException 
	 */
	public NovelEntity find(String novelName) throws SQLException;

	


}
