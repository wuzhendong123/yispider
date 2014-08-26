package org.yi.spider.entity;


public class ChapterEntity extends BaseEntity implements Cloneable{

	/**章节序号**/
	private Integer chapterNo;
	/**章节名**/
    private String chapterName;
    /**章节类型， 正文还是分卷， 在易读中0表示正文， 1表示分卷**/
    private Short chapterType;
    /**章节字数**/
    private Integer size;
    /**章节排序**/
    private Integer chapterOrder;
    
    public ChapterEntity clone() throws CloneNotSupportedException {  
    	ChapterEntity cloned = (ChapterEntity) super.clone();  
    	return cloned;  
    }  
    
	public Integer getChapterNo() {
		return chapterNo;
	}
	public void setChapterNo(Integer chapterNo) {
		this.chapterNo = chapterNo;
	}
	public String getChapterName() {
		return chapterName;
	}
	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}
	public Short getChapterType() {
		return chapterType;
	}
	public void setChapterType(Short chapterType) {
		this.chapterType = chapterType;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Integer getChapterOrder() {
		return chapterOrder;
	}
	public void setChapterOrder(Integer chapterOrder) {
		this.chapterOrder = chapterOrder;
	}

}
