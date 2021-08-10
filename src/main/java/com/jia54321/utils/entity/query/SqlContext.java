package com.jia54321.utils.entity.query;

import java.util.List;
import java.util.StringJoiner;

/**
 * Sql 上下文
 */
public class SqlContext {
    /** 执行的sql */
    private StringBuilder sql;

    /** 主键名称 */
    private String        primaryKey;

    /** 参数，对应sql中的?号 */
    private List<?>  params;

	/** 总数量sql,类似 select count(*) from xxx */
	private StringBuilder countSql;

	/** sqlExceptSelect 例如： FROM ims_renren_shop_member_session WHERE     session_id = ?   ORDER BY create_time DESC */
	private StringBuilder sqlExceptSelect;

	/** sqlTableName 表名，例如： ims_renren_shop_member_session  */
	private String tableName;

	/** sqlWhereConditions 查询条件，例如： session_id = ? and ... */
	private String sqlWhereConditions;

	/** sqlGroupConditions 查询条件，例如： session_id, ...  */
	private String sqlGroupConditions;

	/** sqlOrderConditions 排序条件，例如： create_time DESC, ...  */
	private String sqlOrderConditions;

    public SqlContext(StringBuilder sql, String primaryKey, List<Object> params) {
        this.sql = sql;
        this.primaryKey = primaryKey;
        this.params = params;
    }

	public StringBuilder getSql() {
		return sql;
	}

	public void setSql(StringBuilder sql) {
		this.sql = sql;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List<?> getParams() {
		return params;
	}

	public void setParams(List<?> params) {
		this.params = params;
	}

	public StringBuilder getCountSql() {
		return countSql;
	}

	public void setCountSql(StringBuilder countSql) {
		this.countSql = countSql;
	}

	public StringBuilder getSqlExceptSelect() {
		return sqlExceptSelect;
	}

	public void setSqlExceptSelect(StringBuilder sqlExceptSelect) {
		this.sqlExceptSelect = sqlExceptSelect;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSqlWhereConditions() {
		return sqlWhereConditions;
	}

	public void setSqlWhereConditions(String sqlWhereConditions) {
		this.sqlWhereConditions = sqlWhereConditions;
	}

	public String getSqlGroupConditions() {
		return sqlGroupConditions;
	}

	public void setSqlGroupConditions(String sqlGroupConditions) {
		this.sqlGroupConditions = sqlGroupConditions;
	}

	public String getSqlOrderConditions() {
		return sqlOrderConditions;
	}

	public void setSqlOrderConditions(String sqlOrderConditions) {
		this.sqlOrderConditions = sqlOrderConditions;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", SqlContext.class.getSimpleName() + "[", "]")
				.add("sql=" + sql)
				.add("primaryKey='" + primaryKey + "'")
				.add("params=" + params)
				.add("countSql=" + countSql)
				.add("sqlExceptSelect=" + sqlExceptSelect)
				.add("tableName='" + tableName + "'")
				.add("sqlWhereConditions='" + sqlWhereConditions + "'")
				.add("sqlGroupConditions='" + sqlGroupConditions + "'")
				.add("sqlOrderConditions='" + sqlOrderConditions + "'")
				.toString();
	}
}
