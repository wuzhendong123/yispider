package org.yi.spider.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.ThreadObserver;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.enums.ParamEnum;
import org.yi.spider.enums.RepairParamEnum;
import org.yi.spider.model.CollectParam;
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
		new ThreadObserver(this).start();
		process();
	}
	
	public void process() {
		
		//获取采集类型和规则文件
		CollectParam cpm = new CollectParam();
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
			cpm.setRepairParam(getRepairParam());
		} else if(cmd.hasOption(ParamEnum.REPAIR_ASSIGN.getName())) {
			//指定目标站小说号修复
			cpm.setCollectType(ParamEnum.REPAIR_ASSIGN);
			cpm.setRepairParam(getRepairParam());
		} else if(cmd.hasOption(ParamEnum.IMPORT.getName())) {
			//指定目标站小说号采集
			cpm.setCollectType(ParamEnum.IMPORT);
		}  else {
			//采集所有
			cpm.setCollectType(ParamEnum.COLLECT_All);
		}
		
		if(cmd.hasOption(ParamEnum.REVERSE.getName())) {
			cpm.setReverse(Boolean.TRUE);
		}
		
		if(cmd.hasOption(ParamEnum.RULE_FILE.getName())) {
			if(StringUtils.isNotBlank(cmd.getOptionValue(ParamEnum.RULE_FILE.getName()))) {
				cpm.setRuleFile(cmd.getOptionValue(ParamEnum.RULE_FILE.getName()));
			}
		}
		
		MainParser sp = new MainParser(cpm);
		sp.setCmd(cmd);
		
		int interval = GlobalConfig.collect.getInt(ConfigKey.INTERVAL, 0);
		while(!GlobalConfig.SHUTDOWN) {
			try {
				sp.process();
				if(!GlobalConfig.SHUTDOWN) {
					interval = Math.max(interval, 0);
					logger.info("当前线程{}任务已经全部进入执行状态, {}秒后将检查是否有新任务进入...", Thread.currentThread().getName(), interval);
					Thread.sleep(interval * 1000);
				}
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			} catch (DocumentException e) {
				logger.error(e.getMessage(), e);
				//解析规则文件出错则跳出循环
				break;
			} catch (Exception e) {
				if(logger.isDebugEnabled()){
					logger.error("解析异常, 原因："+e.getMessage(), e);
				} else {
					logger.error("解析异常, 原因："+e.getMessage());
				}
			}
		}
	}

	/**
	 * 获取需要修复的内容， 如果没有制定rp参数， 或者rp参数后没有具体修复项， 则修复所有
	 * @return
	 */
	private List<String> getRepairParam() {
		List<String> params = new ArrayList<String>();
		if(cmd.hasOption(ParamEnum.REPAIR_PARAMS.getName())){
			String values = cmd.getOptionValue(ParamEnum.REPAIR_PARAMS.getName());
			if(StringUtils.isNotBlank(values)){
				params.addAll(Arrays.asList(values.split(",")));
			} else {
				params = RepairParamEnum.toList();
			}
		} else {
			params = RepairParamEnum.toList();
		}
		return params;
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public void setCmd(CommandLine cmd) {
		this.cmd = cmd;
	}

}
