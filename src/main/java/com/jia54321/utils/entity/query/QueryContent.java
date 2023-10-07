package com.jia54321.utils.entity.query;

import java.util.ArrayList;
import java.util.List;


public class QueryContent<T> {
	//------------------------------------------------------
	// key: 描述用
	//------------------------------------------------------
	private String key = "";
	//------------------------------------------------------
	// typeId: 实体定义名
	//------------------------------------------------------
	private String typeId;
	//------------------------------------------------------
	// 之一：原生SQL查询条件
	//------------------------------------------------------
	private String w;
	private List<Object> p;
	//------------------------------------------------------
	// 之一：主键查询条件  XXX in ids;
	//------------------------------------------------------
	private String ids;
	//------------------------------------------------------
	// 之一：并且 或者之一的查询条件
	//------------------------------------------------------
	private List<OperationBean> conditions;
	private List<OperationBean> sorts;
	//------------------------------------------------------
	
	/** 结果 */
	List<T> result;
	/** 分页信息 */
	Page page = new Page();
	
	public QueryContent() {
		key = "";
		conditions = new ArrayList<OperationBean>();
		result = new ArrayList<T>();
		page = new Page();
	}

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

	public String getW() {
		return w;
	}

	public void setW(String w) {
		this.w = w;
	}

	public List<Object> getP() {
		return p;
	}

	public void setP(List<Object> p) {
		this.p = p;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public List<OperationBean> getConditions() {
		return conditions;
	}

	public void setConditions(List<OperationBean> conditions) {
		this.conditions = conditions;
	}

	public List<OperationBean> getSorts() {
		return sorts;
	}

	public void setSorts(List<OperationBean> sorts) {
		this.sorts = sorts;
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
