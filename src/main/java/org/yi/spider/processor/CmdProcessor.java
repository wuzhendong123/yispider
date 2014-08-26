package org.yi.spider.processor;

import org.apache.commons.cli.CommandLine;
import org.yi.spider.enums.ParamEnum;
import org.yi.spider.model.CollectParamModel;
import org.yi.spider.utils.StringUtils;

public class CmdProcessor extends BaseProcessor{
	
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
		}
		
		if(cmd.hasOption(ParamEnum.RULE_FILE.getName())) {
			if(StringUtils.isNotBlank(cmd.getOptionValue(ParamEnum.RULE_FILE.getName()))) {
				cpm.setRuleFile(cmd.getOptionValue(ParamEnum.RULE_FILE.getName()));
			}
		}
		
		Spider sp = new Spider(cpm);
		sp.setCmd(cmd);
		sp.process();
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public void setCmd(CommandLine cmd) {
		this.cmd = cmd;
	}

}
