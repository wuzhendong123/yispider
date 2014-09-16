package org.yi.spider.entity;

import java.util.Date;


public class NovelEntity extends BaseEntity{

    /**关键词**/
    private String keywords;
    /**首字母**/
    private String initial;
    /**作者名**/
    private String author;
    /**小说大类**/
    private Integer topCategory;
    /**小说细类**/
    private Integer subCategory;
    /**小说介绍**/
    private String intro;
    /**小说最后章节号**/
    private Integer lastChapterno;
    /**小说最后章节名**/
    private String lastChapterName;
    /**小说章节数**/
    private Integer chapters;
    /**小说字数**/
    private Integer size;
    /**是否完本**/
    private Boolean fullFlag;
    /**封面图片格式标识**/
    private Integer imgFlag;
    /**最后更新日期**/
    private Date lastUpdate;
    /**拼音**/
    private String pinyin;
    
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getInitial() {
		return initial;
	}
	public void setInitial(String initial) {
		this.initial = initial;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Integer getTopCategory() {
		return topCategory;
	}
	public void setTopCategory(Integer topCategory) {
		this.topCategory = topCategory;
	}
	public Integer getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(Integer subCategory) {
		this.subCategory = subCategory;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public Integer getLastChapterno() {
		return lastChapterno;
	}
	public void setLastChapterno(Integer lastChapterno) {
		this.lastChapterno = lastChapterno;
	}
	public String getLastChapterName() {
		return lastChapterName;
	}
	public void setLastChapterName(String lastChapterName) {
		this.lastChapterName = lastChapterName;
	}
	public Integer getChapters() {
		return chapters;
	}
	public void setChapters(Integer chapters) {
		this.chapters = chapters;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Boolean getFullFlag() {
		return fullFlag;
	}
	public void setFullFlag(Boolean fullFlag) {
		this.fullFlag = fullFlag;
	}
	public Integer getImgFlag() {
		return imgFlag;
	}
	public void setImgFlag(Integer imgFlag) {
		this.imgFlag = imgFlag;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
    
}
