package org.yi.spider.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yi.spider.utils.FileUtils;

public class FileUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetBasePathString() {
		System.out.println(FileUtils.getBasePath("D:/workspace/99_website/spider/lib/c3p0-0.9.2.1.jar"));
	}
	
	@Test
	public void testGetBaseNameString() {
		System.out.println(FileUtils.getFileName("D:/workspace/99_website/spider/lib/c3p0-0.9.2.1.jar"));
	}

}
