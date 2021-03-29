package com.jia54321.utils.entity.impl;


import com.jia54321.utils.entity.dao.CrudFuncDao;
import com.jia54321.utils.entity.service.context.IFuncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gg
 * @date 2019-08-25
 */
public class FuncContext implements IFuncContext {

	private static Logger log = LoggerFactory.getLogger(FuncContext.class);

	private CrudFuncDao funcDao;

	public FuncContext(CrudFuncDao funcDao) {
		this.funcDao = funcDao;
	}

	@Override
	public String callFuncReturnObject(final String funcName,
			final Object... in) {
		return funcDao.callFuncReturnObject(funcName, in);
	}
}
