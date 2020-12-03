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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * IEntityType
 * @author 郭罡
 * @description 2009-7-31 郭罡 新建
 */
public interface IEntityType extends Serializable{

	public abstract String getTypeId();
	public abstract void setTypeId(String typeId);
	
	public abstract String getTypeAliasId();
	public abstract void setTypeAliasId(String typeAliasId);

	public abstract String getTypeDisplayName();
	public abstract void setTypeDisplayName(String typeDisplayName);

	
	public abstract String getTypeMk();
	public abstract void setTypeMk(String typeMk);

	public abstract String getTypeEntityName();
	public abstract void setTypeEntityName(String typeEntityName);
	

	public abstract String getTypePkName();
	public abstract void setTypePkName(String typePkName);
	
	public abstract String getTypePkDisplayName();
	public abstract void setTypePkDisplayName(String typePkDisplayName);
	
	
	public abstract String getOwnerId();
	public abstract void setOwnerId(String ownerId);

	public abstract String getOwnerDisplayName();
	public abstract void setOwnerDisplayName(String ownerDisplayName);
	
	
	public abstract Long getTypeOpts();
	public abstract void setTypeOpts(Long typeOpts);
	
	
	public abstract Collection<MetaItem> getMetaItems();

	
	public abstract void setMetaItem(String itemName, MetaItem value);
	public abstract MetaItem getMetaItem(String itemName);

	
	public abstract Iterator<Entry<String, MetaItem>> iteratorMetaItems();

}