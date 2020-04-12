package org.yi.spider.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {
	
	/**小说序号**/
    private Number novelNo;
    /**小说名**/
    private String novelName;
    /**发布日期**/
    private Date postDate;

	public Integer getNovelNoInteger() {
		return Integer.parseInt(novelNo.toString());
	}
	public Number getNovelNo() {
		return novelNo;
	}
	public void setNovelNo(Number novelNo) {
		this.novelNo = novelNo;
	}
	public String getNovelName() {
		return novelName;
	}
	public void setNovelName(String novelName) {
		this.novelName = novelName;
	}
	public Date getPostDate() {
		return postDate;
	}
	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}
    
}
