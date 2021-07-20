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
	private StringBuilder totalElementsSql;

	/** sqlExceptSelect */
	private StringBuilder sqlExceptSelect;

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

	public StringBuilder getTotalElementsSql() {
		return totalElementsSql;
	}

	public void setTotalElementsSql(StringBuilder totalElementsSql) {
		this.totalElementsSql = totalElementsSql;
	}

	public StringBuilder getSqlExceptSelect() {
		return sqlExceptSelect;
	}

	public void setSqlExceptSelect(StringBuilder sqlExceptSelect) {
		this.sqlExceptSelect = sqlExceptSelect;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", SqlContext.class.getSimpleName() + "[", "]")
				.add("sql=" + sql)
				.add("primaryKey='" + primaryKey + "'")
				.add("params=" + params)
				.add("totalElementsSql=" + totalElementsSql)
				.toString();
	}
}
