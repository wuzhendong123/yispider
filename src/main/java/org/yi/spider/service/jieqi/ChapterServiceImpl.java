package org.yi.spider.service.jieqi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.yi.spider.constants.ConfigKey;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.ChapterEntity;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.model.UserModel;
import org.yi.spider.service.BaseService;
import org.yi.spider.service.IChapterService;
import org.yi.spider.utils.FileUtils;
import org.yi.spider.utils.ObjectUtils;
import org.yi.spider.utils.StringUtils;

public class ChapterServiceImpl extends BaseService implements IChapterService {

	protected static final String DEFAULT_STATICURL = "/reader/#subDir#/#articleNo#/#chapterNo#.html";
	
	private final class ChapterResultSetHandler implements
			ResultSetHandler<ChapterEntity> {
		@Override
		public ChapterEntity handle(ResultSet rs) throws SQLException {
			ChapterEntity chapter = null;
			if(rs != null && rs.next()) {
				chapter = new ChapterEntity();
			}
			chapter.setNovelNo(rs.getInt("articleno"));
			chapter.setNovelName(rs.getString("articlename"));
			chapter.setChapterNo(rs.getInt("chapterno"));
			chapter.setChapterName(rs.getString("chaptername"));
			chapter.setSize(rs.getInt("size"));
			return chapter;
		}
	}

	@Override
	public int save(ChapterEntity chapter) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);
		
		String sql = "insert into jieqi_article_chapter " +
				"(siteid,articleid,articlename,volumeid,posterid,poster," +
				"postdate,lastupdate,chaptername,size,attachment,chapterorder)" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?)";

		Object[] params = new Object[]{0, chapter.getNovelNo(), chapter.getNovelName(), 0, 
				chapter.getChapterName(), chapter.getSize(), Boolean.FALSE,
				new Timestamp(System.currentTimeMillis())};
		
		return queryRunner.save(conn, sql, params);
	}

	@Override
	public Map<String, Object> getTotalInfo(Integer novelno) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);
		
		String sql = "SELECT SUM(size) as size, count(*) as count FROM t_chapter WHERE articleno = ?";
        return queryRunner.query(conn, sql, new MapHandler(), novelno);
	}

	@Override
	public int getChapterOrder(ChapterEntity chapter) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);
		
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
		QueryRunner queryRunner = new QueryRunner(true);  
		
		String sql = "select count(*) from t_chapter where chaptername=?";
		Object count = queryRunner.query(conn, sql, new ScalarHandler<Object>(), new Object[]{chapter.getChapterName()});
		
		return ObjectUtils.obj2Int(count)>0;
	}

	@Override
	public Integer get(ChapterEntity chapter, int i) {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);  
		
		Integer result = -1;
        String sql = "select * from t_chapter where 1=1";
        if (i == -1) {
            sql = sql + " and chapterno < ? order by chapterno desc limit 1";
        } else if (i == 1) {
            sql = sql + " and chapterno > ? order by chapterno asc limit 1";
        }
        try {
        	result = queryRunner.query(conn, sql, new ScalarHandler<Integer>("chapterno"));
        } catch (Exception e) {
        	result = -1;
        }
            
		return result;
	}

	@Override
	public List<ChapterEntity> getChapterList(NovelEntity novel) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);  

		String sql = "select * from t_chapter where articleno = ? order by chapterno asc";
		List<Map<String, Object>> linedMap = queryRunner.query( conn, sql,  
                        new MapListHandler(), novel.getNovelNo());
		List<ChapterEntity> result = new ArrayList<ChapterEntity>();
		for (int i = 0; i < linedMap.size(); i++) {
            Map<String, Object> map = linedMap.get(i);
            ChapterEntity chapter = new ChapterEntity();
            chapter.setNovelNo(novel.getNovelNo());
            chapter.setNovelName(novel.getNovelName());
            chapter.setChapterName(String.valueOf(map.get("chaptername")));
            chapter.setChapterNo(Integer.parseInt(String.valueOf(map.get("chapterno"))));;
            chapter.setChapterOrder(chapter.getChapterNo());
            result.add(chapter);
        }
		return result;
	}

	@Override
	public ChapterEntity get(Integer chapterNo) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);  
		
		String sql = "SELECT articleno,articlename,chapterno,chaptername,size "
				+ "FROM t_chapter WHERE articleno=?";
		
		return queryRunner.query(conn, sql, new ChapterResultSetHandler(), chapterNo);
	}

	@Override
	public ChapterEntity getLasChapter(ChapterEntity chapter) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);  
		
		String sql = "SELECT articleno,articlename,chapterno,chaptername,size "
				+ "FROM t_chapter WHERE articleno=? order by chapterno desc limit 1 offset 0";
		
		return queryRunner.query(conn, sql, new ChapterResultSetHandler(), chapter.getNovelNo());
	}

	@Override
	public List<ChapterEntity> getDuplicateChapter() throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);  
		
		String sql = "select articleno,chapterno from t_chapter where chapterno in ("
				+"	select min(chapterno) from t_chapter tc inner join ("
				+" 		select articleno ,chaptername from t_chapter"
				+" 		group by articleno,chaptername having count(1)>1"
				+" 	) tc1"
				+" 	on tc.chaptername = tc1.chaptername and tc.articleno = tc1.articleno"
				+" 	group by tc.chaptername"
				+" );";
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
	
	@Override
	public ChapterEntity getChapterByChapterNameAndNovelNo(ChapterEntity chapter) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);  
		
		String sql = "SELECT articleno,articlename,chapterno,chaptername,size "
				+ "FROM t_chapter WHERE articleno=? and chaptername=? limit 1 offset 0";
		
		return queryRunner.query(conn, sql, new ChapterResultSetHandler(), 
				new Object[]{chapter.getNovelNo(), chapter.getChapterName()});
	}

	@Override
	public void delete(List<ChapterEntity> chapters) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);  
		
		StringBuffer ids = new StringBuffer("'");
		for(ChapterEntity chapter : chapters){
			ids.append(chapter.getChapterNo()+"',");
		}
		ids.deleteCharAt(ids.length()-1);
		String sql = "delete from t_chapter where chapterno in ("+ids.toString()+")";
		queryRunner.update(conn, sql);
	}

	@Override
	public String getTxtFilePath(ChapterEntity chapter) {
		String txtDir = GlobalConfig.localSite.getTxtDir();
		if(txtDir.endsWith("/")){
			txtDir = txtDir.substring(0,txtDir.length()-1);
		}
		return txtDir + FileUtils.FILE_SEPARATOR + chapter.getNovelNo()/1000
				+ FileUtils.FILE_SEPARATOR + chapter.getNovelNo()
				+ FileUtils.FILE_SEPARATOR + chapter.getChapterNo() + ".txt";
	}

	@Override
	public String getHtmlFilePath(ChapterEntity chapter) {
		String htmlDir = GlobalConfig.localSite.getHtmlDir();
		if(htmlDir.endsWith("/")){
			htmlDir = htmlDir.substring(0,htmlDir.length()-1);
		}
		return htmlDir + FileUtils.FILE_SEPARATOR + chapter.getNovelNo()/1000
				+ FileUtils.FILE_SEPARATOR + chapter.getNovelNo()
				+ FileUtils.FILE_SEPARATOR + chapter.getChapterNo() + ".html";
	}

	@Override
	public String getStaticUrl(Integer articleNo, String chapterNo) {
		String baseUrl = GlobalConfig.localSite.getSiteUrl();
		String htmlUrl = GlobalConfig.config.getString(ConfigKey.STATIC_URL, DEFAULT_STATICURL);
		htmlUrl = htmlUrl.replace("#subDir#", String.valueOf(articleNo/1000))
				.replace("#articleNo#", String.valueOf(articleNo))
				.replace("#chapterNo#", chapterNo);
		return StringUtils.getFullUrl(baseUrl, htmlUrl);
	}

	@Override
	protected UserModel loadAdmin() throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		QueryRunner queryRunner = new QueryRunner(true);  
		return queryRunner.query(conn, "SELECT uid, uname FROM jieqi_system_users ORDER BY uid LIMIT 0,1", new ResultSetHandler<UserModel>() {

			@Override
			public UserModel handle(ResultSet rs) throws SQLException {
				UserModel user = null;
				if(rs != null && rs.next()) {
					user = new UserModel();
				}
				user.setUserId(rs.getString("uid"));
				user.setUserName(rs.getString("uname"));
				return user;
			}
			
		});
	}

}
