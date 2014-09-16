package org.yi.spider.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.model.User;

public abstract class BaseService {
	
	protected static final String EMPTY = "";

	protected static User admin;

	/**
	 * 
	 * <p>加载管理员信息</p>
	 * @return				管理员
	 * @throws SQLException	数据库异常
	 */
	public User getAdmin() throws SQLException {
		if(admin == null) {
			admin = loadAdmin();
		}
		return admin;
	}
	
	protected Integer getJieQiTimeStamp() {
        return Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000));
    }

	protected abstract User loadAdmin() throws SQLException;
	
	/**
     * 
     * <p>根据SQL和参数更新小说信息</p>
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public int update(String sql, Object[] params) throws SQLException {
    	Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
	    return queryRunner.update(conn, sql, params);
    }
    
    public Object query(String sql, Object[] params) throws SQLException {
    	Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true); 
		
		return queryRunner.query(conn, sql, new ScalarHandler<Object>(), params);
    }
    
}
