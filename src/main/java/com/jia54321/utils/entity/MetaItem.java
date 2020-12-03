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

/**
 * 元数据
 * @author 郭罡
 * @description 2009-7-31 郭罡 新建
 */
public class MetaItem {
	private String typeId;
	private String itemId;
	private String itemColName;
	private String itemColDesc;
	private MetaItemLevel itemLevel;
	private MetaItemType itemType;
	private Integer itemLen;
	private Integer itemDec;
	private String  itemDefaultValue;	
	private Integer refTypeId;
	private String itemParentId;
    //增强字段
	private String itemCodeName;
	private String itemCodeType;
	private String itemDisplayName;
	private String itemDisplayType;

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemColName() {
		return itemColName;
	}

	public void setItemColName(String itemColName) {
		this.itemColName = itemColName;
	}

	public String getItemColDesc() {
		return itemColDesc;
	}

	public void setItemColDesc(String itemColDesc) {
		this.itemColDesc = itemColDesc;
	}

	public MetaItemLevel getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(MetaItemLevel itemLevel) {
		this.itemLevel = itemLevel;
	}

	public MetaItemType getItemType() {
		return itemType;
	}

	public void setItemType(MetaItemType itemType) {
		this.itemType = itemType;
	}

	public Integer getItemLen() {
		return itemLen;
	}

	public void setItemLen(Integer itemLen) {
		this.itemLen = itemLen;
	}

	public Integer getItemDec() {
		return itemDec;
	}

	public void setItemDec(Integer itemDec) {
		this.itemDec = itemDec;
	}

	public String getItemDefaultValue() {
		return itemDefaultValue;
	}

	public void setItemDefaultValue(String itemDefaultValue) {
		this.itemDefaultValue = itemDefaultValue;
	}

	public Integer getRefTypeId() {
		return refTypeId;
	}

	public void setRefTypeId(Integer refTypeId) {
		this.refTypeId = refTypeId;
	}

	public String getItemParentId() {
		return itemParentId;
	}

	public void setItemParentId(String itemParentId) {
		this.itemParentId = itemParentId;
	}

	public String getItemCodeName() {
		return itemCodeName;
	}

	public void setItemCodeName(String itemCodeName) {
		this.itemCodeName = itemCodeName;
	}

	public String getItemCodeType() {
		return itemCodeType;
	}

	public void setItemCodeType(String itemCodeType) {
		this.itemCodeType = itemCodeType;
	}

	public String getItemDisplayName() {
		return itemDisplayName;
	}

	public void setItemDisplayName(String itemDisplayName) {
		this.itemDisplayName = itemDisplayName;
	}

	public String getItemDisplayType() {
		return itemDisplayType;
	}

	public void setItemDisplayType(String itemDisplayType) {
		this.itemDisplayType = itemDisplayType;
	}

	@Override
	public String toString() {
		return "MetaItem [typeId=" + typeId + ", itemId=" + itemId
				+ ", itemColName=" + itemColName + ", itemColDesc="
				+ itemColDesc + ", itemLevel=" + itemLevel + ", itemType="
				+ itemType + ", itemLen=" + itemLen + ", itemDec=" + itemDec
				+ ", itemDefaultValue=" + itemDefaultValue + ", refTypeId="
				+ refTypeId + ", itemParentId=" + itemParentId
				+ ", itemCodeName=" + itemCodeName + ", itemCodeType="
				+ itemCodeType + ", itemDisplayName=" + itemDisplayName
				+ ", itemDisplayType=" + itemDisplayType + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}
		
		if(this.toString().equals(obj.toString())) {
			return true;
		}
		
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
}
