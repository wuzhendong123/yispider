package org.yi.spider.pool2;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.yi.spider.factory.impl.ServiceFactory;
import org.yi.spider.service.INovelService;

public class NovelPooledObjectFactory extends BaseKeyedPooledObjectFactory<String, INovelService> {

	@Override
	public INovelService create(String key) throws Exception {
		return new ServiceFactory().createNovelService(key);
	}

	@Override
	public PooledObject<INovelService> wrap(INovelService value) {
		return new DefaultPooledObject<INovelService>(value);

	}

}
