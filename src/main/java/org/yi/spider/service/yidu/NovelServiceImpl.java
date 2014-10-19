package org.yi.spider.service.yidu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.model.User;
import org.yi.spider.service.BaseService;
import org.yi.spider.service.INovelService;
import org.yi.spider.utils.ObjectUtils;
import org.yi.spider.utils.StringUtils;

public class NovelServiceImpl extends BaseService implements INovelService {
	
	private final class NovelEntityResultSetHandler implements
			ResultSetHandler<NovelEntity> {
		@Override
		public NovelEntity handle(ResultSet rs) throws SQLException {
			NovelEntity novel = null;
			if(rs != null && rs.next()) {
				novel = new NovelEntity();
				novel.setNovelNo(rs.getInt("articleno"));
				novel.setNovelName(rs.getString("articlename"));
				novel.setAuthor(rs.getString("author"));
				novel.setTopCategory(rs.getInt("category"));
				novel.setSubCategory(rs.getInt("subcategory"));
				novel.setIntro(rs.getString("intro"));
				novel.setInitial(rs.getString("initial"));
				novel.setKeywords(rs.getString("keywords"));
				novel.setPinyin(rs.getString("pinyin"));
			}
			
			return novel;
		}
	}

	@Override
	protected User loadAdmin() {
		return null;
	}
	
	@Override
	public void repair(NovelEntity novel, NovelEntity newNovel) throws SQLException {
		String sqlPre = "update t_article set lastupdate=? ";
		List<Object> params = new ArrayList<Object>();
		params.add(new Timestamp(System.currentTimeMillis()));
		
		StringBuffer sql = new StringBuffer();
		if(StringUtils.isNotBlank(newNovel.getIntro())){
			sql.append(" ,intro = ?");
			params.add(newNovel.getIntro());
		}
		if(newNovel.getTopCategory() != null){
			sql.append(" ,category = ?");
			params.add(newNovel.getTopCategory());
		}
		if(newNovel.getSubCategory() != null){
			sql.append(" ,subcategory = ?");
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
		sql.append(" where articleno = ?");
		params.add(novel.getNovelNo());
		if(sql.length() > 0) {
			update(sqlPre + sql.toString(), params.toArray());
		}
	}
	
	@Override
	public int update(NovelEntity novel) throws SQLException {
		StringBuffer sql = new StringBuffer("update t_article set lastupdate=?");

		List<Object> params = new ArrayList<Object>();
		params.add(new Timestamp(System.currentTimeMillis()));
		if(novel.getLastChapterno() != null) {
			sql.append(",lastchapterno=?,lastchapter=?");
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
		sql.append(" where articleno = ?");
		params.add(novel.getNovelNo());
	    return update(sql.toString(), params.toArray());
	}

	@Override
	public boolean exist(String name) throws SQLException {
		String sql = "select count(*) from t_article where articlename=?";
		Object count = query(sql, new Object[]{name});
		return ObjectUtils.obj2Int(count)>0;
	}

	@Override
	public Integer saveNovel(NovelEntity novel) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true); 
		
		String sql = "INSERT INTO t_article("
                   + "articlename, pinyin, initial ,keywords ,authorid ,author ,category ,subcategory, "
                   + "intro ,fullflag ,postdate,dayvisit, weekvisit, monthvisit,  "
                   + "allvisit, dayvote, weekvote, monthvote, allvote ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		Object[] params = new Object[]{novel.getNovelName(), novel.getPinyin(), novel.getInitial(), StringUtils.trimToEmpty(novel.getKeywords()), 
				0, novel.getAuthor(), novel.getTopCategory(), novel.getSubCategory(),
				novel.getIntro(), novel.getFullFlag(), new Timestamp(System.currentTimeMillis()), 
				0, 0, 0, 0, 0, 0, 0, 0};
		
		return queryRunner.save(conn, sql, params);
	}

	@Override
	public NovelEntity find(String novelName) throws SQLException {
		
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "select * from t_article where deleteflag=false and articlename=?";
		
		return queryRunner.query(conn, sql, new NovelEntityResultSetHandler(), novelName);
	}

	@Override
	public Map<String, Object> loadSystemParam() {
		
		return null;
	}

	@Override
	public Number getMaxPinyin(String pinyin) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);
		
		String sql = "SELECT count(*) FROM t_article WHERE pinyin ~ '^"+pinyin+"\\d*' ";
		
		return queryRunner.query(conn, sql, new ScalarHandler<Number>());
	}

	@Override
	public NovelEntity get(String novelNo) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "select * from t_article where deleteflag=false and articleno=?";
		
		return queryRunner.query(conn, sql, new NovelEntityResultSetHandler(), Integer.parseInt(novelNo));
	}
	
}
