package com.jia54321.utils.entity.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author G
 */
@SuppressWarnings("rawtypes")
public class SimpleCondition {
	//------------------------------------------------------
	// key: 描述用
	//------------------------------------------------------
	private String key = "";
	//------------------------------------------------------
	// typeId: 实体定义名
	//------------------------------------------------------
	private String typeId;
	//------------------------------------------------------
	// 之一：search查询条件
	//------------------------------------------------------
	private Map<String, Object> search;
	
	//------------------------------------------------------
	// 之一：主键查询条件  XXX in ids;
	//------------------------------------------------------
	private String ids;
	//------------------------------------------------------	
	
	//------------------------------------------------------
	// 之一：原生SQL查询条件
	//------------------------------------------------------
	private String w;
	private List p;
	//------------------------------------------------------
	// 之一：并且 或者之一的查询条件
	//------------------------------------------------------
	private List<Map<String, Object>> and, or;
	private List<Map<String, Object>> sorts;
	//------------------------------------------------------
	// 分页信息
	//------------------------------------------------------
	private Page page;

	public SimpleCondition() {
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

	public Map<String, Object> getSearch() {
		return search;
	}

	public void setSearch(Map<String, Object> search) {
		this.search = search;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getW() {
		return w;
	}

	public void setW(String w) {
		this.w = w;
	}

	public List getP() {
		return p;
	}

	public void setP(List p) {
		this.p = p;
	}

	public List<Map<String, Object>> getAnd() {
		return and;
	}

	public void setAnd(List<Map<String, Object>> and) {
		this.and = and;
	}

	public List<Map<String, Object>> getOr() {
		return or;
	}

	public void setOr(List<Map<String, Object>> or) {
		this.or = or;
	}

	public List<Map<String, Object>> getSorts() {
		return sorts;
	}

	public void setSorts(List<Map<String, Object>> sorts) {
		this.sorts = sorts;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String
				.format("SimpleCondition [key=%s, typeId=%s, search=%s, ids=%s, w=%s, p=%s, and=%s, or=%s, sorts=%s, page=%s]",
						key, typeId,
						search != null ? toString(search.entrySet(), maxLen)
								: null, ids, w, p != null ? toString(p, maxLen)
								: null, and != null ? toString(and, maxLen)
								: null, or != null ? toString(or, maxLen)
								: null, sorts != null ? toString(sorts, maxLen)
								: null, page);
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0) {
                builder.append(", ");
            }
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
}
