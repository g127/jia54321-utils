package com.jia54321.utils.entity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database
 * @author gg
 * @date 2019-06-15
 */
public enum Database {
    h2, mysql, oracle, postgresql(), sqlserver, unknown;

    public static Database fromJdbcUrl(String jdbcUrl) {
        if (jdbcUrl.indexOf(":h2:") > 0 ) {
            return Database.h2;
        } else if (jdbcUrl.indexOf(":mysql:") > 0 ) {
            return Database.mysql;
        } else if (jdbcUrl.indexOf(":oracle:") > 0 ) {
            return Database.oracle;
        } else if (jdbcUrl.indexOf(":postgresql:") > 0 ) {
            return Database.postgresql;
        } else if (jdbcUrl.indexOf(":sqlserver:") > 0 ) {
            return Database.sqlserver;
        } else {
            return Database.unknown;
        }
    }


    public static Database fromDataSource(DataSource dataSource) {
        String jdbcUrl = getJdbcUrlFromDataSource(dataSource);
        return fromJdbcUrl(jdbcUrl);
    }

    /**
     * 获取 JdbcUrlFromDataSource
     * @param dataSource dataSource
     * @return JdbcUrl
     */
    public static String getJdbcUrlFromDataSource(DataSource dataSource) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            if (connection == null) {
                throw new IllegalStateException("Connection returned by DataSource [" + dataSource + "] was null");
            }
            return connection.getMetaData().getURL();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get database url", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

}
