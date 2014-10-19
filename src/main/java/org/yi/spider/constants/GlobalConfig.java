package org.yi.spider.constants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.yi.spider.enums.UserAgentEnum;
import org.yi.spider.model.Category;
import org.yi.spider.model.DuoYinZi;
import org.yi.spider.model.Site;
import org.yi.spider.model.User;

/**
 * 
 * @ClassName: GlobalConfig
 * @Description: 存放全局静态变量， 程序初始化的时候会给变量赋值
 * @author QQ  
 * @date 2014年5月25日 下午11:29:10
 *
 */
public class GlobalConfig {
	
	/**
	 * 本地站点全局配置
	 */
	public static PropertiesConfiguration site;
	
	/**
	 * 程序全局配置
	 */
	public static PropertiesConfiguration collect;
	
	public static List<DuoYinZi> duoyin = new ArrayList<DuoYinZi>(); 
	
	/**
	 * 爬虫的user_agent
	 */
	public static UserAgentEnum USER_AGENT = UserAgentEnum.DEFAULT;
	
	/**
	 * 网站管理员， 章节入库需要关联的用户
	 */
	public static User ADMIN = null;
	
	/**
	 * 本地站信息
	 */
	public static Site localSite = new Site();
	
	/**
	  * 分类--大类
	  **/
    public static List<Category> TOP_CATEGORY = new ArrayList<Category>();
    /**
     * 分类--细类
     */
    public static List<Category> SUB_CATEGORY = new ArrayList<Category>();
    
    /**
     * 是否终止采集器进程
     */
    public static volatile boolean SHUTDOWN = false;

}
