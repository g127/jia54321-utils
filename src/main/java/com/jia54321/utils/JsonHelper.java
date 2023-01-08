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
package com.jia54321.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.jia54321.utils.entity.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.AbstractDateDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.jia54321.utils.entity.DynamicEntity;
import com.jia54321.utils.fastjson.ComplexPropertyPreFilter;


/**
 * Json常用助手类
 * @author 郭罡
 */
public class JsonHelper extends Helper {
	/** log */
	static final Logger log = LoggerFactory.getLogger(JsonHelper.class);
	/** 空字符串 "" */
	public static final String STRING_EMPTY = "";

	static class EntityTimestampSerializer implements ObjectSerializer {
		private static final char SEPERATOR = ',';
		private static final String CAMLE_CREATE_TIME = "createTime";
		private static final String UNDERLINE_CREATE_TIME = "CREATE_TIME";

		private static final String CAMLE_CREATE_TIME_MILLIS = "createTimeMillis";
		private static final String UNDERLINE_CREATE_TIME_MILLIS = "CREATE_TIME_MILLIS";

		private final String pattern;

		/** 增强特性  */
		private static final boolean JSON_HELPER_ENHANCE_TIME_MILLIS = false;

		public EntityTimestampSerializer(String pattern) {
			this.pattern = pattern;
		}

		@Override
		public void write(JSONSerializer serializer, Object object,
						  Object fieldName, Type fieldType, int features) throws IOException {
			if (object == null) {
				serializer.getWriter().writeNull();
				return;
			}

			java.util.Date date = (java.util.Date) object;
			SimpleDateFormat format = new SimpleDateFormat(pattern);

			String text = format.format(date);
			serializer.write(text);

			if(JSON_HELPER_ENHANCE_TIME_MILLIS){
				if(CAMLE_CREATE_TIME.equals(fieldName) || UNDERLINE_CREATE_TIME.equals(fieldName)) {
					if(fieldName.equals(CAMLE_CREATE_TIME) ){
						serializer.getWriter().writeFieldValue(SEPERATOR, CAMLE_CREATE_TIME_MILLIS, date.getTime());
					}
					if(fieldName.equals(UNDERLINE_CREATE_TIME) ){
						serializer.getWriter().writeFieldValue(SEPERATOR, UNDERLINE_CREATE_TIME_MILLIS, date.getTime());
					}
				}
			}
		}
	}


	/** DEFINED_DEFAULT_TIME_MAPPING */
	private static final SerializeConfig DEFINED_DEFAULT_TIME_MAPPING = new SerializeConfig();

	/** DEFINED_DEFAULT_DATE_MAPPING */
	private static final SerializeConfig DEFINED_DEFAULT_DATE_MAPPING = new SerializeConfig();

	/** 去掉循环 */
	private static final ComplexPropertyPreFilter DEFINED_ENTITY_TYPE_FILTER = new ComplexPropertyPreFilter();

	/** */
	//private static final SerializerFeature[] serializerFeatures = new SerializerFeature[] { SerializerFeature.DisableCircularReferenceDetect };
	//private static final SerializerFeature[] serializerFeatures = new SerializerFeature[] { SerializerFeature.PrettyFormat };

//	/** PARSER_CONFIG */
//	public static final ParserConfig PARSER_CONFIG = ParserConfig.getGlobalInstance();
    static {
    	//DEFINED_DEFAULT_TIME_MAPPING begin
        DEFINED_DEFAULT_TIME_MAPPING.put(java.util.Date.class, new EntityTimestampSerializer(DateUtil.Formatter.YYYY_MM_DD_HH_MM_SS.pattern));

        DEFINED_DEFAULT_TIME_MAPPING.put(java.sql.Date.class, new EntityTimestampSerializer(DateUtil.Formatter.YYYY_MM_DD_HH_MM_SS.pattern));
        DEFINED_DEFAULT_TIME_MAPPING.put(java.sql.Timestamp.class, new EntityTimestampSerializer(DateUtil.Formatter.YYYY_MM_DD_HH_MM_SS.pattern));
        //DEFINED_DEFAULT_TIME_MAPPING end

        //DEFINED_DEFAULT_DATE_MAPPING begin
        DEFINED_DEFAULT_DATE_MAPPING.put(java.util.Date.class, new EntityTimestampSerializer(DateUtil.Formatter.YYYY_MM_DD.pattern));

        DEFINED_DEFAULT_DATE_MAPPING.put(java.sql.Date.class, new EntityTimestampSerializer(DateUtil.Formatter.YYYY_MM_DD.pattern));
        DEFINED_DEFAULT_DATE_MAPPING.put(java.sql.Timestamp.class, new EntityTimestampSerializer(DateUtil.Formatter.YYYY_MM_DD.pattern));
        //DEFINED_DEFAULT_DATE_MAPPING end

        //默认过滤器
        DEFINED_ENTITY_TYPE_FILTER.setExcludes(new HashMap<Class<?>, String[]>() {
			private static final long serialVersionUID = 7530643057112668548L;
			{
				// 默认不处理 definedEntityType
				// 默认不处理 result
//                put(PEntityType.class, new String[] { "definedEntityType" });
				put(EntityType.class, new String[] { "tableDesc", "metaItems" });

                put(List.class, new String[] { "result" });
            }
        });

//        PARSER_CONFIG.putDeserializer(Timestamp.class, new EntityTimestampDeserializer());
    }

	/**
	 * 对象互转
	 * @param jsonParams
	 * @param paramsType
	 * @return Object 泛型对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T  cast(String jsonParams,  Class<T> paramsType) {
		return JsonCastUtil.cast(jsonParams, paramsType);
	}

	/**
	 * 对象互转
	 * @param jsonParams Json字符串
	 * @param paramsTypes 参数
	 * @return Object[] 对象数组
	 */
	public static Object[] cast(final String jsonParams, Type[] paramsTypes) {
		return JsonCastUtil.cast(jsonParams, paramsTypes);
	}

    /**
     * 对象转化为Json字符串形式
     * @param obj 对象
     * @return json格式字符串
     */
    public static String toJson(Object obj){
    		return toJSONString(obj);
    }

    /**
     * Json字符串形式转对象
     * @param jsonParams  json格式字符串
     * @param paramsType  参数类型
     * @return 类型实例
     */
	@SuppressWarnings("unchecked")
	public static <T> T  fromJson(String jsonParams,  Class<T> paramsType) {
		final Object[] objects = JsonCastUtil.cast(jsonParams, new Type[]{paramsType});
		return (T) objects[0];
    }

	@SuppressWarnings("unchecked")
	public static  <K, V> LinkedHashMap<K, V> fromJsonAsLinkedHashMap(String jsonParams) {
		return  (LinkedHashMap<K, V>) JSON.parseObject(jsonParams, LinkedHashMap.class, JsonCastUtil.PARSER_CONFIG);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> HashMap<K, V> fromJsonAsHashMap(String jsonParams) {
		return (HashMap<K, V>) JSON.parseObject(jsonParams, HashMap.class, JsonCastUtil.PARSER_CONFIG);
	}

	/**
     * Json字符串形式转List对象
	 * @param jsonParams json格式字符串
	 * @param clazz List参数类型
	 * @return List<T>
	 */
	public static final <T> List<T> fromJsonToList(String jsonParams, Class<T> clazz) {
		return parseArray(jsonParams, clazz);
	}

	/**
     * Json字符串形式转List对象
	 * @param jsonParams json格式字符串
	 * @return  List参数对象实例
	 */
	@SuppressWarnings("unchecked")
	public static final  <K, V> List<HashMap<K, V>> fromJsonToHashMapList(String jsonParams) {
		return (List<HashMap<K, V>>) parseArray(jsonParams, new HashMap<K, V>().getClass());
	}

	/**
     * Json字符串形式转List对象
	 * @param jsonParams json格式字符串
	 * @return List参数对象实例
	 */
	@SuppressWarnings("unchecked")
	public static final  <K, V> List<LinkedHashMap<K, V>> fromJsonToLinkedHashMapList(String jsonParams) {
		return (List<LinkedHashMap<K, V>>) parseArray(jsonParams, new LinkedHashMap<K, V>().getClass());
	}

	/**
     * Json字符串形式转List对象
	 * @param jsonParams json格式字符串
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public static final  <K, V> List<Map<K, V>> fromJsonToMapList(String jsonParams) {
		Map<K, V> tmp = new LinkedHashMap<K, V>();
		return (List<Map<K, V>>) parseArray(jsonParams, tmp.getClass());
	}

    /**
     * 对象转化为数组对象转化为时间 Date 或 Timestamp ,带双引号""
     * @param obj 对象
     * @return string
     */
	public static String toJSONString(Object obj) {
		return JSON.toJSONString(obj, DEFINED_DEFAULT_TIME_MAPPING,
				DEFINED_ENTITY_TYPE_FILTER);
	}

    /**
     * 对象转化为数组
     * @param obj 对象
     * @return Json字符串
     */
	public static String toJSONString(Object obj, boolean prettyFormat) {
		if (!prettyFormat) {
			return toJSONString(obj);
		}
		return JSON.toJSONString(obj, DEFINED_DEFAULT_TIME_MAPPING,
				DEFINED_ENTITY_TYPE_FILTER,
				SerializerFeature.PrettyFormat);
	}



	/**
     * 对象互转
	 * @param text
	 * @param clazz
	 * @return Object 泛型对象
	 */
	public  static final <T> T parseObject(String text, Class<T> clazz){
		return JSON.parseObject(text, clazz);
	}

	/**
     * 对象互转
	 * @param text
	 * @param clazz
	 * @return Object 泛型对象
	 */
	public static final <T> List<T> parseArray(String text, Class<T> clazz) {
		return JSON.parseArray(text, clazz);
	}

	/**
	 * 列表转换
	 * @param lst 参数lst
	 * @return list
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <T> List<Map<String, Object>> toMapList(Iterable<T> lst) {
		List<Map<String, Object>> dtoList = new ArrayList<Map<String, Object>>();

		for (Object o : lst) {
			Map<String, Object> m = null;
			if(o instanceof Map) {
				m = Maps.newLinkedHashMap();
				for (Iterator iterator = ((Map) o).entrySet().iterator(); iterator.hasNext();) {
					Entry<Object, Object> e = (Entry<Object, Object>) iterator.next();
					m.put(String.valueOf(e.getKey()), e.getValue());
				}
			} else if(o instanceof DynamicEntity) {
				m = Maps.newLinkedHashMap();
				Map<String, Object>  javaBeanMap = ((DynamicEntity) o ).getJavaBeanItems();
				javaBeanMap.remove("metaItems");
				m.putAll(javaBeanMap);
			} else {
				m = parseObject(JsonHelper.toJSONString(o), Map.class);

				List<String> willDelKey =  new ArrayList<String>();

		        	Map<String, Object> override = new HashMap<String, Object>();
		        	for(Map.Entry<String, Object> entry : m.entrySet()){
		        		String k = entry.getKey(); Object v = entry.getValue();

		        		// 不为空，且不为原生类型
		        		if(null !=v && ClassUtils.isEntityOrDto(v.getClass())
		        				//不需要删除 属性类型为JSONArray或JSONObject的
		        				&& ! ClassUtils.isAssignableFrom(com.alibaba.fastjson.JSONArray.class, v)
		        				&& ! ClassUtils.isAssignableFrom(com.alibaba.fastjson.JSONObject.class, v)) {
		        			willDelKey.add(k);
		        		}
		        	}

		        	for (String delKey : willDelKey) {
		        		Object val = m.get(delKey);
		        		m.remove(delKey);
		        		if(val instanceof Map) {
							override.putAll((Map) val);
		        		}
		        	}

		        	override.putAll(m);

		        	m = override;

		        	willDelKey.clear();

		        	for(Map.Entry<String, Object> entry : m.entrySet()){
		        		String k = entry.getKey(); Object v = entry.getValue();
		        		// 不为空，且不为原生类型
		        		if(null !=v && ClassUtils.isEntityOrDto(v.getClass())
		        				//不需要删除 属性类型为JSONArray或JSONObject的
								&& !ClassUtils.isAssignableFrom(com.alibaba.fastjson.JSONArray.class, v)
								&& !ClassUtils.isAssignableFrom(com.alibaba.fastjson.JSONObject.class, v)) {
		        			willDelKey.add(k);
		        		}
		        	}

		        	for (String delKey : willDelKey) {
		        		m.remove(delKey);
		        	}
			}
            dtoList.add(m);
        }

        return dtoList;
	}

	/**
	 * 覆盖数据属性
	 * @param jsonStrOrMap 待更新的数据
	 * @param old 旧数据
	 * @return T 新对象
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T overrideObject(final Object jsonStrOrMap, T old) {
		Map<String, Object> input = null;
		if (jsonStrOrMap instanceof String) {
			input = fromJsonAsLinkedHashMap((String) jsonStrOrMap);
		} else if (jsonStrOrMap instanceof Map) {
			input = (Map<String, Object>) jsonStrOrMap;
		} else {
			throw new UnsupportedOperationException("jsonStrOrMap must be json string or Map<String, Object> ");
		}

		if (null == old) {
			throw new UnsupportedOperationException("old Entity must not be null ");
		}

		Map<String, Object> oldInput = fromJsonAsLinkedHashMap(toJson(old));
		// overrideObject
		oldInput.putAll(input);
		return (T) fromJson(toJson(oldInput), old.getClass());
	}


	/**
	 * 取得带相同前缀的Request Parameters, copy from spring WebUtils.
	 * 返回的结果的Parameter名已去除前缀.
	 *
	 * @param jsonStringOrMap 可以为json字符串， Map， ServletRequest 类型
	 * @param prefix
	 * @return 返回的结果的Parameter名已去除前缀.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getParametersStartingWith(Object jsonStringOrMap, String prefix) {
		Map<String, Object> params = new TreeMap<String, Object>();
		if (prefix == null) {
			prefix = STRING_EMPTY;
		}
		if(jsonStringOrMap instanceof String) {
			LinkedHashMap<String, Object> orignMap = fromJsonAsLinkedHashMap((String) jsonStringOrMap);
			for (Map.Entry<String,Object> param : orignMap.entrySet()) {
				if (STRING_EMPTY.equals(prefix) || param.getKey().startsWith(prefix)) {
					String unprefixed = param.getKey().substring(prefix.length());
					params.put(unprefixed, param.getValue());
				}
			}
		} else if(jsonStringOrMap instanceof Map) {
			for (Map.Entry<String,Object> param : ((Map<String, Object>)jsonStringOrMap).entrySet()) {
				if (STRING_EMPTY.equals(prefix) || param.getKey().startsWith(prefix)) {
					String unprefixed = param.getKey().substring(prefix.length());
					params.put(unprefixed, param.getValue());
				}
			}
		}
//		else if(jsonStringOrMap instanceof javax.servlet.http.Cookie[]) {
//			javax.servlet.http.Cookie[] cookies = (javax.servlet.http.Cookie[])jsonStringOrMap;
//			for (javax.servlet.http.Cookie cookie : cookies) {
//				if ("".equals(prefix) || cookie.getName().startsWith(prefix)) {
//					String unprefixed = cookie.getName().substring(prefix.length());
//					params.put(unprefixed, cookie.getValue());
//				}
//			}
//		} else if(jsonStringOrMap instanceof ServletRequest) {
//			params = org.springframework.web.util.WebUtils.getParametersStartingWith((ServletRequest)jsonStringOrMap, prefix);
//		}
		else if( ClassUtils.servletRequestIsAvailable
				&& ClassUtils.isAssignableFrom("javax.servlet.ServletRequest", jsonStringOrMap )) {
			// javax.servlet.ServletRequest getParameterMap
			/**
			 * Returns a java.util.Map of the parameters of this request. Request
			 * parameters are extra information sent with the request. For HTTP
			 * servlets, parameters are contained in the query string or posted form
			 * data.
			 *
			 * @return an immutable java.util.Map containing parameter names as keys and
			 *         parameter values as map values. The keys in the parameter map are
			 *         of type String. The values in the parameter map are of type
			 *         String array.
			 */
			// public Map<String, String[]> getParameterMap();
			try {
				// parameterMap
				Map<String, String[]> parameterMap
						= (Map<String, String[]>)ClassUtils.invokeMethodIfAvailable(jsonStringOrMap, "getParameterMap");
				//
				if( null!= parameterMap && parameterMap.size() > 0) {
					for ( Map.Entry<String, String[]> param: parameterMap.entrySet() ) {
						if (STRING_EMPTY.equals(prefix) || param.getKey().startsWith(prefix)) {
							String unprefixed = param.getKey().substring(prefix.length());
							String[] values = param.getValue();
							if (values == null || values.length == 0) {
								// Do nothing, no values found at all.
							}
							else if (values.length > 1) {
								params.put(unprefixed, values);
							}
							else {
								params.put(unprefixed, values[0]);
							}
						}
					}
				}
			} catch (Throwable t) {
			}
		}
		return params;
	}

	/**
	 * 组合Parameters生成Query String的Parameter部分, 并在paramter name上加上prefix.
	 *
	 * @see #getParametersStartingWith
	 */
	public static String encodeParameterStringWithPrefix(Map<String, Object> params, String prefix) {
		if (isEmpty(params)) {
			return STRING_EMPTY;
		}

		if (prefix == null) {
			prefix = STRING_EMPTY;
		}

		StringBuilder queryStringBuilder = new StringBuilder();
		Iterator<Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			queryStringBuilder.append(prefix).append(entry.getKey()).append('=').append(entry.getValue());
			if (it.hasNext()) {
				queryStringBuilder.append('&');
			}
		}
		return queryStringBuilder.toString();
	}

	/**
	 * url 编码
	 * @param s
	 * @param enc 默认UTF-8
	 * @return string
	 */
	public static String encodeUrl(String s, String... enc) {
		try {
			if (isEmpty(enc)) {
				enc = new String[] { "UTF-8" };
			}
			return java.net.URLEncoder.encode(s, enc[0]);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	/**
	 * url 解码
	 * @param s
	 * @param enc 默认UTF-8
	 * @return string
	 */
	public static String decodeUrl(String s, String... enc) {
		try {
			if (isEmpty(enc)) {
				enc = new String[] { "UTF-8" };
			}
			return java.net.URLDecoder.decode(s, enc[0]);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static String encodeUnicode(String s) {
		throw new UnsupportedOperationException();
	}

	public static String decodeUnicode(String s, String enc) {
		throw new UnsupportedOperationException();
	}

	public static String encodeBase64(final byte[] binaryData) {
		return Base64.encodeBase64String(binaryData);
	}

	public static final byte[] decodeBase64(String base64String) {
		return Base64.decodeBase64(base64String);
	}


}
