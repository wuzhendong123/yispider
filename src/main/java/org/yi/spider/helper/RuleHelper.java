package org.yi.spider.helper;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.yi.spider.model.CollectParam;
import org.yi.spider.model.Rule;
import org.yi.spider.utils.FileUtils;

public class RuleHelper {
	
	private static final String REGEX_NAME = "RegexName";
    private static final String FILTER_PATTERN = "FilterPattern";
    private static final String PATTERN = "Pattern";
    private static final String METHOD = "Method";
    private static final String OPTIONS = "Options";
    
    private static final String RULE_DIR = "rules/";

    /**
     * 解析采集器规则文件夹下的规则文件
     * @param fileName
     * @return
     * @throws DocumentException
     */
    public static Map<String, Rule> parseXml(String fileName) throws DocumentException {
        return parseXml(FileUtils.locateAbsolutePathFromClasspath(RULE_DIR + fileName));
    }
	
    /**
     * 根据传入的文件解析规则， 用于规则定制页面编辑规则时， 将已有规则解析到前台
     * @param file
     * @return
     * @throws DocumentException
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Rule> parseXml(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Map<String, Rule> ruleMap = new HashMap<String, Rule>();
        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element element : elementList) {
                Rule rule = new Rule(); 
            	for (Iterator i = element.nodeIterator(); i.hasNext();) {
                    Node node = (Node) i.next();
                    if (StringUtils.equals(node.getName(), PATTERN)) {
                        rule.setPattern(node.getStringValue());
                    } else if (StringUtils.equals(node.getName(), FILTER_PATTERN)) {
                        rule.setFilterPattern(node.getStringValue());
                    } else if (StringUtils.equals(node.getName(), REGEX_NAME)) {
                        rule.setRegexName(node.getStringValue());
                    } else if (StringUtils.equals(node.getName(), METHOD)) {
                        rule.setMethod(node.getStringValue());
                    } else if (StringUtils.equals(node.getName(), OPTIONS)) {
                        rule.setOptions(node.getStringValue());
                    }
                }
                ruleMap.put(element.getName(), rule);
            }
        } catch (DocumentException e) {
        	throw new DocumentException("解析规则出错！");
        }
        return ruleMap;
    }

	/**
	 * 
	 * <p>获取正则表达式</p>
	 * @param cpm
	 * @param getSiteUrl
	 * @return
	 */
	public static String getPattern(CollectParam cpm, String regexName) {
		
		String regexValue = "";
		
		Map<String,Rule> rules = cpm.getRuleMap();
		
		if(rules.get(regexName) != null) {
			regexValue = rules.get(regexName).getPattern();
		}
		
		return regexValue;
	}
}
