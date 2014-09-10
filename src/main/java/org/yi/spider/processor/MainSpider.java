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
import org.yi.spider.helper.RuleHelper;
import org.yi.spider.helper.SpiderHelper;
import org.yi.spider.model.CollectParamModel;
import org.yi.spider.model.RuleModel;
import org.yi.spider.utils.HttpUtils;
import org.yi.spider.utils.StringUtils;

/**
 * 
 * @ClassName: SpiderProcessor
 * @Description: 采集主控类
 * @author QQ  
 *
 */
public class MainSpider {
	
	private static final Logger logger = LoggerFactory.getLogger(MainSpider.class);
	
	private CollectParamModel cpm;
	
	private CommandLine cmd;
	
	public MainSpider(CollectParamModel cpm) {
		super();
		this.cpm = cpm;
	}

	public void process() throws Exception {
		
		// 初始化HTTP连接
		int timeOut = GlobalConfig.site.getInt(ConfigKey.CONNECTION_TIMEOUT, 60);
		CloseableHttpClient client = HttpUtils.buildClient(timeOut * 1000);
		
		try {
			//解析规则文件
			cpm.setRuleMap(parseRule(cpm));
			//初始化目标站信息
			initRemoteSite(client, cpm);
			//获取要采集的小说序号
			cpm.setNumList(SpiderHelper.getArticleNo(cmd, cpm, client));

			MainParser p = new MainParser(client, cpm);
			p.process();
			
		} catch (DocumentException e) {
			throw new DocumentException(e.getMessage());
		} catch (BaseException e) {
			throw new BaseException(e.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			client.close();
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
		
		String destUrl = RuleHelper.getPattern(cpm, RuleModel.RegexNamePattern.GET_SITE_URL);
		
        if (destUrl.isEmpty()) {
            throw new BaseException("规则文件错误， 错误发生位置: " + RuleModel.RegexNamePattern.GET_SITE_URL);
        }
        logger.debug("目标站URL: " + destUrl);
        cpm.getRemoteSite().setSiteUrl(destUrl);

        // 站点名称
        String siteName = RuleHelper.getPattern(cpm, RuleModel.RegexNamePattern.GET_SITE_NAME);
        logger.debug("目标站名称: " + siteName);
        cpm.getRemoteSite().setSiteName(siteName);
        
        // 站点编码
        String charset = RuleHelper.getPattern(cpm, RuleModel.RegexNamePattern.GET_SITE_CHARSET);
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
		if(StringUtils.isBlank(ruleFile)) {
			ruleFile = GlobalConfig.collect.getString(ConfigKey.RULE_NAME);
		}
		if(StringUtils.isBlank(ruleFile)) {
			throw new BaseException("全局规则和采集命令中必须至少有一个指定采集规则文件！");
		}
		return RuleHelper.parseXml(ruleFile);
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
