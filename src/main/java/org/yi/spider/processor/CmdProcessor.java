package org.yi.spider.processor;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.enums.ParamEnum;
import org.yi.spider.model.CollectParamModel;
import org.yi.spider.utils.StringUtils;

public class CmdProcessor extends BaseProcessor{
	
	private static final Logger logger = LoggerFactory.getLogger(CmdProcessor.class);
	
	private CommandLine cmd ; 
	
	public CmdProcessor() {
		super();
	}

	public CmdProcessor(CommandLine cmd) {
		super();
		this.cmd = cmd;
	}

	public void run() {
		
		//获取采集类型和规则文件
		CollectParamModel cpm = new CollectParamModel();
		//ca、c、ra、r四个参数为互斥关系， 优先级为ca > c > ra > r
		if(cmd.hasOption(ParamEnum.COLLECT_All.getName())) {
			//采集所有
			cpm.setCollectType(ParamEnum.COLLECT_All);
		} else if(cmd.hasOption(ParamEnum.COLLECT_ASSIGN.getName())) {
			//指定目标站小说号采集
			cpm.setCollectType(ParamEnum.COLLECT_ASSIGN);
		} else if(cmd.hasOption(ParamEnum.REPAIR_ALL.getName())) {
			//修复所有
			cpm.setCollectType(ParamEnum.REPAIR_ALL);
		} else if(cmd.hasOption(ParamEnum.REPAIR_ASSIGN.getName())) {
			//指定目标站小说号修复
			cpm.setCollectType(ParamEnum.REPAIR_ASSIGN);
		} else {
			//采集所有
			cpm.setCollectType(ParamEnum.COLLECT_All);
		}
		
		if(cmd.hasOption(ParamEnum.RULE_FILE.getName())) {
			if(StringUtils.isNotBlank(cmd.getOptionValue(ParamEnum.RULE_FILE.getName()))) {
				cpm.setRuleFile(cmd.getOptionValue(ParamEnum.RULE_FILE.getName()));
			}
		}
		
		Spider sp = new Spider(cpm);
		sp.setCmd(cmd);
		
		int interval = GlobalConfig.collect.getInt(ConfigKey.INTERVAL, 0);
		while(true) {
			try {
				sp.process();
				interval = Math.max(interval, 0);
				Thread.sleep(interval * 1000);
				logger.debug("线程{}开始休眠...", Thread.currentThread().getName());
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public void setCmd(CommandLine cmd) {
		this.cmd = cmd;
	}

}
