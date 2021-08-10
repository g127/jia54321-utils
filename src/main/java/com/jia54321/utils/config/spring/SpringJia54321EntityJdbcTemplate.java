package com.jia54321.utils.config.spring;

import com.google.common.base.Joiner;
import com.jia54321.utils.Assert;
import com.jia54321.utils.ClassUtils;
import com.jia54321.utils.JsonHelper;
import com.jia54321.utils.entity.*;
import com.jia54321.utils.entity.dao.CrudDaoConst;
import com.jia54321.utils.entity.dao.CrudDdlDaoConst;
import com.jia54321.utils.entity.jdbc.IEntityTemplate;
import com.jia54321.utils.entity.query.*;
import com.jia54321.utils.entity.rowmapper.AbstractRowMapper;
import com.jia54321.utils.entity.rowmapper.DynamicEntityRowMapper;
import com.jia54321.utils.entity.rowmapper.MetaItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jia54321.utils.IOUtil.closeQuietly;
import static com.jia54321.utils.entity.dao.EntityTypeDaoConst.*;

/**
 * JdbcTemplate 实现 IEntityTemplate
 */
public class SpringJia54321EntityJdbcTemplate extends JdbcTemplate implements IEntityTemplate {

    private static Logger log = LoggerFactory.getLogger(SpringJia54321EntityJdbcTemplate.class);

    private Database database = null;
    /** crudSqlBuilder */
    private SqlBuilder crudSqlBuilder;
    /** sqlPageBuilder */
    private SqlPageBuilder sqlPageBuilder;
//    /** tableDesc */
//    private ITableDesc tableDesc;

    protected AbstractRowMapper<ITableDesc> tableDescMapper  = new AbstractRowMapper<ITableDesc>() {
    };


    protected MetaItemMapper metaItemMapper  = new MetaItemMapper();

    public SpringJia54321EntityJdbcTemplate(DataSource dataSource) {
        super(dataSource, true);
        this.database = Database.fromDataSource(dataSource);
        this.crudSqlBuilder = new SqlBuilder();
        this.sqlPageBuilder = new SqlPageBuilder(dataSource);
    }

    @Override
    public IEntityType create(String typeId, String id) {
        Assert.notNull(typeId, "无法创建类型不存在的数据，具体原因：Entity.typeId must not be empty");

        IEntityType t = getEntityTypeAndItems(typeId);

        if(id != null) {
            //TODO Fix
            String setMethodName = "set" + Character.toUpperCase(t.getTableDesc().getTypePkName().charAt(0)) + t.getTableDesc().getTypePkName().substring(1);
            Boolean isBeanAndExistPropertyPkName = (null != ClassUtils.getMethodIfAvailable(t, setMethodName));
            if (isBeanAndExistPropertyPkName) {
                ClassUtils.invokeBeanWriteMethodIfAvailable(t, t.getTableDesc().getTypePkName(), id);
            } else if (null != ClassUtils.getMethodIfAvailable(t, "set")) {
                ClassUtils.invokeMethodIfAvailable(t, "set", t.getTableDesc().getTypePkName(), id);
            }
        }

        return t;
    }


    /**
     * @param typeId
     * @return
     */
    @Override
    public IEntityType getEntityType(final String typeId) {
        IEntityType sourceEntity = new EntityType();

        ITableDesc tableDesc = CrudTableDesc.CACHE.getIfPresent(typeId);
        if( tableDesc == null) {
            tableDesc = getITableDesc(typeId);

            if (null == tableDesc) {
                if (log.isInfoEnabled()) {
                    log.info(String.format("未找到sys_entity_type中的实体定义 typeId='%s'", typeId));
                }
                return null;
            }

            CrudTableDesc.CACHE.put(typeId, tableDesc);
        }

        sourceEntity.setTableDesc(tableDesc);

        return sourceEntity;
    }


    @Override
    public ITableDesc getITableDesc(final String typeId) {
//        String sql = ENTITY_TYPE_SELECT_FROM;
        String sql = ENTITY_TYPE_SELECT_FROM + ENTITY_TYPE_WHERE_BY_TYPEID;
        List<ITableDesc> types = this.query(sql, new Object[]{typeId}, new RowMapper<ITableDesc>() {
            @Override
            public ITableDesc mapRow(ResultSet rs, int rowNum) throws SQLException {
                return tableDescMapper.mapRow(rs, rowNum);
            }
        });
        if (null == types ) {
            types = new ArrayList<>();
        }
        return types.stream().filter( t -> t.getTypeId().equals(typeId)).findAny().orElse(null);
    }

    /**
     * 获取实体的属性名称集合
     * @param typeId 类型ID值
     * @return List<MetaItem>
     */
    @Override
    public List<MetaItem> getEntityItems(final String typeId) {
        List<MetaItem> metaItems = null;

        IEntityType sourceEntity = getEntityType(typeId);

        Assert.notNull(sourceEntity, "Source Entity must not be null");

        String tabName = sourceEntity.getTableDesc().getTableName();
        String showTabSql = sqlPageBuilder.getShowTabSql(TAB_ENTITY_ITEM);

        List<Map<String, Object>> tabs = this.queryForList(showTabSql);
        if (null != tabs && tabs.size() > 0) {
            String sql = ENTITY_ITEM_SELECT_FROM + ENTITY_ITEM_WHERE_BY_TYPEID;
            metaItems = this.query(sql, new Object[]{typeId}, new RowMapper<MetaItem>() {
                @Override
                public MetaItem mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return metaItemMapper.mapRow(rs, rowNum);
                }
            });
        } else {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Table '%s' doesn't exist. [%s]", TAB_ENTITY_ITEM,
                        showTabSql));
            }
        }

        // 如果没有找到，直接根据表查询
        if (null == metaItems || metaItems.size() == 0) {
            metaItems = getEntityItemsByTabName(typeId, tabName);
        }

        return metaItems;
    }

    @Override
    public IEntityType getEntityTypeAndItems(final String typeId) {
        IEntityType entityType = getEntityType(typeId);
        List<MetaItem> metaItems = getEntityItems(typeId);
        if (null != metaItems) {
            for (MetaItem metaItem : metaItems) {
                entityType.setMetaItem(metaItem.getItemColName(), metaItem);
            }
        }
        return entityType;
    }
    /**
     * 获取表字段，直接根据表查询
     *
     * @param typeId
     * @param tabName
     * @return
     */
    @Override
    public List<MetaItem> getEntityItemsByTabName(final String typeId, final String tabName) {
        //避免循环引用
//		return crudDdlDao.queryMetaItems(typeId, tabName);

        List<MetaItem> metaItems = new ArrayList<MetaItem>();

        String showTabSql = String.format(SHOW_TAB_MYSQL, tabName);
        if (this.database == Database.oracle) {
            showTabSql = String.format(SHOW_TAB_ORACLE, tabName);
        }

        // 判断表名是否存在
        List<Map<String, Object>> tabs = this.queryForList(showTabSql);
        // 存在
        if (null != tabs && tabs.size() > 0) {

            //存在表名查询字段
            String showTabItemsSql = String.format(CrudDdlDaoConst.QUERY_DESC_MYSQL, tabName);
            if (this.database == Database.oracle) {
                showTabItemsSql = String.format(CrudDdlDaoConst.QUERY_DESC_ORACLE, tabName);
            }

            metaItems = this.query(showTabItemsSql, new RowMapper<MetaItem>() {
                @Override
                public MetaItem mapRow(ResultSet rs, int rowNum) throws SQLException {
                    MetaItem item = metaItemMapper.mapRow(rs, rowNum);
                    item.setTypeId(typeId);
                    return item;
                }
            });

            return metaItems;

        } else {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Table '%s' doesn't exist. [%s]", tabName, showTabSql));
            }
        }

        return metaItems;
    }

    @Override
    public SqlBuilder getCrudSqlBuilder() {
        return crudSqlBuilder;
    }

    @Override
    public SqlPageBuilder getSqlPageBuilder() {
        return sqlPageBuilder;
    }

    @Override
    public void insert(final String typeId, final SqlContext sqlContext) {
        update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

                PreparedStatement ps = con.prepareStatement(sqlContext.getSql().toString(),
                        new String[]{ sqlContext.getPrimaryKey() });

                int index = 0;

                for (Object param : sqlContext.getParams()) {
                    index++;
                    if (param instanceof java.util.Date) {
                        ps.setObject(index, new Timestamp(((java.util.Date) param).getTime()), Types.TIMESTAMP);
                    } else if (param instanceof InputStream) { // Blob
                        ps.setBlob(index, (InputStream) param);
                        closeQuietly((InputStream) param);
                    } else {  // include Mysql Text DataType
                        ps.setObject(index, param);
                    }
                }
                return ps;
            }
//        }, keyHolder);
        });
    }

    @Override
    public int update(final String typeId, final SqlContext sqlContext) {
        return update(sqlContext.getSql().toString(), sqlContext.getParams().toArray());
    }

    @Override
    public int[] batchUpdate(final String typeId, final SqlContext sqlContext) {
        int[] result = batchUpdate(sqlContext.getSql().toString(), new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return sqlContext.getParams().size();
            }

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, sqlContext.getParams().get(i));
            }
        });
        return result;
    }

    @Override
    public List<IDynamicEntity> query(final String typeId, final SqlContext sqlContext, final Page page) {
        //
        String sql = sqlContext.getSql().toString();
        if(page != null) {
            sql = getSqlPageBuilder().buildPageSql(sqlContext.getSql().toString(), page);
        }
        //

        final DynamicEntityRowMapper dynamicEntityRowMapper = new DynamicEntityRowMapper() {
            @Override
            public IDynamicEntity newInstance() {
                IDynamicEntity dynamicEntity = new DynamicEntity();
                dynamicEntity.setDefinedEntityType(create(typeId, null));
                return dynamicEntity;
            }
        };

        List<IDynamicEntity> list = query(sql, sqlContext.getParams().toArray(), new RowMapper<IDynamicEntity>() {
            @Override
            public IDynamicEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                return dynamicEntityRowMapper.mapRow(rs, rowNum);
            }
        });
        return list;
    }


    @Override
    public Long count(final String typeId, final SqlContext sqlContext) {
        String sqlTotal = sqlContext.getCountSql().toString();
        Long totalElements = queryForObject(sqlTotal, sqlContext.getParams().toArray(), Long.class);
        return totalElements;
    }

    @Override
    public void executeSql(final String sql) {
        execute(sql);
    }

    @Override
    public void callFunc(final String funcName) {
        execute(String.format(CrudDaoConst.CALL_FUNC, funcName, ""));
    }

    @Override
    public String callFuncReturnObject(final String funcName, final Object... in) {
        String[] inPlaceholders = new String[]{};
        if (null != in) {
            for (int i = 0; i < in.length; i++) {
                inPlaceholders = JsonHelper.addStringToArray(inPlaceholders, "?");
            }
        }
        inPlaceholders = JsonHelper.addStringToArray(inPlaceholders, "?");

        final String placeholdersParams = Joiner.on(',').join(inPlaceholders);
        final int lastParameterIndex = inPlaceholders.length;

        final String storedProc = String.format(CrudDaoConst.CALL_FUNC, funcName, placeholdersParams);// 调用的sql

        return execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con)
                    throws SQLException {
                CallableStatement cs = con.prepareCall(storedProc);
                if (null != in) {
                    for (int i = 0; i < in.length; i++) {
                        cs.setObject(i + 1, in[i]);
                    }
                }

                // 注册输出参数的类型
                cs.registerOutParameter(lastParameterIndex, 12);// OracleTypes.VARCHAR注册输出参数的类型

                return cs;
            }
        }, new CallableStatementCallback<String>() {
            @Override
            public String doInCallableStatement(CallableStatement cs)
                    throws SQLException, DataAccessException {
                cs.execute();
                return cs.getString(lastParameterIndex);// 获取输出参数的值
            }
        });
    }

    public List callFuncReturnList(final String funcName, final Object[] in){
        return null;
    }
}
