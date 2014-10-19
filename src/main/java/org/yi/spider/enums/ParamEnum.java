package org.yi.spider.enums;

public enum ParamEnum implements BaseEnum {
	
	HELP("help", false, "获取帮助信息"),
	VERSION("version", false, "获取软件版本信息"),
	
	MULTI("m", false, "同时采集指定多个配置文件进行采集"),
	
	ADD_NEWBOOK("a", false, "允许新书入库， 若命令中带此参数， 则配置文件中对应项无效"),
	
	COLLECT_All("ca", false, "采集所有规则中指定的小说"),
	COLLECT_ASSIGN("c", true, "采集指定目标站小说, 例如 -c 1,234,5678 或 -c 1-5"),
	
	REPAIR_ALL("ra", false, "修复所有目标站和本站均存在的小说"),
	REPAIR_ASSIGN("r", true, "修复指定小说中目标站和本站均存在的小说,例如 -r 1,234,5678 或 -r 1-5"),
	//修复参数， 为空表示修复所有， cover表示修复封面, intro表示简介, top表示大类， sub表示小类，key表示关键词, degree写作进度,etxt表示修复空章节内容，txt表示重新采集章节内容
	REPAIR_PARAMS("rp", true, "指定小说需要修复的部分，必须指定修复项，修复项包括：intro(简介)、degree(写作进度)、"
			+ "cover(封面图片)、top(小说大类)、sub(小说细类)， 必须和ra或r共用。 如 -ra -rp cover,top,sub,intro,degree,etxt,txt"),
	
	IMPORT("i", false, "导入小说，即只入库小说， 不采集章节"),
	
	REVERSE("reverse", false, "通过本地小说名反查目标站小说号， 规则中必须存在搜索页地址、搜索参数、获得目标小说，此参数只能和-c、-r配合使用， 和-ca、-ra一同使用时无效"),
	
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
