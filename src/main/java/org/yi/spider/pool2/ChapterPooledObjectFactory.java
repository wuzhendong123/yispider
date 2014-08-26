package org.yi.spider.pool2;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.yi.spider.factory.impl.ServiceFactory;
import org.yi.spider.service.IChapterService;

public class ChapterPooledObjectFactory extends BaseKeyedPooledObjectFactory<String, IChapterService> {

	@Override
	public IChapterService create(String key) throws Exception {
		return new ServiceFactory().createChapterService(key);
	}

	@Override
	public PooledObject<IChapterService> wrap(IChapterService value) {
		return new DefaultPooledObject<IChapterService>(value);

	}

}
