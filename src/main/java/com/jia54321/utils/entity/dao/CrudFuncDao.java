package com.jia54321.utils.entity.dao;

import com.jia54321.utils.entity.jdbc.IEntityTemplate;

import java.util.List;

public class CrudFuncDao {

	/**
	 * IEntityTemplate
	 */
	protected IEntityTemplate template;

	public CrudFuncDao(IEntityTemplate template) {
		this.template = template;
	}

    
	public void callFunc(final String funcName) {
		this.template.callFunc(funcName);
	}

	public String callFuncReturnObject(final String funcName, final Object... in) {
		return this.template.callFuncReturnObject(funcName, in);
	}

	public List<Object> callFuncReturnList(final String funcName, final Object... in){
		return this.template.callFuncReturnList(funcName, in);
	}
	
}
