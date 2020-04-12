package org.yi.spider.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.ChapterExtEntity;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.enums.ChapterExtEnum;
import org.yi.spider.service.IChapterExtService;
import org.yi.spider.service.yidu.ChapterExtServiceImpl;
import org.yi.spider.utils.FileUtils;
import org.yi.spider.utils.StringUtils;

public class FileHelper {

	 static IChapterExtService chapterExtService=new ChapterExtServiceImpl();

	 public static String read(Integer chapterNo,ChapterExtEnum chapterExtEnum){
         ChapterExtEntity chapterExtEntity= null;
         try {
             chapterExtEntity = chapterExtService.findByChapterNo(chapterNo,chapterExtEnum);
             if(chapterExtEntity!=null){
                 return chapterExtEntity.getContent();

             }

         } catch (SQLException e) {
             e.printStackTrace();
         }
         return null;

     }
	/**
     * 
     * <p>将章节内容写入txt文件</p>
     * @param novel
     * @param chapter
     * @param content
	 * @throws IOException 
     */
	public static void writeTxtFile(NovelEntity novel, ChapterEntity chapter, String content) throws IOException {
		

        
		try {
			/*
			  String dir = localPath.substring(0, localPath.lastIndexOf("/"));
			if(!new File(dir).exists()){
				new File(dir).mkdirs();
			}*/
			ChapterExtEntity chapterExtEntity=chapterExtService.findByChapterNo(chapter.getChapterNo(),ChapterExtEnum.BOOK_TXT);
			if(chapterExtEntity!=null){
				chapterExtEntity.padd(content,ChapterExtEnum.BOOK_TXT);
				chapterExtService.updateChapter(chapterExtEntity);
			}else{
				 chapterExtEntity=ChapterExtEntity.bulid(chapter,content,ChapterExtEnum.BOOK_TXT);
				chapterExtService.saveChapter(chapterExtEntity);
			}

		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	

	public static void writeLastTxt(NovelEntity novelEntity, String content) throws IOException {

		ChapterExtEntity chapterExtEntity= null;
		try {
			chapterExtEntity = chapterExtService.findByChapterNo(novelEntity.getNovelNoInteger(),ChapterExtEnum.BOOK_LAST_TXT);
			if(chapterExtEntity!=null){
				chapterExtEntity.padd(content,ChapterExtEnum.BOOK_LAST_TXT);
				chapterExtService.updateChapter(chapterExtEntity);
			}else{
				chapterExtEntity=ChapterExtEntity.bulid(novelEntity,content,ChapterExtEnum.BOOK_LAST_TXT);
				chapterExtService.saveChapter(chapterExtEntity);
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}
	/**
	 * 
	 * <p>下载远程图片</p>
	 * @param remotePath	http://www.a.com/b/c.jpg
	 * @param suffix		图片后缀
	 */
    public static void downImage(String remotePath, NovelEntity novelEntity, String suffix){
		byte[] bytes=FileUtils.download(remotePath);
		String base64Image=Base64.encodeBase64String(bytes);
		ChapterExtEntity chapterExtEntity= null;
		try {
			chapterExtEntity = chapterExtService.findByChapterNo(novelEntity.getNovelNoInteger(),ChapterExtEnum.COVER_IMAGE);
			if(chapterExtEntity!=null){
				chapterExtEntity.padd(base64Image,ChapterExtEnum.COVER_IMAGE);
				chapterExtService.updateChapter(chapterExtEntity);
			}else{
				chapterExtEntity=ChapterExtEntity.bulid(novelEntity,base64Image,ChapterExtEnum.COVER_IMAGE);
				chapterExtService.saveChapter(chapterExtEntity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    /**
	 * 
	 * <p>读取多线程配置文件</p>
	 * @param fileName
	 * @return
	 */
	public static List<String[]> readRunArgs(String fileName) {
		URL url = FileUtils.locateFromClasspath(fileName);
		File file = FileUtils.fileFromURL(url);

		List<String[]> list = new ArrayList<String[]>();
		
		try {
			InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                if(StringUtils.isNotBlank(lineTxt)) {
                	//#开头为注释行， 略过
                	if(!lineTxt.trim().startsWith("#")) {
                		list.add(lineTxt.trim().split("\\s"));
                	}
                }
            }
            read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取生成的html文件存放路径
	 * @param novel
	 * @param chapter
	 * @return
	 */
	public static String getHtmlFilePath(NovelEntity novel, ChapterEntity chapter) {
		String chapterNo = chapter == null ? "index" : chapter.getChapterNo().toString();
		return GlobalConfig.localSite.getHtmlFile().replace("#subDir#", String.valueOf(novel.getNovelNo().intValue()/1000))
				.replace("#articleNo#", String.valueOf(novel.getNovelNo()))
				.replace("#chapterNo#", chapterNo)
				.replace("#pinyin#", StringUtils.isBlank(novel.getPinyin()) ? "" : novel.getPinyin());
		
	}

}
