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


import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * @author 郭罡
 * @create 2019-08-28
 */
public class Kv extends LinkedCaseInsensitiveMap<Object> {
    /** . */
	private static final long serialVersionUID = -2640190479356003458L;

	private Kv() {

    }

    /**
     * 创建Kv
     *
     * @return Kv
     */
    public static Kv init() {
        return new Kv();
    }

    @SuppressWarnings("rawtypes")
	public static HashMap newMap() {
        return new HashMap<>(16);
    }

    /**
     * 设置列
     *
     * @param attr  属性
     * @param value 值
     * @return 本身
     */
    public Kv set(String attr, Object value) {
        this.put(attr, value);
        return this;
    }

    /**
     * 设置列，当键或值为null时忽略
     *
     * @param attr  属性
     * @param value 值
     * @return 本身
     */
    public Kv setIgnoreNull(String attr, Object value) {
        if (null != attr && null != value) {
            set(attr, value);
        }
        return this;
    }

    public Object getObj(String key) {
        return super.get(key);
    }

    /**
     * 获得特定类型值
     *
     * @param <T>          值类型
     * @param attr         字段名
     * @param defaultValue 默认值
     * @return 字段值
     */
    @SuppressWarnings("unchecked")
	public <T> T get(String attr, T defaultValue) {
        final Object result = get(attr);
        return (T) (result != null ? result : defaultValue);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public String getStr(String attr) {
        return JsonHelper.toStr(get(attr), null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Integer getInt(String attr) {
        return JsonHelper.toInt(get(attr), -1);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Long getLong(String attr) {
        return JsonHelper.toLong(get(attr), 0L);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Float getFloat(String attr) {
        return JsonHelper.toFloat(get(attr), null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Double getDouble(String attr) {
        return JsonHelper.toDouble(get(attr), null);
    }


    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Boolean getBool(String attr) {
        return JsonHelper.toBoolean(get(attr), null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public byte[] getBytes(String attr) {
        return get(attr, null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Date getDate(String attr) {
        return get(attr, null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Time getTime(String attr) {
        return get(attr, null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Timestamp getTimestamp(String attr) {
        return get(attr, null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Number getNumber(String attr) {
        return get(attr, null);
    }

    @Override
    public Kv clone() {
        return (Kv) super.clone();
    }

}