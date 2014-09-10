package org.yi.spider.helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.Constants;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.enums.CategoryGradeEnum;
import org.yi.spider.exception.BaseException;
import org.yi.spider.model.CollectParamModel;
import org.yi.spider.model.RuleModel;
import org.yi.spider.utils.HttpUtils;
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
	public static String get(String source, RuleModel pattern) {
		return PatternUtils.getValue(source, pattern);
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
	public static String getSource(CollectParamModel cpm, String destURL) throws IOException {
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
	public static String getSource(CloseableHttpClient httpClient, CollectParamModel cpm, String destURL) {
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
	public static String getRuleVersion(CollectParamModel cpm) {
		return cpm.getRuleMap().get(RuleModel.RegexNamePattern.RULE_VERSION).getPattern();
	}
	
	/**
	 * 获取目标站名称
	 * @param cpm
	 * @return
	 */
	public static String getSiteName(CollectParamModel cpm) {
		return cpm.getRuleMap().get(RuleModel.RegexNamePattern.GET_SITE_NAME).getPattern();
	}
	
	/**
	 * 获取目标站编码
	 * @param cpm
	 * @return
	 */
	public static String getSiteCharset(CollectParamModel cpm) {
		return cpm.getRuleMap().get(RuleModel.RegexNamePattern.GET_SITE_CHARSET).getPattern();
	}
	
	/**
	 * 获取目标站url
	 * @param cpm
	 * @return
	 */
	public static String getSiteUrl(CollectParamModel cpm) {
		return cpm.getRuleMap().get(RuleModel.RegexNamePattern.GET_SITE_URL).getPattern();
	}
	
	/**
	 * 获取小说名称
	 * @param cpm
	 * @return
	 */
	public static String getNovelName(String infoSource, CollectParamModel cpm) {
		return PatternUtils.getValue(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_NAME));
	}
	
	/**
	 * 获取小说作者
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getNovelAuthor(String infoSource, CollectParamModel cpm){
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_AUTHOR));
	}

	/**
	 * 
	 * <p>获取目标站小说信息页地址, 规则中指定， 通过替换、计算获取</p>
	 * @param assignURL
	 * @param novelNo
	 * @return
	 */
	public static String getAssignURL(String assignURL, String novelNo) {
		// 小说信息页地址
		String result = assignURL.replace("{NovelKey}", novelNo)
								 .replace("NovelKey", novelNo);
		//如果替换之后依旧存在{则表示存在需要计算的表达式
		if (result.indexOf("{") > 0) {
			//获取计算表达式
			String express = result.substring(result.indexOf("{") + 1,
					result.indexOf("}"));
			String novelKey = String.valueOf(ScriptUtils.calculate(express, null).intValue());
			//使用结算结果替换计算表达式
			result = result.replaceAll("\\{.*\\}", novelKey);
		}
		return result;
	}
	
	/**
	 * 
	 * <p>获取小说所属分类</p>
	 * @param category	分类名
	 * @param top		分类级别(大类、小类)
	 * @return			分类对应的号
	 */
    public static Integer getCategory(String category, CategoryGradeEnum top) {
        int cat = GlobalConfig.collect.getInt(ConfigKey.DEFAULT_CATEGORY, 10);
        if (category != null && !category.isEmpty()) {
            Map<String, List<String>> cats = new HashMap<String, List<String>>();
            if (top == CategoryGradeEnum.TOP) {
                cats = GlobalConfig.TOP_CATEGORY;
            } else {
                cats = GlobalConfig.SUB_CATEGORY;
            }
            Set<Entry<String, List<String>>> set = cats.entrySet();
            Iterator<Entry<String, List<String>>> iter = set.iterator();
            while (iter.hasNext()) {
                Entry<String, List<String>> entry = iter.next();
                String key = entry.getKey();
                List<String> list = entry.getValue();
                for (String s : list) {
                    if (StringUtils.isBlank(s))
                        continue;
                    if (category.equals(s)) {
                        cat = Integer.parseInt(key);
                        break;
                    }
                }
            }
        }
        return cat;
    }
    
    /**
     * 
     * <p>获取封面标识并下载封面图片</p>
     * @param novel
     * @param infoSource
     * @param cpm
     * @return
     */
    public static Integer getNovelCover(NovelEntity novel, String infoSource, CollectParamModel cpm) {
		Integer imgFlag;
		String novelCover = getNovelCoverURL(infoSource, cpm);
        if (novelCover == null || novelCover.isEmpty()) {
            imgFlag = 0;
        } else {
            String suffix = novelCover.substring(novelCover.lastIndexOf("."), novelCover.length());
            novelCover = StringUtils.getFullUrl(cpm.getRemoteSite().getSiteUrl(), novelCover);
            FileHelper.downImage(novelCover, novel.getNovelNo(), suffix);
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
	public static String getNovelCoverURL(String infoSource, CollectParamModel cpm) {
		String novelCover = PatternUtils.getValue(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_COVER));
        if (StringUtils.isBlank(novelCover)) {
            String novelDefaultCoverUrl = cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_DEFAULT_COVER_URL).getPattern();
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
     */
	public static String getInfoRUL(CollectParamModel cpm, String novelNo) {
		String infoURL = "";
		String assignURL = cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_URL).getPattern();
		if(StringUtils.isNotBlank(assignURL)){
			infoURL = ParseHelper.getAssignURL(assignURL, novelNo);
		}
		return infoURL;
	}

	/**
	 * 获取小说大类
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getTopCategory(String infoSource, CollectParamModel cpm) {
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.LAGER_SORT));
	}
	
	/**
	 * 获取小说细类
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getSubCategory(String infoSource, CollectParamModel cpm) {
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.SMALL_SORT));
	}

	/**
	 * 获取小说简介
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getNovelIntro(String infoSource, CollectParamModel cpm) {
		String novelIntro = ParseHelper.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_INTRO));
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
	public static String getNovelKeywrods(String infoSource, CollectParamModel cpm) {
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_KEYWORD));
	}
	
	/**
	 * 获取写作进度
	 * @param infoSource
	 * @param cpm
	 * @return
	 */
	public static String getNovelDegree(String infoSource, CollectParamModel cpm) {
		return ParseHelper.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_KEYWORD));
	}
	
	/**
	 * 获取小说章节目录页地址
	 * @param infoSource
	 * @param novelNo
	 * @param cpm
	 * @return
	 */
	public static String getNovelMenuURL(String infoSource, String novelNo, CollectParamModel cpm){
		 // 可能是/book/1.html、book/1.html、http://www.foo.com/book/1.html等
        String novelPubKey = ParseHelper.get(infoSource, 
        		cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVELINFO_GETNOVELPUBKEY));
        // novelPubKey为空则说明目录页地址不是通过页面获取， 而是在规则中指定好了
        if(StringUtils.isBlank(novelPubKey)){
        	novelPubKey = cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBINDEX_URL).getPattern();
        	if(novelPubKey==null || novelPubKey.isEmpty()){
        		throw new BaseException("无法从页面获取目录页地址， 需要在规则中PubIndexUrl项指定目录页地址");
        	}
        	novelPubKey = ParseHelper.getAssignURL(novelPubKey, novelNo);
        }
        
        // 小说目录页地址 http://a/b/c.html
        return StringUtils.getFullUrl(cpm.getRemoteSite().getSiteUrl(), novelPubKey);
	}
	
	/**
	 * 获取小说目录页源码
	 * @param novelPubKeyURL
	 * @param cpm
	 * @return
	 * @throws Exception
	 */
	public static String getChapterListSource(String novelPubKeyURL, CollectParamModel cpm) throws Exception{
		//获取整个章节列表页源码
		CloseableHttpClient httpClient = HttpUtils.buildClient(Constants.TEST_TIMEOUT);
		String menuSource = HttpHelper.getContent(httpClient, novelPubKeyURL, cpm.getRemoteSite().getCharset());
		//过滤源码
        RuleModel pubIndexContentRule = cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUB_INDEX_CONTENT);
        if(pubIndexContentRule != null){
        	menuSource = ParseHelper.get(menuSource, pubIndexContentRule);
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
	public static List<String> getChapterNameList(String menuSource, CollectParamModel cpm){
		return PatternUtils.getValues(menuSource,
        		cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBCHAPTER_NAME));
	}
	
	/**
	 * 获取所有章节编号
	 * @param menuSource
	 * @param cpm
	 * @return
	 */
	public static List<String> getChapterNoList(String menuSource, CollectParamModel cpm){
		return PatternUtils.getValues(menuSource,
        		cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBCHAPTER_GETCHAPTERKEY));
	}
	
	/**
	 * 获取章节地址
	 * @param novelPubKeyURL
	 * @param novelNo
	 * @param cno
	 * @param cpm
	 * @return
	 */
	public static String getChapterURL(String novelPubKeyURL, String novelNo, String cno, CollectParamModel cpm) {
		String chapterURL = cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBCONTENT_URL).getPattern();
		return StringHelper.getRemoteChapterUrl(chapterURL, novelPubKeyURL, novelNo, cno, cpm);
	}
	
	/**
	 * 获取章节页源码
	 * @param chapterURL
	 * @param cpm
	 * @return
	 * @throws Exception
	 */
	public static String getChapterSource(String chapterURL, CollectParamModel cpm) throws Exception{
		CloseableHttpClient httpClient = HttpUtils.buildClient(Constants.TEST_TIMEOUT);
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
	public static String getChapterContent(String chapterSource, CollectParamModel cpm){
		String chapterContent = ParseHelper.get(chapterSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBCONTENT_TEXT));
		chapterContent = StringUtils.removeBlankLine(chapterContent);
	    chapterContent = StringUtils.replaceHtml(chapterContent);
	    return chapterContent;
	}
	
}
