package org.yi.spider.constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.yi.spider.enums.UserAgentEnum;
import org.yi.spider.model.SiteModel;
import org.yi.spider.model.UserModel;

/**
 * 
 * @ClassName: GlobalConfig
 * @Description: 存放全局静态变量， 程序初始化的时候会给变量赋值
 * @author QQ tkts@qq.com 
 * @date 2014年5月25日 下午11:29:10
 *
 */
public class GlobalConfig {
	
	/**
	 * 全局资源
	 */
	public static ResourceBundle bundle;
	/**
	 * 本地站点全局配置
	 */
	public static PropertiesConfiguration site;
	
	/**
	 * 程序全局配置
	 */
	public static PropertiesConfiguration collect;
	
	/**
	 * 程序全局配置
	 */
	public static PropertiesConfiguration config;
	
	/**
	 * 爬虫的user_agent
	 */
	public static UserAgentEnum USER_AGENT = UserAgentEnum.DEFAULT;
	
	/**
	 * 网站管理员， 章节入库需要关联的用户
	 */
	public static UserModel ADMIN = null;
	
	/**
	 * 本地站信息
	 */
	public static SiteModel localSite = new SiteModel();
	
	 /**
	  * 分类--大类
	  **/
    public static Map<String, List<String>> TOP_CATEGORY = new HashMap<String, List<String>>();
    /**
     * 分类--细类
     */
    public static Map<String, List<String>> SUB_CATEGORY = new HashMap<String, List<String>>();

	
}
