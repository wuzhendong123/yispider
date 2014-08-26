package org.yi.spider.enums;

public enum ProgramEnum implements BaseEnum {
	
	YIDU("yidu"),
	JIEQI("jieqi");
	
	private String name;
	
	private ProgramEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * <p>根据传入的值获取对应的枚举类型</p>
	 * @param @param vlaue
	 * @param @return
	 * @return UserAgent
	 * @throws
	 */
	public static ProgramEnum parseEnum(String vlaue) {
		for(ProgramEnum e:values()) {
			if(vlaue.equals(e.getName())) {
				return e;
			}
		}
		return YIDU;
	}
	

}
