package org.yi.spider.processor;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.enums.CategoryGradeEnum;
import org.yi.spider.enums.ParamEnum;
import org.yi.spider.enums.RepairParamEnum;
import org.yi.spider.exception.BaseException;
import org.yi.spider.model.CollectParamModel;
import org.yi.spider.model.RuleModel;
import org.yi.spider.pool2.ChapterObjectPool;
import org.yi.spider.pool2.NovelObjectPool;
import org.yi.spider.service.IChapterService;
import org.yi.spider.service.INovelService;
import org.yi.spider.utils.FileUtils;
import org.yi.spider.utils.HttpUtils;
import org.yi.spider.utils.ObjectUtils;
import org.yi.spider.utils.ParseUtils;
import org.yi.spider.utils.PatternUtils;
import org.yi.spider.utils.PinYinUtils;
import org.yi.spider.utils.StringUtils;

/**
 * 
 * @ClassName: ParseProcessor
 * @Description: 解析主控类
 * @author QQ tkts@qq.com 
 *
 */
public class Parser {
	
	private static final Logger logger = LoggerFactory.getLogger(Parser.class);
	
	private CloseableHttpClient httpClient;
	
	private CollectParamModel cpm;
	
	private INovelService novelService;
	
	private IChapterService chapterService;
	
	public Parser(CollectParamModel cpm) throws Exception {
		super();
		this.httpClient = HttpUtils.buildClient(120);
		this.cpm = cpm;
		try {
			novelService = NovelObjectPool.getNovelPool().borrowObject(GlobalConfig.localSite.getProgram().getName());
			chapterService = ChapterObjectPool.getChapterPool().borrowObject(GlobalConfig.localSite.getProgram().getName());
		} catch (Exception e) {
			throw new Exception("初始化解析处理器失败,对象池异常！", e);
		}
	}
	
	public Parser(CloseableHttpClient httpClient, CollectParamModel cpm) throws Exception {
		super();
		this.httpClient = httpClient;
		this.cpm = cpm;
		try {
			novelService = NovelObjectPool.getNovelPool().borrowObject(GlobalConfig.localSite.getProgram().getName());
			chapterService = ChapterObjectPool.getChapterPool().borrowObject(GlobalConfig.localSite.getProgram().getName());
		} catch (Exception e) {
			throw new Exception("初始化解析处理器失败,对象池异常！", e);
		}
	}

	public void process() {
		for (String novelNo : cpm.getNumList()) {
			try {
				String assignURL = cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_URL).getPattern();
				String infoURL = ParseUtils.getAssignURL(assignURL, novelNo);
				 
				//小说信息页源码
				String infoSource = ParseUtils.getSource(httpClient, cpm, infoURL);
				
				// 获取书名
	            String novelName = PatternUtils.getValue(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_NAME));
	            if(novelName==null || novelName.isEmpty()){
	            	throw new BaseException("小说名为空, 目标链接："+infoURL);
	            }
	            
	            // 判断小说是否已经存在， 然后根据配置中的是否添加新书，决定是否继续采集
	            NovelEntity novel = novelService.find(novelName);
	            
	        	if(novel != null) {
	        		//如果书籍已存在则从数据库中取出， 如果是修复模式则更新书籍信息
	        		if(cpm.getCollectType()==ParamEnum.REPAIR_ALL || cpm.getCollectType()==ParamEnum.REPAIR_ASSIGN) {
	        			NovelEntity newNovel = getNovelInfo(infoSource, novelName);
	        			novelService.repair(novel, newNovel);
	        			//修复参数中包含封面时重新下载封面
	        			if(cpm.getRepairParam() != null 
	        					&& cpm.getRepairParam().contains(RepairParamEnum.COVER.getValue())) {
	        				getCover(infoSource, novel);
	        			}
	        		}
	        	} else {
	        		//如果书籍不存在则判断是否允许新书入库， 如果允许则抓取书籍信息
	        		if(GlobalConfig.collect.getBoolean(ConfigKey.ADD_NEW_BOOK, false)) {
	        			novel = getNovelInfo(infoSource, novelName);
	        			novel.setNovelNo(novelService.saveNovel(novel));
	        			//下载小说封面
		        		getCover(infoSource, novel);
	        		}
	        	}
	        	if(novel!=null) {
	            	parse(novelNo, novel, infoSource);
	            }
			} catch(Exception e) {
				logger.error("解析异常, 原因："+e.getMessage(), e);
                continue;
			}
		}
		/*//不需要归还， 下次循环使用相同的对象， 其实novelService和chapterService不需要用对象池
		if(novelService != null)
			NovelObjectPool.getNovelPool().returnObject(GlobalConfig.localSite.getProgram().getName(), novelService);
		if(chapterService != null)
			ChapterObjectPool.getChapterPool().returnObject(GlobalConfig.localSite.getProgram().getName(), chapterService);*/
	}
	
	/**
	 * 
	 * <p>解析核心方法， 解析小说信息页</p>
	 * @param novelNo		目标站小说号
	 * @param novel			本地小说对象
	 * @param infoSource	信息页源码
	 * @throws Exception
	 */
	private void parse(String novelNo, NovelEntity novel, String infoSource) throws Exception {
		// 小说目录页地址
        // 可能是/book/770.html、book/770.html、http://www.henniu110.com/book/770.html等
        String novelPubKey = ParseUtils.get(infoSource, 
        		cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVELINFO_GETNOVELPUBKEY));
        // novelPubKey为空则说明目录页地址不是通过页面获取， 而是在规则中指定好了
        if(novelPubKey == null || novelPubKey.isEmpty()){
        	novelPubKey = cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBINDEX_URL).getPattern();
        	if(novelPubKey==null || novelPubKey.isEmpty()){
        		throw new BaseException("无法从页面获取目录页地址， 需要在规则中PubIndexUrl项指定目录页地址");
        	}
        	novelPubKey = ParseUtils.getAssignURL(novelPubKey, novelNo);
        }
        
        // 小说目录页地址 http://a/b/c.html
        String novelPubKeyURL = StringUtils.getFullUrl(cpm.getRemoteSite().getSiteUrl(), novelPubKey);
        
        // 小说目录页内容
        String menuSource = HttpUtils.getContent(httpClient, novelPubKeyURL, cpm.getRemoteSite().getCharset());
        RuleModel pubIndexContentRule = cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUB_INDEX_CONTENT);
        if(pubIndexContentRule != null){
        	menuSource = ParseUtils.get(menuSource, pubIndexContentRule);
        }
        
        // 根据内容取得章节名
        List<String> chapterNameList = PatternUtils.getValues(menuSource,
        		cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBCHAPTER_NAME));
        // 获得章节地址(章节编号)，所获得的数量必须和章节名相同
        List<String> chapterKeyList = PatternUtils.getValues(menuSource,
        		cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBCHAPTER_GETCHAPTERKEY));

        if (chapterNameList.size() != chapterKeyList.size()) {
            logger.warn("小说【" + novel.getNovelName() + "】章节名称数和章节地址数不一致， 可能导致采集结果混乱！");
        }
        
        ChapterEntity chapter = new ChapterEntity();
        chapter.setNovelNo(novel.getNovelNo());
        chapter.setNovelName(novel.getNovelName());
        
        //修复
        if(cpm.getCollectType() == ParamEnum.REPAIR_ALL || cpm.getCollectType() == ParamEnum.REPAIR_ASSIGN) {
        	repaireChapter(novelNo, novel, novelPubKeyURL, chapterNameList,
					chapterKeyList);
        } else {
        	//为防止其他小说中存在同名章节情况， 使用以下方式进行采集判断
	        normalCollect( novelNo, novel, chapter, novelPubKeyURL,
					chapterNameList, chapterKeyList);
        }
        
	}
	
	/**
     * <p>正常采集</p>
     * @param novelNo 			目标站小说号
     * @param novel				为本地站构造的小说对象
     * @param chapter			为本地站构造的章节对象
     * @param novelPubKeyURL	目标站小说内容页采集地址
     * @param chapterNameList	从目标站获取的章节名称列表
     * @param chapterKeyList	从目标站获取的章节序号列表
     * @throws Exception
     */
	private void normalCollect(String novelNo, NovelEntity novel, ChapterEntity chapter,
			String novelPubKeyURL, List<String> chapterNameList, List<String> chapterKeyList) throws Exception {
		//获取已经存在的章节列表
		List<ChapterEntity> chapterListDB = chapterService.getChapterList(novel);
    	for(int i=0;i<chapterNameList.size();i++){
    		String cname = chapterNameList.get(i);
    		boolean needCollect = true;
    		for(ChapterEntity tc:chapterListDB){
    			//章节存在则不做处理， 否则采集
    			if(cname.equalsIgnoreCase(tc.getChapterName())){
    				needCollect = false;
    				break;
    			}
    		}
    		if(needCollect){
    			String cno = chapterKeyList.get(i);
				chapter.setChapterName(cname);
				logger.info("采集小说: {}，章节：{}", new Object[] { novel.getNovelName(), cname});
			    collectChapter( novelNo, cno, novelPubKeyURL, novel, chapter);
    		}
    	}
	}

	/**
	 * 
	 * <p>修复错误章节</p>
	 * @param novelNo			目标站小说号
	 * @param novel				本地站小说对象
	 * @param novelPubKeyURL	目录页地址
	 * @param chapterNameList	采集到的章节名列表
	 * @param chapterKeyList	采集到的章节序号列表
	 * @throws Exception
	 */
	private void repaireChapter(String novelNo, NovelEntity novel,
			String novelPubKeyURL, List<String> chapterNameList,
			List<String> chapterKeyList) throws Exception {
		ChapterEntity chapter = null;
		List<ChapterEntity> chapterListDB = chapterService.getChapterList(novel);
		//修复空章节
		for(int i=0;i<chapterNameList.size();i++){
			String cname = chapterNameList.get(i);
			for(ChapterEntity tc:chapterListDB){
				//章节已存在的时候判断该章节对应的txt文件是否存在， 如果不存在则采集，存在不做处理
				if(cname.equalsIgnoreCase(tc.getChapterName())){
					chapter = chapterService.getChapterByChapterNameAndNovelNo(tc);
					if(chapter != null){
						String txtFile = chapterService.getTxtFilePath(chapter);
						if(!new File(txtFile).exists()){
							logger.info("修复小说: {}，章节：{}", new Object[] { novel.getNovelName(), cname});
		 					collectChapter(novelNo, chapterKeyList.get(i), novelPubKeyURL, novel, chapter);
						}
					}
					break;
				}
			}
		}
	}
	
	/**
	 * 
	 * <p>采集入库主方法</p>
	 * @param novelNo			
	 * @param cno				
	 * @param novelPubKeyURL
	 * @param novel
	 * @param tc
	 * @throws Exception
	 */
	private void collectChapter(String novelNo,String cno, String novelPubKeyURL, 
			NovelEntity novel, ChapterEntity tc) throws Exception {
		
		ChapterEntity chapter = tc.clone();
		
		// 章节地址-不完全地址
		String chapterURL = cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBCONTENT_URL).getPattern();
		chapterURL = StringUtils.getRemoteChapterUrl(chapterURL, novelPubKeyURL, novelNo, cno, cpm);

		// 章节页源码
		String chapterSource = HttpUtils.getContent(httpClient, chapterURL, cpm.getRemoteSite().getCharset());
		// 章节内容
		String chapterContent = ParseUtils.get(chapterSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.PUBCONTENT_TEXT));
//		chapterContent = StringUtil.removeRN(chapterContent);
		chapterContent = StringUtils.removeBlankLine(chapterContent);
	    chapterContent = StringUtils.replaceHtml(chapterContent);

		int chapterOrder = chapterService.getChapterOrder(chapter);
		chapter.setChapterOrder(chapterOrder);
		chapter.setSize(chapterContent.length());
		
		Integer chapterNo = 0;
		if(chapter.getChapterNo()==null||chapter.getChapterNo()==0){
			chapterNo = chapterService.save(chapter);
			// 只有新采集的章节才会在保存后更新小说信息
			Map<String, Object> totalMap = chapterService.getTotalInfo(novel.getNovelNo());
			novel.setChapters(ObjectUtils.obj2Int(totalMap.get("count")));
			novel.setLastChapterName(chapter.getChapterName());
			novel.setLastChapterno(chapterNo);
			novel.setSize(ObjectUtils.obj2Int(totalMap.get("size")));
			novelService.update(novel);
			
			chapter.setChapterNo(chapterNo);
		} else {
			chapterNo = chapter.getChapterNo();
		}
		
		if (StringUtils.isBlank(chapterContent)) {
		    logger.error("章节内容采集出错， 目标地址：{}， 本站小说号：{}， 章节号：{}", 
		    		new Object[] { chapterURL, novel.getNovelNo() ,chapterNo });
		}
		FileUtils.writeTxtFile(novel, chapter, chapterContent);
		if (GlobalConfig.collect.getBoolean(ConfigKey.CREATE_HTML, false)) {
			/*Integer nextChapterNo = chapterService.get(chapter, 1);
			Integer preChapterNo = chapterService.get(chapter, -1);
			List<String> preNext = null;
			//上一章的章节号存在说明当前章节不是本书第一章， 生成静态页的时候需要重新生成上一章
			if(preChapterNo>0){
		        Integer pre2ChapterNo = chapterService.get(chapter, -2);
		        //重新产生上个章节的html内容
		        preNext = getPreNextUrl(pre2ChapterNo, chapter.getChapterNo(), String.valueOf(novel.getNovelNo()));
		        ChapterEntity preChapter = chapterService.get(preChapterNo);
		        String preChapterContent = htmlBuilder.getChapterContent(preChapter);
		        htmlBuilder.generateChapterCntHtml(article, preChapter, preChapterContent, preNext);
			}
		    //生成当前章节的html内容
		    preNext = getPreNextUrl(preChapterNo, nextChapterNo, String.valueOf(novel.getNovelNo()));
		    htmlBuilder.generateChapterCntHtml(article, chapter, chapterContent, preNext);
		    htmlBuilder.generateArticleIndexHtml(article, preNext);*/
		}
	}

	/*private List<String> getPreNextUrl(Integer pre2ChapterNo, Integer chapterNo, String valueOf) {
		//获取目录页地址
        String novelPubKeyURL = Constants.JIEQI_INFO_DYNAMIC_URL;
        String fakeInfo = String.valueOf(Constants.systemParams.get(Constants.FAKE_INFO));
        if (fakeInfo != null && !fakeInfo.isEmpty() && !"null".equalsIgnoreCase(fakeInfo)) {
            novelPubKeyURL = fakeInfo;
        }
        novelPubKeyURL = novelPubKeyURL.replace("{NovelKey}", articleNo).replace("<{$id}>", articleNo);
        novelPubKeyURL = StringUtils.getFullUrl(Constants.localSite.getSiteUrl(), novelPubKeyURL);
        
        //获取章节地址
        String cntUrl = Constants.JIEQI_CNT_URL;
        // 上一章
        if (preChapterNo == null || preChapterNo <= 0) {
            result.add(novelPubKeyURL);
        } else {
            result.add(getLocalChapterUrl(articleNo, cntUrl, String.valueOf(preChapterNo)));
        }
        // 下一章
        if (nextChapterNo == null || nextChapterNo <= 0) {
            result.add(novelPubKeyURL);
        } else {
            result.add(getLocalChapterUrl(articleNo, cntUrl, String.valueOf(nextChapterNo)));
        }
        // 目录页地址
        result.add(novelPubKeyURL);
        return result;
		return null;
	}*/

	/**
	 * 
	 * <p>获取小说信息</p>
	 * @param infoSource
	 * @param novelName
	 * @return
	 */
	private NovelEntity getNovelInfo(String infoSource, String novelName) {

		NovelEntity novel = new NovelEntity();
		novel.setNovelName(novelName);

		String initial = PinYinUtils.getFirst1Spell(novelName);
		novel.setInitial(initial);
		
		String author = ParseUtils.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_AUTHOR));
		novel.setAuthor(author);
		
		String topCat = "";
		Integer cat = 0;
		//正常采集  或者  修复参数中包含对应项时才会采集对应项
		if(willParse(RepairParamEnum.TOP.getValue())) {
			topCat = ParseUtils.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.LAGER_SORT));
	        cat = ParseUtils.getCategory(topCat, CategoryGradeEnum.TOP);
	        novel.setTopCategory(cat);
		}
        
		if(willParse(RepairParamEnum.SUB.getValue())) {
	        String smallSort = ParseUtils.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.SMALL_SORT));
	        cat = ParseUtils.getCategory(smallSort, CategoryGradeEnum.SUB);
	        novel.setSubCategory(cat);
		}
        
		if(willParse(RepairParamEnum.INTRO.getValue())) {
	        String novelIntro = ParseUtils.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_INTRO));
	        if(StringUtils.isNotBlank(novelIntro)) {
		        novelIntro = StringUtils.replaceHtml(novelIntro);
		        novelIntro = StringUtils.removeBlankLine(novelIntro);
	        }
	        novel.setIntro(novelIntro);
		}
        
		if(willParse(RepairParamEnum.KEYWORDS.getValue())) {
			String novelKeyword = ParseUtils.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_KEYWORD));
			novel.setKeywords(novelKeyword);
		}
		if(willParse(RepairParamEnum.DEGREE.getValue())) {
	        String novelDegree = ParseUtils.get(infoSource, cpm.getRuleMap().get(RuleModel.RegexNamePattern.NOVEL_DEGREE));
	        String fullFlagStr = GlobalConfig.collect.getString(ConfigKey.FULL_FLAG, "已完结");
	        // 完本为true， 连载false
	        boolean fullFlag = fullFlagStr.equals(novelDegree) ? true : false;
	        novel.setFullFlag(fullFlag);
		}
        
	    return novel;
	}

	/**
	 * 根据采集参数判断具体的采集项目是否解析
	 * @param param
	 * @return
	 */
	private boolean willParse(String param) {
		return cpm.getCollectType()==ParamEnum.COLLECT_All 
			|| cpm.getCollectType()==ParamEnum.COLLECT_ASSIGN
			|| ((cpm.getCollectType()==ParamEnum.REPAIR_ALL 
					|| cpm.getCollectType()==ParamEnum.REPAIR_ASSIGN)
				&& cpm.getRepairParam() != null && cpm.getRepairParam().contains(param));
	}

	/**
	 * 
	 * <p>获取小说封面图片类型， 并下载封面</p>
	 * @param infoSource
	 * @param novel
	 */
	private void getCover(String infoSource, NovelEntity novel) {
		Integer imgFlag = ParseUtils.getNovelCover(novel, infoSource, cpm);
        novel.setImgFlag(imgFlag);
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public CollectParamModel getCpm() {
		return cpm;
	}

	public void setCpm(CollectParamModel cpm) {
		this.cpm = cpm;
	}

}
