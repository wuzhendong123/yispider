package org.yi.spider.constants;

/**
 * 
 * @ClassName: ConfigKey
 * @Description: 存放配置文件key值
 * @author QQ tkts@qq.com
 *
 */
public class ConfigKey {
	
	/******************************站点信息相关配置**********************************/
	
	/**本地站地址**/
	public static final String LOCAL_SITE_URL = "local_site_url";
	
	/**本地站名称**/
	public static final String LOCAL_SITE_NAME = "local_site_name";
	
	/**网站使用的程序**/
    public static final String LOCAL_PROGRAM = "local_program";
    
    public static final String LOCAL_CHARSET = "charset";
    
    /**网站根路径(绝对地址)**/
    public static final String BASE_PATH = "base_path";
    
    /**网站txt章节文件存放路径(相对地址)**/
    public static final String TXT_DIR = "txt_dir";
    
    /**网站封面文件存放路径(相对地址)**/
    public static final String COVER_DIR = "cover_dir";
    
    /**网站静态HTML文件存放路径(相对地址)**/
    public static final String HTML_DIR = "html_dir";
    
    /**网站内容页静态化地址**/
    public static final String STATIC_URL = "static_url";

    /******************************采集规则相关配置**********************************/
	/**规则文件名**/
    public static final String RULE_NAME = "rule_name";
    
    /**爬虫伪装**/
    public static final String USER_AGENT = "user_agent";
    
    /**新书是否入库**/
    public static final String ADD_NEW_BOOK = "add_new_book";
    
    /**是否采集图片章节**/
    public static final String COLLECT_PICTURE_CHAPTER = "collect_picture_chapter";
    
    /**完本标识**/
    public static final String FULL_FLAG = "full_flag";
    
    /**默认分类**/
    public static final String DEFAULT_CATEGORY = "default_category";
    
    /**是否生成HTML页面**/
    public static final String CREATE_HTML = "create_html";
    
    /**循环间隔, 小于0表示无间隔**/
    public static final String INTERVAL = "interval";
    
    /**连接目标站超时时间**/
    public static final String CONNECTION_TIMEOUT = "connection_timeout";
    
    
    /***********************************程序相关配置***********************************/
    
    public static final String MAX_TAB_SIZE = "max_tab_size";
}
