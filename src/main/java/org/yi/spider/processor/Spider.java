package org.yi.spider.processor;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.http.impl.client.CloseableHttpClient;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.exception.BaseException;
import org.yi.spider.model.CollectParamModel;
import org.yi.spider.model.RuleModel;
import org.yi.spider.utils.HttpUtils;
import org.yi.spider.utils.RuleUtils;
import org.yi.spider.utils.SpiderUtils;
import org.yi.spider.utils.StringUtils;

/**
 * 
 * @ClassName: SpiderProcessor
 * @Description: 采集主控类
 * @author QQ tkts@qq.com 
 *
 */
public class Spider {
	
	private static final Logger logger = LoggerFactory.getLogger(Spider.class);
	
	private CollectParamModel cpm;
	
	private CommandLine cmd;
	
	public Spider(CollectParamModel cpm) {
		super();
		this.cpm = cpm;
	}

	public void process() {
		
		// 初始化HTTP连接
		int timeOut = GlobalConfig.site.getInt(ConfigKey.CONNECTION_TIMEOUT, 60);
		CloseableHttpClient client = HttpUtils.buildClient(timeOut * 1000);
		
		try {
			//解析规则文件
			cpm.setRuleMap(parseRule(cpm));
			//初始化目标站信息
			initRemoteSite(client, cpm);
			//获取要采集的小说序号
			cpm.setNumList(SpiderUtils.getArticleNo(cmd, cpm, client));
		} catch (DocumentException e) {
			logger.error(e.getMessage(), e);
		} catch (BaseException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		Parser p = new Parser(client, cpm);
		int interval = GlobalConfig.collect.getInt(ConfigKey.INTERVAL, 0);
		while(true) {
			p.process();
			logger.debug("线程{}开始休眠...", Thread.currentThread().getName());
			try {
				interval = Math.max(interval, 0);
				Thread.sleep(interval * 1000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 
	 * <p>获取目标站站点名称、编码、小说列表信息</p>
	 * @param client
	 * @param cpm
	 * @throws Exception
	 */
	private void initRemoteSite(CloseableHttpClient client, CollectParamModel cpm) throws BaseException {
		
		String destUrl = RuleUtils.getPattern(cpm, RuleModel.RegexNamePattern.GET_SITE_URL);
		
        if (destUrl.isEmpty()) {
            throw new BaseException("规则文件错误， 错误发生位置: " + RuleModel.RegexNamePattern.GET_SITE_URL);
        }
        logger.debug("目标站URL: " + destUrl);
        cpm.getRemoteSite().setSiteUrl(destUrl);

        // 站点名称
        String siteName = RuleUtils.getPattern(cpm, RuleModel.RegexNamePattern.GET_SITE_NAME);
        logger.debug("目标站名称: " + siteName);
        cpm.getRemoteSite().setSiteName(siteName);
        
        // 站点编码
        String charset = RuleUtils.getPattern(cpm, RuleModel.RegexNamePattern.GET_SITE_CHARSET);
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
	private Map<String, RuleModel> parseRule(CollectParamModel cpm) throws DocumentException {
		String ruleFile = cpm.getRuleFile();
		if(StringUtils.isEmpty(ruleFile)) {
			ruleFile = GlobalConfig.collect.getString(ConfigKey.RULE_NAME);
		}
		if(StringUtils.isEmpty(ruleFile)) {
			throw new BaseException("参数和规则必须有一个指定采集使用的规则文件！");
		}
		return RuleUtils.parseXml(ruleFile);
	}
	
	public CollectParamModel getCpm() {
		return cpm;
	}

	public void setCpm(CollectParamModel cpm) {
		this.cpm = cpm;
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public void setCmd(CommandLine cmd) {
		this.cmd = cmd;
	}

}
