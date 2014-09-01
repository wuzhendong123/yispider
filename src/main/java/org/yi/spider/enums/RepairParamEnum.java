package org.yi.spider.enums;

public enum RepairParamEnum implements BaseEnum { 
	
	COVER("cover", "修复封面"),
	TXT("txt", "章节内容"),
	INTRO("intro", "简介"),
	TOP("top", "大类"),
	SUB("sub", "小类"),
	KEYWORDS("keywords", "关键词"),
	DEGREE("degree", "写作进度");
	
	private String value;
	
	private String desc;
	
	private RepairParamEnum(String value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	/**
	 * 
	 * <p>根据传入的值获取对应的枚举类型</p>
	 * @param vlaue
	 * @return
	 */
	public static RepairParamEnum parseEnum(String vlaue) {
		for(RepairParamEnum e:values()) {
			if(vlaue.equals(e.getValue())) {
				return e;
			}
		}
		return null;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
