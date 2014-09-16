package org.yi.spider.model;

import java.util.List;

public class Category {

	private String id;
	
	private String name;
	
	private List<String> words;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	@Override
	public String toString() {
		return id + "," + name + "," + words.toString();
	}
	
}
