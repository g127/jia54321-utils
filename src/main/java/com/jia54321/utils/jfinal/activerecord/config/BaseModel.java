package com.jia54321.utils.jfinal.activerecord.config;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jia54321.utils.JsonHelper;
import com.jia54321.utils.entity.query.*;

import java.util.Map;

public class BaseModel<M extends Model> extends Model<M> {

    SqlBuilder sqlBuilder = new SqlBuilder();
    /**
     * 查询分页列表
     *
     * @param and        查询条件and
     * @param or         查询条件or
     * @param sort       排序条件sort
     * @param pageNumber 页码
     * @param pageSize   页数
     * @return 实体列表
     */
    public QueryContent<M> _findPageList(CrudTableDesc crudTableDesc, Map<String, Object> and, Map<String, Object> or,
                Map<String, Object> sort,
                Integer pageNumber, Integer pageSize)
    {
        pageNumber = JsonHelper.toInt(pageNumber, 1);
        pageSize = JsonHelper.toInt(pageSize, 20);
        // 构造查询 QueryContent
        QueryContent qc = QueryContentFactory.createQueryContent(SimpleConditionFactory.createByAndWithOr(
                and, or, sort,pageNumber, pageSize));
        // 构造Sql上下文 querySqlContext
        SqlContext querySqlContext = sqlBuilder.buildQueryCondition(crudTableDesc, qc.getConditions(), qc.getSorts());

        String sqlExceptSelect = querySqlContext.getSqlExceptSelect().toString();
        Object[] paras = querySqlContext.getParams().toArray();
        // paginate
        Page page = paginate(qc.getPage().getPageNo(), qc.getPage().getPageSize(),"select *", sqlExceptSelect, paras);
        //
        qc.setResult(page.getList());
        qc.getPage().setTotalElements((long)page.getTotalRow());
        qc.getPage().setTotalPages((long)page.getTotalPage());

        qc.setConditions(null);
        qc.setKey(null);
        qc.setSorts(null);
        return qc;
    }
}
