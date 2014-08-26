package org.yi.spider.cfg;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yi.spider.utils.PropertiesUtils;

public class ConfigurationTest {

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
	public void testIniCfg() {
		new HierarchicalConfiguration();
		try {
			HierarchicalINIConfiguration cfg = PropertiesUtils.loadIni("category.ini");
			System.out.println(cfg.getString(""));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

}
