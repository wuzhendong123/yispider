package org.yi.spider.model;

import java.util.List;

import org.yi.spider.enums.ProgramEnum;

public class SiteModel {

	/**
	 * 网址
	 */
	private String siteUrl;
	
	/**
	 * 网站名称
	 */
	private String siteName;
	
	/**
	 * 网站使用的程序
	 */
	private ProgramEnum program;
	
	/**
	 * 网站编码
	 */
	private String charset;
	
	/**
	 * 小说txt文件存放路径
	 */
	private String txtDir;
	
	/**
	 * 小说html文件存放路径
	 */
	private String htmlDir;
	
	/**
	 * 小说封面存放路径
	 */
	private String coverDir;
	
	/**
	 * 伪静态内容页URL
	 */
	private String staticUrl;
	
	/**
	 * 网站物理路径
	 */
	private String basePath;

	/**
	 * 需要采集的章节列表， 目标站必须有值
	 */
	private List<String> chapterList;
	
	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public ProgramEnum getProgram() {
		return program;
	}

	public void setProgram(ProgramEnum program) {
		this.program = program;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getTxtDir() {
		return txtDir;
	}

	public void setTxtDir(String txtDir) {
		this.txtDir = txtDir;
	}

	public String getHtmlDir() {
		return htmlDir;
	}

	public void setHtmlDir(String htmlDir) {
		this.htmlDir = htmlDir;
	}

	public String getCoverDir() {
		return coverDir;
	}

	public void setCoverDir(String coverDir) {
		this.coverDir = coverDir;
	}

	public List<String> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<String> chapterList) {
		this.chapterList = chapterList;
	}

	public String getStaticUrl() {
		return staticUrl;
	}

	public void setStaticUrl(String staticUrl) {
		this.staticUrl = staticUrl;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}
