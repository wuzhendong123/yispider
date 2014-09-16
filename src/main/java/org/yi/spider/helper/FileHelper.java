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
     * @param article
     * @param chapter
     * @param content
     */
	public static void writeTxtFile(NovelEntity article, ChapterEntity chapter, String content) {
		
		int novelNo = article.getNovelNo().intValue();
		int subDir = novelNo/1000;
        String localPath = GlobalConfig.localSite.getTxtDir() + FileUtils.FILE_SEPARATOR
		        		+ subDir + FileUtils.FILE_SEPARATOR
		        		+ novelNo + FileUtils.FILE_SEPARATOR;
        
		Writer writer = null;
		try {
			if(!new File(localPath).exists()){
				new File(localPath).mkdirs();
			}
			localPath = localPath+chapter.getChapterNo()+".txt";
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
    public static void downImage(String remotePath, int novelNo, String suffix){
        
        int subDir = novelNo/1000;
        String localPath = GlobalConfig.localSite.getCoverDir() + FileUtils.FILE_SEPARATOR
		        		+ subDir + FileUtils.FILE_SEPARATOR
		        		+ novelNo + FileUtils.FILE_SEPARATOR;
        if(!new File(localPath).exists()){
        	new File(localPath).mkdirs();
        }
        localPath = localPath + novelNo + "s" + suffix;
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
	
}
