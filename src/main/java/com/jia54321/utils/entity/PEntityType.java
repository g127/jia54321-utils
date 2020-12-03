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
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * PEntityType
 * @author 郭罡
 * @description 2009-7-31 郭罡 新建
 */
public class PEntityType implements IEntityType {
	/**  */
	private static final long serialVersionUID = -2284547333974752074L;
	
	private IEntityType definedEntityType = new EntityType();

	/**
	 * @return the definedEntityType
	 */
	public IEntityType getDefinedEntityType() {
		return definedEntityType;
	}

	/**
	 * @param definedEntityType the definedEntityType to set
	 */
	public void setDefinedEntityType(IEntityType definedEntityType) {
		this.definedEntityType = definedEntityType;
	}

	@Override
	public String getTypeId() {
		return definedEntityType.getTypeId();
	}

	@Override
	public void setTypeId(String typeId) {
		definedEntityType.setTypeId(typeId);		
	}
	
	@Override
	public String getTypeAliasId() {
		return definedEntityType.getTypeAliasId();
	}

	@Override
	public void setTypeAliasId(String typeAliasId) {
		definedEntityType.setTypeAliasId(typeAliasId);;
	}

	@Override
	public String getTypeDisplayName() {
		return definedEntityType.getTypeDisplayName();
	}

	@Override
	public void setTypeDisplayName(String typeDisplayName) {
	    definedEntityType.setTypeDisplayName(typeDisplayName);
	}

	@Override
	public String getTypeMk() {
		return definedEntityType.getTypeMk();
	}

	@Override
	public void setTypeMk(String typeMk) {
		definedEntityType.setTypeMk(typeMk);
		
	}

	@Override
	public String getTypeEntityName() {
		return definedEntityType.getTypeEntityName();
	}

	@Override
	public void setTypeEntityName(String typeEntityName) {
		definedEntityType.setTypeEntityName(typeEntityName);		
	}

	@Override
	public String getTypePkName() {
		return definedEntityType.getTypePkName();
	}

	@Override
	public void setTypePkName(String typePkName) {
		definedEntityType.setTypePkName(typePkName);
	}
	
	@Override
	public String getTypePkDisplayName() {
		return definedEntityType.getTypePkDisplayName();
	}

	@Override
	public void setTypePkDisplayName(String typePkDisplayName) {
		definedEntityType.setTypePkDisplayName(typePkDisplayName);
	}
	
	@Override
	public String getOwnerId() {
		return definedEntityType.getOwnerId();
	}
	
	@Override
	public void setOwnerId(String ownerId) {
		definedEntityType.setOwnerId(ownerId);
	}
	@Override
	public String getOwnerDisplayName() {
		return definedEntityType.getOwnerDisplayName();
	}
	
	@Override
	public void setOwnerDisplayName(String ownerDisplayName) {
		definedEntityType.setOwnerDisplayName(ownerDisplayName);
	}
	
	@Override
	public Long getTypeOpts() {
		return definedEntityType.getTypeOpts();
	}
	
	@Override
	public void setTypeOpts(Long typeOpts) {
		definedEntityType.setTypeOpts(typeOpts);
	}

	@Override
	public Collection<MetaItem> getMetaItems(){
		return definedEntityType.getMetaItems();
	}
	
	@Override
	public void setMetaItem(String itemName, MetaItem value) {
		definedEntityType.setMetaItem(itemName, value);
		
	}

	@Override
	public MetaItem getMetaItem(String itemName) {
		return definedEntityType.getMetaItem(itemName);
	}

	@Override
	public Iterator<Entry<String, MetaItem>> iteratorMetaItems() {
		return definedEntityType.iteratorMetaItems();
	}
	
}
