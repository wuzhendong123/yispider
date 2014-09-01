package org.yi.spider.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.http.impl.client.CloseableHttpClient;
import org.yi.spider.constants.Constants;
import org.yi.spider.enums.ParamEnum;
import org.yi.spider.exception.CmdParamException;
import org.yi.spider.model.CollectParamModel;
import org.yi.spider.model.RuleModel;

public class SpiderUtils {

	private static final String ASSIGN_EVERY_SEPARATOR = ",";
	
	private static final String ASSIGN_SECTIOIN_SEPARATOR = "-";
	
	private static final String RULE_LINE_SEPARATOR = "\n";
	
	/**
	 * <p>获取要采集的目标站小说号</p>
	 * @param cmd
	 * @param cpm
	 * @param httpClient
	 * @return
	 * @throws Exception
	 */
	public static List<String> getArticleNo(CommandLine cmd, CollectParamModel cpm) throws Exception {
		CloseableHttpClient client = HttpUtils.buildClient(Constants.TEST_TIMEOUT);
		List<String> list = null;
		try {
			getArticleNo(cmd, cpm, client);
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			client.close();
		}
		return list;
	}
	
	/**
	 * <p>获取要采集的目标站小说号， 如果没有指定则通过规则文件到目标站抓取， 否则获取指定的小说号</p>
	 * @param cmd
	 * @param cpm
	 * @param httpClient
	 * @return
	 * @throws Exception
	 */
	public static List<String> getArticleNo(CommandLine cmd, CollectParamModel cpm, 
			CloseableHttpClient client) throws Exception {
		
		List<String> list = new ArrayList<String>();
		
		if(cmd.hasOption(ParamEnum.COLLECT_All.getName())
				|| cmd.hasOption(ParamEnum.REPAIR_ALL.getName())) {
			String allUrl = cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_LIST_URL).getPattern();
            String[] listUrls = allUrl.split(RULE_LINE_SEPARATOR);
            for(String url: listUrls){
				String listContent = HttpUtils.getContent(client, url.trim(), cpm.getRemoteSite().getCharset());
				list.addAll(PatternUtils.getValues(listContent,cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVELLIST_GETNOVELKEY)));
            }
		} else {
			String value = cmd.getOptionValue(ParamEnum.COLLECT_ASSIGN.getName());
			if(StringUtils.isBlank(value)) {
				value = cmd.getOptionValue(ParamEnum.REPAIR_ASSIGN.getName());
			}
			//处理-c 1,2,3,4
			if(value.indexOf(ASSIGN_EVERY_SEPARATOR) > 0){
				String[] vv = value.split(ASSIGN_EVERY_SEPARATOR);
				list.addAll(Arrays.asList(vv));
			} else if(value.indexOf(ASSIGN_SECTIOIN_SEPARATOR) > 0){
				//处理-c 1-5
				try {
					String startStr = value.split(ASSIGN_SECTIOIN_SEPARATOR)[0];
					//判断起始书号如果不是数字， 则设置为0
					if(!Pattern.matches("\\d*", startStr)) {
						startStr = "0";
					}
					int start = Integer.parseInt(startStr);
					String endStr = value.split(ASSIGN_SECTIOIN_SEPARATOR)[1];
					if(!Pattern.matches("\\d*", endStr)) {
						
					}
					int end = Integer.parseInt(endStr);
					
					int s = Math.min(start, end);
					int e = Math.max(start, end);
					
					for(int i=s;i<=e;i++) {
						list.add(String.valueOf(i));
					}
				} catch (CmdParamException e) {
					throw new CmdParamException("命令行参数错误！");
				}
			} else {
				list.add(value);
			}
		}
		
		return list;
	}
	
}
