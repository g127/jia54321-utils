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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * EntityType
 * @author 郭罡
 * @description 2009-7-31 郭罡 新建
 */
public class EntityType implements IEntityType {
	/**  */
	private static final long serialVersionUID = -3618164022281940398L;
	
	/** 类型ID */
	private String typeId;
	/** 类型别名ID */
	private String typeAliasId;
	
	
	/** 类型显示名称 */
	private String typeDisplayName;
	
	/** 类型模块 */
	private String typeMk;
	/** 类型实体名称 */
	private String typeEntityName;
	
	/** 类型主键 字段名 */
	private String typePkName;
	/** 类型主键 显示名称 字段名 */
	private String typePkDisplayName;
	
	/** 类型 拥有者 */
	private String ownerId;
	/** 类型 拥有者 名称 */
	private String ownerDisplayName;
	
	/** 类型 可选项 */
	private Long typeOpts;
	
	/**  元数据定义.  */
	private Map<String, MetaItem> metaItems = new HashMap<String, MetaItem>();

	@Override
	public String getTypeId() {
		return typeId;
	}

	@Override
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	@Override
	public String getTypeAliasId() {
		return typeAliasId;
	}

	@Override
	public void setTypeAliasId(String typeAliasId) {
		this.typeAliasId = typeAliasId;
	}

	@Override
	public String getTypeDisplayName() {
		return typeDisplayName;
	}

	@Override
	public void setTypeDisplayName(String typeDisplayName) {
		this.typeDisplayName = typeDisplayName;
	}

	@Override
	public String getTypeMk() {
		return typeMk;
	}

	@Override
	public void setTypeMk(String typeMk) {
		this.typeMk = typeMk;
	}

	@Override
	public String getTypeEntityName() {
		return typeEntityName;
	}

	@Override
	public void setTypeEntityName(String typeEntityName) {
		this.typeEntityName = typeEntityName;
	}

	@Override
	public String getTypePkName() {
		return typePkName;
	}

	@Override
	public void setTypePkName(String typePkName) {
		this.typePkName = typePkName;
	}

	@Override
	public String getTypePkDisplayName() {
		return typePkDisplayName;
	}

	@Override
	public void setTypePkDisplayName(String typePkDisplayName) {
		this.typePkDisplayName = typePkDisplayName;
	}

	@Override
	public String getOwnerId() {
		return ownerId;
	}

	@Override
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}

	@Override
	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
	}

	@Override
	public Long getTypeOpts() {
		return typeOpts;
	}

	@Override
	public void setTypeOpts(Long typeOpts) {
		this.typeOpts = typeOpts;
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EntityType [typeId=").append(typeId)
				.append(", typeDisplayName=").append(typeDisplayName)
				.append(", typeMk=").append(typeMk).append(", typeEntityName=")
				.append(typeEntityName).append(", typePkName=")
				.append(typePkName).append(", metaItems=").append(metaItems)
				.append("]");
		return builder.toString();
	}
	
}
