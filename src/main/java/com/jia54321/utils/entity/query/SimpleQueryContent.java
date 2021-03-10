package com.jia54321.utils.entity.query;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author G
 */
public class SimpleQueryContent<T> {
	//------------------------------------------------------
	// key: 描述用
	//------------------------------------------------------
	@JSONField(ordinal=1)
	String key = "";
	//------------------------------------------------------
	// typeId: 实体定义名
	//------------------------------------------------------
	@JSONField(ordinal=2)
	String typeId;
	//------------------------------------------------------
	
	/** 页号  默认 第1页  */
	@JSONField(ordinal=3)
	Integer pageNo = 1;
	/** 每页大小 默认 16条 */
	@JSONField(ordinal=4)
	Integer pageSize = 16;
	/** 总数 */
	@JSONField(ordinal=5)
	Long totalElements;
	/** 总页数 */
	@JSONField(ordinal=6)
	Long totalPages;
	@JSONField(ordinal=7)
	List<T> rows;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Long getTotalElements() {
		return totalElements;
	}
	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}
	public Long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Long totalPages) {
		this.totalPages = totalPages;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}