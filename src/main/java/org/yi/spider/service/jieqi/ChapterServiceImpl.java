package org.yi.spider.service.jieqi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.model.User;
import org.yi.spider.service.BaseService;
import org.yi.spider.service.IChapterService;
import org.yi.spider.utils.ObjectUtils;

public class ChapterServiceImpl extends BaseService implements IChapterService {

	private final class ChapterResultSetHandler implements
			ResultSetHandler<ChapterEntity> {
		@Override
		public ChapterEntity handle(ResultSet rs) throws SQLException {
			ChapterEntity chapter = null;
			if(rs != null && rs.next()) {
				chapter = new ChapterEntity();
				chapter.setNovelNo(rs.getInt("articleid"));
				chapter.setNovelName(rs.getString("articlename"));
				chapter.setChapterNo(rs.getInt("chapterid"));
				chapter.setChapterName(rs.getString("chaptername"));
				chapter.setSize(rs.getInt("size"));
			}
			return chapter;
		}
	}
	
	@Override
	public Number save(ChapterEntity chapter) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);
		
		String sql = "insert into jieqi_article_chapter " +
				"(siteid,articleid,articlename,volumeid,posterid,poster," +
				"postdate,lastupdate,chaptername,size,attachment,chapterorder)" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?)";

		Object[] params = new Object[]{0, chapter.getNovelNo(), chapter.getNovelName(), 0, 
				admin.getUserId(), admin.getUserName(), 
				getJieQiTimeStamp(), getJieQiTimeStamp(),
				chapter.getChapterName(), chapter.getSize(),
				EMPTY, getChapterOrder(chapter)};
		
		return queryRunner.save(conn, sql, params);
	}

	@Override
	public Map<String, Object> getTotalInfo(Number novelno) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);
		
		String sql = "SELECT SUM(size) as size, count(*) as count FROM jieqi_article_chapter WHERE articleid = ?";
        return queryRunner.query(conn, sql, new MapHandler(), novelno);
	}

	@Override
	public int getChapterOrder(ChapterEntity chapter) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);
		
		int order = 0;
		String sql = "select max(chapterorder) from jieqi_article_chapter WHERE articleid = ?";
		Object obj = queryRunner.query(conn, sql, new ScalarHandler<Object>(), chapter.getNovelNo());
		if(obj==null) {
			order = 1;
		} else {
			order = ObjectUtils.obj2Int(obj);
		}
		return order;
	}

	@Override
	public boolean exist(ChapterEntity chapter) throws SQLException {
		
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "select count(*) from jieqi_article_chapter where chaptername=?";
		Object count = queryRunner.query(conn, sql, new ScalarHandler<Object>(), new Object[]{chapter.getChapterName()});
		
		return ObjectUtils.obj2Int(count)>0;
	}

	@Override
	public ChapterEntity get(ChapterEntity chapter, int i) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
        String sql = "select * from jieqi_article_chapter where articleid = ?";
        if (i < 0) {
            sql = sql + " and chapterid < ? order by chapterid desc limit "+(Math.abs(i)-1)+", 1";
        } else if (i > 0) {
            sql = sql + " and chapterid > ? order by chapterid asc limit "+(i-1)+", 1";
        }
            
		return queryRunner.query(conn, sql, new ChapterResultSetHandler(), 
				new Object[]{chapter.getNovelNo(), chapter.getChapterNo()});
	}

	@Override
	public List<ChapterEntity> getChapterList(NovelEntity novel) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  

		String sql = "select * from jieqi_article_chapter where articleid = ? order by chapterid asc";
		List<Map<String, Object>> linedMap = queryRunner.query( conn, sql,  
                        new MapListHandler(), novel.getNovelNo());
		List<ChapterEntity> result = new ArrayList<ChapterEntity>();
		for (int i = 0; i < linedMap.size(); i++) {
            Map<String, Object> map = linedMap.get(i);
            ChapterEntity chapter = new ChapterEntity();
            chapter.setNovelNo(novel.getNovelNo());
            chapter.setNovelName(novel.getNovelName());
            chapter.setChapterName(String.valueOf(map.get("chaptername")));
            chapter.setChapterNo(Integer.parseInt(String.valueOf(map.get("chapterid"))));;
            chapter.setChapterOrder(chapter.getChapterNo());
            result.add(chapter);
        }
		return result;
	}

	@Override
	public ChapterEntity get(Number chapterid) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "SELECT articleid,articlename,chapterid,chaptername,size "
				+ "FROM jieqi_article_chapter WHERE articleid=?";
		
		return queryRunner.query(conn, sql, new ChapterResultSetHandler(), chapterid);
	}

	@Override
	public ChapterEntity getLasChapter(ChapterEntity chapter) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "SELECT articleid,articlename,chapterid,chaptername,size "
				+ "FROM jieqi_article_chapter WHERE articleid=? order by chapterid desc limit 1 offset 0";
		
		return queryRunner.query(conn, sql, new ChapterResultSetHandler(), chapter.getNovelNo());
	}

	@Override
	public List<ChapterEntity> getDuplicateChapter() throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "select articleid,chapterid from jieqi_article_chapter where chapterid in ("
				+"	select min(chapterid) from jieqi_article_chapter tc inner join ("
				+" 		select articleid ,chaptername from jieqi_article_chapter"
				+" 		group by articleid,chaptername having count(1)>1"
				+" 	) tc1"
				+" 	on tc.chaptername = tc1.chaptername and tc.articleid = tc1.articleid"
				+" 	group by tc.chaptername"
				+" );";
		List<ChapterEntity> result = new ArrayList<ChapterEntity>();
		List<Map<String,Object>> chapterList = queryRunner.query(conn, sql, new MapListHandler());
		for (int i = 0; i < chapterList.size(); i++) {
            Map<String, Object> map = chapterList.get(i);
            ChapterEntity chapter = new ChapterEntity();
            chapter.setNovelNo(Integer.parseInt(String.valueOf(map.get("articleid"))));
            chapter.setNovelName(String.valueOf(map.get("chaptername")));
            chapter.setChapterNo(Integer.parseInt(String.valueOf(map.get("chapterid"))));
            result.add(chapter);
		}
		return result;
	}
	
	@Override
	public ChapterEntity getChapterByChapterNameAndNovelNo(ChapterEntity chapter) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "SELECT articleid,articlename,chapterid,chaptername,size "
				+ "FROM jieqi_article_chapter WHERE articleid=? and chaptername=? limit 1 offset 0";
		
		return queryRunner.query(conn, sql, new ChapterResultSetHandler(), 
				new Object[]{chapter.getNovelNo(), chapter.getChapterName()});
	}

	@Override
	public void delete(List<ChapterEntity> chapters) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		StringBuffer ids = new StringBuffer("'");
		for(ChapterEntity chapter : chapters){
			ids.append(chapter.getChapterNo()+"',");
		}
		ids.deleteCharAt(ids.length()-1);
		String sql = "delete from jieqi_article_chapter where chapterid in ("+ids.toString()+")";
		queryRunner.update(conn, sql);
	}

	@Override
	protected User loadAdmin() throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		return queryRunner.query(conn, "SELECT uid, uname FROM jieqi_system_users ORDER BY uid LIMIT 0,1", new ResultSetHandler<User>() {

			@Override
			public User handle(ResultSet rs) throws SQLException {
				User user = null;
				if(rs != null && rs.next()) {
					user = new User();
				}
				user.setUserId(rs.getString("uid"));
				user.setUserName(rs.getString("uname"));
				return user;
			}
			
		});
	}

	@Override
	public int updateSize(ChapterEntity chapter) throws SQLException {
		String sql = "update jieqi_article_chapter set size=? " +
    			" where chapterid = ?";
    	return update(sql, new Object[]{chapter.getSize(), chapter.getChapterNo()});
	}

}
