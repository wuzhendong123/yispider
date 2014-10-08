package org.yi.spider.db;

import java.beans.PropertyVetoException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.yi.spider.utils.PropertiesUtils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBPool {
	
	private static final String JDBC_FILE = "jdbc.properties";

	private static DBPool dbPool;
	private static ComboPooledDataSource dataSource;
	
	static {
		try {
			PropertiesConfiguration dbcfg = PropertiesUtils.load(JDBC_FILE, Charset.forName("UTF-8"));
			
			dataSource = new ComboPooledDataSource();
			dataSource.setUser(dbcfg.getString("jdbc.username"));
			dataSource.setPassword(dbcfg.getString("jdbc.password"));
			dataSource.setJdbcUrl(dbcfg.getString("jdbc.url"));
			dataSource.setDriverClass(dbcfg.getString("jdbc.driverClassName"));
			
			//初始花连接数，取值应在minPoolSize与maxPoolSize之间。默认: 3
			dataSource.setInitialPoolSize(dbcfg.getInt("jdbc.initialPoolSize", 4));
			//连接池最小连接数
			dataSource.setMinPoolSize(dbcfg.getInt("jdbc.minPoolSize", 1));
			//连接池最大连接数
			dataSource.setMaxPoolSize(dbcfg.getInt("jdbc.maxPoolSize", 8));
			
			dataSource.setMaxIdleTime(dbcfg.getInt("jdbc.maxIdleTime", 120));
			dataSource.setAcquireIncrement(dbcfg.getInt("jdbc.acquireIncrement", 1));
			dataSource.setAcquireRetryAttempts(dbcfg.getInt("jdbc.acquireRetryAttempts", 30));
			dataSource.setAcquireRetryDelay(dbcfg.getInt("jdbc.acquireRetryDelay", 1000));
			dataSource.setTestConnectionOnCheckin(dbcfg.getBoolean("jdbc.testConnectionOnCheckin", false));
			dataSource.setAutomaticTestTable(dbcfg.getString("jdbc.automaticTestTable", "c3p0TestTable"));
			dataSource.setCheckoutTimeout(dbcfg.getInt("jdbc.checkoutTimeout", 0));
			
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	private DBPool() {}

	public final static DBPool getInstance() {
		if(dbPool == null) {
			synchronized (DBPool.class) {  
				if(dbPool == null) {
					dbPool = new DBPool();
				}
			}
		}
		return dbPool;
	}

	public final Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("从数据源获取链接失败! ", e);
		}
	}
	
	public final void releaseConnection(Connection conn) {
		try {
			if(null != conn)
				conn.close();
		} catch (SQLException e) {
			throw new RuntimeException("释放数据库连接失败! ", e);
		}
	}

}
