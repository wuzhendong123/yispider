package org.yi.spider.loader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.enums.ProgramEnum;
import org.yi.spider.utils.FileUtils;
import org.yi.spider.utils.LogUtils;
import org.yi.spider.utils.StringUtils;

import ch.qos.logback.core.joran.spi.JoranException;

public class InitCfgLoader {
	
	/**
	 * 
	 * <p>加载配置文件</p>
	 * @throws Exception 
	 */
	public static void load() throws Exception {
		loadLogback();
		loadSiteConfig();
		loadCollectConfig();
		loadCategories(); 
	}

	/**
	 * @throws IOException 
	 * @throws JoranException 
	 * 
	 * <p>加载logback配置</p>
	 * @param 
	 * @return void
	 * @throws
	 */
	private static void loadLogback() throws JoranException, IOException {
		//加载logback配置
		try {
			LogUtils.load(FileUtils.locateAbsolutePathFromClasspath("logback.xml").getAbsolutePath());
		} catch (IOException e) {
			throw new IOException("查找logback.xml失败！");
		} catch (JoranException e) {
			throw new JoranException(e.getMessage());
		}
	}
	
	/**
	 * 
	 * <p>加载本地站点初始化配置， 存储位置：<code>GlobalConfig.site</code></p>
	 * @throws ConfigurationException
	 */
	private static void loadSiteConfig() throws ConfigurationException {
		// 初始化设定文件
        try {
			GlobalConfig.site = new PropertiesConfiguration("site.ini");
		} catch (ConfigurationException e) {
			throw new ConfigurationException("读取配置文件出错，"+e.getMessage());
		}
        GlobalConfig.localSite.setSiteUrl(GlobalConfig.site.getString(ConfigKey.LOCAL_SITE_URL));
        GlobalConfig.localSite.setSiteName(GlobalConfig.site.getString(ConfigKey.LOCAL_SITE_NAME));
        GlobalConfig.localSite.setProgram(
        		ProgramEnum.parseEnum(GlobalConfig.site.getString(ConfigKey.LOCAL_PROGRAM)));
        GlobalConfig.localSite.setBasePath(GlobalConfig.site.getString(ConfigKey.BASE_PATH));
        GlobalConfig.localSite.setCharset(GlobalConfig.site.getString(ConfigKey.LOCAL_CHARSET));
        GlobalConfig.localSite.setTxtDir(GlobalConfig.site.getString(ConfigKey.TXT_DIR));
        GlobalConfig.localSite.setHtmlDir(GlobalConfig.site.getString(ConfigKey.HTML_DIR));
        GlobalConfig.localSite.setCoverDir(GlobalConfig.site.getString(ConfigKey.COVER_DIR));
        GlobalConfig.localSite.setStaticUrl(GlobalConfig.site.getString(ConfigKey.STATIC_URL));
	}
	
	/**
	 * 
	 * <p>加载采集配置</p>
	 * @throws ConfigurationException
	 */
	public static void loadCollectConfig() throws ConfigurationException {
		// 初始化设定文件
        try {
			GlobalConfig.collect = new PropertiesConfiguration("collect.ini");
		} catch (ConfigurationException e) {
			throw new ConfigurationException("读取配置文件出错，"+e.getMessage());
		}
	}
	
	/**
	 * 
	 * <p>加载小说类别</p>
	 * @throws Exception 
	 */
	private static void loadCategories() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
            		new FileInputStream(FileUtils.locateAbsolutePathFromClasspath("category.ini")), "UTF-8"));
            String line = null;
            String grade = "big";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (StringUtils.isBlank(line))
                    continue;
                if ("[small]".equalsIgnoreCase(line)) {
                    grade = "small";
                }
                int index = line.indexOf("=");
                if (index != -1) {
                    String key = line.substring(0, index).trim();
                    key = key.substring(0, key.indexOf("|"));
                    String value = line.substring(index + 1, line.length()).trim();
                    List<String> v = Arrays.asList(value.split(","));
                    if ("big".equalsIgnoreCase(grade)) {
                    	GlobalConfig.TOP_CATEGORY.put(key, v);
                    } else {
                    	GlobalConfig.SUB_CATEGORY.put(key, v);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
        	 throw new Exception("加载分类异常！");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    throw new IOException("IO异常！");
                }
            }
        }
    }
}
