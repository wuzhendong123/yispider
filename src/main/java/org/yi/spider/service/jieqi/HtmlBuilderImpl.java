package org.yi.spider.service.jieqi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.enums.CategoryGradeEnum;
import org.yi.spider.helper.FileHelper;
import org.yi.spider.model.Category;
import org.yi.spider.model.PreNextChapter;
import org.yi.spider.service.IHtmlBuilder;
import org.yi.spider.utils.FileUtils;
import org.yi.spider.utils.PatternUtils;
import org.yi.spider.utils.ScriptUtils;
import org.yi.spider.utils.StringUtils;

public class HtmlBuilderImpl implements IHtmlBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(HtmlBuilderImpl.class);

	private static final String LOOP_START = "{?section";
	
	private static final String LOOP_END = "{?/section?}";
	
	private static final String PARSE_CNT_START = "{?else?}";
	
	private static final String PARSE_CNT_END = "{?/if?}";
	
	private static final String pattern = "[\\s\\S]+(<if[\\s\\S]+<else[\\s\\S]+</if)[\\s\\S]+";
	private static final String conditionPattern = "<if(.+?)>";
	private static final String ifPattern = "<if.+?>([\\s\\S]+)<else";
	private static final String elsePattern = "<else([\\s\\S]+)</if";
	
	@Override
	public void buildChapterListHtml(NovelEntity novel, List<ChapterEntity> chapterList) {

		String localPath = FileHelper.getHtmlFilePath(novel, null);
		
		String dir = localPath.substring(0, localPath.lastIndexOf("/"));
        if(!new File(dir).exists()){
        	new File(dir).mkdirs();
        }
        
    	String template  = GlobalConfig.localSite.getTemplate().getChapter();
		try {
			FileInputStream fis = new FileInputStream(template);// 读取模板文件
			int lenght = fis.available();
			byte bytes[] = new byte[lenght];
			fis.read(bytes);
			fis.close();
			String templateContent = new String(bytes, GlobalConfig.localSite.getCharset());
			
			//存在section说明是目录页
			if(templateContent.indexOf(LOOP_START)>0) {
				int start = templateContent.indexOf(LOOP_START);
				int end = templateContent.indexOf(LOOP_END) + LOOP_END.length();
				String loopContent = templateContent.substring(start, end);
				templateContent = templateContent.substring(0, start)
						+ getArticleIndexLoop(loopContent, novel, chapterList)
						+ templateContent.substring(end, templateContent.length());
			}
			
			templateContent = templateContent.replace("{?$articleid?}", String.valueOf(novel.getNovelNo()));
			templateContent = templateContent.replace("{?$article_id?}", String.valueOf(novel.getNovelNo()));
			templateContent = templateContent.replace("{?$intro?}", String.valueOf(novel.getIntro()));			
			
			templateContent = templateContent.replace("{?$article_title?}", novel.getNovelName());
			templateContent = templateContent.replace("{?$jieqi_sitename?}", GlobalConfig.localSite.getSiteName());
			templateContent = templateContent.replace("{?$jieqi_url?}", GlobalConfig.localSite.getSiteUrl());
			templateContent = templateContent.replace("{?$jieqi_main_url?}", GlobalConfig.localSite.getSiteUrl());
			templateContent = templateContent.replace("{?$jieqi_local_url?}", GlobalConfig.localSite.getSiteUrl());
			templateContent = templateContent.replace("{?$jieqi_charset?}", GlobalConfig.localSite.getCharset());
			
			String modulesArticleUrl = GlobalConfig.localSite.getSiteUrl()+"/modules/article/";
			templateContent = templateContent.replace("{?$jieqi_modules['article']['url']?}", modulesArticleUrl);
			templateContent = templateContent.replace("{?$url_bookroom?}", modulesArticleUrl);
			templateContent = templateContent.replace("{?$dynamic_url?}", modulesArticleUrl);
			
			templateContent = templateContent.replace("{?$sortid?}", String.valueOf(novel.getTopCategory()));
			templateContent = templateContent.replace("{?$sortname?}", 
					getCategoryById(String.valueOf(novel.getTopCategory()), CategoryGradeEnum.TOP).getName());
			templateContent = templateContent.replace("{?$author?}", novel.getAuthor());
			templateContent = templateContent.replace("{?$meta_author?}", novel.getAuthor());
			
			templateContent = templateContent.replace("{pinyin}", 
					StringUtils.isBlank(novel.getPinyin())?"":novel.getPinyin());
			templateContent = templateContent.replace("{?$3ey_pyh?}", 
					StringUtils.isBlank(novel.getPinyin())?"":novel.getPinyin());
			
			
			templateContent = templateContent.replace("{?$url_articleinfo?}", 
					getTrueURL(GlobalConfig.localSite.getTemplate().getInfoURL(), novel, null));
			
			// 根据时间得文件名
			FileOutputStream fos = new FileOutputStream(localPath);// 建立文件输出流
			byte tag_bytes[] = templateContent.getBytes(GlobalConfig.localSite.getCharset());
			fos.write(tag_bytes);
			fos.close();
		} catch (Exception e) {
			logger.error("生成[{}]目录页异常： ", novel.getNovelName(), e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 解析杰奇目录页模版中循环部分
	 * @param loopContent
	 * @param novel 
	 * @param chapterList
	 * @return
	 */
	private String getArticleIndexLoop(String loopContent, NovelEntity novel, List<ChapterEntity> chapterList) {
		//不支持分卷， 所以最外层的if else只执行else部分
		int startIndex = loopContent.indexOf("cname")+1;
		//获取外层if else
		String temp = loopContent.substring(0, startIndex);
		startIndex = temp.lastIndexOf(PARSE_CNT_START) + PARSE_CNT_START.length();
		int endIndex = loopContent.lastIndexOf(PARSE_CNT_END);
		//获取else endif之间的内容
		String content = loopContent.substring(startIndex, endIndex);
		
		Integer rowSize = GlobalConfig.localSite.getTemplate().getRowSize();
		//需要循环的行数
		int loopSize = chapterList.size()%rowSize==0 ? chapterList.size()/rowSize : chapterList.size()/rowSize+1;
		StringBuffer loopBuffer = new StringBuffer();
		
		//将{?、?}替换为<、>方便正则查找
		content = content.replace("{?$", "#").replace("?}\"", "\"").replace("$", "#")
					.replace("{?", "<").replace("\"?}", "\">").replace("?}", "");
		
		String ifelseStr,condStr,ifStr,elseStr;
		for(int k=0;k<loopSize;k++) {
			
			//本次循环当前所在行最大的序号
			int _rowSize = rowSize;
			if(_rowSize > (chapterList.size()-k*rowSize)) {
				_rowSize = chapterList.size()-k*rowSize;
			}
			
			//替换变量$indexrows[i].cname1、$indexrows[i].curl1
			String newContent = content;
			for(int i=0;i<_rowSize;i++){
				ChapterEntity chapter = chapterList.get(k*rowSize+i);
				String cName = chapter.getChapterName();
				String cUrl = GlobalConfig.localSite.getTemplate().getReaderURL()
						.replace("#subDir#", String.valueOf(chapter.getNovelNo().intValue()/1000))
	        			.replace("#articleNo#", String.valueOf(chapter.getNovelNo()))
	        			.replace("#chapterNo#", String.valueOf(chapter.getChapterNo()));
				if(GlobalConfig.localSite.getUsePinyin() == 1) {
					cUrl = cUrl.replace("#pinyin#", 
							StringUtils.isBlank(novel.getPinyin()) ? "" : novel.getPinyin());
				}
				
				newContent = newContent.replace("#indexrows[i].cname"+(i+1), "#0#"+cName+"#0#")
						.replace("#indexrows[i].curl"+(i+1), cUrl);
			}
			
			//以上替换在最后 一行时会有部分无法替换， 单独遍历最后一行， 并将其替换
			for(int i=_rowSize;i<rowSize;i++){
				newContent = newContent.replace("#indexrows[i].cname"+(i+1), "#0##0#")
						.replace("#indexrows[i].curl"+(i+1), "");
			}
			
			while(PatternUtils.match(newContent, pattern)) {
				ifelseStr = PatternUtils.getValue(newContent, pattern);
				condStr = PatternUtils.getValue(ifelseStr, conditionPattern)
							.replace("#0#", "\"");
				ifStr = PatternUtils.getValue(ifelseStr, ifPattern);
				ifStr = StringUtils.isBlank(ifStr)?"":ifStr;
				elseStr = PatternUtils.getValue(ifelseStr, elsePattern);
				elseStr = StringUtils.isBlank(elseStr)?"":elseStr;
				
				if((Boolean)ScriptUtils.evalBoolean(condStr, null)){
					newContent = newContent.replace(ifelseStr, ifStr.replace("#0#", "\""));
				} else {
					newContent = newContent.replace(ifelseStr, elseStr.replace("#0#", ""));
				}
			}
			loopBuffer.append(newContent);
		}
		return loopBuffer.toString();
	}

	@Override
	public void buildChapterCntHtml(NovelEntity novel,
			ChapterEntity chapter, String content, PreNextChapter preNext) {
		int novelNo = novel.getNovelNo().intValue();
        String localPath = FileHelper.getHtmlFilePath(novel, chapter);
       
        String dir = localPath.substring(0, localPath.lastIndexOf("/"));
        if(!new File(dir).exists()){
        	new File(dir).mkdirs();
        }
        
    	String template  = GlobalConfig.localSite.getTemplate().getReader();
		try {
			FileInputStream fis = new FileInputStream(template);// 读取模板文件
			int lenght = fis.available();
			byte bytes[] = new byte[lenght];
			fis.read(bytes);
			fis.close();
			String templateContent = new String(bytes, GlobalConfig.localSite.getCharset());
			
			templateContent = templateContent.replace("{?$articleid?}", String.valueOf(novelNo));
			templateContent = templateContent.replace("{?$chapterid?}", String.valueOf(chapter.getChapterNo()));
			templateContent = templateContent.replace("{?$article_id?}", String.valueOf(novelNo));
			templateContent = templateContent.replace("{?$chapter_id?}", String.valueOf(chapter.getChapterNo()));
			templateContent = templateContent.replace("{?$jieqi_title?}", chapter.getChapterName());
			templateContent = templateContent.replace("{?$jieqi_chapter?}", chapter.getChapterName());
			templateContent = templateContent.replace("{?$jieqi_volume?}", "正文");
			
			templateContent = templateContent.replace("{?$article_title?}", novel.getNovelName());
			templateContent = templateContent.replace("{?$jieqi_sitename?}", GlobalConfig.localSite.getSiteName());
			templateContent = templateContent.replace("{?$jieqi_url?}", GlobalConfig.localSite.getSiteUrl());
			templateContent = templateContent.replace("{?$jieqi_main_url?}", GlobalConfig.localSite.getSiteUrl());
			templateContent = templateContent.replace("{?$jieqi_local_url?}", GlobalConfig.localSite.getSiteUrl());
			templateContent = templateContent.replace("{?$jieqi_charset?}", GlobalConfig.localSite.getCharset());
			
			String modulesArticleUrl = GlobalConfig.localSite.getSiteUrl()+"/modules/article/";
			templateContent = templateContent.replace("{?$jieqi_modules['article']['url']?}", modulesArticleUrl);
			templateContent = templateContent.replace("{?$url_bookroom?}", modulesArticleUrl);
			templateContent = templateContent.replace("{?$dynamic_url?}", modulesArticleUrl);
			
			templateContent = templateContent.replace("{?$sortid?}", String.valueOf(novel.getTopCategory()));
			templateContent = templateContent.replace("{?$sortname?}", 
					getCategoryById(String.valueOf(novel.getTopCategory()), CategoryGradeEnum.TOP).getName());
			
			content = StringUtils.str2Html(content);
			templateContent = templateContent.replace("{?$jieqi_content?}", content);
			templateContent = templateContent.replace("{?$author?}", novel.getAuthor());
			templateContent = templateContent.replace("{?$meta_author?}", novel.getAuthor());
			
			//替换拼音
			templateContent = templateContent.replace("{pinyin}", 
					StringUtils.isBlank(novel.getPinyin())?"":novel.getPinyin());
			templateContent = templateContent.replace("{?$3ey_pyh?}", 
					StringUtils.isBlank(novel.getPinyin())?"":novel.getPinyin());
			
			templateContent = templateContent.replace("{?$preview_page?}", preNext.getPreURL());
			templateContent = templateContent.replace("{?$next_page?}", preNext.getNextURL());
			templateContent = templateContent.replace("{?$index_page?}", preNext.getChapterListURL());
			
			templateContent = templateContent.replace("{?$url_articleinfo?}", 
					getTrueURL(GlobalConfig.localSite.getTemplate().getInfoURL(), novel, null));

			FileOutputStream fos = new FileOutputStream(localPath);// 建立文件输出流
			byte tag_bytes[] = templateContent.getBytes(GlobalConfig.localSite.getCharset());
			fos.write(tag_bytes);
			fos.close();
		} catch (Exception e) {
			logger.error("生成[{}][{}]章节内容异常！", novel.getNovelName(), chapter.getChapterName());
		}
	}

	@Override
	public String loadChapterContent(ChapterEntity chapter) {
		String localPath = FileHelper.getTxtFilePath(chapter);
		return FileUtils.readFile(localPath, GlobalConfig.localSite.getCharset());
	}
	
	@Override
	public Category getCategoryById(String id, CategoryGradeEnum grade) {
		List<Category> list = null;
		if(grade == CategoryGradeEnum.TOP){
			list = GlobalConfig.TOP_CATEGORY;
		} else {
			list = GlobalConfig.SUB_CATEGORY;
		}
		for(Category c : list) {
			if(id.equalsIgnoreCase(c.getId())){
				return c;
			}
		}
		return new Category();
	}

	private String getTrueURL(String psedoURL, NovelEntity novel, ChapterEntity chapter){
		String trueURL = "";
		if(StringUtils.isNotBlank(psedoURL)){
			trueURL = psedoURL.replace("#subDir#", String.valueOf(novel.getNovelNo().intValue()/1000))
						.replace("#articleNo#", String.valueOf(novel.getNovelNo()));
			if(chapter != null) {
				trueURL = trueURL.replace("#chapterNo#", String.valueOf(chapter.getChapterNo()));
			}
			trueURL = trueURL.replace("#pinyin#", StringUtils.isBlank(novel.getPinyin()) ? "" : novel.getPinyin());
		}
		return trueURL;
	}
	
}
