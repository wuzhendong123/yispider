package org.yi.spider.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.enums.ProgramEnum;
import org.yi.spider.exception.BaseException;
import org.yi.spider.loader.impl.JieQiConfigLoader;
import org.yi.spider.loader.impl.YiDuConfigLoader;

/**
 * 
 * @ClassName: SimpleLoaderFactory
 * @Description: 简单工厂， 产生配置加载策略对象
 * @author QQ  
 *
 */
public class SimpleLoaderFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleLoaderFactory.class);

	/**
	 * 
	 * <p>根据传入的程序产生对应的配置加载策略</p>
	 * @param e
	 * @return
	 */
	public static ILoader create(ProgramEnum e) {
		if(e == null) {
			throw new BaseException("获取本地站点使用的程序异常！");
		}
		
		ILoader loader = null;
		if(e == ProgramEnum.YIDU) {
			loader = new YiDuConfigLoader();
		} else if (e == ProgramEnum.JIEQI) {
			loader = new JieQiConfigLoader();
		}
		logger.debug("产生加载策略： " + e.getName() );
		return loader;
	}
	
}
