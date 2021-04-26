package com.jia54321.utils.entity.dao;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jia54321.utils.Assert;
import com.jia54321.utils.entity.DynamicEntity;
import com.jia54321.utils.entity.IDynamicEntity;
import com.jia54321.utils.entity.IEntityType;
import com.jia54321.utils.entity.converter.CrudTableConverter;
import com.jia54321.utils.entity.jdbc.IEntityTemplate;
import com.jia54321.utils.entity.query.CrudTableDesc;
import com.jia54321.utils.entity.query.OperationBean;
import com.jia54321.utils.entity.query.QueryContent;
import com.jia54321.utils.entity.query.SqlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通用增删改查Dao
 *
 * @author gg
 * @date 2019-08-25
 */
public abstract class CrudDao<T extends IEntityType> {

    private static Logger log = LoggerFactory.getLogger(CrudDao.class);

    /**
     * IEntityTemplate
     */
    protected IEntityTemplate template;

    public CrudDao(IEntityTemplate template) {
        this.template = template;
    }

    /**
     * 创建一个 IEntityType 主键值为 Id 的对象
     * @param typeId 类型ID值
     * @param id     主键ID值
     * @return IEntityType
     */
    public IEntityType create(String typeId, String id) {
        // Assert.notNull(typeId, "无法创建类型不存在的数据，具体原因：Entity.typeId must not be empty");
        return template.create(typeId, id);
    }

    /**
     * 插入一条记录
     *
     * @param entity
     */
    public String insert(T entity) {
        String typeId = entity.getTypeId();
        entity.setDefinedEntityType(template.create(entity.getTypeId(), null));
        final CrudTableDesc table = new CrudTableConverter().convert(entity);
        //
        final SqlContext sqlContext = template.getCrudSqlBuilder().buildInsertSql(table);
        //Oracle 不支持 GeneratedKeyHolder，故下面注释
        //KeyHolder keyHolder = new GeneratedKeyHolder();

        template.insert(typeId, sqlContext);

        String primaryValue = "";
        //if(null == keyHolder.getKey()){
        primaryValue = String.valueOf(table.getPrimaryValue());
        //}else{
        //	primaryValue = String.valueOf(keyHolder.getKey().longValue());
        //}
        return primaryValue;
    }
//


    /**
     * 更新记录
     *
     * @param entity entity
     */
    public Integer update(T entity) {
        String typeId = entity.getTypeId();
        entity.setDefinedEntityType(template.create(entity.getTypeId(), null));
        final CrudTableDesc table = new CrudTableConverter().convert(entity);
        //
        SqlContext sqlContext = template.getCrudSqlBuilder().buildUpdateSql(table);
        return Integer.valueOf(template.update(entity.getTypeId(), sqlContext));
    }


    /**
     * 删除记录
     *
     * @param ids
     */
    public Integer[] delete(String typeId, String ids) {
        final List<String> lstIds = Arrays.asList(ids.split(","));

        final CrudTableDesc table = new CrudTableConverter().convert(template.create(typeId, ids));

        SqlContext sqlContext = template.getCrudSqlBuilder().buildDeleteSql(table);
        if (lstIds.size() == 1) {
            Integer result = template.update(typeId, sqlContext);
            return new Integer[]{result};
        } else if (lstIds.size() > 1) {
            sqlContext.setParams(lstIds);
            int[] result = template.batchUpdate(typeId, sqlContext);
            Integer[] integerArray = new Integer[result.length];
            for (int j = 0; j < result.length; j++) {
                integerArray[j] = Integer.valueOf(result[j]);
            }
            return integerArray;
        }
        return new Integer[]{Integer.valueOf(0)};
    }

    /**
     * 删除所有记录
     */
    public void deleteAll(String typeId) {
        final CrudTableDesc table = new CrudTableConverter().convert( template.create(typeId, null) );
        String sql = " TRUNCATE TABLE " + table.getTableDesc().getTableName();
        template.executeSql(sql);
    }


    /**
     * 获得编号为typeId,主键为id的某条记录
     *
     * @param typeId 编号
     * @param id     主键
     * @return T
     */
    public T getDynamicEntity(final String typeId, final String id) {
        IDynamicEntity result = null;
        if (log.isDebugEnabled()) {
            log.debug(String.format("[typeId='%s', id='%s']", typeId, id));
        }
        final IEntityType obj = template.create(typeId, id);

        if (null != obj) {
            final CrudTableDesc table = new CrudTableConverter().convert(obj);
            SqlContext sqlContext = template.getCrudSqlBuilder().buildGetSql(table);

            if (log.isDebugEnabled()) {
                log.debug(String.format("[sql=%s]", String.valueOf(sqlContext)));
            }

            List<IDynamicEntity> list = template.query(typeId, sqlContext, null);

            if (null == list || list.size() != 1) {
                //日志记录
                String logMsg = String.format("未找到实体 ,[编号:%s,表:%s,主键:%s,sql上下文:%s]",
                        typeId, table.getTableDesc().getTableName(), id, String.valueOf(sqlContext));

                if (log.isErrorEnabled()) {
                    log.error(logMsg);
                }
                throw new RuntimeException(logMsg);
            }
            result = list.get(0);
        } else {
            log.error(String.format("Source Entity[%s] must not be null", typeId));
        }
        return (T) result;
    }


    /**
     * 得到记录
     *
     * @param typeId       类型
     * @param queryContent 查询实体
     * @return QueryContent
     */
    public QueryContent<DynamicEntity> query(final String typeId, final QueryContent queryContent) {
        QueryContent qc = queryContent;
        if (null == qc) {
            qc = new QueryContent();
        }
        final IEntityType obj = template.create(typeId, null);
        if (null != obj) {
            final CrudTableDesc table = new CrudTableConverter().convert(obj);

            SqlContext sqlContext = null;
            if (null != qc.getIds() && !"".equals(qc.getIds())) {
                String srcIds = qc.getIds();
                List<String> srcLstIds = Lists.newArrayList(Splitter.on(',').split(srcIds));
                List<String> distLstIds = Lists.newArrayList();
                for (String id : srcLstIds) {
                    if (!id.matches("'[0-9A-Za-z]+'")) {
                        id = id.replaceAll("([0-9A-Za-z]+)", "'$1'");
                    }
                    distLstIds.add(id);
                }
                String distIds = Joiner.on(',').join(distLstIds);
                sqlContext = template.getCrudSqlBuilder().buildQueryCondition(table, Lists
                                .newArrayList(OperationBean.conditionAnd(null,
                                        "IN_" + obj.getTableDesc().getTypePkName(),
                                        String.format("(%s)", distIds))),
                        qc.getSorts());
            } else if (null != qc.getW() && !"".equals(qc.getW())) {
                sqlContext = template.getCrudSqlBuilder().buildQuerySQL(table, qc.getW(), qc.getP(), true);
            } else {
                sqlContext = template.getCrudSqlBuilder().buildQueryCondition(table, qc.getConditions(), qc.getSorts());
            }

            qc.setResult(template.query(typeId, sqlContext, qc.getPage()));

            Long totalElements = template.count(typeId, sqlContext);

            qc.getPage().setTotalElements(totalElements);
            qc.getPage().setTotalPages((totalElements / qc.getPage().getPageSize() + 1));
        } else {
            log.error(String.format("Source Entity[%s] must not be null", typeId));
        }

        if (null == qc.getResult()) {
            qc.setResult(new ArrayList<DynamicEntity>(0));
        }
        return qc;
    }

    /**
     * 得到记录
     *
     * @param queryContents 查询实体
     * @return queryContents
     */
    public List<QueryContent<DynamicEntity>> multiQuery(final List<QueryContent<DynamicEntity>> queryContents) {
        List<QueryContent<DynamicEntity>> rs = Lists.newArrayList();
        for (QueryContent<DynamicEntity> queryContent : queryContents) {
            rs.add(query(queryContent.getTypeId(), queryContent));
        }
        return rs;
    }

}
