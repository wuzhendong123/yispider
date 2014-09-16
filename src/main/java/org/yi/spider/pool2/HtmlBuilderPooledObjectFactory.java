package org.yi.spider.pool2;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.yi.spider.factory.impl.ServiceFactory;
import org.yi.spider.service.IHtmlBuilder;

public class HtmlBuilderPooledObjectFactory extends BaseKeyedPooledObjectFactory<String, IHtmlBuilder> {

	@Override
	public IHtmlBuilder create(String key) throws Exception {
		return new ServiceFactory().createHtmlBuilder(key);
	}

	@Override
	public PooledObject<IHtmlBuilder> wrap(IHtmlBuilder value) {
		return new DefaultPooledObject<IHtmlBuilder>(value);
	}

}
