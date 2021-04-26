package com.jia54321.utils.entity.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.jia54321.utils.ClassUtils;
import com.jia54321.utils.JsonHelper;
import com.jia54321.utils.NumberUtils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jia54321.utils.entity.DynamicEntity;


/**
 *
 * @author G
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class QueryContentFactory {
	/** */
	private static final String DOT_SPLIT_REGEX = "\\.";

	/**
	 *
	 * @param sc
	 * @return
	 */
	public static List<QueryContent<DynamicEntity>> createQueryContents(List<SimpleCondition> sc){
		List<QueryContent<DynamicEntity>> rs = Lists.newArrayList();
		for (SimpleCondition simpleCondition : sc) {
			rs.add(createQueryContent(simpleCondition));
		}
		return rs;
	}

	/**
	 *
	 * @param sc
	 * @return
	 */
	public static QueryContent<DynamicEntity> createQueryContent(SimpleCondition sc){
		QueryContent<DynamicEntity> queryContent = new QueryContent();
		if (sc != null) {
			queryContent.setKey(sc.getKey());
			queryContent.setTypeId(sc.getTypeId());

			queryContent.setP(sc.getP());
			queryContent.setW(sc.getW());

			queryContent.setIds(sc.getIds());

			List<OperationBean> conditions = new ArrayList<OperationBean>();
			conditions = OperationBean.conditionOneMapAnd(conditions, sc.getSearch());
			conditions = OperationBean.conditionOneListMapAnd(conditions, sc.getAnd());
			conditions = OperationBean.conditionOneListMapOr(conditions, sc.getOr());
			queryContent.setConditions(conditions);

			List<OperationBean> sort = new ArrayList<OperationBean>();
			sort = OperationBean.conditionOneListMapAnd(sort, sc.getSorts());
			queryContent.setSorts(sort);

			if (null != sc.getPage()) {
				queryContent.setPage(sc.getPage());
			}
		}
		return queryContent;
	}

	public static SimpleQueryContent<Map<String, Object>> createSimpleQueryContent(QueryContent<DynamicEntity> qc){
		final SimpleQueryContent<Map<String, Object>> qresult = new SimpleQueryContent<Map<String, Object>>();
		List<?> list = qc.getResult();
		qresult.setKey(qc.getKey());
		qresult.setTypeId(qc.getTypeId());
		qresult.setPageNo(qc.getPage().getPageNo());
		qresult.setPageSize(qc.getPage().getPageSize());
		qresult.setTotalPages(qc.getPage().getTotalPages());
		qresult.setTotalElements(qc.getPage().getTotalElements());

		List<Map<String, Object>> qresultlist = new ArrayList<Map<String, Object>>(list.size());
		qresult.setRows(qresultlist);

		if (list.size() > 0 && (list.get(0) instanceof DynamicEntity)) {
			for (int i = 0; i < list.size(); i++) {
				qresultlist.add(((DynamicEntity) list.get(i)).getItems());
			}
		} else {
			//qresult.setRows(list);
		}
		return qresult;
	}

	public static List<SimpleQueryContent<Map<String, Object>>> createSimpleQueryContents(List<QueryContent<DynamicEntity>> qcList) {
		List<SimpleQueryContent<Map<String, Object>>> rs = Lists.newArrayList();
		for (QueryContent<DynamicEntity> queryContent : qcList) {
			rs.add(createSimpleQueryContent(queryContent));
		}
		return rs;
	}

	/**
	 * 排序
	 * @param source
	 * @param fieldName
	 * @return
	 */
	public static QueryContent<DynamicEntity> sortByField(QueryContent<DynamicEntity> source, final String fieldName ){
		if (null != source && null != source.getResult() && source.getResult().size() > 0) {
			if(null!=source.getResult().get(0).getMetaItem(fieldName)){
				// 根据fieldName升序排序
		        Collections.sort(source.getResult(), new Comparator<DynamicEntity>() {
					@Override
					public int compare(DynamicEntity arg0, DynamicEntity arg1) {
						return Integer.valueOf(NumberUtils.toInt(String.valueOf(arg0.get(fieldName))))
								.compareTo(
							Integer.valueOf(NumberUtils.toInt(String.valueOf(arg1.get(fieldName)))));
					}
		        });
			}
		}
		return source;
	}

	/**
	 * 常用转换  将数据，转换为keyValues中描述的数据
	 * keyValues的个数为新列表的列数   新列名=原列名，  如 "value=SEND_DOC_HEAD_ID","text=SEND_DOC_HEAD"
	 * @param srcMap
	 * @param splitRegex
	 * @param keyValues
	 * @return
	 */
	public static Map<String,Object> createMapKV(Map<String, Object> srcMap, String splitRegex, String... keyValues){
		if (null == splitRegex || "".equals(splitRegex)) {
			splitRegex = DOT_SPLIT_REGEX;
		}
		Map<String, Object> distMap = Maps.newLinkedHashMap();
		for (int j = 0; j < keyValues.length; j++) {
			String[] keyValue = keyValues[j].split(splitRegex);
			distMap.put(keyValue[0], srcMap.get(keyValue[1]));
		}
		return distMap;
	}

	/**
	 * 常用转换  将列表数据，转换为keyValues中描述的列表数据 ，用于将列表数据转换为下拉框中数据
	 * keyValues的个数为新列表的列数   新列名=原列名，  如 "value=SEND_DOC_HEAD_ID","text=SEND_DOC_HEAD"
	 * @param sqc
	 * @param splitRegex
	 * @param keyValues
	 * @return
	 */
	public static List<Map<String,Object>> createListKV(SimpleQueryContent sqc, String splitRegex, String... keyValues){
		List<Map<String, Object>> list = (List<Map<String, Object>>)sqc.getRows();
		List<Map<String, Object>> qresultlist = new ArrayList<Map<String, Object>>(list.size());
		for (int i = 0; i < list.size(); i++) {
			qresultlist.add(createMapKV(list.get(i), splitRegex, keyValues));
		}
		return qresultlist;
	}


	/**
	 * 常用转换  将数据，转换为keyValues中描述的数据
	 * keyValues的个数为新列表的列数   新列名=原列名，  如 "value=SEND_DOC_HEAD_ID","text=SEND_DOC_HEAD"
	 * @param srcMap
	 * @param splitRegex
	 * @param keyValues
	 * @return
	 */
	public static Map<String,Object> createMapIdKV(Map<String, Object> srcMap, String splitRegex, String... idKeyValues){
		if (null == splitRegex || "".equals(splitRegex)) {
			splitRegex = DOT_SPLIT_REGEX;
		}
		Map<String, Object> distMap = Maps.newLinkedHashMap(srcMap);
		for (String tmp : idKeyValues) {
			String[] IdKeyValue = tmp.split(splitRegex);
			//三个参数或者4个。
			if(IdKeyValue.length == 3 || IdKeyValue.length == 4) {
				// id.0.男 id.1.女  id==0?id_name=男 id==1?id_name=女
				String condtionField = IdKeyValue[0], condtionEqualVal = IdKeyValue[1], newField, newVal;

				Object condtionActualObjectVal = srcMap.get(condtionField);
				boolean equalsIdValue = false;
				if(condtionActualObjectVal instanceof String) {
					String condtionActualVal = (String) condtionActualObjectVal;
					equalsIdValue = condtionEqualVal == condtionActualVal
							|| condtionEqualVal.equals(condtionActualVal);
				} else if(condtionActualObjectVal instanceof BigDecimal){
					BigDecimal condtionActualVal = (BigDecimal) condtionActualObjectVal;
					equalsIdValue = condtionActualVal.equals(new BigDecimal(condtionEqualVal));
				} else {

				}

				if(IdKeyValue.length == 3){
					newField = IdKeyValue[0] + "_NAME";
					newVal	=  IdKeyValue[2];
				}else{
					newField = IdKeyValue[2];
					newVal	=  IdKeyValue[3];
				}

				boolean notExistDefinedNameField = JsonHelper.isEmpty(srcMap.get(newField));
				if ( notExistDefinedNameField && equalsIdValue) {
					distMap.put(newField, newVal);
				}
			}
//			if(IdKeyValue.length == 3 ){
//				String definedIdField = IdKeyValue[0];
//				String definedIdValue = IdKeyValue[1];
//				String definedNameField = IdKeyValue[0] + "_NAME";
//				String definedNameValue = IdKeyValue[2]; //男
//				String actualIdValue = String.valueOf(srcMap.get(definedIdField)); // 1
//				boolean equalsIdValue = definedIdValue.equals(actualIdValue);
//				boolean notExistDefinedNameField = StringUtils.isEmpty(srcMap.get(definedNameField));
//				if ( notExistDefinedNameField && equalsIdValue) {
//					distMap.put(definedNameField, definedNameValue);
//				}
//			}
		}
		return distMap;
	}

	/**
	 * 常用转换   将列表数据，转换为keyValues中描述的列表数据
	 * @param sqc
	 * @param splitRegex
	 * @param IdKeyValues  id.0.男 id.1.女  id==0?id_name=男 id==1?id_name=女
	 * @return
	 */
	public static List<Map<String,Object>> createListIdKV(SimpleQueryContent sqc, String splitRegex, String... idKeyValues){
		List<Map<String, Object>> list = (List<Map<String, Object>>)sqc.getRows();
		List<Map<String, Object>> qresultlist = new ArrayList<Map<String, Object>>(list.size());
		for (int i = 0; i < list.size(); i++) {
			qresultlist.add(createMapIdKV(list.get(i), splitRegex, idKeyValues));
		}
		return qresultlist;
	}

	/**
	 *
	 * @param sqc
	 * @param keyValues
	 * @return
	 */
	public static List<Map<String,Object>> createListKVBySplitDot(Object queryContentOrSimpleQueryContent, String... keyValues){
		if(ClassUtils.isAssignableFrom(SimpleQueryContent.class, queryContentOrSimpleQueryContent)){
			return createListKV((SimpleQueryContent)queryContentOrSimpleQueryContent, DOT_SPLIT_REGEX, keyValues);
		} else if(ClassUtils.isAssignableFrom(QueryContent.class, queryContentOrSimpleQueryContent)){
			return createListKV(createSimpleQueryContent((QueryContent)queryContentOrSimpleQueryContent), DOT_SPLIT_REGEX, keyValues);
		}
		return null;
	}

	/**
	 *
	 * @param sqc
	 * @param keyValues
	 * @return
	 */
	public static List<Map<String,Object>> createListIdKVBySplitDot(Object queryContentOrSimpleQueryContent, String... keyValues){
		if(ClassUtils.isAssignableFrom(SimpleQueryContent.class, queryContentOrSimpleQueryContent)){
			return createListIdKV((SimpleQueryContent)queryContentOrSimpleQueryContent, DOT_SPLIT_REGEX, keyValues);
		} else if(ClassUtils.isAssignableFrom(QueryContent.class, queryContentOrSimpleQueryContent)){
			return createListIdKV(createSimpleQueryContent((QueryContent)queryContentOrSimpleQueryContent), DOT_SPLIT_REGEX, keyValues);
		}
		return null;
	}

}
