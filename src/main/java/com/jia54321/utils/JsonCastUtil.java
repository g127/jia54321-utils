package com.jia54321.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.AbstractDateDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JsonCastUtil {
    /** PARSER_CONFIG */
    public static final ParserConfig PARSER_CONFIG = ParserConfig.getGlobalInstance();
    static {
        PARSER_CONFIG.putDeserializer(Timestamp.class, new EntityTimestampDeserializer());
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
                            objects[i] = Lists.newArrayList(inputObjectI).toArray((Object[]) Array.newInstance(inputObjectI.getClass(), 0));
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


    static class EntityTimestampDeserializer extends AbstractDateDeserializer implements ObjectDeserializer {
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
}
