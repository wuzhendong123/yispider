package org.yi.spider.model;

public class Template {
	
	/**
	 * 首页模版路径
	 */
	private String index;
	/**
	 * 列表页
	 */
	private String list;
	/**
	 * 排行榜页
	 */
	private String top;
	/**
	 * 小说信息页
	 */
	private String info;
	
	/**
	 * 小说信息页url
	 */
	private String infoURL;
	
	/**
	 * 章节列表页
	 */
	private String chapter;
	
	/**
	 * 目录页每行显示多少章节
	 */
	private Integer rowSize;
	/**
	 * 章节目录也网址
	 */
	private String chapterURL;
	/**
	 * 阅读页
	 */
	private String reader;
	
	/**
	 * 阅读页网址
	 */
	private String readerURL;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getTop() {
		return top;
	}

	public void setTop(String top) {
		this.top = top;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public String getReader() {
		return reader;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public String getChapterURL() {
		return chapterURL;
	}

	public void setChapterURL(String chapterURL) {
		this.chapterURL = chapterURL;
	}

	public String getReaderURL() {
		return readerURL;
	}

	public void setReaderURL(String readerURL) {
		this.readerURL = readerURL;
	}

	public Integer getRowSize() {
		return rowSize;
	}

	public void setRowSize(Integer rowSize) {
		this.rowSize = rowSize;
	}

	public String getInfoURL() {
		return infoURL;
	}

	public void setInfoURL(String infoURL) {
		this.infoURL = infoURL;
	}
	
}
