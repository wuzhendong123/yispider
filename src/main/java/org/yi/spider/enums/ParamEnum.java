package org.yi.spider.enums;

public enum ParamEnum implements BaseEnum {
	
	HELP("help", false, "获取帮助信息"),
	VERSION("version", false, "获取软件版本信息"),
	
	MULTI("m", false, "同时采集指定多个配置文件进行采集"),
	
	COLLECT_All("ca", false, "采集所有目标站小说"),
	COLLECT_ASSIGN("c", true, "采集指定目标站小说, 例如 -c 1,234,5678 或 -c 1-5"),
	
	REPAIR_ALL("ra", false, "修复说有目标站和本站均存在的小说"),
	REPAIR_ASSIGN("r", true, "修复指定小说中目标站和本站均存在的小说,例如 -r 1,234,5678 或 -r 1-5"),
	//修复参数， 为空表示修复所有， cover表示修复封面, txt表示修复章节内容, intro表示简介, top表示大类， sub表示小类，key表示关键词, degree写作进度
	REPAIR_ASSIGN_PARAMS("rp", true, "需要修复的内容， 必须和-r或-ra一起使用才有效， 如 -r -rp cover,txt,intro,degree"),
	
	REPAIR_PARAMS("i", true, "指定小说需要修复的部分， 指令包括：intro(简介)、degree(写作进度)、cover(封面图片)、category(小说类别)， 必须和ra或r公用。"),
	REPAIR_SITE("s", true, "指定修复数据目标站， 即从哪些网站对数据进行修复"),
	
	//默认-c 1,2,3  、 -r 1,2,3 的小说号为目标站小说号， -l表示小说号为本地站小说号
	
	
	RULE_FILE("rule", "file", "指定采集使用的规则文件");
	
	private String name;
	
	private String valueName;
	
	private boolean hasArgs;
	
	private String desc;
	
	private ParamEnum(String name, boolean hasArgs, String desc){
		this.name = name;
		this.hasArgs = hasArgs;
		this.desc = desc;
	}
	
	private ParamEnum(String name, String valueName, String desc){
		this.name = name;
		this.valueName = valueName;
		this.desc = desc;
	}
	
	/**
	 * 
	 * <p>根据传入的值获取对应的枚举类型</p>
	 * @param vlaue
	 * @return
	 */
	public static ParamEnum parseEnum(String vlaue) {
		for(ParamEnum e:values()) {
			if(vlaue.equals(e.getName())) {
				return e;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isHasArgs() {
		return hasArgs;
	}

	public void setHasArgs(boolean hasArgs) {
		this.hasArgs = hasArgs;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
