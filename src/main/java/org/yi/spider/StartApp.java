package org.yi.spider;

import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.loader.InitCfgLoader;
import org.yi.spider.loader.SimpleLoaderFactory;

/**
 * 
 * @ClassName: StartApp
 * @Description: 程序主控类
 * @author QQ 
 *
 */
public class StartApp {
	
	public static void main(String[] args){
		
		try {
			InitCfgLoader.load();
			SimpleLoaderFactory.create(GlobalConfig.localSite.getProgram()).load();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		new MainThread(args).run();
		
	}

}
