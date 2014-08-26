package org.yi.spider.utils;

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
import org.yi.spider.model.CollectParamModel;
import org.yi.spider.model.RuleModel;

public class RuleUtils {
	
	private static final String REGEX_NAME = "RegexName";
    private static final String FILTER_PATTERN = "FilterPattern";
    private static final String PATTERN = "Pattern";
    private static final String METHOD = "Method";
    private static final String OPTIONS = "Options";
    
    private static final String RULE_DIR = "rules/";

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, RuleModel> parseXml(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        Map<String, RuleModel> ruleMap = new HashMap<String, RuleModel>();
        try {
        	File ruleFile = FileUtils.locateAbsolutePathFromClasspath(RULE_DIR + fileName);
            Document document = reader.read(ruleFile);
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element element : elementList) {
                RuleModel rule = new RuleModel(); 
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
	public static String getPattern(CollectParamModel cpm, String regexName) {
		
		String regexValue = "";
		
		Map<String,RuleModel> rules = cpm.getRuleMap();
		
		if(rules.get(regexName) != null) {
			regexValue = rules.get(regexName).getPattern();
		}
		
		return regexValue;
	}
}
