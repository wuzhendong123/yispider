package org.yi.spider.service.jieqi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.model.UserModel;
import org.yi.spider.service.BaseService;
import org.yi.spider.service.INovelService;
import org.yi.spider.utils.ObjectUtils;
import org.yi.spider.utils.StringUtils;

public class NovelServiceImpl extends BaseService implements INovelService {
	
	protected UserModel loadAdmin() throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
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

	@Override
	public int update(NovelEntity novel) throws SQLException {
		String sql = "update jieqi_article_article set lastupdate=?," +
    			" lastvolumeid=?,lastvolume=?,lastchapterid=?,lastchapter=?,chapters=?,size=? " +
    			" where articleid = ?";
    	return update(sql, new Object[]{getJieQiTimeStamp(), 0, "", 
    			novel.getLastChapterno(), novel.getLastChapterName(),
    			novel.getChapters(), novel.getSize(), novel.getNovelNo()});
	}
	
	@Override
	public void repair(NovelEntity novel, NovelEntity newNovel)
			throws SQLException {
		String sqlPre = "update t_article set lastupdate=? ";
		List<Object> params = new ArrayList<Object>();
		params.add(new Timestamp(System.currentTimeMillis()));
		
		StringBuffer sql = new StringBuffer();
		if(StringUtils.isBlank(novel.getIntro())){
			sql.append(" ,intro = ?");
			params.add(newNovel.getIntro());
		}
		if(StringUtils.isBlank(novel.getInitial())){
			sql.append(" ,`initial` = ?");
			params.add(newNovel.getInitial());
		}
		if(StringUtils.isBlank(novel.getKeywords())){
			sql.append(" ,keywords = ?");
			params.add(newNovel.getKeywords());
		}
		if(sql.length() > 0) {
			update(sqlPre + sql.toString(), params.toArray());
		}
	}
	
	private Integer getJieQiTimeStamp() {
        return Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000));
    }

	@Override
	public boolean exist(String name) throws SQLException {
		String sql = "select count(*) from jieqi_article_article where articlename=?";
		Object count = query( sql, new Object[]{name});
		return ObjectUtils.obj2Int(count)>0;
	}

	@Override
	public Integer saveNovel(NovelEntity novel) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true); 
		
		String sql = "insert into jieqi_article_article " +
				"(siteid,postdate,lastupdate,articlename,keywords,`initial`," +
				"authorid,author,posterid,poster,sortid,typeid,intro,notice,setting,fullflag)" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] params = new Object[]{0,getJieQiTimeStamp(),getJieQiTimeStamp(), 
				novel.getNovelName(), novel.getKeywords(), novel.getInitial(),
				0, novel.getAuthor(), -1, "待完成", novel.getTopCategory(), 
				novel.getSubCategory(), novel.getIntro(), EMPTY, EMPTY, novel.getFullFlag()};
		
		return queryRunner.save(conn, sql, params);
	}

	@Override
	public NovelEntity find(String novelName) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "select articleid,articlename,author,sortid,typeid from jieqi_article_article where articlename=?";
		
		return queryRunner.query(conn, sql, new ResultSetHandler<NovelEntity>() {

			@Override
			public NovelEntity handle(ResultSet rs) throws SQLException {
				NovelEntity novel = null;
				if(rs != null && rs.next()) {
					novel = new NovelEntity();
				}
				novel.setNovelNo(rs.getInt("articleid"));
				novel.setNovelName(rs.getString("articlename"));
				novel.setAuthor(rs.getString("author"));
				novel.setTopCategory(rs.getInt("sortid"));
				novel.setSubCategory(rs.getInt("typeid"));
				return novel;
			}
			
		}, novelName);
	}

}
