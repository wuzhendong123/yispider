package org.yi.spider.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.model.CollectParamModel;

public class StringUtils {
	
	private static final String PROTOCAL_SPLIT = "://";
	private static final String EMPTY = "";
	
	/**
	 * 匹配域名aaa.bbb.ccc/ddd
	 */
	private static final String REGEX_DOMAIN = ".+\\..+/.+";
	
	/**
     * <p>字符串是否为空 ("") 或 null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str  需要验证的字符串
     * @return <code>true</code> 字符串为空或者字符串对象为null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>字符串是否为不空 ("") 或 不为null.</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  需要验证的字符串
     * @return <code>true</code> 字符串不为空并且字符串对象不为null
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * <p>字符串是否为空白, 空字符串("") 或者 null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  需要验证的字符串
     * @return <code>true</code> 字符串为空白, 空字符串("") 或者 null
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>字符串是否不为 (""), 不为null 并且不为 空白.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  需要验证的字符串
     * @return <code>true</code> 字符串是否不为 (""), 不为null 并且不为 空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String returning an empty String ("") if the String
     * is empty ("") after the trim or if it is <code>null</code>.
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #stripToEmpty(String)}.</p>
     *
     * <pre>
     * StringUtils.trimToEmpty(null)          = ""
     * StringUtils.trimToEmpty("")            = ""
     * StringUtils.trimToEmpty("     ")       = ""
     * StringUtils.trimToEmpty("abc")         = "abc"
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if <code>null</code> input
     * @since 2.0
     */
    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * 
     * <p>将输入流转换成字符串</p>
     * @param inputStream	
     * @param charset	编码
     * @return String
     * @throws UnsupportedEncodingException
     */
    public static String stream2String(InputStream inputStream, String charset) throws UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
    
	/**
	 * 
	 * <p>清除文本内容中的空白行</p>
	 * @param source
	 * @return
	 */
	public static String removeBlankLine(String source) {
		StringBuffer sb = new StringBuffer();
		StringReader reader = new StringReader(source);
		try {
			BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;
            while((line = bufferedReader.readLine()) != null){
            	 if(StringUtils.isNotBlank(line)) {
                	sb.append(line + System.getProperty("line.separator"));
                }
            }
            reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * <p>判断字符串数组中是否包含目标串</p>
	 * @param src
	 * @param dest
	 * @return
	 */
	public static boolean contains(String[] src, String dest) {
		if(src.length>0){
			for(String str : src) {
				if(str.equals(dest)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * <p>将文本内容中的HTML格式的空格、换行替换成普通文本对应的空白和换行</p>
	 * @param intro
	 * @return
	 */
	public static String replaceHtml(String intro) {
		if(StringUtils.isBlank(intro)) {
			return intro;
		}
		String[] srcStr = new String[]{"&nbsp;","<br/>","<br />"};
		String[] destStr = new String[]{" ","\n","\n"};
		for(int i=0;i<srcStr.length;i++) {
			intro = intro.replaceAll(srcStr[i], destStr[i]);
		}
		return intro;
	}
	
	/**
	 * <p>生成HTML时将文字中的空格、换行替换为HTML格式</p>
	 * @param content
	 * @return
	 */
	public static String str2Html(String content) {
		if(content!=null && !content.isEmpty()){
			content = content.replaceAll("\n", "<br/>").replaceAll(" ", "&nbsp;");
		}
		return content;
	}
	
	/**
	 * 
	 * <p>获取完整的URL地址</p>
	 * @param baseUrl	目标网址
	 * 					一般为http://a.com
	 * @param destUrl	要抓取的页面地址
	 * 					可能为  http://a.com/b/c.html
	 * 					    /b/c.html
	 * @return	完整的抓取目标地址	如：http://a.com/b/c.html
	 */
	public static String getFullUrl(String baseUrl, String destUrl) {
		
		int index = destUrl.indexOf(PROTOCAL_SPLIT);
		//处理 http://a.com/b/c.html，获取不带协议的地址字符串a.com/b/c.html
		if(index>0) {
			destUrl = destUrl.substring(index + PROTOCAL_SPLIT.length(), destUrl.length());
		}
		//获取根域名后的地址/b/c.html
		index = destUrl.indexOf("/");
		if(index>=0) {
			destUrl = destUrl.substring(index, destUrl.length());
		}
		
		if(baseUrl.endsWith("/") && destUrl.startsWith("/")) {
			baseUrl = baseUrl.substring(0, baseUrl.length()-1);
		}
		destUrl = baseUrl + destUrl;
		return destUrl;
	}
	
	/**
	 * 
	 * <p>获取资源文件中的内容</p>
	 * @param name	资源文件中的key
	 * @param param	参数
	 * @return
	 */
	public static String getBundleString(String name, Object[] param) {
		String value = GlobalConfig.bundle.getString(name);
		if(isEmpty(value)) {
			return "";
		}
		return MessageFormat.format(value, param);
	}
	
	/**
	 * 
	 * <p>获取资源文件中的内容</p>
	 * @param name
	 * @return
	 */
	public static String getBundleString(String name) {
		return GlobalConfig.bundle.getString(name);
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
     * 获取目标站内容页的完整地址
     * @param novelNo	目标站小说号
     * @param novelPubKeyURL 目标站内容页地址
     * @param cno	目标站章节号
	 * @param cpm 
     * @return
     */
    public static String getRemoteChapterUrl(String chapterUrl, String novelPubKeyURL, String novelNo, 
    		String cno, CollectParamModel cpm) {
        chapterUrl = chapterUrl.replace("{ChapterKey}", cno);
        chapterUrl = ParseUtils.getAssignURL(chapterUrl, novelNo);
        
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

	
}
