package org.yi.spider.service.impl;

import org.apache.commons.dbutils.ResultSetHandler;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.SpiderLogEntity;
import org.yi.spider.service.ISpiderLogService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @program: yispider
 * @description:爬出采集日志记录
 * @author: zhendong.wu
 * @create: 2020-04-12 19:19
 **/
public class SpiderLogServiceImpl implements ISpiderLogService {
    private final class SpiderLogEntitySetHandler implements
            ResultSetHandler<SpiderLogEntity> {
        @Override
        public SpiderLogEntity handle(ResultSet rs) throws SQLException {
            SpiderLogEntity spiderLogEntity = null;
            if(rs != null && rs.next()) {
                spiderLogEntity = new SpiderLogEntity();
                spiderLogEntity.setNovelNo(rs.getString("novel_no"));
                spiderLogEntity.setCpm(rs.getString("cpm"));
                spiderLogEntity.setCno(rs.getString("cno"));
                spiderLogEntity.setUrl(rs.getString("url"));
                spiderLogEntity.setStatus(rs.getString("status"));
                spiderLogEntity.setSpiderRulXml(rs.getString("spider_rule_xml"));
                spiderLogEntity.setArticleNo(rs.getString("article_no"));
            }

            return spiderLogEntity;
        }
    }
    @Override
    public SpiderLogEntity findByArticleNo(String articleNo) throws SQLException {
        YiQueryRunner queryRunner = new YiQueryRunner(true);
        Connection conn = DBPool.getInstance().getConnection();

        String sql = "select  novel_no, cpm, cno, url, status, spider_rule_xml,article_no " +
                " from t_spider_log  where article_no=?  ";

        return queryRunner.query(conn,sql,new SpiderLogEntitySetHandler(),articleNo);
    }

    @Override
    public void save(SpiderLogEntity spiderLogEntity) throws SQLException {
        Connection conn = DBPool.getInstance().getConnection();
        YiQueryRunner queryRunner = new YiQueryRunner(true);

        String sql = "INSERT INTO t_spider_log(  novel_no, cpm, cno, url, status, spider_rule_xml,article_no,create_time,update_time ) " +
                " VALUES ( ?, ?, ?, ?,  ?, ?,?,?,?) ;";

        Object[] params = new Object[]{spiderLogEntity.getNovelNo(), spiderLogEntity.getCpm(), spiderLogEntity.getCno(),
                spiderLogEntity.getUrl(), spiderLogEntity.getStatus(),
                spiderLogEntity.getSpiderRulXml(),spiderLogEntity.getArticleNo(),new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis())};

        queryRunner.save(conn, sql, params);
    }

    @Override
    public void update(SpiderLogEntity spiderLogEntity) throws SQLException {
        Connection conn = DBPool.getInstance().getConnection();
        YiQueryRunner queryRunner = new YiQueryRunner(true);

        String sql = "update  t_spider_log set status=?, update_time=?, cno=?      " +
                "where article_no=? ";

        Object[] params = new Object[]{spiderLogEntity.getStatus(),new Timestamp(System.currentTimeMillis()),spiderLogEntity.getCno(),spiderLogEntity.getArticleNo()};

        queryRunner.update(conn, sql, params);

    }
}
