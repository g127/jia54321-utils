package com.jia54321.utils.entity.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jia54321.utils.JsonHelper;
import com.jia54321.utils.entity.IStorageConstants;


/**
 * SimpleCondition 的对象工厂
 * @author G
 *
 */
public class SimpleConditionFactory {

	private static final String CREATE_IN_ARG_2 = "%s IN ( %s )";
	/**. */
	private static final String[] SEARCH_FIXED_REGEX_AND_REPLACE = new String[]{"\"search\"[ ]*:[ ]*([,:}]+)", "\"search\" : {} $1"};
	/**. */
	private static final String SIMPLE_CONDITION_KEY_IDS_SEARCH_ARG_5 = "{ \"key\": \"%s\", \"ids\": \"%s\", \"search\": %s,  \"page\": { \"pageNo\": %s , \"pageSize\": %s } }";

	/**
	 * 创建条件
	 * @param w  where 语句
	 * @param p  params List形式参数
	 * @return SimpleCondition
	 */
	public static SimpleCondition create(final String w, final List<?> p){
		SimpleCondition sc = new SimpleCondition();
		sc.setW(w);
		sc.setP(p);
		return sc;
	}

	/**
	 * 创建条件
	 * @param w
	 * @param commaDelimitedVals  多个逗号分隔的参数值。 例如：参数值,参数值, ... 参数值
	 * @return SimpleCondition
	 */
	public static SimpleCondition create(final String w, final String commaDelimitedVals){
		return create(w, Lists.newArrayList(commaDelimitedVals.split(",")));
	}

	/**
	 * 创建特殊的In条件
	 * @param fieldName 字段名
	 * @param commaDelimitedVals  多个逗号分隔的参数值。 例如：参数值,参数值, ... 参数值
	 * @return SimpleCondition
	 */
	public static SimpleCondition createIn(String fieldName, String commaDelimitedVals) {
		SimpleCondition sc = new SimpleCondition();
		sc.setW(String.format(CREATE_IN_ARG_2, fieldName, commaDelimitedVals.replaceAll("[^,]+", "?")));
		sc.setP(Lists.newArrayList(commaDelimitedVals.split(",")));
		//如果查询同一表主键，下面语句没有问题，但是查询关联表ID就有问题
		//sc.setPage(new Page(sc.getP().size()));
		sc.setPage(new Page(Integer.MAX_VALUE));
		return sc;
	}

	/**
	 * 创建Page查询
	 * @param pageNo
	 * @param pageSize
	 * @return SimpleCondition
	 */
	public static SimpleCondition createByPage(Integer pageNo, Integer pageSize) {
		SimpleCondition sc = new SimpleCondition();
		sc.getPage().setPageNo(pageNo);
		sc.getPage().setPageSize(pageSize);
		return sc;
	}

	/**
	 * 创建Ids查询
	 * @param ids  多个逗号分隔的参数值。 例如：参数值,参数值, ... 参数值
	 * @return SimpleCondition
	 */
	public static SimpleCondition createByIds(String ids){
		SimpleCondition sc = new SimpleCondition();
		sc.setIds(ids);
		return sc;
	}

	/**
	 * 创建SimpleCondition
	 * @param search   之一：search查询条件
	 * @return
	 */
	public static SimpleCondition createBySearch(Map<String, Object> search){
		SimpleCondition sc = new SimpleCondition();
		sc.setSearch(Maps.newHashMap(search));
		return sc;
	}

	/**
	 * 创建SimpleCondition search条件
	 *
	 * @param keyAndValPairs
	 * @return SimpleCondition
	 */
	public static SimpleCondition createBySearchPairs(String... keyAndValPairs) {
		SimpleCondition sc = new SimpleCondition();
		if (!JsonHelper.isEmpty(keyAndValPairs) && keyAndValPairs.length % 2 == 0) {
			Map<String, Object> search = new HashMap<String, Object>();
			for (int i = 0; i < keyAndValPairs.length; i++) {
				if (i % 2 == 0) {
					search.put(keyAndValPairs[i], keyAndValPairs[i + 1]);
				}
			}
			sc.setSearch(search);
		}
		return sc;
	}




	/**
	 * 创建SimpleCondition
	 * @param and and
	 * @return
	 */
	public static SimpleCondition createByAnd(Map<String, Object> and, int pageNo, int pageSize) {
		SimpleCondition sc = new SimpleCondition();
		sc.setAnd(Lists.newArrayList(and));
		sc.setPage(new Page(pageNo, pageSize));
		return sc;
	}

	/**
	 * 创建SimpleCondition
	 * @param or or
	 * @return
	 */
	public static SimpleCondition createByOr(Map<String, Object> or, int pageNo, int pageSize) {
		SimpleCondition sc = new SimpleCondition();
		sc.setOr(Lists.newArrayList(or));
		sc.setPage(new Page(pageNo, pageSize));
		return sc;
	}

	/**
	 * 创建SimpleCondition
	 * @param and and
	 * @return
	 */
	public static SimpleCondition createByAndWithOr(Map<String, Object> and, Map<String, Object> or, Map<String, Object> sort, int pageNo, int pageSize) {
		SimpleCondition sc = new SimpleCondition();
		if(and != null && and.size() > 0) {
			sc.setAnd(Lists.newArrayList(and));
		}
		if(or != null && or.size() > 0) {
			sc.setOr(Lists.newArrayList(or));
		}
		if(sort != null && sort.size() > 0) {
			sc.setSorts(Lists.newArrayList(sort));
		}
		sc.setPage(new Page(pageNo, pageSize));
		return sc;
	}

	/**
	 * 创建 storage object 实体 SimpleCondition
	 * @param formId
	 * @return
	 */
	public static SimpleCondition createByTypeObjectSearchEQ1(String formId, int pageNo, int pageSize){
		SimpleCondition sc = new SimpleCondition();
		sc.setSearch(ImmutableMap.of("EQ_BIZ_FORM_ID", (Object)formId));
		sc.setTypeId(IStorageConstants.TYPE_ID_OBJECT);
		sc.setPage(new Page(pageNo, pageSize));
		return sc;
	}

	/**
	 * 创建 storage object 实体 SimpleCondition
	 * @param formKey
	 * @param formId
	 * @return
	 */
	public static SimpleCondition createByTypeObjectSearchEQ2(String formKey, String formId, int pageNo, int pageSize){
		SimpleCondition sc = new SimpleCondition();
		sc.setSearch(ImmutableMap.of("EQ_BIZ_FORM_ID", (Object)formId, "EQ_BIZ_FORM_KEY", (Object)formKey));
		sc.setTypeId(IStorageConstants.TYPE_ID_OBJECT);
		sc.setPage(new Page(pageNo, pageSize));
		return sc;
	}

	/**
	 * 创建 storage object 实体 SimpleCondition
	 * @param formKey
	 * @param formId
	 * @param pathKey
	 * @return
	 */
	public static SimpleCondition createByTypeObjectSearchEQ3(String formKey, String formId, String pathKey, int pageNo, int pageSize){
		SimpleCondition sc = new SimpleCondition();
		sc.setSearch(ImmutableMap.of("EQ_BIZ_FORM_ID", (Object)formId, "EQ_BIZ_FORM_KEY", (Object)formKey, "EQ_OBJECT_PATH_KEY", (Object)pathKey));
		sc.setTypeId(IStorageConstants.TYPE_ID_OBJECT);
		sc.setPage(new Page(pageNo, pageSize));
		return sc;
	}

	/**
	 * 创建SimpleCondition
	 * @param key      描述用
	 * @param ids      之一：主键查询条件  XXX in ids;
	 * @param search   之一：search查询条件
	 * @param pageNo   分页信息
	 * @param pageSize 分页信息
	 * @return
	 */
	public static SimpleCondition createByParams(String key, String ids,
			String search, int pageNo, int pageSize) {
		if(JsonHelper.isEmpty(search)){
			search = "{}";
		}
		final String json = String.format(SIMPLE_CONDITION_KEY_IDS_SEARCH_ARG_5, key, ids, search, pageNo, pageSize);
		List<SimpleCondition> result = createByJsonParam(json);
		if(null!= result && result.size() > 0){
			return result.get(0);
		}
		return null;
	}

	/**
	 * 创建SimpleCondition仅仅根据SimpleConditionGroup 来解析 json
	 * @param json
	 * @return List<SimpleCondition>
	 */
	public static SimpleCondition createByOneJsonParam(String json) {
		List<SimpleCondition> result = createByJsonParam(json);
		if(null == result || result.size() <=0 ) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * 创建List<SimpleCondition> 根据 json
	 * @param sc  {} == SimpleCondition 或者  [{}] == List<SimpleCondition> json 形式
	 * @return List<SimpleCondition>
	 */
	public static List<SimpleCondition> createByJsonParam(String json) {
		List<SimpleCondition> result = null;
		//FIXED
		//一定程度上避免 "{ \"ids\": \"11\",\"search\" :  },{ \"search\" : ，\"ids\": \"11\" }"
		String scList = json.replaceAll(SEARCH_FIXED_REGEX_AND_REPLACE[0], SEARCH_FIXED_REGEX_AND_REPLACE[1]);

		try {
			SimpleCondition one = JsonHelper.parseObject(scList, SimpleCondition.class);
			result = Lists.newArrayList(one);
		} catch (Exception e) {
		}
		if (null == result || result.size() <= 0) {
			// result = JsonHelper.parseArray(json, SimpleCondition.class);
			List<JSONObject> jsonObjlst = JsonHelper.parseArray(scList, JSONObject.class);
			result = Lists.newArrayList(Lists.transform(jsonObjlst, new Function<JSONObject, SimpleCondition>() {
						@Override
						public SimpleCondition apply(JSONObject jsonObj) {;
							SimpleCondition sc = null;
							if(!JsonHelper.isEmpty(jsonObj)){
								sc = jsonObj.toJavaObject(SimpleCondition.class);
//								//
//								if (!JsonHelper.isEmpty(jsonObj.get("dir"))) {
//									sc.setKey((String)jsonObj.get("key"));
//								}
//								if (!JsonHelper.isEmpty(jsonObj.get("objectId"))) {
//									sc.setIds((String)jsonObj.get("objectId"));
//								}
								// 判断
								if (JsonHelper.isEmpty(sc.getIds())
										&& null == sc.getSearch()
										&& JsonHelper.isEmpty(sc.getW())
										&& JsonHelper.isEmpty(sc.getAnd())
										&& JsonHelper.isEmpty(sc.getOr())) {
									return null;
								}
							}
							return sc;
						}
					}));
		}
		return result;
	}

}
