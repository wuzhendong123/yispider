package org.yi.spider.utils;

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
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.enums.CategoryGradeEnum;
import org.yi.spider.exception.BaseException;
import org.yi.spider.model.CollectParamModel;
import org.yi.spider.model.RuleModel;

/**
 * 
 * @ClassName: ParseUtil
 * @Description: 解析帮助类
 * @author QQ tkts@qq.com 
 *
 */
public class ParseUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(ParseUtils.class);
	
	
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
	 */
	public static String getSource(CloseableHttpClient httpClient, CollectParamModel cpm, String destURL) {
		logger.debug("获取源文件， 目标地址： " + destURL);
		//小说信息页源码
		String content = "";
		try {
			content = HttpUtils.getContent(httpClient, destURL, cpm.getRemoteSite().getCharset());
		} catch (Exception e) {
			throw new BaseException("小说名为空, 目标链接：" + destURL);
		}
		if (StringUtils.isBlank(content)) {
			throw new BaseException("小说名为空, 目标链接：" + destURL);
		}
		return content;
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
		String novelCover = PatternUtils.getValue(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_COVER));
        String novelDefaultCoverUrl = cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_DEFAULT_COVER_URL).getPattern();
        Integer imgFlag = 0;
        if (novelCover == null || novelCover.isEmpty()) {
            if (novelDefaultCoverUrl != null && !novelDefaultCoverUrl.isEmpty()) {
                novelCover = novelDefaultCoverUrl;
            }
        }
        if (novelCover == null || novelCover.isEmpty()) {
            imgFlag = 0;
        } else {
            String suffix = novelCover.substring(novelCover.lastIndexOf("."), novelCover.length());
            novelCover = StringUtils.getFullUrl(cpm.getRemoteSite().getSiteUrl(), novelCover);
            FileUtils.downImage(novelCover, novel.getNovelNo(), suffix);
            imgFlag = StringUtils.getImgFlag(novelCover);
        }
		return imgFlag;
	}
    
}
