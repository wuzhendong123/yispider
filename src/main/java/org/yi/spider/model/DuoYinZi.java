package org.yi.spider.model;

public class DuoYinZi {

	private String name;
	
	private String pinyin;

	public DuoYinZi(String name, String pinyin) {
		this.name = name;
		this.pinyin = pinyin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	
}
