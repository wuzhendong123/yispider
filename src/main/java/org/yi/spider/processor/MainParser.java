package org.yi.spider.processor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.exception.BaseException;
import org.yi.spider.helper.RuleHelper;
import org.yi.spider.helper.SpiderHelper;
import org.yi.spider.model.CollectParam;
import org.yi.spider.model.Rule;
import org.yi.spider.utils.StringUtils;

/**
 * 
 * @ClassName: SpiderProcessor
 * @Description: 采集主控类
 * @author QQ  
 *
 */
public class MainParser {
	
	private static final Logger logger = LoggerFactory.getLogger(MainParser.class);
	
	private CollectParam cpm;
	
	private CommandLine cmd;
	
	public MainParser(CollectParam cpm) {
		super();
		this.cpm = cpm;
	}

	public void process() throws DocumentException, BaseException, Exception {
		
		try {
			//解析规则文件
			cpm.setRuleMap(parseRule(cpm));
			//初始化目标站信息
			initRemoteSite(cpm);
			//获取要采集的小说序号
//			cpm.setNumList(SpiderHelper.getArticleNo(cmd, cpm, client));
			List<String> novelNoList = SpiderHelper.getArticleNo(cmd, cpm);
			int concurrent = GlobalConfig.collect.getInt("concurrent_novel_task", 1);
			if(concurrent > 1) {
				ExecutorService pool = Executors.newFixedThreadPool(GlobalConfig.collect.getInt("concurrent_novel_task", 1));
				for(String novelNo : novelNoList) {
					logger.trace("多线程，开始采集： " + cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_NAME).getPattern());
					pool.execute(new NovelParser(cpm, novelNo));
				}
				pool.shutdown();
				pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
				logger.debug("关闭二级线程池！");
			} else {
				logger.trace("单线程，开始采集： " + cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_NAME).getPattern());
				for(String novelNo : novelNoList) {
					if(!GlobalConfig.SHUTDOWN) {
						new NovelParser(cpm, novelNo).prase();
					}
				}
			}
		} catch (DocumentException e) {
			throw new DocumentException(e.getMessage());
		} catch (BaseException e) {
			throw new BaseException(e.getMessage());
		}
	}
	
	/**
	 * 
	 * <p>获取目标站站点名称、编码、小说列表信息</p>
	 * @param client
	 * @param cpm
	 * @throws Exception
	 */
	private void initRemoteSite(CollectParam cpm) throws BaseException {
		
		String destUrl = RuleHelper.getPattern(cpm, Rule.RegexNamePattern.GET_SITE_URL);
		
        if (destUrl.isEmpty()) {
            throw new BaseException("规则文件错误， 错误发生位置: " + Rule.RegexNamePattern.GET_SITE_URL);
        }
        logger.debug("目标站URL: " + destUrl);
        cpm.getRemoteSite().setSiteUrl(destUrl);

        // 站点名称
        String siteName = RuleHelper.getPattern(cpm, Rule.RegexNamePattern.GET_SITE_NAME);
        logger.debug("目标站名称: " + siteName);
        cpm.getRemoteSite().setSiteName(siteName);
        
        // 站点编码
        String charset = RuleHelper.getPattern(cpm, Rule.RegexNamePattern.GET_SITE_CHARSET);
        logger.debug("目标站编码: " + charset);
        cpm.getRemoteSite().setCharset(charset);
	}

	/**
	 * 
	 * <p>解析规则文件</p>
	 * @param cpm
	 * @return
	 * @throws DocumentException
	 */
	private Map<String, Rule> parseRule(CollectParam cpm) throws DocumentException {
		String ruleFile = cpm.getRuleFile();
		if(StringUtils.isBlank(ruleFile)) {
			ruleFile = GlobalConfig.collect.getString(ConfigKey.RULE_NAME);
		}
		if(StringUtils.isBlank(ruleFile)) {
			throw new BaseException("全局规则和采集命令中必须至少有一个指定采集规则文件！");
		}
		logger.debug("开始解析规则：{}", ruleFile);
		return RuleHelper.parseXml(ruleFile);
	}
	
	public CollectParam getCpm() {
		return cpm;
	}

	public void setCpm(CollectParam cpm) {
		this.cpm = cpm;
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public void setCmd(CommandLine cmd) {
		this.cmd = cmd;
	}

}
