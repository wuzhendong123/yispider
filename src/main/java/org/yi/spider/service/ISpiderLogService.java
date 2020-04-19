package org.yi.spider.service;

import org.yi.spider.entity.NovelEntity;
import org.yi.spider.entity.SpiderLogEntity;

import java.sql.SQLException;
import java.util.Map;

public interface ISpiderLogService {

	SpiderLogEntity findByArticleNo(String articleNo) throws SQLException;
	void save(SpiderLogEntity spiderLogEntity) throws SQLException;

	void update(SpiderLogEntity spiderLogEntity) throws SQLException;
	void updateGraspTime(SpiderLogEntity spiderLogEntity) throws SQLException;
}
