/**
 * MIT License
 * 
 * Copyright (c) 2009-present GuoGang and other contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.jia54321.utils.entity;

import java.util.Iterator;
import java.util.Map;

/**
 * 动态实体
 * 
 * @author 郭罡
 * @description 2009-7-31 郭罡 新建
 */
public interface IDynamicEntity extends IEntityType {
	/**
	 * 根据属性名称设置值.
	 * 
	 * @param itemColName
	 *            属性名称
	 * @param value
	 *            值
	 */
	void set(final String itemColName, Object value);

	/**
	 * 根据属性名称获取值.
	 * 
	 * @param itemColName
	 *            属性名称
	 * @return 值
	 */
	Object get(final String itemColName);
	
	/**
	 * 移出某个属性值
	 * @param propertyName
	 * @return Object
	 */
	Object remove(String propertyName);
	
	/**
	 * 清楚数据
	 */
	void clear();

	/**
	 * 动态属性迭代器.
	 * 
	 * @return 动态属性迭代器
	 */
	Iterator<Map.Entry<String, Object>> iteratorItems();
}
