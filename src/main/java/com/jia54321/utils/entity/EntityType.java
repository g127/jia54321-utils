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

import com.jia54321.utils.CamelNameUtil;
import com.jia54321.utils.entity.query.CrudTableDesc;
import com.jia54321.utils.entity.query.ITableDesc;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

/**
 * EntityType
 * @author 郭罡
 * @description 2009-7-31 郭罡 新建
 */
public class EntityType implements IEntityType {
	/**  */
	private static final long       serialVersionUID = 1L;
	/**类型实体描述  */
	protected ITableDesc tableDesc;
//
//	/** 类型 拥有者 */
//	private String ownerId;
//	/** 类型 拥有者 名称 */
//	private String ownerDisplayName;
	
	/**  元数据定义.  */
	protected Map<String, MetaItem> metaItems = new HashMap<String, MetaItem>();

	/** 动态实体. */
	protected Map<String, Object> items = new HashMap<String, Object>();

	/** 动态实体. */
	protected Map<String, Object> javaBeanItems = new HashMap<String, Object>();

	public ITableDesc getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(ITableDesc tableDesc) {
		this.tableDesc = tableDesc;
	}

	public String getTypeId() {
		return tableDesc.getTypeId();
	}

	public void setTypeId(String typeId) {
		try {
			ITableDesc desc = CrudTableDesc.CACHE.get(typeId);
			setTableDesc(desc);
		} catch (ExecutionException e) {
			// e.printStackTrace();
		}
		// setTableDesc(CrudTableDesc.CACHE.getIfPresent(typeId));
		this.tableDesc.setTypeId(typeId);
	}

	@Override
	public Collection<MetaItem> getMetaItems(){
		if (metaItems == null) {
			metaItems = new HashMap<String, MetaItem>();
		}
		return metaItems.values();
	}
	
	@Override
	public void setMetaItem(String itemName, MetaItem value) {
		if (metaItems == null) {
			metaItems = new HashMap<String, MetaItem>();
		}
		metaItems.put(itemName.toUpperCase(), value);
	}

	@Override
	public MetaItem getMetaItem(String itemName) {
		return metaItems.get(itemName.toUpperCase());
	}

	@Override
	public Iterator<Entry<String, MetaItem>> iteratorMetaItems() {
		return metaItems.entrySet().iterator();
	}

	@Override
	public Map<String, Object> getItems() {
		return items;
	}

	@Override
	public void setItems(Map<String, Object> items) {
		this.items = items;
		if (null != items) {
			for (Map.Entry<String, Object> e : items.entrySet()) {
				javaBeanItems.put(CamelNameUtil.underlineToCamelLowerCase(e.getKey()), e.getValue());
			}
		}
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
	public void setDefinedEntityType(IEntityType entityType) {
		this.setTableDesc(entityType.getTableDesc());
		this.getMetaItems().clear();
		for (MetaItem metaItem: entityType.getMetaItems()) {
			this.metaItems.put(metaItem.getItemColName(), metaItem);
		}
	}


	@Override
	public String toString() {
		return new StringJoiner(", ", EntityType.class.getSimpleName() + "[", "]")
				.add("tableDesc=" + tableDesc)
				.add("metaItems=" + metaItems)
				.toString();
	}
}
