package com.jia54321.utils.entity.jdbc;

import com.jia54321.utils.entity.query.SqlBuilder;
import com.jia54321.utils.entity.query.SqlPageBuilder;

public interface ISqlBuilderDao {
    /**
     * crud的sql构造器
     * @return
     */
    public SqlBuilder getCrudSqlBuilder();

    /**
     * page的sql构造器
     * @return
     */
    public SqlPageBuilder getSqlPageBuilder();
}
