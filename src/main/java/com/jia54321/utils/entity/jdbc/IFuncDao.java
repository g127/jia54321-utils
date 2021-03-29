package com.jia54321.utils.entity.jdbc;

import com.jia54321.utils.entity.IEntityType;
import com.jia54321.utils.entity.MetaItem;
import com.jia54321.utils.entity.dao.CrudDaoConst;
import com.jia54321.utils.entity.query.ITableDesc;
import com.jia54321.utils.entity.query.SqlBuilder;
import com.jia54321.utils.entity.query.SqlPageBuilder;

import java.util.List;

/**
 * IFuncDao
 */
public interface IFuncDao {

    /**
     * callFunc
     * @param funcName
     */
    public void callFunc(final String funcName);

    /**
     * callFuncReturnObject
     * @param funcName
     * @param in
     * @return callFuncReturnObject
     */
    public String callFuncReturnObject(final String funcName, final Object... in);


    /**
     * callFuncReturnList
     * @param funcName
     * @param in
     * @return callFuncReturnObject
     */
    public List<Object> callFuncReturnList(final String funcName, final Object... in);

}
