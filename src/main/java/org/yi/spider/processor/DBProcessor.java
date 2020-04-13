package org.yi.spider.processor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.ThreadObserver;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.enums.ChapterExtEnum;
import org.yi.spider.enums.ParamEnum;
import org.yi.spider.helper.FileHelper;
import org.yi.spider.helper.FileOldHelper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: yispider
 * @description:
 * @author: zhendong.wu
 * @create: 2020-04-13 15:02
 **/
public class DBProcessor extends  BaseProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DBProcessor.class);
    private CommandLine cmd;

    public DBProcessor(CommandLine cmd) {
        this.cmd = cmd;
    }

    public void run() {
        new ThreadObserver(this).start();
        process();
    }

    public void process() {
        try {
            String type=cmd.getOptionValue(ParamEnum.TXT_TO_DB.getName());
            while(!GlobalConfig.SHUTDOWN){



                if(ChapterExtEnum.BOOK_TXT.name().equals(type)){
                    List<ChapterEntity> chapterEntitys=  findTxTAll(type);
                    if(chapterEntitys==null||chapterEntitys.size()<=0){
                        logger.info("数据同步完毕");
                        break;
                    }
                    for(ChapterEntity chapterEntity:chapterEntitys){
                        NovelEntity novelEntity=new NovelEntity();
                        novelEntity.setNovelNo(chapterEntity.getNovelNo());
                        String context=  FileOldHelper.readTxtFile(novelEntity,chapterEntity)
                                ;
                        logger.info("ChapterNo={},ChapterName={}",chapterEntity.getChapterNo(),chapterEntity.getChapterName());
                        FileHelper.writeTxtFile(novelEntity,chapterEntity,context);
                    }
                }else if(ChapterExtEnum.COVER_IMAGE.name().equals(type)){
                    List<NovelEntity> novelEntitys=  findNovelEntityAll(type);
                    if(novelEntitys==null||novelEntitys.size()<=0){
                        logger.info("数据同步完毕");
                        break;
                    }
                    for(NovelEntity novelEntity:novelEntitys){
                        String path=  FileOldHelper.getCoverDir(novelEntity);
                        path= String.format("file:/%s",path);
                        logger.info("getNovelNo={},getNovelName={},path={}",novelEntity.getNovelNo(),novelEntity.getNovelName(),path);
                        FileHelper.downImage(path,novelEntity,null);
                    }
                }else{
                    logger.warn("参数不正确,type={}",type);
                    break;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<ChapterEntity> findTxTAll(String type) throws SQLException {
        Connection conn = DBPool.getInstance().getConnection();
        YiQueryRunner queryRunner = new YiQueryRunner(true);

        String sql = "select t.articleno,t.chapterno,t.* from t_chapter t \n" +
                "left join t_chapter_ext te on te.chapterno=t. chapterno and te.articleno=t.articleno and te.type='BOOK_TXT'\n" +
                "where 1=1 and te.chapterno is  null and t.size >0 limit 10000";
        List<ChapterEntity> result = new ArrayList<ChapterEntity>();
        List<Map<String,Object>> chapterList = queryRunner.query(conn, sql, new MapListHandler());
        for (int i = 0; i < chapterList.size(); i++) {
            Map<String, Object> map = chapterList.get(i);
            ChapterEntity chapter = new ChapterEntity();
            chapter.setNovelNo(Integer.parseInt(String.valueOf(map.get("articleno"))));
            chapter.setNovelName(String.valueOf(map.get("chaptername")));
            chapter.setChapterNo(Integer.parseInt(String.valueOf(map.get("chapterno"))));
            result.add(chapter);
        }
        return result;
    }
    private List<NovelEntity> findNovelEntityAll(String type) throws SQLException {
        Connection conn = DBPool.getInstance().getConnection();
        YiQueryRunner queryRunner = new YiQueryRunner(true);

        String sql = "select t.articleno,t.articlename from \n" +
                "t_article t \n" +
                "left join t_chapter_ext te on te.chapterno=t.articleno  and te.type='COVER_IMAGE'\n" +
                "where 1=1  and te.chapterno is  null and t.imgflag is not null limit 10000";
        List<NovelEntity> result = new ArrayList<NovelEntity>();
        List<Map<String,Object>> chapterList = queryRunner.query(conn, sql, new MapListHandler());
        for (int i = 0; i < chapterList.size(); i++) {
            Map<String, Object> map = chapterList.get(i);
            NovelEntity chapter = new NovelEntity();
            chapter.setNovelNo(Integer.parseInt(String.valueOf(map.get("articleno"))));
            chapter.setNovelName(String.valueOf(map.get("articlename")));
            result.add(chapter);
        }
        return result;
    }
}
