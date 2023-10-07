package com.jia54321.utils.entity.query;

/**
 * 分页对象
 * @author G
 */
public class Page {
	/** 页号  默认 第1页  */
	Integer pageNo = 1;
	/** 每页大小 默认 16条 */
	Integer pageSize;
	/** 总页数 */
	Long totalPages;
	/** 总数 */
	Long totalElements;
	
	public Page() {
		this(1, 16);
	}
	public Page(Integer pageSize) {
		this(1, pageSize);
	}
	public Page(Integer pageNo, Integer pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize; 
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
	public Long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Long totalPages) {
		this.totalPages = totalPages;
	}
	public Long getTotalElements() {
		return totalElements;
	}
	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}
	@Override
	public String toString() {
		return String.format("Page [pageNo=%s, pageSize=%s, totalPages=%s, totalElements=%s]",
						pageNo, pageSize, totalPages, totalElements);
	}
}
