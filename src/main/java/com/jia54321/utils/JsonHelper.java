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
public class JsonHelper extends DateUtil {
	static final Logger log = LoggerFactory.getLogger(JsonHelper.class);

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

	static class EntityTimestampDeserializer extends AbstractDateDeserializer implements ObjectDeserializer  {
	    public final static EntityTimestampDeserializer INSTANCE = new EntityTimestampDeserializer();

	    @Override
		@SuppressWarnings("unchecked")
	    protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {

	        if (val == null) {
	            return null;
	        }

	        if (val instanceof java.util.Date) {
	            return (T) new java.sql.Timestamp(((Date) val).getTime());
	        }

	        if (val instanceof Number) {
	            return (T) new java.sql.Timestamp(((Number) val).longValue());
	        }

	        if (val instanceof String) {
	            String strVal = (String) val;
				if (null == strVal || strVal.length() == 0) {
					return null;
				}

				try {
					java.util.Date time = null;
					String srcTime = ( (String) strVal ).trim();
					time = DateUtil.Formatter.valueOfSourceLength(srcTime).parse(srcTime, Locale.CHINESE);

					if(null == time) {
						return null;
					}
					return (T) new Timestamp(time.getTime());
				} catch (ParseException e) {
					//log
				}

	            long longVal = Long.parseLong(strVal);
	            return (T) new java.sql.Timestamp(longVal);
	        }

	        throw new JSONException("parse error");
	    }

	    @Override
		public int getFastMatchToken() {
	        return JSONToken.LITERAL_INT;
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

	/** PARSER_CONFIG */
	public static final ParserConfig PARSER_CONFIG = ParserConfig.getGlobalInstance();
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

        PARSER_CONFIG.putDeserializer(Timestamp.class, new EntityTimestampDeserializer());
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
		final Object[] objects = cast(jsonParams, new Type[]{paramsType});
		return (T) objects[0];
    }

	@SuppressWarnings("unchecked")
	public static  <K, V> LinkedHashMap<K, V> fromJsonAsLinkedHashMap(String jsonParams) {
		return  (LinkedHashMap<K, V>) JSON.parseObject(jsonParams, LinkedHashMap.class, PARSER_CONFIG);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> HashMap<K, V> fromJsonAsHashMap(String jsonParams) {
		return (HashMap<K, V>) JSON.parseObject(jsonParams, HashMap.class, PARSER_CONFIG);
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
     * @param jsonParams Json字符串
     * @param paramsTypes 参数
     * @return Object[] 对象数组
     */
	public static Object[] cast(final String jsonParams, Type[] paramsTypes) {
		final Object json = JSON.parse(jsonParams);
		final boolean isJsonObject = (json instanceof JSONArray) == false;
		final Object[] objects = new Object[paramsTypes.length];
		if(null == json){
			return objects;
		}

		JSONArray jsonArray = null;
		if(!isJsonObject) {
			jsonArray = (JSONArray)json;
		}
		if (null == paramsTypes || paramsTypes.length <= 0) {

		} else if (paramsTypes.length >= 1) {
			for (int i = 0; i < paramsTypes.length; i++) {
				if(isJsonObject) {
					if(i==0) { //只有在==0的时候处理
						TypeToken<?> type = TypeToken.of(paramsTypes[i]);

						if (type.isArray()){
							type = type.getComponentType();
						}

						Object inputObjectI = null;

						if(type.isPrimitive()){
							inputObjectI = TypeUtils.cast(jsonParams, type.getRawType(), PARSER_CONFIG);
						} else if (type.getRawType() == jsonParams.getClass()) {
				        	inputObjectI = jsonParams;
						} else if(type.isSubtypeOf(Iterable.class) && type.isSubtypeOf(List.class)) {
							inputObjectI = json;
				        } else {
							//解析支持 ExtraProcessor, 支持parserConfig
							inputObjectI = JSON.parseObject(jsonParams, type.getRawType(), PARSER_CONFIG);
				        }

						if (type.isArray()){
							objects[i] = new Object[]{inputObjectI};
						} else if(type.isSubtypeOf(Iterable.class) && type.isSubtypeOf(List.class)) {
							objects[i] = Lists.newArrayList(inputObjectI);
						} else {
							objects[i] = inputObjectI;
						}
					} else {
						objects[i] = null;
					}
				} else {
					//
					TypeToken<?> type = TypeToken.of(paramsTypes[i]);

					if (type.isArray()){
						type = type.getComponentType();
					}

					Object inputObjectI = null;
					if(i < jsonArray.size()){
						Object o = jsonArray.get(i);
						if (null == o) {
							inputObjectI = null;
						} else if (type.isPrimitive()) {
							inputObjectI = TypeUtils.cast(String.valueOf(jsonArray.get(i)), type.getRawType(), PARSER_CONFIG);
						} else if (type.getRawType() == o.getClass()) {
				        		inputObjectI = o;
						} else if(type.isSubtypeOf(Iterable.class) && type.isSubtypeOf(List.class)) {
							if(paramsTypes.length == 1) {
								if(o instanceof JSONArray && jsonArray.size() == 1) {
									inputObjectI = jsonArray.get(0); // 处理 JsonHelper.fromJson(params, List.class) 兼容 容 [[]] 两种情况
								} else {
									inputObjectI = jsonArray; // 处理 JsonHelper.fromJson(params, List.class) 兼容 [] 两种情况
								}
							} else if(paramsTypes.length == jsonArray.size()){
								inputObjectI = o; // 处理 JsonHelper.fromJson(params, List.class) 情况
							}
						} else  { // is class
							if(o instanceof JSONObject){
								//解析支持 ExtraProcessor, 支持parserConfig
								inputObjectI = JSON.parseObject(o.toString(), type.getRawType(), PARSER_CONFIG);
					        } else if(o instanceof JSONArray){
					        		//TODO 解析不支持 ExtraProcessor, 不支持parserConfig
								inputObjectI = JSON.parseArray(o.toString(), type.getRawType());
					        }
				        }
					}

					if (type.isArray()) {
						objects[i] = null;
					} else if (TypeToken.of(paramsTypes[i]).isArray()) {
						if(null != inputObjectI && paramsTypes.length == 1) {
							// 处理 JsonHelper.fromJson(params, List.class) 兼容 [] 两种情况
							objects[i] = Lists.newArrayList(inputObjectI).toArray((Object[])Array.newInstance(inputObjectI.getClass(), 0));
						} else if(null != inputObjectI && paramsTypes.length == jsonArray.size()) {
							// 处理 JsonHelper.fromJson(params, List.class) 兼容 [[]] 两种情况
							objects[i] = Lists.newArrayList(inputObjectI).toArray((Object[])Array.newInstance(inputObjectI.getClass(), 0));
						}
					} else if (null != inputObjectI && type.isSubtypeOf(Iterable.class) && type.isSubtypeOf(List.class)) {
						if(paramsTypes.length == 1) {
							objects[i] = Lists.newArrayList((Iterable<?>)inputObjectI); // 处理 JsonHelper.fromJson(params, List.class) 兼容 [] 两种情况
						} else if(paramsTypes.length == jsonArray.size()){
							objects[i] = Lists.newArrayList(inputObjectI); // 处理 JsonHelper.fromJson(params, List.class) 兼容 [[]] 两种情况
						}

						//objects[0] = JSON.parseArray(jsonParams, type.getRawType());
					} else {
						objects[i] = inputObjectI;
					}
				}
			}
		}

		return objects;
    }

    /**
     * 对象互转
     * @param jsonParams
     * @param paramsType
     * @return Object 泛型对象
     */
	@SuppressWarnings("unchecked")
	public static <T> T  cast(String jsonParams,  Class<T> paramsType) {
		final Object[] objects = cast(jsonParams, new Type[]{paramsType});
		return (T) objects[0];
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
	 * 切分总数量
	 * @param splitNum  切分数量
	 * @param list      总列表
	 * @param <T>       实体类型
	 * @return          子列表
	 */
	public static <T> List<List<T>> toSplitList(int splitNum, List<T> list) {
		List<List<T>> splitList = new LinkedList<>();
		// groupFlag >= 1
		int groupFlag = list.size() % splitNum == 0 ? (list.size() / splitNum) : (list.size() / splitNum + 1);
		for (int j = 1; j <= groupFlag; j++) {
			if ((j * splitNum) <= list.size()) {
				splitList.add(list.subList(j * splitNum - splitNum, j * splitNum));
			} else if ((j * splitNum) > list.size()) {
				splitList.add(list.subList(j * splitNum - splitNum, list.size()));
			}
		}
		return splitList;
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
	 * 文件大小 自动转换 B KB MB GB
	 * @param size 文件大小
	 * @return 文件大小
	 */
	public static String toFilePrintSize(long size) {
		// 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		double value = (double) size;
		if (value < 1024) {
			return String.valueOf(value) + "B";
		} else {

			value = BigDecimal.valueOf( value / 1024 ).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// 因为还没有到达要使用另一个单位的时候
		// 接下去以此类推
		if (value < 1024) {
			return String.valueOf(value) + "KB";
		} else {
			value = BigDecimal.valueOf( value / 1024 ).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		}
		if (value < 1024) {
			return String.valueOf(value) + "MB";
		} else {
			// 否则如果要以GB为单位的，先除于1024再作同样的处理
			value = BigDecimal.valueOf( value / 1024 ).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
			return String.valueOf(value) + "GB";
		}
	}

    /**
     * <p>可判断字符是否为空  , 以下字符都将判断为空：</p>
     * <pre>
     *   null , "" , "null", "(null)", "undefined"
     * </pre>
     * <p>可判断java.util.Map是否为空 </p>
     * <p>可判断java.util.Collection是否为空 </p>
	 * @param o input object
     * @return true or false
     */
    public static boolean isEmpty(Object o) {
		if(null == o || "".equals(o)) {
			return true;
		}
		if("null".equals(o)  || "(null)".equals(o) || "undefined".equals(o) ) {
			return true;
		}
		if(o instanceof Collection) {
			return ((Collection<?>)o).isEmpty();
		}
		if(o instanceof Map) {
			return ((Map<?,?>)o).isEmpty();
		}

		// 数组元素是否为空判断
		if (o instanceof Object[]) {
			return ((Object[]) o).length == 0;
		} else if (o instanceof boolean[]) {
			return ((boolean[]) o).length == 0;
		} else if (o instanceof byte[]) {
			return ((byte[]) o).length == 0;
		} else if (o instanceof char[]) {
			return ((char[]) o).length == 0;
		} else if (o instanceof double[]) {
			return ((double[]) o).length == 0;
		} else if (o instanceof float[]) {
			return ((float[]) o).length == 0;
		} else if (o instanceof int[]) {
			return ((int[]) o).length == 0;
		} else if (o instanceof long[]) {
			return ((long[]) o).length == 0;
		} else if (o instanceof short[]) {
			return ((short[]) o).length == 0;
		}
		return false;
	}

	public static boolean isNotEmpty(Object o) {
    	return ! isEmpty(o);
	}

    /**
     * <p>Convert a <code>String</code> to an <code>int</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, the default value is returned.</p>
     *
     * <pre>
     *   NumberUtils.toInt(null, 1) = 1
     *   NumberUtils.toInt("", 1)   = 1
     *   NumberUtils.toInt("1", 0)  = 1
     * </pre>
     *
     * @param o  the string to convert, may be null
     * @param defVal  the default value
     * @return the int represented by the string, or the default if conversion fails
     */
	public static int toInt(Object o, int defVal) {
		if (o == null) {
			return defVal;
		}
		if (o instanceof String) {
			try {
				return Integer.parseInt((String) o);
			} catch (final NumberFormatException nfe) {
				return defVal;
			}
		}
		if (o instanceof byte[]) {
			byte[] res = (byte[]) o;
			if (res.length == 4) {
				return (res[0] & 0x00ff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);
			}
		}
		if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		return defVal;
	}

	public static Long toLong(Object o, Long defVal) {
		String str = String.valueOf(o);
        if (str == null) {
            return defVal;
        }
        try {
            return Long.parseLong(str);
        } catch (final NumberFormatException nfe) {
            return defVal;
        }
	}

	public static Float toFloat(Object o, Float defVal) {
		String str = String.valueOf(o);
		if (str == null) {
			return defVal;
		}
		try {
			return Float.parseFloat(str);
		} catch (final NumberFormatException nfe) {
			return defVal;
		}
	}

	public static Double toDouble(Object o, Double defVal) {
		String str = String.valueOf(o);
        if (str == null) {
            return defVal;
        }
        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException nfe) {
            return defVal;
        }
	}

	/**
	 * 整数
	 * @param o object
	 * @return string
	 */
	public static String toHexString(Object o) {
		return toRadixString(o, 16);
	}

	/**
	 * 整数,最大36进制
	 * @param o
	 * @return string
	 */
	public static String toRadixString(Object o, int radix) {
		BigDecimal n = toBigDecimal(o, null);
		if (null == n) {
			return null;
		}
		return Long.toString(n.longValue(), radix).toUpperCase();
	}

	/**
     * 对象转化为 BigDecimal
     * @param o
     * @param defVal
     * @return BigDecimal
	 */
	public static BigDecimal toBigDecimal(Object o, BigDecimal defVal) {
		BigDecimal val = defVal;
		if (o == null) {
			return val;
		}
		try {
			val = new BigDecimal(String.valueOf(o));
		} catch (Exception e) {
			val = defVal;
		}
		return val;
	}

	/**
     * <p>以下字符都将被默认值<code>defVal<、code>取代：</p>
     * <pre>
     *   null , "" , "null", "(null)", "undefined"
     * </pre>
     * <pre>
     *   支持获取 javax.servlet.ServletRequest.getInputStream（utf-8）的字符串
     * </pre>
     * <pre>
     *   支持获取 InputStream（utf-8）的字符串
     * </pre>
	 * @param o  输入
	 * @param defVal
	 * @return string
	 * @see JsonHelper
	 */
	public static String toStr(Object o, String defVal) {
		return toStr(o, defVal, "utf-8");
	}

	private static String toStr(Object o, String defVal, String chartset) {
		if (isEmpty(o)) {
			return defVal;
		}
//		if(o instanceof javax.servlet.ServletRequest) {
//			try {
//				return toStr(((javax.servlet.ServletRequest) o).getInputStream(), defVal, chartset);
//			} catch (Exception e) {
//				// log
//				if (log.isDebugEnabled()) {
//					log.debug("解析ServletRequest.getInputStream的字符串失败", e);
//				}
//				return defVal;
//			}
//		}
		if(o instanceof InputStream) {
			InputStream in = (InputStream) o;
			List<Byte> byteList = new LinkedList<>();
			ReadableByteChannel channel = null;
			try {
				channel = Channels.newChannel(in);
				Byte[] bytes = new Byte[0];
				ByteBuffer byteBuffer = ByteBuffer.allocate(9600);
				while (channel.read(byteBuffer) != -1) {
					byteBuffer.flip();// 为读取做好准备
					while (byteBuffer.hasRemaining()) {
						// builder.append((char)byteBuffer.get());
						byteList.add(byteBuffer.get());
					}
					byteBuffer.clear();// 为下一次写入做好准备
				}
				bytes = byteList.toArray(new Byte[byteList.size()]);
				byte[] bytes1 = new byte[bytes.length];

				for (int i = 0; i < bytes.length; i++) {
					bytes1[i] = bytes[i].byteValue();
				}

				return toStr(new String(bytes1, chartset), defVal, chartset);
			} catch (Exception e) {
				// log
				if (log.isDebugEnabled()) {
					log.debug("解析InputStream的字符串失败", e);
				}
				return defVal;
			} finally {
				try {
					if( null != channel) {
						channel.close();
					}
				} catch (IOException e) {
				}
			}
		}
		return String.valueOf(o);
	}

	/**
	 * <p>处理String分隔符，转化为Set</p>
	 * @param o 对象
	 * @param separator 分隔符
	 * @return LinkedHashSet
	 */
	public static LinkedHashSet<String> toSplitAsLinkedHashSet(Object o, String separator) {
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		String[] values = toStr(o, "").split(separator);
		for (String v: values ) {
			result.add(toStr(v, "").trim());
        }
		return result;
//		return  Sets.newLinkedHashSet(Splitter.on(separator).trimResults().splitToList(toStr(o, "")));
	}

	/**
     * <p>以下字符都将被默认值<code>defVal<、code>取代：</p>
     * <pre>
     *   null , "" , "null", "(null)", "undefined"
     * </pre>
	 * @param o  输入
	 * @param defVal 默认值
	 * @return boolean
	 */
	public static boolean toBoolean(Object o, Boolean defVal) {
		return Boolean.valueOf(toStr(o, defVal.toString())).booleanValue();
	}


	//---------------------------------------------------------------------
	// Convenience methods for working with String arrays
	//---------------------------------------------------------------------

	/**
	 * Append the given String to the given String array, returning a new array
	 * consisting of the input array contents plus the given String.
	 * @param array the array to append to (can be {@code null})
	 * @param str the String to append
	 * @return the new array (never {@code null})
	 */
	public static String[] addStringToArray(String[] array, String str) {
		if (isEmpty(array)) {
			return new String[] {str};
		}
		String[] newArr = new String[array.length + 1];
		System.arraycopy(array, 0, newArr, 0, array.length);
		newArr[array.length] = str;
		return newArr;
	}

	/**
	 * Concatenate the given String arrays into one,
	 * with overlapping array elements included twice.
	 * <p>The order of elements in the original arrays is preserved.
	 * @param array1 the first array (can be {@code null})
	 * @param array2 the second array (can be {@code null})
	 * @return the new array ({@code null} if both given arrays were {@code null})
	 */
	public static String[] concatenateStringArrays(String[] array1, String[] array2) {
		if (isEmpty(array1)) {
			return array2;
		}
		if (isEmpty(array2)) {
			return array1;
		}
		String[] newArr = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, newArr, 0, array1.length);
		System.arraycopy(array2, 0, newArr, array1.length, array2.length);
		return newArr;
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
			prefix = "";
		}
		if(jsonStringOrMap instanceof String) {
			LinkedHashMap<String, Object> orignMap = fromJsonAsLinkedHashMap((String) jsonStringOrMap);
			for (Map.Entry<String,Object> param : orignMap.entrySet()) {
				if ("".equals(prefix) || param.getKey().startsWith(prefix)) {
					String unprefixed = param.getKey().substring(prefix.length());
					params.put(unprefixed, param.getValue());
				}
			}
		} else if(jsonStringOrMap instanceof Map) {
			for (Map.Entry<String,Object> param : ((Map<String, Object>)jsonStringOrMap).entrySet()) {
				if ("".equals(prefix) || param.getKey().startsWith(prefix)) {
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
						if ("".equals(prefix) || param.getKey().startsWith(prefix)) {
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
			return "";
		}

		if (prefix == null) {
			prefix = "";
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

//	public static void main(String[] args) {
//		System.out.println(JsonHelper.toTimeString("1984-01-27", "yy"));
//		List  lst =
//		JsonHelper.fromJson("[[{\"menuIds\":\"800616469086998528\",\"superIds\":\"800616397846745088\",\"menuName\":\"车辆里程记录\",\"menuAlias\":\"\"},{\"menuIds\":\"800616397846745088\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆里程统计\",\"menuAlias\":\"\"},{\"menuIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"superIds\":\"0\",\"menuName\":\"车辆管理\",\"menuAlias\":\"\"},{\"menuIds\":\"2b5b17b8-d553-484b-9bce-72d5326008c0\",\"superIds\":\"0\",\"menuName\":\"流程发起\",\"menuAlias\":\"\"},{\"menuIds\":\"58097e7d396fd7a274763565\",\"superIds\":\"2b5b17b8-d553-484b-9bce-72d5326008c0\",\"menuName\":\"行政人事流程\",\"menuAlias\":\"\"},{\"menuIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"superIds\":\"0\",\"menuName\":\"流程审批\",\"menuAlias\":\"\"},{\"menuIds\":\"75dff22a-9ca9-41a0-ad48-3002752201a9\",\"superIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"menuName\":\"待办\",\"menuAlias\":\"\"},{\"menuIds\":\"2de77a09-251c-4efe-ad62-bf1a898b63a2\",\"superIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"menuName\":\"已办\",\"menuAlias\":\"\"},{\"menuIds\":\"bfcf5347-98fe-4df2-9e56-a7f892ef0c0a\",\"superIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"menuName\":\"办结\",\"menuAlias\":\"\"},{\"menuIds\":\"111b8a14-fc4f-4034-a74a-c7ad6b99295d\",\"superIds\":\"0\",\"menuName\":\"公告管理\",\"menuAlias\":\"\"},{\"menuIds\":\"f741ebed-c325-403d-b046-b0556739c8dd\",\"superIds\":\"111b8a14-fc4f-4034-a74a-c7ad6b99295d\",\"menuName\":\"公告浏览\",\"menuAlias\":\"\"},{\"menuIds\":\"effb359b-0b81-4c7a-b07b-e1a21db5a31a\",\"superIds\":\"0\",\"menuName\":\"知识中心\",\"menuAlias\":\"\"},{\"menuIds\":\"7b440295-f771-4f75-8584-ff6ea202f3d1\",\"superIds\":\"effb359b-0b81-4c7a-b07b-e1a21db5a31a\",\"menuName\":\"公开知识库\",\"menuAlias\":\"\"},{\"menuIds\":\"d9ba5f09-33ef-46d2-9c48-7688fee3f490\",\"superIds\":\"effb359b-0b81-4c7a-b07b-e1a21db5a31a\",\"menuName\":\"私有知识库\",\"menuAlias\":\"\"},{\"menuIds\":\"0ee8c936-6900-449f-83f7-381af3604745\",\"superIds\":\"0\",\"menuName\":\"工作周报\",\"menuAlias\":\"\"},{\"menuIds\":\"795886106095783936\",\"superIds\":\"0ee8c936-6900-449f-83f7-381af3604745\",\"menuName\":\"我的周报\",\"menuAlias\":\"\"},{\"menuIds\":\"798124019558780928\",\"superIds\":\"0ee8c936-6900-449f-83f7-381af3604745\",\"menuName\":\"他人周报\",\"menuAlias\":\"\"},{\"menuIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"superIds\":\"0\",\"menuName\":\"会议管理\",\"menuAlias\":\"\"},{\"menuIds\":\"7cc44bb8-928d-42c7-ade0-9316b28f38a0\",\"superIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"menuName\":\"会议室使用申请\",\"menuAlias\":\"\"},{\"menuIds\":\"5845eb09-d8f1-40ce-961f-b7a32e541a71\",\"superIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"menuName\":\"会议浏览\",\"menuAlias\":\"\"},{\"menuIds\":\"795524081805037568\",\"superIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"menuName\":\"会议室登记管理\",\"menuAlias\":\"\"},{\"menuIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"superIds\":\"0\",\"menuName\":\"车辆管理\",\"menuAlias\":\"\"},{\"menuIds\":\"6aade599-274d-422e-a73c-936e41846a2c\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆管理\",\"menuAlias\":\"\"},{\"menuIds\":\"348a0fc3-e14f-423a-89f7-0423f62813a4\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"司机管理\",\"menuAlias\":\"\"},{\"menuIds\":\"794808201714470912\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆使用登记\",\"menuAlias\":\"\"},{\"menuIds\":\"800616990241853440\",\"superIds\":\"800616397846745088\",\"menuName\":\"车辆里程汇总\",\"menuAlias\":\"\"},{\"menuIds\":\"800617170206855168\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆油料统计\",\"menuAlias\":\"\"},{\"menuIds\":\"800617432417964032\",\"superIds\":\"800617170206855168\",\"menuName\":\"车辆油料记录\",\"menuAlias\":\"\"},{\"menuIds\":\"800617579487039488\",\"superIds\":\"800617170206855168\",\"menuName\":\"车辆油料汇总\",\"menuAlias\":\"\"},{\"menuIds\":\"cc984bc9-02d2-4877-ad04-057eca5a3e3b\",\"superIds\":\"0\",\"menuName\":\"个人日程\",\"menuAlias\":\"\"},{\"menuIds\":\"c9d61f45-d588-428a-ba0d-fa50571275ea\",\"superIds\":\"cc984bc9-02d2-4877-ad04-057eca5a3e3b\",\"menuName\":\"日程查看\",\"menuAlias\":\"\"},{\"menuIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"superIds\":\"0\",\"menuName\":\"考勤管理\",\"menuAlias\":\"\"},{\"menuIds\":\"7ed90622-dce4-471d-8df9-7e5fa195b6ee\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"考勤组设置\",\"menuAlias\":\"\"},{\"menuIds\":\"5822ae68-b7f9-4f3d-b5a9-0e1286d13689\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"我的考勤\",\"menuAlias\":\"\"},{\"menuIds\":\"d292b106-cf38-4a80-924b-3b00b6aac280\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"考勤汇总\",\"menuAlias\":\"\"},{\"menuIds\":\"e45350da-1111-49f6-827b-dab982ab30ae\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"考勤导入\",\"menuAlias\":\"\"}]]", List.class);
//	    System.out.println(lst.size());
//	    lst =
//	    		JsonHelper.fromJson("[{\"menuIds\":\"800616469086998528\",\"superIds\":\"800616397846745088\",\"menuName\":\"车辆里程记录\",\"menuAlias\":\"\"},{\"menuIds\":\"800616397846745088\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆里程统计\",\"menuAlias\":\"\"},{\"menuIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"superIds\":\"0\",\"menuName\":\"车辆管理\",\"menuAlias\":\"\"},{\"menuIds\":\"2b5b17b8-d553-484b-9bce-72d5326008c0\",\"superIds\":\"0\",\"menuName\":\"流程发起\",\"menuAlias\":\"\"},{\"menuIds\":\"58097e7d396fd7a274763565\",\"superIds\":\"2b5b17b8-d553-484b-9bce-72d5326008c0\",\"menuName\":\"行政人事流程\",\"menuAlias\":\"\"},{\"menuIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"superIds\":\"0\",\"menuName\":\"流程审批\",\"menuAlias\":\"\"},{\"menuIds\":\"75dff22a-9ca9-41a0-ad48-3002752201a9\",\"superIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"menuName\":\"待办\",\"menuAlias\":\"\"},{\"menuIds\":\"2de77a09-251c-4efe-ad62-bf1a898b63a2\",\"superIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"menuName\":\"已办\",\"menuAlias\":\"\"},{\"menuIds\":\"bfcf5347-98fe-4df2-9e56-a7f892ef0c0a\",\"superIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"menuName\":\"办结\",\"menuAlias\":\"\"},{\"menuIds\":\"111b8a14-fc4f-4034-a74a-c7ad6b99295d\",\"superIds\":\"0\",\"menuName\":\"公告管理\",\"menuAlias\":\"\"},{\"menuIds\":\"f741ebed-c325-403d-b046-b0556739c8dd\",\"superIds\":\"111b8a14-fc4f-4034-a74a-c7ad6b99295d\",\"menuName\":\"公告浏览\",\"menuAlias\":\"\"},{\"menuIds\":\"effb359b-0b81-4c7a-b07b-e1a21db5a31a\",\"superIds\":\"0\",\"menuName\":\"知识中心\",\"menuAlias\":\"\"},{\"menuIds\":\"7b440295-f771-4f75-8584-ff6ea202f3d1\",\"superIds\":\"effb359b-0b81-4c7a-b07b-e1a21db5a31a\",\"menuName\":\"公开知识库\",\"menuAlias\":\"\"},{\"menuIds\":\"d9ba5f09-33ef-46d2-9c48-7688fee3f490\",\"superIds\":\"effb359b-0b81-4c7a-b07b-e1a21db5a31a\",\"menuName\":\"私有知识库\",\"menuAlias\":\"\"},{\"menuIds\":\"0ee8c936-6900-449f-83f7-381af3604745\",\"superIds\":\"0\",\"menuName\":\"工作周报\",\"menuAlias\":\"\"},{\"menuIds\":\"795886106095783936\",\"superIds\":\"0ee8c936-6900-449f-83f7-381af3604745\",\"menuName\":\"我的周报\",\"menuAlias\":\"\"},{\"menuIds\":\"798124019558780928\",\"superIds\":\"0ee8c936-6900-449f-83f7-381af3604745\",\"menuName\":\"他人周报\",\"menuAlias\":\"\"},{\"menuIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"superIds\":\"0\",\"menuName\":\"会议管理\",\"menuAlias\":\"\"},{\"menuIds\":\"7cc44bb8-928d-42c7-ade0-9316b28f38a0\",\"superIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"menuName\":\"会议室使用申请\",\"menuAlias\":\"\"},{\"menuIds\":\"5845eb09-d8f1-40ce-961f-b7a32e541a71\",\"superIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"menuName\":\"会议浏览\",\"menuAlias\":\"\"},{\"menuIds\":\"795524081805037568\",\"superIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"menuName\":\"会议室登记管理\",\"menuAlias\":\"\"},{\"menuIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"superIds\":\"0\",\"menuName\":\"车辆管理\",\"menuAlias\":\"\"},{\"menuIds\":\"6aade599-274d-422e-a73c-936e41846a2c\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆管理\",\"menuAlias\":\"\"},{\"menuIds\":\"348a0fc3-e14f-423a-89f7-0423f62813a4\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"司机管理\",\"menuAlias\":\"\"},{\"menuIds\":\"794808201714470912\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆使用登记\",\"menuAlias\":\"\"},{\"menuIds\":\"800616990241853440\",\"superIds\":\"800616397846745088\",\"menuName\":\"车辆里程汇总\",\"menuAlias\":\"\"},{\"menuIds\":\"800617170206855168\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆油料统计\",\"menuAlias\":\"\"},{\"menuIds\":\"800617432417964032\",\"superIds\":\"800617170206855168\",\"menuName\":\"车辆油料记录\",\"menuAlias\":\"\"},{\"menuIds\":\"800617579487039488\",\"superIds\":\"800617170206855168\",\"menuName\":\"车辆油料汇总\",\"menuAlias\":\"\"},{\"menuIds\":\"cc984bc9-02d2-4877-ad04-057eca5a3e3b\",\"superIds\":\"0\",\"menuName\":\"个人日程\",\"menuAlias\":\"\"},{\"menuIds\":\"c9d61f45-d588-428a-ba0d-fa50571275ea\",\"superIds\":\"cc984bc9-02d2-4877-ad04-057eca5a3e3b\",\"menuName\":\"日程查看\",\"menuAlias\":\"\"},{\"menuIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"superIds\":\"0\",\"menuName\":\"考勤管理\",\"menuAlias\":\"\"},{\"menuIds\":\"7ed90622-dce4-471d-8df9-7e5fa195b6ee\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"考勤组设置\",\"menuAlias\":\"\"},{\"menuIds\":\"5822ae68-b7f9-4f3d-b5a9-0e1286d13689\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"我的考勤\",\"menuAlias\":\"\"},{\"menuIds\":\"d292b106-cf38-4a80-924b-3b00b6aac280\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"考勤汇总\",\"menuAlias\":\"\"},{\"menuIds\":\"e45350da-1111-49f6-827b-dab982ab30ae\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"考勤导入\",\"menuAlias\":\"\"}]", List.class);
//	    	    System.out.println(lst.size());
//
//	    	    lst =
//	    	    		JsonHelper.fromJson("{\"menuIds\":\"800616469086998528\",\"superIds\":\"800616397846745088\",\"menuName\":\"车辆里程记录\",\"menuAlias\":\"\"}", List.class);
//	    	    	    System.out.println(lst.size());
//
//
//	    	 Object o =   		JsonHelper.fromJson("[\"app.captcha.enabled\"]", String.class);
//	    	 System.out.println(o);
//
//	String json = "[{\"menuIds\":\"800616469086998528\",\"superIds\":\"800616397846745088\",\"menuName\":\"车辆里程记录\",\"menuAlias\":\"\"},{\"menuIds\":\"800616397846745088\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆里程统计\",\"menuAlias\":\"\"},{\"menuIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"superIds\":\"0\",\"menuName\":\"车辆管理\",\"menuAlias\":\"\"},{\"menuIds\":\"2b5b17b8-d553-484b-9bce-72d5326008c0\",\"superIds\":\"0\",\"menuName\":\"流程发起\",\"menuAlias\":\"\"},{\"menuIds\":\"58097e7d396fd7a274763565\",\"superIds\":\"2b5b17b8-d553-484b-9bce-72d5326008c0\",\"menuName\":\"行政人事流程\",\"menuAlias\":\"\"},{\"menuIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"superIds\":\"0\",\"menuName\":\"流程审批\",\"menuAlias\":\"\"},{\"menuIds\":\"75dff22a-9ca9-41a0-ad48-3002752201a9\",\"superIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"menuName\":\"待办\",\"menuAlias\":\"\"},{\"menuIds\":\"2de77a09-251c-4efe-ad62-bf1a898b63a2\",\"superIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"menuName\":\"已办\",\"menuAlias\":\"\"},{\"menuIds\":\"bfcf5347-98fe-4df2-9e56-a7f892ef0c0a\",\"superIds\":\"4b11924e-7db2-4639-ab37-e757e51d6075\",\"menuName\":\"办结\",\"menuAlias\":\"\"},{\"menuIds\":\"111b8a14-fc4f-4034-a74a-c7ad6b99295d\",\"superIds\":\"0\",\"menuName\":\"公告管理\",\"menuAlias\":\"\"},{\"menuIds\":\"f741ebed-c325-403d-b046-b0556739c8dd\",\"superIds\":\"111b8a14-fc4f-4034-a74a-c7ad6b99295d\",\"menuName\":\"公告浏览\",\"menuAlias\":\"\"},{\"menuIds\":\"effb359b-0b81-4c7a-b07b-e1a21db5a31a\",\"superIds\":\"0\",\"menuName\":\"知识中心\",\"menuAlias\":\"\"},{\"menuIds\":\"7b440295-f771-4f75-8584-ff6ea202f3d1\",\"superIds\":\"effb359b-0b81-4c7a-b07b-e1a21db5a31a\",\"menuName\":\"公开知识库\",\"menuAlias\":\"\"},{\"menuIds\":\"d9ba5f09-33ef-46d2-9c48-7688fee3f490\",\"superIds\":\"effb359b-0b81-4c7a-b07b-e1a21db5a31a\",\"menuName\":\"私有知识库\",\"menuAlias\":\"\"},{\"menuIds\":\"0ee8c936-6900-449f-83f7-381af3604745\",\"superIds\":\"0\",\"menuName\":\"工作周报\",\"menuAlias\":\"\"},{\"menuIds\":\"795886106095783936\",\"superIds\":\"0ee8c936-6900-449f-83f7-381af3604745\",\"menuName\":\"我的周报\",\"menuAlias\":\"\"},{\"menuIds\":\"798124019558780928\",\"superIds\":\"0ee8c936-6900-449f-83f7-381af3604745\",\"menuName\":\"他人周报\",\"menuAlias\":\"\"},{\"menuIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"superIds\":\"0\",\"menuName\":\"会议管理\",\"menuAlias\":\"\"},{\"menuIds\":\"7cc44bb8-928d-42c7-ade0-9316b28f38a0\",\"superIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"menuName\":\"会议室使用申请\",\"menuAlias\":\"\"},{\"menuIds\":\"5845eb09-d8f1-40ce-961f-b7a32e541a71\",\"superIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"menuName\":\"会议浏览\",\"menuAlias\":\"\"},{\"menuIds\":\"795524081805037568\",\"superIds\":\"a98d7371-6cc7-4bdb-b911-24210b972826\",\"menuName\":\"会议室登记管理\",\"menuAlias\":\"\"},{\"menuIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"superIds\":\"0\",\"menuName\":\"车辆管理\",\"menuAlias\":\"\"},{\"menuIds\":\"6aade599-274d-422e-a73c-936e41846a2c\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆管理\",\"menuAlias\":\"\"},{\"menuIds\":\"348a0fc3-e14f-423a-89f7-0423f62813a4\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"司机管理\",\"menuAlias\":\"\"},{\"menuIds\":\"794808201714470912\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆使用登记\",\"menuAlias\":\"\"},{\"menuIds\":\"800616990241853440\",\"superIds\":\"800616397846745088\",\"menuName\":\"车辆里程汇总\",\"menuAlias\":\"\"},{\"menuIds\":\"800617170206855168\",\"superIds\":\"284f96b6-7ea0-4113-bb0b-c4cd4a331a12\",\"menuName\":\"车辆油料统计\",\"menuAlias\":\"\"},{\"menuIds\":\"800617432417964032\",\"superIds\":\"800617170206855168\",\"menuName\":\"车辆油料记录\",\"menuAlias\":\"\"},{\"menuIds\":\"800617579487039488\",\"superIds\":\"800617170206855168\",\"menuName\":\"车辆油料汇总\",\"menuAlias\":\"\"},{\"menuIds\":\"cc984bc9-02d2-4877-ad04-057eca5a3e3b\",\"superIds\":\"0\",\"menuName\":\"个人日程\",\"menuAlias\":\"\"},{\"menuIds\":\"c9d61f45-d588-428a-ba0d-fa50571275ea\",\"superIds\":\"cc984bc9-02d2-4877-ad04-057eca5a3e3b\",\"menuName\":\"日程查看\",\"menuAlias\":\"\"},{\"menuIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"superIds\":\"0\",\"menuName\":\"考勤管理\",\"menuAlias\":\"\"},{\"menuIds\":\"7ed90622-dce4-471d-8df9-7e5fa195b6ee\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"考勤组设置\",\"menuAlias\":\"\"},{\"menuIds\":\"5822ae68-b7f9-4f3d-b5a9-0e1286d13689\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"我的考勤\",\"menuAlias\":\"\"},{\"menuIds\":\"d292b106-cf38-4a80-924b-3b00b6aac280\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"考勤汇总\",\"menuAlias\":\"\"},{\"menuIds\":\"e45350da-1111-49f6-827b-dab982ab30ae\",\"superIds\":\"4c8f1aed-c2cf-41c7-8421-25113fcb83f8\",\"menuName\":\"考勤导入\",\"menuAlias\":\"\"}]"	;
//	List<Map<String, Object>>  aa = JsonHelper.fromJsonToMapList(json);
//	System.out.println(aa);
//	}
}
