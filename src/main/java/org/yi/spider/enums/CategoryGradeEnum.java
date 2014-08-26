package org.yi.spider.enums;

public enum CategoryGradeEnum implements BaseEnum {
	
	TOP(0),
	SUB(1);
	
	private int value;
	
	private CategoryGradeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
