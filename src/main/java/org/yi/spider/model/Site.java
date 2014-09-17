package org.yi.spider.model;

import java.util.List;

import org.yi.spider.enums.ProgramEnum;

public class Site {

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
	private String txtFile;
	
	/**
	 * 小说html文件存放路径
	 */
	private String htmlFile;
	
	/**
	 * 小说封面存放路径
	 */
	private String coverDir;
	
	/**
	 * 网站物理路径
	 */
	private String basePath;

	/**
	 * 小说模版配置
	 */
	private Template template;
	
	/**
	 * 网站版权信息
	 */
	private String copyright;
	/**
	 * 网站描述
	 */
	private String description;
	/**
	 * 网站关键字
	 */
	private String keywords;
	
	/**
	 * 需要采集的章节列表， 目标站必须有值
	 */
	private List<String> chapterList;
	/**
	 * 是否开启拼音字段， 只对杰奇有效， 开启为1， 否则为0
	 */
	private Integer usePinyin;
	
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

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return keywords;
	}

	public String getTxtFile() {
		return txtFile;
	}

	public void setTxtFile(String txtFile) {
		this.txtFile = txtFile;
	}

	public String getHtmlFile() {
		return htmlFile;
	}

	public void setHtmlFile(String htmlFile) {
		this.htmlFile = htmlFile;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Integer getUsePinyin() {
		return usePinyin;
	}

	public void setUsePinyin(Integer usePinyin) {
		this.usePinyin = usePinyin;
	}

}
