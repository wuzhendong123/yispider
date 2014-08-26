package org.yi.spider.utils;

import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;
import org.yi.spider.constants.GlobalConfig;

public class CMDUtils {
	
	/**
	 * 
	 * <p>打印帮助信息</p>
	 */
	public static void showHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "yispider", ParamUtils.getOptions() );
	}
	
	/**
	 * 
	 * <p>打印版本信息</p>
	 */
	public static void showVersion() {
		PrintWriter pw = new PrintWriter(System.out);
		
		StringBuffer sb = new StringBuffer("yispider: ");
		sb.append(GlobalConfig.config.getString("version"));
		sb.append(System.getProperty("line.separator"));
		
		pw.write(sb.toString());
		pw.flush();
		pw.close();
	}

}
