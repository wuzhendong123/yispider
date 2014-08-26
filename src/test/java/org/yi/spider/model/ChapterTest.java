package org.yi.spider.model;

import org.junit.Test;
import org.yi.spider.entity.ChapterEntity;


public class ChapterTest {

	@Test
	public void testClone() {
		ChapterEntity c = new ChapterEntity();
		c.setNovelName("novel-name");
		c.setNovelNo(1);
		c.setChapterName("chapter-name");
		c.setChapterNo(11);
		c.setSize(1213);
		System.out.println(c);
		ChapterEntity c2 = null;
		try {
			c2 = c.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		System.out.println(c2);
		System.out.println(c2.getNovelName());
		System.out.println(c2.getNovelNo());
		System.out.println(c2.getChapterName());
		System.out.println(c2.getChapterNo());
		System.out.println(c2.getSize());
	}
	
}
