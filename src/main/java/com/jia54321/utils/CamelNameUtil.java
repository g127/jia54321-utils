/**
 * MIT License
 * <p>
 * Copyright (c) 2009-present GuoGang and other contributors
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.jia54321.utils;

/**
 * 驼峰命名法(CamelCase)和下划线风格(UnderScoreCase)字符串
 * 互转
 * @author 郭罡
 */
public class CamelNameUtil {
    /** 下划线 */
    public static final char UNDERLINE = '_';
    /** 连字符 */
    public static final char HYPHEN    = '-';

    /**
     * 驼峰转下划线
     * @param param
     * @return string
     */
    public static String camelToUnderline(String param) {
        return camelToXX(UNDERLINE, param);
    }

    /**
     * 驼峰转下划线
     * @param param
     * @return string
     */
    public static String camelToUnderlineUpperCase(String param) {
        return camelToUnderline(param).toUpperCase();
    }

    /**
     *  下划线转驼峰	(首字母小写)
     * @param param
     * @return string
     */
    public static String underlineToCamelLowerCase(String param) {
        return xxToCamelLowerCase(UNDERLINE, param);
    }


    /**
     *  连字符转驼峰	(首字母小写)
     * @param param
     * @return string
     */
    public static String hyphenToCamelLowerCase(String param) {
        return xxToCamelLowerCase(HYPHEN, param);
    }

    /**
     * 驼峰转xx分隔符
     * @param param
     * @return string
     */
    public static String camelToXX(char xx, String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        // 存在 xx符号，不做处理，仅处理大小写转换
        boolean existXX = param.indexOf(xx) > 0;
        int           len = param.length();
        StringBuilder sb  = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            char before = i > 0 ? param.charAt(i - 1) : xx;
            // 大写转小写： 首字母 或者  字符前一个为"分隔符号"
            if (  i == 0 || before == xx ) {
                c = Character.toLowerCase(c);
            }
            // 大写转小写： 连续两个为大写
            if( i > 0 &&  Character.isUpperCase(before) && Character.isUpperCase(c) ) {
                c = Character.toLowerCase(c);
            }
            // 大写字符时
            if (Character.isUpperCase(c) ) {
                // 仅在整个字符串中不含有分隔符时，才进行分隔符添加
                if(!existXX) {
                    sb.append(xx);
                }
                // 拼接原小写字符串
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     *  xx分隔符转驼峰	(首字母小写)
     * @param param
     * @return string
     */
    public static String xxToCamelLowerCase(char xx, String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        StringBuilder paramLowerCase = new StringBuilder(param.toLowerCase());
        int           len            = param.length();
        StringBuilder sb             = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = paramLowerCase.charAt(i);
            if (c == xx) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(paramLowerCase.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
//    /**
//     * 下划线转驼峰	(首字母小写)
//     * @param param
//     * @return
//     */
//    public static String underlineToCamelLowerCase2(String param){
//        if (param==null||"".equals(param.trim())){
//            return "";
//        }
//        StringBuilder sb=new StringBuilder(param.toLowerCase());
//        Matcher mc= Pattern.compile("_").matcher(param);
//        int i=0;
//        while (mc.find()){
//            int position=mc.end()-(i++);
//            //String.valueOf(Character.toUpperCase(sb.charAt(position)));
//            sb.replace(position-1,position+1,sb.substring(position,position+1).toUpperCase());
//        }
//        return sb.toString();
//    }

    //    public static void main(String[] args) {
    //    	System.out.println(camelToUnderlineUpperCase("createTime"));
    //    	System.out.println(underlineToCamelLowerCase("CREATE_TIME"));
    //    	System.out.println(camelToUnderlineUpperCase("CREATE_TIME"));
    //    	System.out.println(underlineToCamelLowerCase("createTime"));
    // }
}
