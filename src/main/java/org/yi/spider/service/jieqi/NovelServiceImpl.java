package org.yi.spider.service.jieqi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.model.User;
import org.yi.spider.service.BaseService;
import org.yi.spider.service.INovelService;
import org.yi.spider.utils.ObjectUtils;
import org.yi.spider.utils.StringUtils;

public class NovelServiceImpl extends BaseService implements INovelService {
	
	private final class NovelEntityResutlSetHandler implements
			ResultSetHandler<NovelEntity> {
		@Override
		public NovelEntity handle(ResultSet rs) throws SQLException {
			NovelEntity novel = null;
			if(rs != null && rs.next()) {
				novel = new NovelEntity();
				novel.setNovelNo(rs.getInt("articleid"));
				novel.setNovelName(rs.getString("articlename"));
				novel.setAuthor(rs.getString("author"));
				novel.setTopCategory(rs.getInt("sortid"));
				novel.setSubCategory(rs.getInt("typeid"));
				novel.setIntro(rs.getString("intro"));
				if(GlobalConfig.localSite.getUsePinyin() == 1) {
					novel.setPinyin(rs.getString("pyh"));
				}
			}
			return novel;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(NovelServiceImpl.class);
	
	public NovelServiceImpl() {
		try {
			getAdmin();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	protected User loadAdmin() throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		return queryRunner.query(conn, "SELECT uid, uname FROM jieqi_system_users ORDER BY uid LIMIT 0,1", new ResultSetHandler<User>() {

			@Override
			public User handle(ResultSet rs) throws SQLException {
				User user = null;
				if(rs != null && rs.next()) {
					user = new User();
					user.setUserId(rs.getString("uid"));
					user.setUserName(rs.getString("uname"));
				}				
				return user;
			}
			
		});
	}

	@Override
	public int update(NovelEntity novel) throws SQLException {
		StringBuffer sql = new StringBuffer("update jieqi_article_article set lastupdate=?," +
    			" lastvolumeid=?,lastvolume=?");
		List<Object> params = new ArrayList<Object>();
		params.add(getJieQiTimeStamp());
		params.add(0);
		params.add("");
		if(novel.getLastChapterno() != null) {
			sql.append(",lastchapterid=?,lastchapter=?");
			params.add(novel.getLastChapterno());
			params.add(novel.getLastChapterName());
		}
		if(novel.getChapters() != null) {
			sql.append(",chapters=?,size=?");
			params.add(novel.getChapters());
			params.add(novel.getSize());
		}
		
		if(novel != null && novel.getImgFlag() != null) {
			sql.append(",imgflag=? ");
			params.add(novel.getImgFlag());
		}
    			
		sql.append(" where articleid = ?");
		params.add(novel.getNovelNo());
		
    	return update(sql.toString(), params.toArray());
	}
	
	@Override
	public void repair(NovelEntity novel, NovelEntity newNovel)
			throws SQLException {
		String sqlPre = "update jieqi_article_article set lastupdate=? ";
		List<Object> params = new ArrayList<Object>();
		params.add(getJieQiTimeStamp());

		StringBuffer sql = new StringBuffer();
		if(StringUtils.isNotBlank(newNovel.getIntro())){
			sql.append(" ,intro = ?");
			params.add(newNovel.getIntro());
		}
		if(newNovel.getTopCategory() != null){
			sql.append(" ,sortid = ?");
			params.add(newNovel.getTopCategory());
		}
		if(newNovel.getSubCategory() != null){
			sql.append(" ,typeid = ?");
			params.add(newNovel.getSubCategory());
		}
		if(newNovel.getFullFlag() != null){
			sql.append(" ,fullflag = ?");
			params.add(newNovel.getFullFlag());
		}
		if(StringUtils.isNotBlank(newNovel.getKeywords())){
			sql.append(" ,keywords = ?");
			params.add(newNovel.getKeywords());
		}
		sql.append(" where articleid = ?");
		params.add(novel.getNovelNo());
		if(sql.length() > 0) {
			update(sqlPre + sql.toString(), params.toArray());
		}
	}
	
	@Override
	public boolean exist(String name) throws SQLException {
		String sql = "select count(*) from jieqi_article_article where articlename=?";
		Object count = query( sql, new Object[]{name});
		return ObjectUtils.obj2Int(count)>0;
	}

	@Override
	public Number saveNovel(final NovelEntity novel) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true); 
		
		List<Object> params = new ArrayList<Object>(){

			private static final long serialVersionUID = 1L;
			{
				add(0);
				add(getJieQiTimeStamp());
				add(getJieQiTimeStamp());
				add(novel.getNovelName());
				add(novel.getKeywords());
				add(novel.getInitial());
				add(0);
				add(novel.getAuthor());
				add(admin.getUserId());
				add(admin.getUserName());
				add(novel.getTopCategory());
				add(novel.getSubCategory());
				add(novel.getIntro());
				add(EMPTY);
				add(EMPTY);
				add(novel.getFullFlag());
			}
		};
		
		String ziduan = "", zhi = "";
		if(GlobalConfig.localSite.getUsePinyin() == 1) {
			ziduan = ", pyh, zysoft_pinyin";
			zhi = ",?,?";
			params.add(novel.getPinyin());
			params.add(novel.getPinyin());
		}
		
		String sql = "insert into jieqi_article_article " +
				"(siteid,postdate,lastupdate,articlename,keywords,`initial`," +
				"authorid,author,posterid,poster,sortid,typeid,intro,notice,setting,fullflag "+ziduan+")" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? "+zhi+")";
		
		return queryRunner.save(conn, sql, params.toArray());
	}

	@Override
	public NovelEntity find(String novelName) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String ziduan = "";
		if(GlobalConfig.localSite.getUsePinyin() == 1) {
			ziduan = ", pyh";
		}
		String sql = "select articleid,articlename,author,sortid,typeid,intro"+ziduan+" from jieqi_article_article where articlename=?";
		
		return queryRunner.query(conn, sql, new NovelEntityResutlSetHandler(), novelName);
	}

	@Override
	public Map<String, Object> loadSystemParam() throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "SELECT cname, cvalue FROM jieqi_system_configs where modname='article'";
		
		return queryRunner.query(conn, sql, new MapHandler());
	}

	@Override
	public Number getMaxPinyin(String pinyin) throws SQLException {
		//如果杰奇定制了拼音则查询拼音重复出现次数， 否则返回0
		if(GlobalConfig.localSite.getUsePinyin() == 1) { 
			Connection conn = DBPool.getInstance().getConnection();
			YiQueryRunner queryRunner = new YiQueryRunner(true);
			
			String sql = "SELECT count(*) FROM jieqi_article_article WHERE pyh REGEXP '"+pinyin+"[0-9]*' ";
			
			return queryRunner.query(conn, sql, new ScalarHandler<Integer>());
		}
		return 0;
	}

	@Override
	public NovelEntity get(String novelNo) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String ziduan = "";
		if(GlobalConfig.localSite.getUsePinyin() == 1) {
			ziduan = ", pyh";
		}
		String sql = "select articleid,articlename,author,sortid,typeid,intro"+ziduan+" from jieqi_article_article where articleid=?";
		
		return queryRunner.query(conn, sql, new NovelEntityResutlSetHandler(), novelNo);
	}

}
