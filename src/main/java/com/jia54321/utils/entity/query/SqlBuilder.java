package com.jia54321.utils.entity.query;

import java.util.*;
import java.util.Map.Entry;

import com.jia54321.utils.CamelNameUtil;
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
    public static final String WHERE = "WHERE";
    public static final String GROUP_BY = "GROUP BY";
    public static final String ORDER_BY = "ORDER BY";

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
        sql.append(' ').append(WHERE).append(' ').append(primaryName).append(" = ?");
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
        sql.append(' ').append(WHERE).append(' ').append(" 1=1 ");
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
        sql.append(' ').append(WHERE).append(' ').append(primaryName).append(" = ?");
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
        StringBuilder sqlExceptSelect = new StringBuilder();

        String tableName = table.getTableDesc().getTableName();
        String primaryName = table.getTableDesc().getTypePkName();

        sql.append("SELECT * FROM ").append(tableName);
        totalElementsSql.append("SELECT COUNT(*) FROM ").append(tableName);
        sqlExceptSelect.append("FROM ").append(tableName);

        List<Object> filterParams = new ArrayList<Object>();
        for (Object p : params) {
            if (p instanceof String) {
                filterParams.add(((String) p).replaceAll("\\?", "\\%"));
            } else {
                filterParams.add(p);
            }
        }

        if ( whereSql == null || whereSql.length() == 0 || whereSql.trim().length() == 0) {
            SqlContext returnObj = new SqlContext(sql, primaryName, filterParams);
            returnObj.setTableName(tableName);
            returnObj.setCountSql(totalElementsSql);
            returnObj.setSqlExceptSelect(sqlExceptSelect);
            return returnObj;
        }
        whereSql = whereSql.trim();

        // [where ,group, order]
        int idxGroup = whereSql.indexOf(GROUP_BY);
        int idxOrder = whereSql.indexOf(ORDER_BY);
        //
        String wSql = whereSql;
        // order
        final String order = wSql.substring( idxOrder >= 0 ? idxOrder: wSql.length() , wSql.length() );
        wSql = wSql.replaceAll(order, "");
        // group
        final String group = wSql.substring( idxGroup >= 0 ? idxGroup: wSql.length() , wSql.length() );
        wSql = wSql.replaceAll(group, "");
        // where
        final String where = wSql;

        //
        sql.append(' ').append(where).append(group).append(order);
        //
        totalElementsSql.append(' ').append(where).append(group).append(order);
        //
        sqlExceptSelect.append(' ').append(where).append(group).append(order);


        SqlContext returnObj = new SqlContext(sql, primaryName, filterParams);
        returnObj.setTableName(tableName);
        returnObj.setSqlWhereConditions(where.replaceAll(WHERE, " "));
        returnObj.setSqlGroupConditions(where.replaceAll(GROUP_BY, " "));
        returnObj.setSqlOrderConditions(where.replaceAll(ORDER_BY, " "));
        returnObj.setCountSql(totalElementsSql);
        returnObj.setSqlExceptSelect(sqlExceptSelect);

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
        StringBuilder where = new StringBuilder();
        StringBuilder conds = new StringBuilder();
        StringBuilder group = new StringBuilder();
        StringBuilder order = new StringBuilder();

        List<Object> params = new ArrayList<Object>();

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

            String strAttribute = CamelNameUtil.camelToUnderline(condition.getAttribute()); // 属性名转下划线
            String strOperator = condition.getOperator();
            Object objValue = condition.getValue();

            if ("ORDER".equalsIgnoreCase(strOperator)) {
                if (isAddOrderBy) {
                    order.append(",");
                } else {
                    order.append(' ').append(ORDER_BY).append(' ');
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
                    group.append(' ').append(GROUP_BY).append(' ');
                    isAddGroupBy = true;
                }
                group.append(strAttribute);
            }
        }

        for (int i = 0; i < whereOperations.size(); i++) {
            condition = whereOperations.get(i);
            if (i == 0) {
                condition.setLogicalOperator(" ");
            }
            buildOperationBean(conds, params, condition);
        }

        if(conds.length() > 0 ) {
            conds = new StringBuilder(conds.toString().trim()).insert(0, ' ' + WHERE + ' ');
        }

        return buildQuerySQL(table, where.append(conds).append(group).append(order).toString(), params, isNeedTotalElements);
    }

    /*
     * 构造单个操作符
     */
    private void buildOperationBean(StringBuilder conds, List<Object> params, OperationBean condition) {
        String strLogicalOperator = condition.getLogicalOperator();
//        if (i == 0) {
//            where.append(" WHERE ");
//            strLogicalOperator = "";
//        }
        String strLeftBracket = condition.getLeftBracket();
        String strRightBracket = condition.getRightBracket();
        String strAttribute = CamelNameUtil.camelToUnderline(condition.getAttribute()); // 属性名转下划线
        String strOperator = condition.getOperator();
        Object objValue = condition.getValue();

        if(!strLogicalOperator.equalsIgnoreCase(" ")) {
            strLogicalOperator = " " + strLogicalOperator + " ";
        }

        // 如果value为列表
        if(condition.getValue() instanceof List) {
            conds.append(strLogicalOperator);

            conds.append(Operator.LEFT_BRACKET.toString());
            List<OperationBean> subOperations = (List<OperationBean>)condition.getValue();
            for (int j = 0; j < subOperations.size(); j++) {
                if (j == 0) {
                    subOperations.get(0).setLogicalOperator(" ");
                }
                buildOperationBean(conds, params, subOperations.get(j));
            }
            conds.append(Operator.RIGHT_BRACKET.toString());

        }
        // 如果操作符为 IN， NOTIN
        else if (Operator.IN.toString().equalsIgnoreCase(strOperator) || Operator.NOTIN.toString().equalsIgnoreCase(strOperator)) {
            conds.append(strLogicalOperator);
            conds.append(strLeftBracket);
            conds.append(" ");
            conds.append(strAttribute);
            conds.append(" ");
            conds.append(strOperator);
            conds.append(" ");

            Object[] inObjValue = new Object[]{};
            // 数组
            if (objValue.getClass().isArray()) {
                inObjValue = (Object[]) objValue;
                // 集合
            } else if (objValue instanceof Collection) {
                inObjValue = (Object[]) ((Collection) objValue).toArray();
                // 字符串分隔的数据
            } else if (objValue instanceof String /*&& ((String) filter.value).indexOf(',') > 0 */) { // 单个In 参数无法查询
                inObjValue = (Object[]) (String.valueOf(objValue).split(","));
            }

            conds.append('(');
            for (Object inValue : inObjValue) {
                conds.append('?').append(',');
                params.add(inValue);
            }
            conds.deleteCharAt(conds.length()-1);
            conds.append(')');
            conds.append(strRightBracket);
        }
        // 如果操作符为 INSTR
        else if ("INSTR".equalsIgnoreCase(strOperator)) {
            conds.append(strLogicalOperator);
            conds.append(strLeftBracket);
            conds.append(" ");
            conds.append(strAttribute);
            conds.append(" LIKE ");
            conds.append("'%");
            conds.append(objValue);
            conds.append("%'");
            conds.append(" ");
            conds.append(strRightBracket);
        }
        // 如果操作符为 ISNULL
        else if ("ISNULL".equalsIgnoreCase(strOperator)) {
            conds.append(strLogicalOperator);
            conds.append(strLeftBracket);
            conds.append(" ");
            conds.append(strAttribute);
            conds.append(" IS ");
            conds.append(" NULL ");
            conds.append(" ");
            conds.append(strRightBracket);
        }
        // 如果操作符为 EMPTY
        else if ("EMPTY".equalsIgnoreCase(strOperator)) {
            conds.append(strLogicalOperator);
            conds.append(strLeftBracket);
            conds.append(" (");
            conds.append(strAttribute);
            conds.append(" IS ");
            conds.append(" NULL ");
            conds.append("  or ");
            conds.append(strAttribute);
            conds.append(" ='' )");
            conds.append(strRightBracket);
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
                conds.append(strLogicalOperator);
                conds.append(strLeftBracket);
                conds.append(" ");
                conds.append(strAttribute);
                conds.append(" ");
                conds.append(strOperator);
                conds.append(" ");
                conds.append("NULL");
                conds.append(strRightBracket);
            } else {
                conds.append(strLogicalOperator);
                conds.append(strLeftBracket);
                conds.append(" ");
                conds.append(strAttribute);
                conds.append(" ");
                conds.append(strOperator);
                conds.append(" ");
                conds.append("?");
                conds.append(" ");
                conds.append(strRightBracket);
                params.add(objValue);
            }
        }
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
