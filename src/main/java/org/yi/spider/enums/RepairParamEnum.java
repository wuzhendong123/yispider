package org.yi.spider.enums;

import java.util.ArrayList;
import java.util.List;

public enum RepairParamEnum implements BaseEnum { 
	
	COVER("cover", "修复封面"),
	INTRO("intro", "简介"),
	TOP("top", "大类"),
	SUB("sub", "小类"),
	KEYWORDS("keywords", "关键词"),
	DEGREE("degree", "写作进度"),
	ETXT("etxt","空章节内容"),
	TXT("txt","章节内容");
	
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
	
	public static List<String> toList() {
		List<String> list = new ArrayList<String>();
		for(RepairParamEnum e:values()) {
			list.add(e.getValue());
		}
		return list;
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
