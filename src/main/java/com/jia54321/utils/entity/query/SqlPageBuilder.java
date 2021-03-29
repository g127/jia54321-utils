package com.jia54321.utils.entity.query;

import com.jia54321.utils.entity.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class SqlPageBuilder {
    /** */
    private static Logger log = LoggerFactory.getLogger(SqlPageBuilder.class);

    private Database database = null;

    private MySqlPageBuilder mySqlPageBuilder = new MySqlPageBuilder();

    private OracleSqlPageBuilder oracleSqlPageBuilder = new OracleSqlPageBuilder();

    public SqlPageBuilder(DataSource dataSource) {
        if(dataSource != null) {
            database = Database.fromDataSource(dataSource);
        }
    }

    public String buildPageSql(String sourceSql, Page p) {
        if (p.getPageNo() < 1) {
            p.setPageNo(1);
        }
        if (p.getPageSize() < 1) {
            p.setPageSize(16);
        }
        if (Database.mysql.equals(database)) {
            return mySqlPageBuilder.buildPageSql(sourceSql, p);
        } else if (Database.oracle.equals(database)) {
            return oracleSqlPageBuilder.buildPageSql(sourceSql, p);
        } else {
            return mySqlPageBuilder.buildPageSql(sourceSql, p);
        }
    }

    protected String getPageSql() {
        if (Database.mysql.equals(database)) {
            return mySqlPageBuilder.getPageSql();
        } else if (Database.oracle.equals(database)) {
            return oracleSqlPageBuilder.getPageSql();
        } else {
            return mySqlPageBuilder.getPageSql();
        }
    }

    /**
     * 返回 查询表语句
     * @param tabName 表名称
     * @return sql
     */
    public String getShowTabSql(String tabName) {
        if (Database.mysql.equals(database)) {
            return mySqlPageBuilder.getShowTabSql(tabName);
        } else if (Database.oracle.equals(database)) {
            return oracleSqlPageBuilder.getShowTabSql(tabName);
        } else {
            return mySqlPageBuilder.getShowTabSql(tabName);
        }
    }

    public class MySqlPageBuilder  {

        private final static String PAGE_SQL = "%s LIMIT %d,%d";

        public final static String SHOW_TAB = "SHOW TABLES LIKE '%s'";


        public String buildPageSql(String sourceSql, Page p) {
            String returnSql =String.format(getPageSql() ,sourceSql, (p.getPageNo()-1) * p.getPageSize(), p.getPageSize());
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s, %s, %s", sourceSql, p, returnSql));
            }
            return returnSql;
        }

        public String getPageSql(){
            return PAGE_SQL;
        }

        public String getShowTabSql(String tabName) {
            return String.format(SHOW_TAB, tabName);
        }
    }

    public class OracleSqlPageBuilder {

        private final static String PAGE_SQL = "SELECT * FROM ( SELECT A.*, ROWNUM RN FROM (%s) A WHERE ROWNUM <= %d ) WHERE RN >= %d";

        private final static String SHOW_TAB = "SELECT TABLE_NAME FROM TABS WHERE TABLE_NAME LIKE '%s'";
        /**
         * 构建查询条件er
         */
        public String buildPageSql(String sourceSql, Page p) {
            String returnSql = String.format(getPageSql(), sourceSql, p.getPageNo() * p.getPageSize(), ( p.getPageNo() -1 ) * p.getPageSize() + 1);
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s, %s, %s", sourceSql, p, returnSql));
            }
            return returnSql;
        }

        public String getPageSql(){
            return PAGE_SQL;
        }

        public String getShowTabSql(String tabName) {
            return String.format(SHOW_TAB, tabName);
        }
    }

}
