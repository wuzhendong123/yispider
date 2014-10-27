package org.yi.spider.processor;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
import org.yi.spider.enums.ProgramEnum;
import org.yi.spider.enums.RepairParamEnum;
import org.yi.spider.exception.BaseException;
import org.yi.spider.factory.impl.ServiceFactory;
import org.yi.spider.helper.FileHelper;
import org.yi.spider.helper.ParseHelper;
import org.yi.spider.model.CollectParam;
import org.yi.spider.model.DuoYinZi;
import org.yi.spider.model.PreNextChapter;
import org.yi.spider.model.Rule;
import org.yi.spider.service.IChapterService;
import org.yi.spider.service.IHtmlBuilder;
import org.yi.spider.service.INovelService;
import org.yi.spider.utils.HttpUtils;
import org.yi.spider.utils.ObjectUtils;
import org.yi.spider.utils.PinYinUtils;
import org.yi.spider.utils.StringUtils;

/**
 * 
 * @ClassName: ParseProcessor
 * @Description: 解析主控类
 * @author QQ
 */
public class NovelParser extends BaseProcessor{
	
	private static final Logger logger = LoggerFactory.getLogger(NovelParser.class);
	
	private CollectParam cpm;
	private String novelNo;
	
	private INovelService novelService;
	private IChapterService chapterService;
	private IHtmlBuilder htmlBuilder;
	
	public NovelParser(CollectParam cpm) throws Exception {
		super();
		this.cpm = cpm;
		init();
	}
	
	public NovelParser(CollectParam cpm, String novelNo) throws Exception {
		super();
		this.cpm = cpm;
		this.novelNo = novelNo;
		init();
	}
	
	private void init() throws Exception {
		/*try {
			novelService = NovelObjectPool.getPool().borrowObject(GlobalConfig.localSite.getProgram().getName());
			chapterService = ChapterObjectPool.getPool().borrowObject(GlobalConfig.localSite.getProgram().getName());
			if (GlobalConfig.collect.getBoolean(ConfigKey.CREATE_HTML, false)) {
				//需要生成静态html时， 获取HtmlBuilder对象
				htmlBuilder = HtmlBuilderObjectPool.getPool().borrowObject(GlobalConfig.localSite.getProgram().getName());
			}
		} catch (Exception e) {
			throw new Exception("初始化解析处理器失败,对象池异常！", e);
		}*/
		novelService = new ServiceFactory().createNovelService(GlobalConfig.localSite.getProgram().getName());
		chapterService = new ServiceFactory().createChapterService(GlobalConfig.localSite.getProgram().getName());
		if (GlobalConfig.collect.getBoolean(ConfigKey.CREATE_HTML, false)) {
			//需要生成静态html时， 获取HtmlBuilder对象
			htmlBuilder = new ServiceFactory().createHtmlBuilder(GlobalConfig.localSite.getProgram().getName());
		}
	}
	
	public void run() {
		prase();
	}
	
	public void prase() {
		if(!GlobalConfig.SHUTDOWN) {
			proc();
		}
	}

	public void proc() {
		
		// 初始化HTTP连接
		int timeOut = GlobalConfig.site.getInt(ConfigKey.CONNECTION_TIMEOUT, 60);
		CloseableHttpClient httpClient = HttpUtils.buildClient(timeOut * 1000);
				
		try {
			
			String infoURL = ParseHelper.getInfoRUL(cpm, novelNo);
			 
			//小说信息页源码
			String infoSource = ParseHelper.getSource(httpClient, cpm, infoURL);
			
			// 获取书名
            String novelName = ParseHelper.getNovelName(infoSource, cpm);
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
        				logger.debug("修复小说{}封面", novel.getNovelName());
        				getCover(infoSource, novel);
        			}
        		}
        	} else {
        		//如果书籍不存在则判断是否允许新书入库， 如果允许则抓取书籍信息
        		if(cpm.getCollectType()==ParamEnum.COLLECT_All || cpm.getCollectType()==ParamEnum.COLLECT_ASSIGN){
        			if(GlobalConfig.collect.getBoolean(ConfigKey.ADD_NEW_BOOK, false)) {
	        			novel = addNovel(infoSource, novelName);
	        		}
        		}
        	}
        	//-i参数， 只入库小说， 不采集章节
        	if(cpm.getCollectType()==ParamEnum.IMPORT) {
        		if(novel == null)
        			novel = addNovel(infoSource, novelName);
        	} else if(novel!=null) {
        		if(!GlobalConfig.SHUTDOWN) {
        			parse(novelNo, novel, infoSource);
        		}
            }
		} catch(Exception e) {
			if(logger.isDebugEnabled()){
				logger.error("解析异常, 原因："+e.getMessage(), e);
			} else {
				logger.error("解析异常, 原因："+e.getMessage());
			}
		} finally {
			try {
				if(httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				logger.error("关闭连接出错,原因：{}", e.getMessage());
			}
		}
		//归还， 下次循环使用相同的对象， 其实novelService和chapterService不需要用对象池
//		if(novelService != null)
//				NovelObjectPool.getPool().returnObject(GlobalConfig.localSite.getProgram().getName(), novelService);
//		if(chapterService != null)
//				ChapterObjectPool.getPool().returnObject(GlobalConfig.localSite.getProgram().getName(), chapterService);
	}

	/**
	 * 小说入库
	 * @param infoSource
	 * @param novelName
	 * @return
	 * @throws Exception 
	 */
	private synchronized NovelEntity addNovel(String infoSource, String novelName)
			throws Exception {
		NovelEntity novel = getNovelInfo(infoSource, novelName);
		
		String pinyin = novelName;
		//处理多音字情况
		for(DuoYinZi dyz : GlobalConfig.duoyin) {
			pinyin = pinyin.replace(dyz.getName(), dyz.getPinyin());
		}
		pinyin = PinYinUtils.getPinYin(pinyin).trim();
		Integer count = novelService.getMaxPinyin(pinyin).intValue();
		if(count > 0){
			pinyin = pinyin + (count+1);
		}
		novel.setPinyin(pinyin);
		novel.setInitial(PinYinUtils.getPinyinShouZiMu(pinyin));
		novel.setNovelNo(novelService.saveNovel(novel));
		logger.debug("新入库小说{}", novelName);
		//下载小说封面
		getCover(infoSource, novel);
		return novel;
	}
	
	

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

//		String initial = PinYinUtils.getFirst1Spell(novelName);
//		novel.setInitial(initial);
		
		String author = ParseHelper.getNovelAuthor(infoSource, cpm);
		novel.setAuthor(author);
		
		String topCat = "";
		//正常采集  或者  修复参数中包含对应项时才会采集对应项
		if(willParse(RepairParamEnum.TOP.getValue())) {
			logger.debug("获取小说{}大类", novel.getNovelName());
			topCat = ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.LAGER_SORT));
			Integer cat = ParseHelper.getCategory(topCat, CategoryGradeEnum.TOP);
	        novel.setTopCategory(cat);
		}
        
		if(willParse(RepairParamEnum.SUB.getValue())) {
			logger.debug("获取小说{}细类", novel.getNovelName());
	        String smallSort = ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.SMALL_SORT));
	        Integer cat = ParseHelper.getCategory(smallSort, CategoryGradeEnum.SUB);
	        novel.setSubCategory(cat);
		}
        
		if(willParse(RepairParamEnum.INTRO.getValue())) {
			logger.debug("获取小说{}简介", novel.getNovelName());
			String intro = ParseHelper.getNovelIntro(infoSource, cpm);
	        novel.setIntro(StringUtils.isBlank(intro)?"":intro);
		}
        
		if(willParse(RepairParamEnum.KEYWORDS.getValue())) {
			logger.debug("获取小说{}关键词", novel.getNovelName());
			String keywords = ParseHelper.getNovelKeywrods(infoSource, cpm);
			novel.setKeywords(StringUtils.isBlank(keywords)?"":keywords);
		}
		if(willParse(RepairParamEnum.DEGREE.getValue())) {
			logger.debug("获取小说{}写作进度", novel.getNovelName());
	        String novelDegree = ParseHelper.get(infoSource, cpm.getRuleMap().get(Rule.RegexNamePattern.NOVEL_DEGREE));
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
			|| cpm.getCollectType()==ParamEnum.IMPORT
			|| ((cpm.getCollectType()==ParamEnum.REPAIR_ALL 
					|| cpm.getCollectType()==ParamEnum.REPAIR_ASSIGN)
				&& cpm.getRepairParam() != null && cpm.getRepairParam().contains(param));
	}

	/**
	 * 
	 * <p>获取小说封面图片类型， 并下载封面</p>
	 * @param infoSource
	 * @param novel
	 * @throws Exception 
	 */
	private void getCover(String infoSource, NovelEntity novel) throws Exception {
		Integer imgFlag = ParseHelper.getNovelCover(novel, infoSource, cpm);
        novel.setImgFlag(imgFlag);
	}
	
	/**
	 * 
	 * @param novelNo		目标站小说号
	 * @param novel			本地小说对象
	 * @param infoSource	信息页源码
	 * @throws Exception
	 */
	private void parse(String novelNo, NovelEntity novel, String infoSource) throws Exception {
		// 小说目录页地址
        String novelPubKeyURL = ParseHelper.getNovelMenuURL(infoSource, novelNo, cpm);
        
        String novelInfoExtra = ParseHelper.getNovelInfoExtra(infoSource, cpm);
        if(StringUtils.isNotBlank(novelInfoExtra)){
        	//写入last.txt
        	FileHelper.writeLastTxtFile(FileHelper.getLastTxtFilePath(novel), novelInfoExtra);
        }
        
        // 小说目录页内容
        String menuSource = ParseHelper.getChapterListSource(novelPubKeyURL, cpm);
        
        // 根据内容取得章节名
        List<String> chapterNameList = ParseHelper.getChapterNameList(menuSource, cpm);
        // 获得章节地址(章节编号)，所获得的数量必须和章节名相同
        List<String> chapterKeyList = ParseHelper.getChapterNoList(menuSource, cpm);

        if (chapterNameList.size() != chapterKeyList.size()) {
            logger.warn("规则：{}, 小说[{}]章节名称数和章节地址数不一致， 可能导致采集结果混乱！", 
            		cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_NAME).getPattern(), novel.getNovelName());
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
    		String cname = chapterNameList.get(i).trim();
    		boolean needCollect = true;
    		for(ChapterEntity tc:chapterListDB){
    			//章节存在则不做处理， 否则采集
    			if(cname.equalsIgnoreCase(tc.getChapterName().trim())){
    				needCollect = false;
    				break;
    			}
    		}
    		if(needCollect && !GlobalConfig.SHUTDOWN){
    			String cno = chapterKeyList.get(i);
				chapter.setChapterName(cname);
				logger.info("采集小说: {}，章节：{}， 规则：{}", 
						novel.getNovelName(), cname, cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_NAME).getPattern());
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
				if(!GlobalConfig.SHUTDOWN) {
					//章节已存在的时候判断该章节对应的txt文件是否存在， 如果不存在则采集，存在不做处理
					if(cname.equalsIgnoreCase(tc.getChapterName())){
						chapter = chapterService.getChapterByChapterNameAndNovelNo(tc);
						if(chapter != null){
					        if(cpm.getRepairParam() != null 
					        		&& cpm.getRepairParam().contains(RepairParamEnum.ETXT.getValue())) {
					        	//参数为-r或-ra， 并且-rp参数中包含etxt时只采集本地缺失的章节内容
					        	String txtFile = FileHelper.getTxtFilePath(chapter);
								if(!new File(txtFile).exists()){
									logger.debug("修复小说: {}，规则:{}，修复空章节：{}", 
											new Object[] { novel.getNovelName(), 
												cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_NAME).getPattern(),cname});
				 					collectChapter(novelNo, chapterKeyList.get(i), novelPubKeyURL, novel, chapter);
								}
					        } else if(cpm.getRepairParam() != null 
					        		&& cpm.getRepairParam().contains(RepairParamEnum.TXT.getValue())){
					        	//参数为-r或-ra， 并且-rp参数中包含txt时重新采集章节内容
					        	logger.debug("修复小说: {}，规则：{}，重新采集章节：{}", 
					        			new Object[] { novel.getNovelName(), 
					        				cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_NAME).getPattern(), cname});
					        	collectChapter(novelNo, chapterKeyList.get(i), novelPubKeyURL, novel, chapter);
					        } 
						}
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * <p>采集入库主方法</p>
	 * @param novelNo			目标站小说号	
	 * @param cno				目标站章节号
	 * @param novelPubKeyURL	
	 * @param novel
	 * @param tc
	 * @throws Exception
	 */
	private void collectChapter(String novelNo,String cno, String novelPubKeyURL, 
			NovelEntity novel, ChapterEntity tc) throws Exception {
		
		ChapterEntity chapter = tc.clone();
		
		setChapterOrder(chapter);
		
		Integer chapterNo = 0;
		//章节号不存在说明入口是正常采集normalCollect， 此时判断判断数据库中如果存在对应章节则说明已经采集过， 直接返回
		if(chapter.getChapterNo()==null||chapter.getChapterNo()==0){
			//此处即使做了判断也可能会重复采集， 这种情况通过在数据库中增加唯一索引进行控制
			boolean chapterExist = chapterService.exist(chapter);
			if(chapterExist) {
				return ;
			}
			chapter.setSize(0);
			chapterNo = chapterService.save(chapter).intValue();
			chapter.setChapterNo(chapterNo);
			//新采集的小说需要更新最后章节
			novel.setLastChapterName(chapter.getChapterName());
			novel.setLastChapterno(chapterNo);
		} else {
			chapterNo = chapter.getChapterNo();
		}
		
		// 章节地址-不完全地址
		String chapterURL = ParseHelper.getChapterURL(novelPubKeyURL, novelNo, cno, cpm);

		//如果无法获取章节地址， 则不做操作， 适用于只采集目录， 不采集内容的情况
		if(StringUtils.isNotBlank(chapterURL)) {
			// 章节页源码
			String chapterSource = ParseHelper.getChapterSource(chapterURL, cpm);
			// 章节内容
			String chapterContent = ParseHelper.getChapterContent(chapterSource, cpm);

			if (StringUtils.isBlank(chapterContent)) {
			    logger.error("章节内容采集出错， 目标地址：{}， 本站小说号：{}， 章节号：{}", 
			    		new Object[] { chapterURL, novel.getNovelNo() ,chapterNo });
			}
			//写txt文件
			if(StringUtils.isBlank(chapterContent)) {
				logger.error("采集到空章节， 规则：{}， 小说名：{}， 章节名：{}", 
						cpm.getRuleMap().get(Rule.RegexNamePattern.GET_SITE_NAME).getPattern(), novel.getNovelName(), chapter.getChapterName());
			} else {
				FileHelper.writeTxtFile(novel, chapter, chapterContent);
			}
			
			//更新对应章节chapter的size字段
			chapter.setSize(chapterContent.length());
			chapterService.updateSize(chapter);
			
			// 无论是新采集的还是修复的， 统一重新统计章节数量和章节总字数
			Map<String, Object> totalMap = chapterService.getTotalInfo(novel.getNovelNo());
			novel.setChapters(ObjectUtils.obj2Int(totalMap.get("count")));
			novel.setSize(ObjectUtils.obj2Int(totalMap.get("size")));
			novelService.update(novel);
			
			//设置生成静态页则执行生成操作
			if (GlobalConfig.collect.getBoolean(ConfigKey.CREATE_HTML, false)) {
				ChapterEntity nextChapter = chapterService.get(chapter, 1);
				ChapterEntity preChapter = chapterService.get(chapter, -1);
				PreNextChapter preNext = null;
				//上一章存在说明当前章节不是本书第一章， 生成静态页的时候需要重新生成上一章
				if(preChapter != null){
					//重新产生上个章节的html内容
					//获取上页的上页
					ChapterEntity pre2Chapter = chapterService.get(chapter, -2);
			        preNext = getPreNext(pre2Chapter, chapter, novel);
			        String preChapterContent = htmlBuilder.loadChapterContent(preChapter);
			        htmlBuilder.buildChapterCntHtml(novel, preChapter, preChapterContent, preNext);
				}
			    //生成当前章节的html内容
			    preNext = getPreNext(preChapter, nextChapter, novel);
			    htmlBuilder.buildChapterCntHtml(novel, chapter, chapterContent, preNext);
			    htmlBuilder.buildChapterListHtml(novel, chapterService.getChapterList(novel));
			}
		}
	}

	private void setChapterOrder(ChapterEntity chapter) throws SQLException {
		if(GlobalConfig.localSite.getProgram() == ProgramEnum.JIEQI) {
			int chapterOrder = chapterService.getChapterOrder(chapter);
			chapter.setChapterOrder(chapterOrder + 1);
		}
	}

	/**
	 * 获取当前章节的上个章节、下个章节
	 * @param pre
	 * @param next
	 * @param novelNo
	 * @return
	 * @throws Exception 
	 */
	private PreNextChapter getPreNext(ChapterEntity pre, ChapterEntity next, NovelEntity novel) throws Exception {
		PreNextChapter pn = new PreNextChapter();
		//获取目录页地址
        String novelPubKeyURL = GlobalConfig.localSite.getTemplate().getChapterURL();
        int novelNo = novel.getNovelNo().intValue();
        
        novelPubKeyURL = novelPubKeyURL.replace("#subDir#", String.valueOf(novelNo/1000))
        		.replace("#articleNo#", String.valueOf(novelNo));
        
        if(GlobalConfig.localSite.getUsePinyin() == 1) {
        	novelPubKeyURL = novelPubKeyURL.replace("#pinyin#", novel.getPinyin());
		}
        
        novelPubKeyURL = StringUtils.getFullUrl(GlobalConfig.localSite.getSiteUrl(), novelPubKeyURL);
        
        // 如果上一章不存在，则url赋值为目录页地址
        if (pre == null) {
            pn.setPreURL(novelPubKeyURL);
        } else {
        	pn.setPreURL(getLocalChapterUrl(GlobalConfig.localSite.getTemplate().getReaderURL(),
        			novel, pre.getChapterNo()));
        }
        // 下一章
        if (next == null) {
            pn.setNextURL(novelPubKeyURL);
        } else {
            pn.setNextURL(getLocalChapterUrl(GlobalConfig.localSite.getTemplate().getReaderURL(),
            		novel, next.getChapterNo()));
        }
        // 目录页地址
        pn.setChapterListURL(novelPubKeyURL);
		return pn;
	}
	
	private String getLocalChapterUrl(String url, NovelEntity novel, Integer chapterNo) throws Exception {
		int novelNo = novel.getNovelNo().intValue();
        url = url.replace("#subDir#", String.valueOf(novelNo/1000))
				.replace("#articleNo#", String.valueOf(novelNo))
				.replace("#chapterNo#", String.valueOf(chapterNo));
        if(GlobalConfig.localSite.getUsePinyin() == 1) {
        	url = url.replace("#pinyin#", novel.getPinyin());
		}
        // 章节地址-全路径
        url = StringUtils.getFullUrl(GlobalConfig.localSite.getSiteUrl(), url);
        return url;
    }

}
