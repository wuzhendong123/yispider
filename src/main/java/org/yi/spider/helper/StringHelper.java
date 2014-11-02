package org.yi.spider.helper;

import org.yi.spider.model.CollectParam;
import org.yi.spider.utils.StringUtils;

public class StringHelper {
	
	/**
	 * 匹配域名aaa.bbb.ccc/ddd
	 */
	private static final String REGEX_DOMAIN = ".+\\..+/.+";
	
	/**
     * 获取目标站内容页的完整地址
     * @param novelNo	目标站小说号
     * @param novelPubKeyURL 目标站内容页地址
     * @param cno	目标站章节号
	 * @param cpm 
     * @return
	 * @throws Exception 
     */
    public static String getRemoteChapterUrl(String chapterUrl, String novelPubKeyURL, String novelNo, 
    		String cno, CollectParam cpm) throws Exception {
    	if(StringUtils.isBlank(chapterUrl)) {
    		return "";
    	}
        chapterUrl = chapterUrl.replace("{ChapterKey}", cno);
        chapterUrl = ParseHelper.getAssignURL(chapterUrl, novelNo, cpm);
        
        // 获取的章节地址可能是：
        // 18641672.html、reader/1/123.html、/reader/1/123.html、http://www.xxx.com/reader/1/123.html
        if (!chapterUrl.startsWith("/") && !"http".equalsIgnoreCase(chapterUrl.substring(0, 4))
        // 获取的地址不是www.henniu110.com/reader/1/123.html
                && !chapterUrl.matches(REGEX_DOMAIN)) {
            chapterUrl = novelPubKeyURL.substring(0, novelPubKeyURL.lastIndexOf("/") + 1) + chapterUrl;
        }
        // 章节地址-全路径
        chapterUrl = StringUtils.getFullUrl(cpm.getRemoteSite().getSiteUrl(), chapterUrl);
        return chapterUrl;
    }
    
    /**
	 * 
	 * <p>根据封面后缀标识获取封面后缀名</p>
	 * @param flag
	 * @return
	 */
	public static String getCoverSuffix(int flag){
		String suffix = "";
		switch(flag)
		{
			case 0:
				break;
			case 1:
				suffix = ".jpg";
				break;
			case 2:
				suffix = ".gif";
				break;
			case 3:
				suffix = ".png";
				break;
			default:
				break;	
		}
		return suffix;
	}
	
	/**
	 * 
	 * <p>获取封面后缀标志	</p>
	 * 0-无封面
	 * 1-JPG
	 * 2-GIF
	 * 3-PNG
	 * @param novelCover
	 * @return
	 */
	public static Integer getImgFlag(String novelCover) {
		Integer flag = 0;
		if(novelCover != null && !novelCover.isEmpty()) {
			int index = novelCover.lastIndexOf(".");
			if(index>0) {
				String suffix = novelCover.substring(index, novelCover.length());
				if(".JPG".equalsIgnoreCase(suffix)) {
					flag = 1;
				} else if(".GIF".equalsIgnoreCase(suffix)) {
					flag = 2;
				} else if(".PNG".equalsIgnoreCase(suffix)) {
					flag = 3;
				} else {
					flag = 0;
				}
			}
		}
		return flag;
	}

}
