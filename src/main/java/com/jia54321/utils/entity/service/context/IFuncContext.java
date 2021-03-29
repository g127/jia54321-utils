package com.jia54321.utils.entity.service.context;

/**
 * 函数上下文
 * @author G
 */
public interface IFuncContext {
	/**
	 * 函数调用
	 * @param funcName
	 * @param in
	 * @return
	 */
	String callFuncReturnObject(final String funcName, final Object... in);
}
