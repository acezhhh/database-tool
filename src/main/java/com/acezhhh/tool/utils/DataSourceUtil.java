package com.acezhhh.tool.utils;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author acezhhh
 * @date 2022/4/20
 */
public class DataSourceUtil {

    private static DataSource dataSource;

    private static NamedParameterJdbcTemplate jdbcTemplate;

    public static String dataBase;

    public synchronized static boolean connect(String url, String dataBaseName, String userName, String passWord) {
        try {
            if (dataSource != null) {
                close();
            }
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            basicDataSource.setUrl(url);
            basicDataSource.setUsername(userName);
            basicDataSource.setPassword(passWord);
            dataSource = basicDataSource;
            dataBase = dataBaseName;
            dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized void close() throws SQLException {
        if (dataSource != null) {
            return;
        }
        dataSource.getConnection().close();
    }

    public static NamedParameterJdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate != null) {
            return jdbcTemplate;
        }
        return buildJdbcTemplate();
    }

    public static synchronized NamedParameterJdbcTemplate buildJdbcTemplate() {
        if (jdbcTemplate != null) {
            return jdbcTemplate;
        }
        if (dataSource == null) {
            throw new RuntimeException("先初始化数据库连接");
        }
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return jdbcTemplate;
    }


}
