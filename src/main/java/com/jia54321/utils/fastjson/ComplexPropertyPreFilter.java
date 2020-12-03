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

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 去掉循环 fastjson 针对类型的属性选择过滤器(可以跨层级) 
 * @author 郭罡
 */
public class ComplexPropertyPreFilter implements PropertyPreFilter  {
    
    private Map<Class<?>, String[]> includes = new HashMap<Class<?>, String[]>();
    private Map<Class<?>, String[]> excludes = new HashMap<Class<?>, String[]>();
       
	static {
		//-------------------------------------------------------------------------
		// 禁止循环引用属性
		JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
        
		//-------------------------------------------------------------------------
//    	//  禁止循环引用属性去掉
//    	if(JSON.DEFAULT_GENERATE_FEATURE >= SerializerFeature.DisableCircularReferenceDetect.getMask()){
//    		JSON.DEFAULT_GENERATE_FEATURE -= SerializerFeature.DisableCircularReferenceDetect.getMask();
//    	}
		//-------------------------------------------------------------------------
    }
       
    public ComplexPropertyPreFilter() {
           
    }
       
    public ComplexPropertyPreFilter(Map<Class<?>, String[]> includes) {
        super();
        this.includes = includes;
    }
       
    @Override
    public boolean apply(JSONSerializer serializer, Object source, String name) {
           
        //对象为空。直接放行
        if (source == null) {
            return true;
        }
           
        // 获取当前需要序列化的对象的类对象
        Class<?> clazz = source.getClass();
           
        // 无需序列的对象、寻找需要过滤的对象，可以提高查找层级
        // 找到不需要的序列化的类型
        for (Map.Entry<Class<?>, String[]> item : this.excludes.entrySet()) {
            // isAssignableFrom()，用来判断类型间是否有继承关系
            if (item.getKey().isAssignableFrom(clazz)) {
                String[] strs = item.getValue();
                   
                // 该类型下 此 name 值无需序列化
                if (isHave(strs, name)) {
                    return false;
                }
            }
        }
           
        // 需要序列的对象集合为空 表示 全部需要序列化
        if (this.includes.isEmpty()) {
            return true;
        }
           
        // 需要序列的对象
        // 找到不需要的序列化的类型
        for (Map.Entry<Class<?>, String[]> item : this.includes.entrySet()) {
            // isAssignableFrom()，用来判断类型间是否有继承关系
            if (item.getKey().isAssignableFrom(clazz)) {
                String[] strs = item.getValue();
                // 该类型下 此 name 值无需序列化
                if (isHave(strs, name)) {
                    return true;
                }
            }
        }
           
        return false;
    }
       
    /*
     * 此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串
     */
    public static boolean isHave(String[] strs, String s) {
           
        for (int i = 0; i < strs.length; i++) {
            // 循环查找字符串数组中的每个字符串中是否包含所有查找的内容
            if (strs[i].equals(s)) {
                // 查找到了就返回真，不在继续查询
                return true;
            }
        }
           
        // 没找到返回false
        return false;
    }
       
    public Map<Class<?>, String[]> getIncludes() {
        return includes;
    }
       
    public void setIncludes(Map<Class<?>, String[]> includes) {
        this.includes = includes;
    }
       
    public Map<Class<?>, String[]> getExcludes() {
        return excludes;
    }
       
    public void setExcludes(Map<Class<?>, String[]> excludes) {
        this.excludes = excludes;
    }
       
}
