package org.yi.spider.model;

import java.util.List;
import java.util.Map;

import org.yi.spider.enums.ParamEnum;

public class CollectParamModel {
	
	/**
	 * 采集类型
	 */
	private ParamEnum collectType;
	
	private List<String> repairParam;
	
	/**
	 * 指定需要采集的小说序号
	 */
	private List<String> numList;
	
	/**
	 * 指定采集规则文件
	 */
	private String ruleFile;
	
	/**
	 * 根据参数指定的文件解析后得到得采集规则
	 */
	private Map<String, RuleModel> ruleMap;

	/**
	 * 目标站信息
	 */
	private SiteModel remoteSite = new SiteModel();
	
	public ParamEnum getCollectType() {
		return collectType;
	}

	public void setCollectType(ParamEnum collectType) {
		this.collectType = collectType;
	}

	public List<String> getNumList() {
		return numList;
	}

	public void setNumList(List<String> numList) {
		this.numList = numList;
	}

	public String getRuleFile() {
		return ruleFile;
	}

	public void setRuleFile(String ruleFile) {
		this.ruleFile = ruleFile;
	}

	public Map<String, RuleModel> getRuleMap() {
		return ruleMap;
	}

	public void setRuleMap(Map<String, RuleModel> ruleMap) {
		this.ruleMap = ruleMap;
	}

	public SiteModel getRemoteSite() {
		return remoteSite;
	}

	public void setRemoteSite(SiteModel remoteSite) {
		this.remoteSite = remoteSite;
	}

	public List<String> getRepairParam() {
		return repairParam;
	}

	public void setRepairParam(List<String> repairParam) {
		this.repairParam = repairParam;
	}
	
}
