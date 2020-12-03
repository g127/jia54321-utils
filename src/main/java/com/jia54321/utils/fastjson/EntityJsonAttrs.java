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
package com.jia54321.utils.fastjson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;

import com.alibaba.fastjson.parser.deserializer.ExtraProcessable;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeBeanInfo;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.JavaBeanInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.collect.Maps;
import com.jia54321.utils.JsonHelper;

/**
 * 比较慢
 * @author 郭罡
 */
@SuppressWarnings("unchecked") 
public class EntityJsonAttrs implements JSONSerializable, ExtraProcessable {
   /** */
	private Map<String, Object> attrs = Maps.newLinkedHashMap();
	
	public Map<String, Object> attrs(Map<String, Object>... attrsArrays) {
    	if (null != attrsArrays && attrsArrays.length >= 1) {
			this.attrs.putAll(attrsArrays[0]);
		}
    	return this.attrs;
    }

    public Object attr(Object name, Object... val) {
		if (null == val) {
			return attrs.get(name);
		} else if (val.length == 1) {
			return attrs.put(String.valueOf(name), val[0]);
		} else {
			return attrs.put(String.valueOf(name), val);
			//return attributes.put(String.valueOf(name), Joiner.on(',').skipNulls().join(val));
		}
    }


	@Override
	public void processExtra(String key, Object value) {
		FieldInfo[] setFieldInfos = JavaBeanInfo.build(this.getClass(), this.getClass(), null).fields;
		boolean isJavaBeanPropWrote = false;
		for (FieldInfo field : setFieldInfos) {
			if(field.name.equals(key)){
				try {
					field.method.invoke(this, TypeUtils.cast(value, field.fieldClass, JsonHelper.PARSER_CONFIG));
					isJavaBeanPropWrote = true;
					break;
				} catch (IllegalAccessException e) {
				} catch (IllegalArgumentException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}
		if(!isJavaBeanPropWrote){
			this.attrs.put(key, value); // 定制反序列化
		}
	}

	@Override
	public void write(JSONSerializer serializer, Object fieldName,
			Type fieldType, int features) throws IOException {
		FieldInfo[] getFieldInfos = null;
		if (null == getFieldInfos) {
			Field getFields;
			try {
				SerializeBeanInfo serializeBeanInfo = TypeUtils.buildBeanInfo(this.getClass(), null, null);
				getFields = serializeBeanInfo.getClass().getDeclaredField("fields");
				getFields.setAccessible(true);
				getFieldInfos = (FieldInfo[])getFields.get(serializeBeanInfo);
			} catch (IllegalArgumentException | IllegalAccessException e) {
			} catch (NoSuchFieldException e) {
			} catch (SecurityException e) {
			}

		}
		
		Map<String, Object> newAttrs = Maps.newLinkedHashMap(attrs);
		
		for (FieldInfo field : getFieldInfos) {
			try {
				Object val = field.method.invoke(this);
				newAttrs.put(field.name, val);
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
		}
		serializer.write(newAttrs); // 定制序列化
	}

}
