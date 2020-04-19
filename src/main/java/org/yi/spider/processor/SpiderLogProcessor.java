package org.yi.spider.processor;

import org.apache.commons.dbutils.handlers.MapListHandler;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.SpiderLogEntity;
import org.yi.spider.exception.BaseException;
import org.yi.spider.helper.RuleHelper;
import org.yi.spider.model.CollectParam;
import org.yi.spider.model.Rule;
import org.yi.spider.utils.DateUtils;
import org.yi.spider.utils.JsonUtils;
import org.yi.spider.utils.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: yispider
 * @description:
 * @author: zhendong.wu
 * @create: 2020-04-18 20:22
 **/
public class SpiderLogProcessor  extends BaseProcessor{
    private static final Logger logger = LoggerFactory.getLogger(SpiderLogProcessor.class);


    public void process(){
        int index=0;
        while(!GlobalConfig.SHUTDOWN){
            logger.info("书本更新记录开始,次数={}",++index);
            List<SpiderLogEntity> list=  findByLaterTime();
            if(list==null||list.size()<0){
                logger.info("书本更新记录,之前数据已更新完毕,次数={}",index);
                Date tomorrow=org.apache.commons.lang.time.DateUtils.addDays(new Date(),1);
                tomorrow=DateUtils.getDate(DateUtils.getDateStr(tomorrow,"yyyy-MM-dd"),"yyyy-MM-dd");
                try {
                    long currTIme=System.currentTimeMillis();
                    Thread.sleep((currTIme>=tomorrow.getTime())?1000:tomorrow.getTime()-System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            for(SpiderLogEntity spiderLogEntity:list){
                String novelNo=spiderLogEntity.getNovelNo();
                CollectParam cpm=JsonUtils.toObject(spiderLogEntity.getCpm(),CollectParam.class);
                try {
                    //解析规则文件
                    cpm.setRuleMap(parseRule(cpm));
                    new NovelParser(cpm,novelNo,spiderLogEntity).prase();
                } catch (Exception e) {
                    logger.warn("更新异常,爬虫日志编号={}",spiderLogEntity.getArticleNo());
                    e.printStackTrace();

                }
            }
            logger.info("书本更新记录结束,次数={}",index);



        }
        logger.info("书本更新记录停止");

    }

    public static void main(String[] args) {
        Date tomorrow=org.apache.commons.lang.time.DateUtils.addDays(new Date(),1);
        System.out.println(tomorrow);
        tomorrow=DateUtils.getDate(DateUtils.getDateStr(tomorrow,"yyyy-MM-dd"),"yyyy-MM-dd");
      System.out.println(tomorrow);
        System.out.println( tomorrow.getTime()-System.currentTimeMillis());
    }
    public List<SpiderLogEntity>  findByLaterTime(){
        Connection conn = DBPool.getInstance().getConnection();
        YiQueryRunner queryRunner = new YiQueryRunner(true);


        String sql = "select url,spider_rule_xml,cno,status,article_no,cpm,novel_no from t_spider_log tl where 1=1  " +
                " and( tl.grasp_time<'%s'  or tl.grasp_time is null )" +
                "and tl.novel_no is not null " +
                "and tl.status in('INIT','SPIDERING')\n" +
                "order by tl.update_time asc limit 1000 ";
        List<SpiderLogEntity> result = new ArrayList<SpiderLogEntity>();
        List<Map<String,Object>> chapterList = null;
        try {

               sql=     String.format(sql, DateUtils.getDateStr(new Date(),"yyyy-MM-dd"));
               logger.info("sql={}",sql);
            chapterList = queryRunner.query(conn, sql,new MapListHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < chapterList.size(); i++) {
            Map<String, Object> map = chapterList.get(i);
            SpiderLogEntity  chapter = new SpiderLogEntity();
            chapter.setNovelNo(String.valueOf(map.get("novel_no")));
            chapter.setUrl(String.valueOf(map.get("url")));
            chapter.setSpiderRulXml(String.valueOf(map.get("spider_rule_xml")));
            chapter.setStatus(String.valueOf(map.get("status")));
            chapter.setArticleNo(String.valueOf(map.get("article_no")));
            chapter.setCpm(String.valueOf(map.get("cpm")));
            chapter.setCno((map.get("cno")==null?null:String.valueOf(map.get("cno"))));
            result.add(chapter);
        }
        return result;
    }
    private Map<String, Rule> parseRule(CollectParam cpm) throws DocumentException {
        String ruleFile = cpm.getRuleFile();
        if(StringUtils.isBlank(ruleFile)) {
            ruleFile = GlobalConfig.collect.getString(ConfigKey.RULE_NAME);
        }
        cpm.setRuleFile(ruleFile);
        if(StringUtils.isBlank(ruleFile)) {
            throw new BaseException("全局规则和采集命令中必须至少有一个指定采集规则文件！");
        }
        //	 GlobalConfig.collect.setProperty(ConfigKey.RULE_NAME,ruleFile);

        logger.debug("开始解析规则：{}", ruleFile);
        return RuleHelper.parseXml(ruleFile);
    }

}
