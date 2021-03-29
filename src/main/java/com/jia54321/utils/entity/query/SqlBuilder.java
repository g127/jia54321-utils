package com.jia54321.utils.entity.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jia54321.utils.IdGeneration;
import com.jia54321.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CrudSqlBuilder
 * @author gg
 * @date 2019-08-25
 */
public class SqlBuilder {
    static final Logger log = LoggerFactory.getLogger(JsonHelper.class);
    /**
     * 构建insert语句
     *
     * @param table
     * @return
     */
    public SqlContext buildInsertSql(CrudTableDesc table) {
        StringBuilder sql = new StringBuilder();
        StringBuilder args = new StringBuilder();
        List<Object> params = new ArrayList<Object>();

        String tableName = table.getTableDesc().getTableName();
        String primaryName = table.getTableDesc().getTypePkName();

        //主键值
        Object columnPrimaryValue = getIgnoreCaseKey(table.getColumnProps(), primaryName);

        //主键由 数据库自动 组成
        Boolean isDbAutoIncreaseId = table.getTableDesc().isPkUseDbAutoIncreaseId();
        //主键由 程序自动的 VARCHAR ObjectId 组成
        Boolean isUseObjectId  = table.getTableDesc().isPkUseVarcharId();
        //主键由 主键程序生成NUMBER 类型
        Boolean isUseNumberId = table.getTableDesc().isPkUseNumberId();

        //是否包含主键提交
        Boolean columnNameNotIncludePrimaryName = JsonHelper.isEmpty(columnPrimaryValue);


        sql.append("INSERT INTO ").append(tableName);
        sql.append("(");

        args.append("(");

        if (isUseObjectId) {
            if (columnNameNotIncludePrimaryName) {
                table.setPrimaryValue(IdGeneration.getStringId());
            } else {
                if (log.isWarnEnabled()) {
                    log.warn(String.format("字段中包含主键值%s, 主键值%s将被覆盖", columnPrimaryValue, table.getPrimaryValue()));
                }
                table.setPrimaryValue(columnPrimaryValue);
            }
        }
        if (isUseNumberId) {
            // table.setPrimaryValue(IdGeneration.getPrimaryKey());
            throw new RuntimeException("不支持数字类型主键");
        }

        if (!isDbAutoIncreaseId) {
            // 增加主键SQL
            sql.append(table.getTableDesc().getTypePkName()).append(",");
            args.append("?").append(",");
            params.add(table.getPrimaryValue());
        }

        // 项目数量为0
        if (params.size() <= 0 && table.getColumnProps().size() <= 0) {
            throw new RuntimeException("请至少填写一个项目。");
        }

        if (table.getColumnProps().size() > 0) {
            for (Iterator<Entry<String, Object>> iterator = table.iteratorColumnProps(); iterator.hasNext(); ) {
                Entry<String, Object> e = iterator.next();

                String columnName = e.getKey();
                Object value = e.getValue();
                if (value == null || primaryName.equalsIgnoreCase(columnName)) {
                    continue;
                }
                sql.append(columnName).append(",");
                args.append("?").append(",");
                params.add(value);
            }
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        sql.append(" VALUES ");

        args.deleteCharAt(args.length() - 1);
        args.append(")");
        sql.append(args);

        return new SqlContext(sql, primaryName, params);
    }

    /**
     * 构建更新sql
     *
     * @param table
     * @return
     */
    public SqlContext buildUpdateSql(CrudTableDesc table) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<Object>();

        Object primaryValue = null;
        String tableName = table.getTableDesc().getTableName();
        String primaryName = table.getTableDesc().getTypePkName();

        sql.append("UPDATE ").append(tableName);
        sql.append(" SET ");

        for (Iterator<Entry<String, Object>> iterator = table.iteratorColumnProps(); iterator.hasNext(); ) {
            Entry<String, Object> e = iterator.next();
            String columnName = e.getKey();
            Object value = e.getValue();
            if (value == null) {
                continue;
            }
            if (primaryName.equalsIgnoreCase(columnName)) {
                primaryValue = value;
            }
            sql.append(columnName).append(" = ").append("?");
            sql.append(",");
            params.add(value);
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE ").append(primaryName).append(" = ?");
        params.add(primaryValue);
        if (null == primaryValue) {
            throw new RuntimeException("更新操作需要明确主键。");
        }

        return new SqlContext(sql, primaryName, params);
    }

    /**
     * 构建buildDeleteSql
     *
     * @param table
     * @return
     */
    public SqlContext buildDeleteSql(CrudTableDesc table) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<Object>();

        Object primaryValue = null;
        String tableName = table.getTableDesc().getTableName();
        String primaryName = table.getTableDesc().getTypePkName();

        sql.append("DELETE FROM ").append(tableName);
        sql.append(" WHERE 1=1 ");
        for (Iterator<Entry<String, Object>> iterator = table.iteratorColumnProps(); iterator.hasNext(); ) {
            Entry<String, Object> e = iterator.next();
            String columnName = e.getKey();
            Object value = e.getValue();
            if (value == null) {
                continue;
            }
            if (primaryName.equalsIgnoreCase(columnName)) {
                primaryValue = value;
                continue;
            }
            sql.append(" AND ");
            sql.append(columnName).append(" = ").append("?");
            params.add(value);
        }
        sql.append(" AND ").append(primaryName).append(" = ?");
        params.add(primaryValue);

        return new SqlContext(sql, primaryName, params);
    }


    /**
     * 构建Get sql
     *
     * @param table
     * @return SqlContent
     */
    public SqlContext buildGetSql(CrudTableDesc table) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<Object>();

        Object primaryValue = null;
        String tableName = table.getTableDesc().getTableName();
        String primaryName = table.getTableDesc().getTypePkName();
        for (Iterator<Entry<String, Object>> iterator = table.iteratorColumnProps(); iterator.hasNext(); ) {
            Entry<String, Object> e = iterator.next();
            String columnName = e.getKey();
            Object value = e.getValue();
            if (value == null) {
                continue;
            }
            if (primaryName.equalsIgnoreCase(columnName)) {
                primaryValue = value;
                break;
            }
        }
        sql.append("SELECT * FROM ").append(tableName);
        sql.append(" WHERE ").append(primaryName).append(" = ?");
        params.add(primaryValue);

        return new SqlContext(sql, primaryName, params);
    }

    /**
     * 构造查询条件
     *
     * @param table
     * @param cons
     * @param sort
     * @return
     */
    public SqlContext buildQueryCondition(CrudTableDesc table, List<? extends OperationBean> cons, List<? extends OperationBean> sort) {
        if (null == cons) {
            cons = new ArrayList<OperationBean>(0);
        }
        if (null == sort) {
            sort = new ArrayList<OperationBean>(0);
        }
        List<OperationBean> operations = new ArrayList<OperationBean>(cons.size() + sort.size());
        operations.addAll(cons);
        operations.addAll(sort);
        return buildQueryOperation(table, operations);
    }

    /**
     * 构造查询条件
     *
     * @param table
     * @param operations
     * @return
     */
    public SqlContext buildQueryOperation(CrudTableDesc table, List<? extends OperationBean> operations) {
        return buildQueryOperation(table, operations, true);
    }


    /**
     * 构建查询条件
     *
     * @param table
     * @param whereSql
     * @param params
     * @param isNeedTotalElements
     * @return
     */
    public SqlContext buildQuerySQL(CrudTableDesc table, String whereSql, List<Object> params, Boolean isNeedTotalElements) {
        // Assert.notNull(whereSql, "not null.");
        if (whereSql == null) {
            throw new IllegalArgumentException("not null.");
        }
        StringBuilder sql = new StringBuilder();
        StringBuilder totalElementsSql = new StringBuilder();

        String tableName = table.getTableDesc().getTableName();
        String primaryName = table.getTableDesc().getTypePkName();

        sql.append("SELECT * FROM ").append(tableName);

		if (whereSql.length() > 0 && whereSql.trim().length() > 0) {
			// 只有ORDER，直接拼接
			if (whereSql.trim().indexOf("ORDER") == 0) {
				sql.append(" ").append(whereSql.trim().replaceAll("WHERE", ""));
			} else {
				sql.append(" WHERE ").append(whereSql.trim().replaceAll("WHERE", ""));
			}
		}
           

        List<Object> filterParams = new ArrayList<Object>();
        for (Object p : params) {
            if (p instanceof String) {
                filterParams.add(((String) p).replaceAll("\\?", "\\%"));
            } else {
                filterParams.add(p);
            }
        }
        SqlContext returnObj = new SqlContext(sql, primaryName, filterParams);

        if (isNeedTotalElements) {
            totalElementsSql.append("SELECT COUNT(*) FROM ").append(tableName);
            if (whereSql.length() > 0 && whereSql.trim().length() > 0 ) {
    			// 只有ORDER，直接拼接
    			if (whereSql.trim().indexOf("ORDER") == 0) {
    				totalElementsSql.append(" ").append(whereSql.trim().replaceAll("WHERE", ""));
    			} else {
    				totalElementsSql.append(" WHERE ").append(whereSql.replaceAll("WHERE", ""));
    			}
               
            }
            returnObj.setTotalElementsSql(totalElementsSql);
        }

        return returnObj;
    }

    /**
     * 构建查询条件
     *
     * @param table
     * @param operations
     * @param isNeedTotalElements
     * @return
     */
    private SqlContext buildQueryOperation(CrudTableDesc table, List<? extends OperationBean> operations, Boolean isNeedTotalElements) {
        //Assert.notNull(operations, "not null.");
        if (operations == null) {
            throw new IllegalArgumentException("not null.");
        }
//        StringBuilder sql = new StringBuilder();
        StringBuilder where = new StringBuilder(" ");
        StringBuilder group = new StringBuilder();
        StringBuilder order = new StringBuilder();

        List<Object> params = new ArrayList<Object>();
//        String tableName = table.getTableName();
//        String primaryName = table.getPrimaryName();
        //String[] columnNames = table.getColumnName();
        //Object[] columnValues = table.getColumnValue();

        OperationBean condition = null;
        boolean isAddOrderBy = false;
        boolean isAddGroupBy = false;

        List<OperationBean> whereOperations = new ArrayList<OperationBean>();
        List<OperationBean> otherOperations = new ArrayList<OperationBean>();
        for (OperationBean oper : operations) {
            if (Operator.isConditionOper(oper.getOperator().toUpperCase())) {
                whereOperations.add(oper);
            } else {
                otherOperations.add(oper);
            }
        }

        for (int i = 0; i < otherOperations.size(); i++) {
            condition = otherOperations.get(i);

            String strAttribute = condition.getAttribute();
            String strOperator = condition.getOperator();
            Object objValue = condition.getValue();

            if ("ORDER".equalsIgnoreCase(strOperator)) {
                if (isAddOrderBy) {
                    order.append(",");
                } else {
                    order.append(" ORDER BY ");
                    isAddOrderBy = true;
                }
                order.append(strAttribute);
                if ("ASC".equalsIgnoreCase(String.valueOf(objValue))) {
                    order.append(" ASC ");
                } else {
                    order.append(" DESC ");
                }

            } else if ("GROUP".equalsIgnoreCase(strOperator)) {
                if (isAddGroupBy) {
                    group.append(",");
                } else {
                    group.append(" GROUP BY ");
                    isAddGroupBy = true;
                }
                group.append(strAttribute);
            }
        }

        for (int i = 0; i < whereOperations.size(); i++) {
            condition = whereOperations.get(i);
            String strLogicalOperator = condition.getLogicalOperator();
            if (i == 0) {
                where.append(" WHERE ");
                strLogicalOperator = "";
            }
            String strLeftBracket = condition.getLeftBracket();
            String strRightBracket = condition.getRightBracket();
            String strAttribute = condition.getAttribute();
            String strOperator = condition.getOperator();
            Object objValue = condition.getValue();

            if ("IN".equalsIgnoreCase(strOperator)) {
                where.append(" ");
                where.append(strLogicalOperator);
                where.append(" ");
                where.append(strLeftBracket);
                where.append(" ");
                where.append(strAttribute);
                where.append(" ");
                where.append(strOperator);
                where.append(" ");
				if (String.valueOf(objValue).indexOf('(') == -1) {
					objValue = '(' + String.valueOf(objValue);
				}
				if (String.valueOf(objValue).indexOf(')') == -1) {
					objValue = String.valueOf(objValue) + ')';
				}
                where.append(objValue);
                where.append(strRightBracket);
            } else if ("INSTR".equalsIgnoreCase(strOperator)) {
                where.append(" ");
                where.append(strLogicalOperator);
                where.append(" ");
                where.append(strLeftBracket);
                where.append(" ");
                where.append(strAttribute);
                where.append(" LIKE ");
                where.append("'%");
                where.append(objValue);
                where.append("%'");
                where.append(" ");
                where.append(strRightBracket);
            } else if ("ISNULL".equalsIgnoreCase(strOperator)) {
                where.append(" ");
                where.append(strLogicalOperator);
                where.append(" ");
                where.append(strLeftBracket);
                where.append(" ");
                where.append(strAttribute);
                where.append(" IS ");
                where.append(" NULL ");
                where.append(" ");
                where.append(strRightBracket);
            } else if ("EMPTY".equalsIgnoreCase(strOperator)) {
                where.append(" ");
                where.append(strLogicalOperator);
                where.append(" ");
                where.append(strLeftBracket);
                where.append(" (");
                where.append(strAttribute);
                where.append(" IS ");
                where.append(" NULL ");
                where.append("  or ");
                where.append(strAttribute);
                where.append(" ='' )");
                where.append(strRightBracket);
            } else {
                if ("LIKE".equalsIgnoreCase(strOperator)) {
                    String strTemp = (String) objValue;
                    if ((strTemp == null) || ("".equals(strTemp))) {
                        objValue = "%";
                    } else if (strTemp.indexOf('%') == -1) {
                        objValue = "%" + strTemp + "%";
                    }
                }
                if (objValue == null) {
                    where.append(" ");
                    where.append(strLogicalOperator);
                    where.append(" ");
                    where.append(strLeftBracket);
                    where.append(" ");
                    where.append(strAttribute);
                    where.append(" ");
                    where.append(strOperator);
                    where.append(" ");
                    where.append("NULL");
                    where.append(strRightBracket);
                } else {
                    where.append(" ");
                    where.append(strLogicalOperator);
                    where.append(" ");
                    where.append(strLeftBracket);
                    where.append(" ");
                    where.append(strAttribute);
                    where.append(" ");
                    where.append(strOperator);
                    where.append(" ");
                    where.append("?");
                    where.append(" ");
                    where.append(strRightBracket);
                    params.add(objValue);
                }
            }
        }

        return buildQuerySQL(table, where.append(group).append(order).toString(), params, isNeedTotalElements);
    }

    private Object getIgnoreCaseKey(Map<String, Object> m, String key) {
        if (null != m) {
            for (Iterator<Entry<String, Object>> iterator = m.entrySet().iterator(); iterator.hasNext(); ) {
                Entry<String, Object> e = iterator.next();

                String columnName = e.getKey();
                Object value = e.getValue();
                if (value == null) {
                    continue;
                }
                // 主键处理
                if (key.equalsIgnoreCase(columnName)) {
                    return value;
                }
            }
        }
        return null;
    }

}
