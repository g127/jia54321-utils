package com.jia54321.utils.entity.dao;

/**
 * 
 * @author G
 *
 */
public class CrudDdlDaoConst {
	public final static String SQL_BAK_TAB = "CREATE TABLE %s AS SELECT * FROM %s ";
	public final static String SQL_DROP_TAB = "DROP TABLE %s";
	
	//建表BY数字类型主键
	public final static String SQL_CREATE_TAB_BY_NUMERIC_PK = "CREATE TABLE  %s( %s NUMERIC(10,0) NOT NULL, PRIMARY KEY(%s))";
	public final static String SQL_CREATE_NUMERIC_PK_AUTO_INCREMENT = "ALTER TABLE %s MODIFY %s INTEGER AUTO_INCREMENT";
	
	//建表BY字符类型主键
	public final static String SQL_CREATE_TAB_BY_VARCHAR_PK = "CREATE TABLE  %s( %s VARCHAR(40) NOT NULL, PRIMARY KEY(%s)) ";
	
 
	//ALTER TABLE 【表名字】 ADD 【列名称】 INT NOT NULL  COMMENT '注释说明'
	//private String SQL_ADD_COL_TAB    = " ALTER TABLE XT{0}YWDJ ADD {1} {2} {3} COMMENT '{4}'";
	
	/** 数字类型 mysql*/
	public final static String MYSQL_SQL_ADD_COL_TAB_NUMBER    = " ALTER TABLE %s ADD %s NUMERIC(%d,%d) COMMENT '%s'";
	/** 字符类型 mysql*/
	public final static String MYSQL_SQL_ADD_COL_TAB_VARCHAR   = " ALTER TABLE %s ADD %s VARCHAR(%d) COMMENT '%s'";
	/** 时间类型 mysql */
	public final static String MYSQL_SQL_ADD_COL_TAB_DATETIME  = " ALTER TABLE %s ADD %s TIMESTAMP COMMENT '%s'";
	
	/** 引擎定义InnoDB mysql*/
	public final static String MYSQL_SQL_ADD_ENGINE_INNODB   = " ALTER TABLE %s ENGINE='InnoDB'";
	/** 引擎定义MyISAM mysql*/
	public final static String MYSQL_SQL_ADD_ENGINE_MYISAM   = " ALTER TABLE %s ENGINE='MyISAM'";
	
	/** 字段注释 mysql  */
	public final static String MYSQL_SQL_COL_COMMENT_NUMBER   = "ALTER TABLE %s MODIFY %s NUMERIC(%d,%d) COMMENT '%s'";
	public final static String MYSQL_SQL_COL_COMMENT_VARCHAR  = "ALTER TABLE %s MODIFY %s VARCHAR(%d)  COMMENT '%s'";
	public final static String MYSQL_SQL_COL_COMMENT_DATETIME   = "ALTER TABLE %s MODIFY %s TIMESTAMP COMMENT '%s'";
	/** 表注释 mysql  */
	public final static String MYSQL_SQL_TABLE_COMMENT  = " ALTER TABLE %s COMMENT ='%s' ";
	
	
	/** 数字类型 */
	public final static String ORACLE_SQL_ADD_COL_TAB_NUMBER    = " ALTER TABLE %s ADD %s NUMERIC(%d,%d)";
	/** 字符类型 */
	public final static String ORACLE_SQL_ADD_COL_TAB_VARCHAR   = " ALTER TABLE %s ADD %s VARCHAR(%d)";
	/** 时间类型 oracle  */
	public final static String ORACLE_SQL_ADD_COL_TAB_DATETIME  = " ALTER TABLE %s ADD %s TIMESTAMP ";
	/** 字段注释 oracle  */
	public final static String ORACLE_SQL_COL_COMMENT  = " COMMENT ON COLUMN %s.%s IS '%s'";
	/** 表注释 oracle  */
	public final static String ORACLE_SQL_TABLE_COMMENT  = " COMMENT ON TABLE %s IS '%s'";
	
	
	
	/** 表字段查询  */
	public final static String QUERY_DESC_MYSQL = "DESC %s";
	/** 表字段查询  */
	public final static String QUERY_DESC_ORACLE = "SELECT COLUMN_NAME, DATA_TYPE FROM USER_TAB_COLUMNS WHERE TABLE_NAME='%s' ";
}
