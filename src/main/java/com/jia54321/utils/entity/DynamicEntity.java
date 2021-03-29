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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.jia54321.utils.CamelNameUtil;

/**
 * 动态实体对象
 * @author 郭罡
 * @create
 * @description 2009-7-31 郭罡 新建
 */
public class DynamicEntity extends EntityType implements IDynamicEntity {
	/**  */
	private static final long serialVersionUID = 1L;

	public Map<String, Object> getItems() {
		return items;
	}

	public void setItems(Map<String, Object> items) {
		this.items = items;
		if (null != items) {
			for (Map.Entry<String, Object> e : items.entrySet()) {
				javaBeanItems.put(CamelNameUtil.underlineToCamelLowerCase(e.getKey()), e.getValue());
			}
		}
	}

	public Map<String, Object> getJavaBeanItems() {
		return javaBeanItems;
	}

	@Override
	public void set(String propertyName, Object value) {
		if (items == null) {
			items = new HashMap<String, Object>();
		}
		javaBeanItems.put(CamelNameUtil.underlineToCamelLowerCase(propertyName), value);
		items.put(CamelNameUtil.camelToUnderlineUpperCase(propertyName), value);
	}

	@Override
	public Object get(String propertyName) {
		return items.get(CamelNameUtil.camelToUnderlineUpperCase(propertyName));
	}

	@Override
	public Object remove(String propertyName) {
		Object o = null;
		String p1 = CamelNameUtil.underlineToCamelLowerCase(propertyName);
		if (javaBeanItems.containsKey(p1)) {
			javaBeanItems.remove(p1);
		}

		String p2 = CamelNameUtil.camelToUnderlineUpperCase(propertyName);
		if (items.containsKey(p2)) {
			o = items.remove(p2);
		}
		return o;
	}

	@Override
	public void clear() {
		javaBeanItems.clear();
		items.clear();
	}

	@Override
	public Iterator<Entry<String, Object>> iteratorItems() {
		return items.entrySet().iterator();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DynamicDx [typeId=").append(getTableDesc().getTypeId()).append(", items=").append(items).append("]");
		return builder.toString();
	}
}
