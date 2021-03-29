package com.jia54321.utils.entity.jdbc;

import com.jia54321.utils.entity.IDynamicEntity;
import com.jia54321.utils.entity.query.Page;
import com.jia54321.utils.entity.query.SqlContext;

import java.util.List;

/**
 *
 */
public interface ISqlContextDao {
    /**
     * 新增sqlContext
     * @param typeId
     * @param sqlContext
     */
    public void insert(final String typeId, final SqlContext sqlContext);

    /**
     * 更新sqlContext
     * @param sqlContext
     * @return int
     */
    public int update(final String typeId, final SqlContext sqlContext);

    /**
     * 更新sqlContext
     * @param sqlContext
     * @return int
     */
    public int[] batchUpdate(final String typeId, final SqlContext sqlContext);

    /**
     * 查询 IDynamicEntity
     * @param typeId  类型Id
     * @param sqlContext sqlContext
     * @param page    分页对象
     * @return IDynamicEntity列表
     */
    public List<IDynamicEntity> query(final String typeId, final SqlContext sqlContext, final Page page);

    /**
     * 查询IDynamicEntity 数量
     * @param typeId  类型Id
     * @param sqlContext sqlContext
     * @return 数量
     */
    public Long count(final String typeId, final SqlContext sqlContext);


    /**
     * 执行某个特定的sql
     * @param sql sql
     * @return void
     */
    public void executeSql(final String sql);
}
