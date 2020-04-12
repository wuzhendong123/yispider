package org.yi.spider.service.yidu;

import org.apache.commons.dbutils.ResultSetHandler;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.ChapterExtEntity;
import org.yi.spider.enums.ChapterExtEnum;
import org.yi.spider.model.User;
import org.yi.spider.service.BaseService;
import org.yi.spider.service.IChapterExtService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @program: yispider
 * @description:
 * @author: zhendong.wu
 * @create: 2020-04-09 17:31
 **/
public class ChapterExtServiceImpl extends BaseService implements IChapterExtService {

    private final class ChapterResultSetHandler implements
            ResultSetHandler<ChapterExtEntity> {
        @Override
        public ChapterExtEntity handle(ResultSet rs) throws SQLException {
            ChapterExtEntity chapter = null;
            if(rs != null && rs.next()) {
                chapter = new ChapterExtEntity();
                chapter.setNovelNo(rs.getInt("articleno"));
                chapter.setNovelName(rs.getString("articlename"));
                chapter.setChapterNo(rs.getInt("chapterno"));
                chapter.setChapterName(rs.getString("chaptername"));
                chapter.setType(rs.getString("type"));
                chapter.setContent(rs.getString("content"));
            }

            return chapter;
        }
    }
    @Override
    protected User loadAdmin() throws SQLException {
        return null;
    }

    @Override
    public void saveChapter(ChapterExtEntity chapterExtEntity) throws SQLException {
        Connection conn = DBPool.getInstance().getConnection();
        YiQueryRunner queryRunner = new YiQueryRunner(true);

        String sql = "INSERT INTO t_chapter_ext( articleno, articlename, chapterNo, chapterName, "
                + "         type, content) VALUES ( ?, ?, ?, ?,  ?, ?) ;";

        Object[] params = new Object[]{chapterExtEntity.getNovelNo(), chapterExtEntity.getNovelName(), chapterExtEntity.getChapterNo(),
                chapterExtEntity.getChapterName(), chapterExtEntity.getType(),
                chapterExtEntity.getContent()};

         queryRunner.save(conn, sql, params);
    }
    @Override
    public void updateChapter(ChapterExtEntity chapterExtEntity) throws SQLException {
        Connection conn = DBPool.getInstance().getConnection();
        YiQueryRunner queryRunner = new YiQueryRunner(true);

        String sql = "update  t_chapter_ext set articleno=?, articlename=?, chapterName=?, "
                + "          content=? where chapterNo=? and type=?";

        Object[] params = new Object[]{chapterExtEntity.getNovelNo(), chapterExtEntity.getNovelName(),
                chapterExtEntity.getChapterName(), chapterExtEntity.getContent(),chapterExtEntity.getChapterNo(),
                chapterExtEntity.getType()};

        queryRunner.update(conn, sql, params);
    }

    @Override
    public ChapterExtEntity findByChapterNo(Integer chapterNo,ChapterExtEnum type) throws SQLException {
        YiQueryRunner queryRunner = new YiQueryRunner(true);
        Connection conn = DBPool.getInstance().getConnection();

        String sql = "select  articleno, articlename, chapterNo, chapterName, type, content " +
                " from t_chapter_ext  where chapterNo=?  and type=?";

        return queryRunner.query(conn,sql,new ChapterResultSetHandler(),chapterNo,type.name());
    }
}
