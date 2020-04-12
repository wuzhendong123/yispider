package org.yi.spider.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.Constants;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.enums.CategoryGradeEnum;
import org.yi.spider.enums.ProgramEnum;
import org.yi.spider.exception.BaseException;
import org.yi.spider.factory.impl.ServiceFactory;
import org.yi.spider.model.Category;
import org.yi.spider.model.CollectParam;
import org.yi.spider.model.Rule;
import org.yi.spider.service.INovelService;
import org.yi.spider.utils.HttpUtils;
import org.yi.spider.utils.Native2AsciiUtils;
import org.yi.spider.utils.PatternUtils;
import org.yi.spider.utils.ScriptUtils;
import org.yi.spider.utils.StringUtils;

/**
 * 
 * @ClassName: ParseHelper
 * @Description: 解析帮助类
 * @author QQ 
 *
 */
public class ParseHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(ParseHelper.class);
	
	
	/**
	 * 
	 * <p>根据正则表达式解析源码， 获取需要的内容</p>
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static String get(String source, Rule pattern) {
		return StringUtils.trimToEmpty(PatternUtils.getValue(source, pattern));
	}
	
	/**
	 * 
	 * <p>获取目标URL的源文件</p>
	 * @param httpClient
	 * @param cpm
	 * @param destURL
	 * @return
	 * @throws IOException 
	 */
	public static String getSource(CollectParam cpm, String destURL) throws IOException {
		CloseableHttpClient httpClient = HttpUtils.buildClient(Constants.TEST_TIMEOUT);
		String source = getSource(httpClient, cpm, destURL);
		httpClient.close();
		return source;
	}
	
	/**
	 * 
	 * <p>获取目标URL的源文件</p>
	 * @param httpClient
	 * @param cpm
	 * @param destURL
	 * @return
	 */
	public static String getSource(CloseableHttpClient httpClient, CollectParam cpm, String destURL) {
		logger.debug("获取源文件， 目标地址： " + destURL);
		//小说信息页源码
		String content = "";
		try {
			content = HttpHelper.getContent(httpClient, destURL, cpm.getRemoteSite().getCharset());
		} catch (Exception e) {
			throw new BaseException("小说名为空, 目标链接：" + destURL);
		}
		if (StringUtils.isBlank(content)) {
			throw new BaseException("小说名为空, 目标链接：" + destURL);
		}
		return content;
	}
	
	/**
	 * 获取规则版本
	 * @param cpm
	 * @return
	 */
	public static String getRuleVersion(CollectParam cpm) {
		return cpm.getRuleMap().get(Rule.RegexNamePattern.RULE_VERSION).getPattern();
	}
	
	/**
	 * 获取目标站名称
	 * @param cpm
	 * @return
	 */
	public static String getSiteName(CollectParam cpm) {
		return cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_NAME).getPattern();
	}
	
	/**
	 * 获取目标站编码
	 * @param cpm
	 * @return
	 */
	public static String getSiteCharset(CollectParam cpm) {
		return cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_CHARSET).getPattern();
	}
	
	/**
	 * 获取目标站url
	 * @param cpm
	 * @return
	 */
	public static String getSiteUrl(CollectParam cpm) {
		return cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_URL).getPattern();
	}
	
	/**
	 * 获取小说名称
	 * @param cpm
	 * @return
	 */
	public static String getNovelName(String infoSource, CollectParam cpm) {
		return StringUtils.trimToEmpty(
				PatternUtils.getValue(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_NAME)));
	}
	
	/**
	 * 获取小说作者
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getNovelAuthor(String infoSource, CollectParam cpm){
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_AUTHOR));
	}

	/**
	 * 
	 * <p>获取目标站小说信息页地址, 规则中指定， 通过替换、计算获取</p>
	 * @param assignURL
	 * @param novelNo
	 * @param cpm 
	 * @return
	 * @throws ScriptException 
	 */
	public static String getAssignURL(String assignURL, String novelNo, CollectParam cpm) throws ScriptException {
		
		String result  = assignURL;
		String novelNo2 = "";
		//如果获得真实小说编号不为空， 则通过随机获取的小说号获取真实小说编号
		if(cpm != null && StringUtils.isNotBlank(cpm.getRuleMap().get(Rule.RegexNamePattern.NOVELLIST_GETNOVELKEY2).getPattern())) {
			novelNo2 = getNovelNo2(cpm, novelNo);
			result = result.replace("{NovelKey2}", novelNo2);
		}
		
		// 小说信息页地址
		result = result.replace("{NovelKey}", novelNo)
								 .replace("{NovelPubKey}", novelNo)
								 .replace("NovelKey", novelNo);
		
		//如果替换之后依旧存在{则表示存在需要计算的表达式
		if (result.indexOf("{") > 0) {
			//获取计算表达式
			String express = result.substring(result.indexOf("{") + 1,
					result.indexOf("}"));
			String novelKey = String.valueOf(ScriptUtils.evalInt(express, null));
			//使用结算结果替换计算表达式
			result = result.replaceAll("\\{.*\\}", novelKey);
		}
		return result;
	}
	
	/**
	 * 
	 * <p>获取小说所属分类对象</p>
	 * @param category	分类名
	 * @param top		分类级别(大类、小类)
	 * @return			分类对应的号
	 */
    private static Category getCategoryObj(String catStr, CategoryGradeEnum top) {
    	Category category = null;
        if (StringUtils.isNotBlank(catStr)) {
        	List<Category> cats = new ArrayList<Category>();
            if (top == CategoryGradeEnum.TOP) {
                cats = GlobalConfig.TOP_CATEGORY;
            } else {
                cats = GlobalConfig.SUB_CATEGORY;
            }
            for(Category c : cats){
            	if(c.getWords().contains(catStr)){
        			category = c;
        			break;
            	}
            }
        }
        return category;
    }
    
    /**
	 * 
	 * <p>获取小说所属分类</p>
	 * @param category	分类名
	 * @param top		分类级别(大类、小类)
	 * @return			分类对应的号
	 */
    public static Integer getCategory(String catStr, CategoryGradeEnum top) {
    	Category c = getCategoryObj(catStr, top);
    	if(c == null) {
    		return GlobalConfig.collect.getInt(ConfigKey.DEFAULT_CATEGORY, 10);
    	} else {
    		return Integer.parseInt(c.getId());
    	}
    }
    
    /**
     * 
     * <p>获取封面标识并下载封面图片</p>
     * @param novel
     * @param infoSource
     * @param cpm
     * @return
     * @throws Exception 
     */
    public static Integer getNovelCover(NovelEntity novel, String infoSource, CollectParam cpm) throws Exception {
		Integer imgFlag;
		String novelCover = getNovelCoverURL(infoSource, cpm);
        if (novelCover == null || novelCover.isEmpty()) {
            imgFlag = 0;
        } else {
            String suffix = novelCover.substring(novelCover.lastIndexOf("."), novelCover.length());
            novelCover = StringUtils.getFullUrl(getSiteUrl(cpm), novelCover);
            FileHelper.downImage(novelCover, novel, suffix);
            imgFlag = StringHelper.getImgFlag(novelCover);
        }
		return imgFlag;
	}

    /**
     * 获取小说封面图片地址
     * @param infoSource
     * @param cpm
     * @return
     */
	public static String getNovelCoverURL(String infoSource, CollectParam cpm) {
		String novelCover = PatternUtils.getValue(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_COVER));
        if (StringUtils.isBlank(novelCover)) {
            String novelDefaultCoverUrl = cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_DEFAULT_COVER_URL).getPattern();
            if (novelDefaultCoverUrl != null && !novelDefaultCoverUrl.isEmpty()) {
                novelCover = novelDefaultCoverUrl;
            }
        }
		return novelCover;
	}

    /**
     * 获取小说信息页地址
     * @param cpm
     * @param novelNo
     * @return
     * @throws ScriptException 
     */
	public static String getInfoRUL(CollectParam cpm, String novelNo) throws ScriptException {
		String infoURL = "";
		String assignURL = cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_URL).getPattern();
		if(StringUtils.isNotBlank(assignURL)){
			infoURL = ParseHelper.getAssignURL(assignURL, novelNo, null);
		}
		return infoURL;
	}

	/**
	 * 获取小说大类
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getTopCategory(String infoSource, CollectParam cpm) {
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.LAGER_SORT));
	}
	
	/**
	 * 获取小说细类
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getSubCategory(String infoSource, CollectParam cpm) {
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.SMALL_SORT));
	}

	/**
	 * 获取小说简介
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getNovelIntro(String infoSource, CollectParam cpm) {
		String novelIntro = ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_INTRO));
        if(StringUtils.isNotBlank(novelIntro)) {
	        novelIntro = StringUtils.replaceHtml(novelIntro);
	        novelIntro = StringUtils.removeBlankLine(novelIntro);
        }
        return novelIntro;
	}

	/**
	 * 获取写作关键词
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getNovelKeywrods(String infoSource, CollectParam cpm) {
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_KEYWORD));
	}
	
	/**
	 * 获取写作进度
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getNovelDegree(String infoSource, CollectParam cpm) {
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_DEGREE));
	}
	
	/**
	 * 获取小说章节目录页地址
	 * @param infoSource
	 * @param novelNo
	 * @param cpm
	 * @return
	 * @throws Exception 
	 */
	public static String getNovelMenuURL(String infoSource, String novelNo, CollectParam cpm) throws Exception{
		 // 可能是/book/1.html、book/1.html、http://www.foo.com/book/1.html等
        String novelPubKey = ParseHelper.get(infoSource, 
        		cpm.getRuleMap().get(Rule.RegexNamePattern.NOVELINFO_GETNOVELPUBKEY));
        // novelPubKey为空则说明目录页地址不是通过页面获取， 而是在规则中指定好了
        if(StringUtils.isBlank(novelPubKey)){
        	novelPubKey = cpm.getRuleMap().get(Rule.RegexNamePattern.PUBINDEX_URL).getPattern();
        	if(StringUtils.isBlank(novelPubKey)){
        		throw new BaseException("无法从页面获取目录页地址， 需要在规则中PubIndexUrl项指定目录页地址");
        	}
        	novelPubKey = ParseHelper.getAssignURL(novelPubKey, novelNo, null);
        }
        
        // 小说目录页地址 http://a/b/c.html
        if(StringUtils.isBlank(novelPubKey)) {
        	return novelPubKey;
        }
        return StringUtils.getFullUrl(cpm.getRemoteSite().getSiteUrl(), novelPubKey);
	}
	
	/**
	 * 获取小说信息页额外信息
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getNovelInfoExtra(String infoSource, CollectParam cpm) {
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_INFO_EXTRA));
	}
	
	/**
	 * 获取小说目录页源码
	 * @param novelPubKeyURL
	 * @param cpm
	 * @return
	 * @throws Exception
	 */
	public static String getChapterListSource(String novelPubKeyURL, CollectParam cpm) throws Exception{
		//获取整个章节列表页源码
		CloseableHttpClient httpClient = HttpUtils.buildClient(Constants.TEST_TIMEOUT);
		String menuSource = HttpHelper.getContent(httpClient, novelPubKeyURL, cpm.getRemoteSite().getCharset());
		//过滤一些干扰性的源码 -- 如果设置了目录范围， 则只取需要范围内的源码
        Rule pubChapterRegion = cpm.getRuleMap().get(Rule.RegexNamePattern.PUBCHAPTER_REGION);
        if(pubChapterRegion != null && StringUtils.isNotBlank(pubChapterRegion.getPattern())){
        	menuSource = ParseHelper.get(menuSource, pubChapterRegion);
        }
        httpClient.close();
        return menuSource;
	}
    
	/**
	 * 获取所有章节名称
	 * @param menuSource
	 * @param cpm
	 * @return
	 */
	public static List<String> getChapterNameList(String menuSource, CollectParam cpm){
		return PatternUtils.getValues(menuSource,
        		cpm.getRuleMap().get(Rule.RegexNamePattern.PUBCHAPTER_NAME));
	}
	
	/**
	 * 获取所有章节编号
	 * @param menuSource
	 * @param cpm
	 * @return
	 */
	public static Map<String,String> getChapterNoList(String menuSource, CollectParam cpm){

		return PatternUtils.getOrderValues(menuSource,
        		cpm.getRuleMap().get(Rule.RegexNamePattern.PUBCHAPTER_GETCHAPTERKEY),cpm.getRuleMap().get(Rule.RegexNamePattern.CHAPTER_NO));
	}
	
	/**
	 * 获取章节地址
	 * @param novelPubKeyURL
	 * @param novelNo
	 * @param cno
	 * @param cpm
	 * @return
	 * @throws ScriptException 
	 */
	public static String getChapterURL(String novelPubKeyURL, String novelNo, String cno, CollectParam cpm) throws Exception {
		String chapterURL = cpm.getRuleMap().get(Rule.RegexNamePattern.PUBCONTENT_URL).getPattern();
		return StringHelper.getRemoteChapterUrl(chapterURL, novelPubKeyURL, novelNo, cno.trim(), cpm);
	}
	
	/**
	 * 获取章节页源码
	 * @param chapterURL
	 * @param cpm
	 * @return
	 * @throws Exception
	 */
	public static String getChapterSource(String chapterURL, CollectParam cpm) throws Exception{
		CloseableHttpClient httpClient = HttpUtils.buildClient(Constants.TEST_TIMEOUT);
		chapterURL = StringUtils.getFullUrl(getSiteUrl(cpm), chapterURL);
		String source = HttpHelper.getContent(httpClient, chapterURL, cpm.getRemoteSite().getCharset());
		httpClient.close();
		return source;
	}
	
	/**
	 * 获取章节内容
	 * @param chapterSource
	 * @param cpm
	 * @return
	 */
	public static String getChapterContent(String chapterSource, CollectParam cpm){
		String chapterContent = ParseHelper.get(chapterSource, cpm.getRuleMap().get(Rule.RegexNamePattern.PUBCONTENT_TEXT));
		Rule textAscii = cpm.getRuleMap().get(Rule.RegexNamePattern.PUBCONTENT_TEXT_ASCII);
		if(textAscii != null 
				&& StringUtils.isNotBlank(textAscii.getPattern())
				&& "true".equalsIgnoreCase(textAscii.getPattern())) {
			chapterContent = Native2AsciiUtils.ascii2Native(chapterContent);
			chapterContent = PatternUtils.filter(chapterContent, cpm.getRuleMap().get(Rule.RegexNamePattern.PUBCONTENT_TEXT));
		}
		chapterContent = StringUtils.removeBlankLine(chapterContent);
	    chapterContent = StringUtils.replaceHtml(chapterContent);
	    return chapterContent;
	}
	
	/**
	 * 获取搜索结果页源码
	 * @param client
	 * @param novelNo
	 * @param cpm
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getSearchContent(CloseableHttpClient client, String novelNo, CollectParam cpm) throws Exception {
		String program = (GlobalConfig.localSite == null || GlobalConfig.localSite.getProgram() == null) 
				? ProgramEnum.YIDU.getName() : GlobalConfig.localSite.getProgram().getName();
		INovelService novelService = new ServiceFactory().createNovelService(program);
		NovelEntity novel = null;
		//novelNo为空说明程序入口是规则测试， 通过测试时指定的小说名获取小说对象
		Boolean test = false;
		if(StringUtils.isNotBlank(novelNo)) {
			novel = novelService.get(novelNo);
		} else {
			test = true;
			novel = novelService.find(cpm.getRuleMap().get(Rule.RegexNamePattern.TESTSEARCH_NOVELNAME).getPattern());
		}
		Map<String, Object> result = null;
		if(novel != null) {
			String searchURL = cpm.getRuleMap().get(Rule.RegexNamePattern.NOVELSEARCH_URL).getPattern();
			if(StringUtils.isNotBlank(searchURL)) {
				result = new HashMap<String, Object>();
				result.put("novel", novel);
				searchURL = searchURL.replace("{SearchNovelName}", novel.getNovelName());
				result.put("searchContent", HttpHelper.getContent(client, searchURL, cpm.getRemoteSite().getCharset(), test));
			}
		}
		return result;
	}
	
	/**
	 * 获取搜索结果页源码
	 * @param client
	 * @param novelNo
	 * @param cpm
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getSearchContent(CollectParam cpm) throws Exception {
		CloseableHttpClient client = HttpUtils.buildClient(Constants.TEST_TIMEOUT);
		Map<String, Object> result = getSearchContent(client, null, cpm);
		client.close();
		return result;
	}
	
	/**
	 * 获取搜索到的目标站小说号
	 * @param client
	 * @param searchURL
	 * @param novelNo
	 * @param cpm
	 * @return
	 * @throws Exception
	 */
	public static String getSearchNovelNo(CloseableHttpClient client, String novelNo, CollectParam cpm) throws Exception {
		
		Map<String, Object> result = getSearchContent(client, novelNo, cpm);
		if(result != null) {
			NovelEntity novel = (NovelEntity)result.get("novel");
			String searchContent = String.valueOf(result.get("searchContent"));
			if(novel != null && StringUtils.isNotBlank(searchContent)) {
				//返回搜索到的小说号和小说名
				List<String> novelNoList = PatternUtils.getValues(searchContent, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVELSEARCH_GETNOVELKEY));
				List<String> novelNameList = PatternUtils.getValues(searchContent, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVELSEARCH_GETNOVELNAME));
				//比较获取到的小说名， 和传入参数一致的则返回对应的小说号， 使用novelNoList.size()防止出现空指针
				for(int i=0;i<novelNoList.size();i++) {
					if(novel.getNovelName().equalsIgnoreCase(novelNameList.get(i))) {
						return novelNoList.get(i);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取搜索到的目标站小说号-测试用
	 * @param searchURL
	 * @param novelNo
	 * @param cpm
	 * @return
	 * @throws Exception
	 */
	public static String getSearchNovelNo(CollectParam cpm) throws Exception {
		CloseableHttpClient client = HttpUtils.buildClient(Constants.TEST_TIMEOUT);
		String no = getSearchNovelNo(client, null, cpm);
		client.close();
		return no;
	}

	public static String getNovelNo2(CollectParam cpm, String novelNo) {
		return PatternUtils.getValue(novelNo, 
				cpm.getRuleMap().get(Rule.RegexNamePattern.NOVELLIST_GETNOVELKEY2).getPattern());
	}

	public static String getPubContentURL2(String chapterSource,
			CollectParam cpm) {
		return PatternUtils.getValue(chapterSource, 
				cpm.getRuleMap().get(Rule.RegexNamePattern.PUBCONTENT_URL2).getPattern());
	}
}
