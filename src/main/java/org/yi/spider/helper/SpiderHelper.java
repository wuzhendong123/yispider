package org.yi.spider.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.http.impl.client.CloseableHttpClient;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.Constants;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.enums.ParamEnum;
import org.yi.spider.exception.CmdParamException;
import org.yi.spider.model.CollectParam;
import org.yi.spider.model.Rule;
import org.yi.spider.utils.HttpUtils;
import org.yi.spider.utils.PatternUtils;
import org.yi.spider.utils.StringUtils;

public class SpiderHelper {

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
	public static List<String> getArticleNo(CollectParam cpm) throws Exception {
		CloseableHttpClient client = HttpUtils.buildClient(Constants.TEST_TIMEOUT);
		List<String> list = null;
		try {
			list = getArticleNo(null, cpm, client);
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
	
	public static List<String> getArticleNo(CommandLine cmd, CollectParam cpm) throws Exception{
		int timeOut = GlobalConfig.site.getInt(ConfigKey.CONNECTION_TIMEOUT, 60);
		CloseableHttpClient client = HttpUtils.buildClient(timeOut * 1000);
		List<String> list = getArticleNo(cmd, cpm, client);
		client.close();
		return list;
	}
			
	public static List<String> getArticleNo(CommandLine cmd, CollectParam cpm, 
			CloseableHttpClient client) throws Exception {
		
		List<String> list = new ArrayList<String>();
		
		//cmd==null说明调用入口是测试功能
		//COLLECT_All采集所有
		//REPAIR_ALL修复所有
		//IMPORT只入库小说不采集章节
		if(cmd == null 
				|| cmd.hasOption(ParamEnum.COLLECT_All.getName())
				|| cmd.hasOption(ParamEnum.REPAIR_ALL.getName())
				|| cmd.hasOption(ParamEnum.IMPORT.getName())) {
			String allUrl = cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_LIST_URL).getPattern();
            String[] listUrls = allUrl.split(RULE_LINE_SEPARATOR);
            for(String url: listUrls){
				String listContent = HttpHelper.getContent(client, url.trim(), cpm.getRemoteSite().getCharset());
				list.addAll(PatternUtils.getValues(listContent,cpm.getRuleMap().get(Rule.RegexNamePattern.NOVELLIST_GETNOVELKEY)));
            }
		} else {
			List<String> list2 = new ArrayList<String>();
			String value = cmd.getOptionValue(ParamEnum.COLLECT_ASSIGN.getName());
			if(StringUtils.isBlank(value)) {
				value = cmd.getOptionValue(ParamEnum.REPAIR_ASSIGN.getName());
			}
			//处理-c 1,2,3,4
			if(value.indexOf(ASSIGN_EVERY_SEPARATOR) > 0){
				String[] vv = value.split(ASSIGN_EVERY_SEPARATOR);
				list2.addAll(Arrays.asList(vv));
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
					if(Pattern.matches("\\d*", endStr)) {
						int end = Integer.parseInt(endStr);
						
						int s = Math.min(start, end);
						int e = Math.max(start, end);
						
						for(int i=s;i<=e;i++) {
							list2.add(String.valueOf(i));
						}
					}
				} catch (CmdParamException e) {
					throw new CmdParamException("命令行参数错误！");
				}
			} else {
				list2.add(value);
			}
			if(cpm.getReverse() != null && cpm.getReverse()) {
				//遍历本站小说号， 反查目标站小说号
				for(String n : list2) {
					//通过搜索配置反查目标站小说号
					String remoteNovelNo = ParseHelper.getSearchNovelNo(client, n, cpm);
					if(StringUtils.isNotBlank(remoteNovelNo)) {
						list.add(remoteNovelNo);
					}
				}
			} else{
				list.addAll(list2);
			}
		}
		
		return list;
	}
	
}
