package org.yi.spider.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.utils.FileUtils;
import org.yi.spider.utils.StringUtils;

public class FileHelper {
	
	/**
     * 
     * <p>将章节内容写入txt文件</p>
     * @param novel
     * @param chapter
     * @param content
     */
	public static void writeTxtFile(NovelEntity novel, ChapterEntity chapter, String content) {
		
        String localPath = getTxtFilePath(chapter);
        String dir = localPath.substring(0, localPath.lastIndexOf("/"));
        
		Writer writer = null;
		try {
			if(!new File(dir).exists()){
				new File(dir).mkdirs();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localPath), GlobalConfig.localSite.getCharset()));
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * <p>下载远程图片</p>
	 * @param remotePath	http://www.a.com/b/c.jpg
	 * @param novelNo		本地小说号	
	 * @param suffix		图片后缀
	 */
    public static void downImage(String remotePath, NovelEntity novel, String suffix){
        
        String localPath = getCoverDir(novel);
        
        if(!new File(localPath).exists()){
        	new File(localPath).mkdirs();
        }
        localPath = localPath + novel.getNovelNo() + "s" + suffix;
        if(!new File(localPath).exists()){
	    	FileUtils.download(remotePath, localPath);
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
		String htmlFile = GlobalConfig.localSite.getHtmlFile();
		if(htmlFile.endsWith("/")){
			htmlFile = htmlFile.substring(0,htmlFile.length()-1);
		}
		String chapterNo = chapter == null ? "index" : chapter.getChapterNo().toString();
		htmlFile = htmlFile.replace("#subDir#", String.valueOf(novel.getNovelNo().intValue()/1000))
				.replace("#articleNo#", String.valueOf(novel.getNovelNo()))
				.replace("#chapterNo#", chapterNo)
				.replace("#pinyin#", StringUtils.isBlank(novel.getPinyin()) ? "" : novel.getPinyin());
		
		return htmlFile;
	}
	
	/**
	 * 获取txt文件路径
	 * @param novel
	 * @param chapter
	 * @return
	 */
	public static String getTxtFilePath(ChapterEntity chapter) {
		String txtFile = GlobalConfig.localSite.getTxtFile();
		if(txtFile.endsWith("/")){
			txtFile = txtFile.substring(0,txtFile.length()-1);
		}
		return txtFile.replace("#subDir#", String.valueOf(chapter.getNovelNo().intValue()/1000))
				.replace("#articleNo#", String.valueOf(chapter.getNovelNo()))
				.replace("#chapterNo#", String.valueOf(chapter.getChapterNo()));
	}
	
	/**
	 * 获取小说封面目录
	 * @param chapter
	 * @return
	 */
	public static String getCoverDir(NovelEntity novel){
		String file = GlobalConfig.localSite.getCoverDir();
		if(!file.endsWith("/")){
			file = file + "/";
		}
		return file.replace("#subDir#", String.valueOf(novel.getNovelNo().intValue()/1000))
				.replace("#articleNo#", String.valueOf(novel.getNovelNo()));
	}
	
}
