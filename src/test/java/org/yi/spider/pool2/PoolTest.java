package org.yi.spider.pool2;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.junit.Test;
import org.yi.spider.service.INovelService;

public class PoolTest {

	@Test
	public void test() {
		GenericKeyedObjectPool<String, INovelService> novelPool = new GenericKeyedObjectPool<String, INovelService>(new NovelPooledObjectFactory());
		try {
			INovelService a = novelPool.borrowObject("yidu");
			INovelService b = novelPool.borrowObject("yidu");
			System.out.println(a);
			System.out.println(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
