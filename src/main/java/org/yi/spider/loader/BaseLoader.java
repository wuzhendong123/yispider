package org.yi.spider.loader;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.enums.UserAgentEnum;

public class BaseLoader {
	
	private static Logger logger = LoggerFactory.getLogger(BaseLoader.class);
	
	/**
	 * 
	 * <p>加载初始化参数</p>
	 * @param 
	 * @return void
	 * @throws
	 */
	public static void loadInitParam(){
		GlobalConfig.USER_AGENT = UserAgentEnum.parseEnum(GlobalConfig.collect.getString("user_agent"));
	}
	
	/**
	 * 
	 * <p>加载分类信息</p>
	 * @param @throws ConfigurationException
	 * @return void
	 * @throws
	 */
	public void loadCategory() throws ConfigurationException {
		logger.debug("开始加载分类目录...");
		//PropertiesUtils.loadIni("category.ini", "UTF-8");
	}

}
