package org.yi.spider.loader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.enums.ProgramEnum;
import org.yi.spider.model.Category;
import org.yi.spider.model.DuoYinZi;
import org.yi.spider.model.Template;
import org.yi.spider.utils.FileUtils;
import org.yi.spider.utils.LogUtils;
import org.yi.spider.utils.PropertiesUtils;

import ch.qos.logback.core.joran.spi.JoranException;

public class InitCfgLoader {
	
	/**
	 * 
	 * <p>加载配置文件</p>
	 * @throws Exception 
	 */
	public static void load() throws Exception {
		//加载日志组件
		loadLogback();
		//加载采集配置
		loadCollectConfig();
		//加载分类配置
		loadCategories();
		//加载站点配置公共信息
		loadSiteConfig();
		//加载多音字配置
		loadDuoYinZi();
	}

	private static void loadDuoYinZi() throws ConfigurationException {
		List<String> list = FileUtils.readFile2List("duoyinzi", "utf-8");
		for(String ss : list){
			GlobalConfig.duoyin.add(new DuoYinZi(ss.split("=")[0],ss.split("=")[1]));
		}
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
	 * <p>加载采集配置</p>
	 * @throws ConfigurationException
	 */
	public static void loadCollectConfig() throws ConfigurationException {
		// 初始化设定文件
        try {
			GlobalConfig.collect = PropertiesUtils.load("collect.ini", "utf-8");
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
            int grade = 1;
            while ((line = reader.readLine()) != null) {
            	if(StringUtils.isNotBlank(line) && line.indexOf("]")>0) {
	            	if ("[small]".equalsIgnoreCase(line)) {
	            		grade = 0;
	            	} else {
	            		grade = 1;
	            	}
            	}
                String[] s = line.split("\\|");
                if (s.length == 3) {
                	Category c = new Category();
                	c.setId(s[0]);
                	c.setName(s[1]);
                	c.setWords(Arrays.asList(s[2].split(",")));
                	if (grade == 0) {
                		GlobalConfig.SUB_CATEGORY.add(c);
                    } else {
                    	GlobalConfig.TOP_CATEGORY.add(c);
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
	
	/**
	 * 
	 * <p>加载本地站点初始化配置， 存储位置：<code>GlobalConfig.site</code></p>
	 * @throws ConfigurationException
	 */
	private static void loadSiteConfig() throws ConfigurationException {
		// 初始化设定文件
        try {
			GlobalConfig.site = PropertiesUtils.load("site.ini", "utf-8");
		} catch (ConfigurationException e) {
			throw new ConfigurationException("读取配置文件出错，"+e.getMessage());
		}
        GlobalConfig.localSite.setSiteUrl(GlobalConfig.site.getString(ConfigKey.LOCAL_SITE_URL));
        GlobalConfig.localSite.setSiteName(GlobalConfig.site.getString(ConfigKey.LOCAL_SITE_NAME));
        GlobalConfig.localSite.setProgram(
        		ProgramEnum.parseEnum(GlobalConfig.site.getString(ConfigKey.LOCAL_PROGRAM)));
        GlobalConfig.localSite.setBasePath(GlobalConfig.site.getString(ConfigKey.BASE_PATH));
        GlobalConfig.localSite.setCharset(GlobalConfig.site.getString(ConfigKey.LOCAL_CHARSET));
        GlobalConfig.localSite.setTxtFile(GlobalConfig.site.getString(ConfigKey.TXT_FILE));
        GlobalConfig.localSite.setHtmlFile(GlobalConfig.site.getString(ConfigKey.HTML_FILE));
        GlobalConfig.localSite.setCoverDir(GlobalConfig.site.getString(ConfigKey.COVER_DIR));
        GlobalConfig.localSite.setUsePinyin(GlobalConfig.site.getInteger(ConfigKey.USE_PINYIN, 0));
        
        //加载模版相关配置
        Template tp = new Template();
        tp.setIndex(GlobalConfig.site.getString(ConfigKey.TEMPLATE_INDEX));
        tp.setList(GlobalConfig.site.getString(ConfigKey.TEMPLATE_LIST));
        tp.setTop(GlobalConfig.site.getString(ConfigKey.TEMPLATE_TOP));
        tp.setInfo(GlobalConfig.site.getString(ConfigKey.TEMPLATE_INFO));
        tp.setInfoURL(GlobalConfig.site.getString(ConfigKey.URL_INFO));
        tp.setChapter(GlobalConfig.site.getString(ConfigKey.TEMPLATE_CHAPTER));
        tp.setRowSize(GlobalConfig.site.getInt(ConfigKey.CHAPTER_ROW_SIZE, 4));
        tp.setChapterURL(GlobalConfig.site.getString(ConfigKey.URL_CHAPTER));
        tp.setReader(GlobalConfig.site.getString(ConfigKey.TEMPLATE_READER));
        tp.setReaderURL(GlobalConfig.site.getString(ConfigKey.URL_READER));
        GlobalConfig.localSite.setTemplate(tp);
	}

}
