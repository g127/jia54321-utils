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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期工具类
 *
 * @author gg
 * @create 2019-07-31
 */
public class DateUtil {
	static final Logger log = LoggerFactory.getLogger(DateUtil.class);
    /**
     * <p>将  java.util.Date java.sql.Date , java.sql.Timestamp , Number(毫秒)，或 String 格式的时间  转换为 字符串显示</p>
     *
     * @param obj java.util.Date java.sql.Date , java.sql.Timestamp , Number，或 String
     * @return Long  转换失败返回  null
     */
    public static Long toTimeMillis(Object obj) {
        Date time = null;

        if (null == obj || "".equals(obj)) {
            // log
            return null;
        } else if (obj instanceof Date
                || obj instanceof java.sql.Date
                || obj instanceof java.sql.Timestamp) {
            time = (Date) obj;
        } else if (obj instanceof String) {
            String srcTime = ((String) obj).trim();
            try {
                time = Formatter.valueOfSourceLength(srcTime).parse2(srcTime, TimeZone.getDefault());

            } catch (ParseException e) {
                //log
                if (log.isDebugEnabled()) {
                    log.debug("解析时间字符串失败", e);
                }
            }
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }

        if (null == time) {
            //log
            return null;
        }

        return time.getTime();
    }

    /**
     * 返回当前时间为当年的第几周
     *
     * @return int
     */
    public static int toTimeChinaWeekOfYear(Object obj) {
        Long timeMillis = toTimeMillis(obj);
        if (null == timeMillis) {
            return -1;
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        return c.get(Calendar.WEEK_OF_YEAR) - 1;
    }

    /**
     * 将  java.util.Date java.sql.Date , java.sql.Timestamp , Number(毫秒)，或 String 格式的时间  转换为 字符串显示
     *
     * @param obj        java.util.Date java.sql.Date , java.sql.Timestamp , Number，或 String
     * @param dateFormat 格式( 可选 ) 默认 YYYY_MM_DD
     * @return String
     */
    public static String toTimeString(Object obj, String... dateFormat) {
        Long timeMillis = toTimeMillis(obj);
        if (null == timeMillis) {
            return null;
        }
        Date time = new Date(timeMillis);

		if (null == dateFormat || 0 == dateFormat.length) {
			dateFormat = new String[] { Formatter.YYYY_MM_DD.pattern };
		}

        return Formatter.valueOf(dateFormat[0]).print(time, Locale.CHINESE);
    }

    /**
     * 将  java.util.Date java.sql.Date , java.sql.Timestamp , Number(毫秒)，或 String 格式的时间  转换为 字符串显示
     *
     * @param dateFormat
     * @return string
     */
    public static String toNow(String... dateFormat) {
    	if (null == dateFormat || 0 == dateFormat.length) {
            dateFormat = new String[]{Formatter.YYYY_MM_DD_HH_MM_SS.pattern};
        }
        return toTimeString(Calendar.getInstance().getTime(), dateFormat[0]);
    }

    /**
     * 当前时间字符串 日期格式 "yyyy-MM-dd"
     *
     * @return String
     */
    public static String toNowDataString() {
        return toTimeString(Calendar.getInstance().getTime(), Formatter.YYYY_MM_DD.pattern);
    }

    /**
     * 当前时间字符串 时间格式 "yyyy-MM-dd HH:mm:ss"
     *
     * @return String
     */
    public static String toNowTimeString() {
        return toTimeString(Calendar.getInstance().getTime(), Formatter.YYYY_MM_DD_HH_MM_SS.pattern);
    }

    /**
     * 当前时间字符串 日期格式 "yyMMddHHmmss"
     *
     * @return String
     */
    public static String toNowStampString() {
        return toTimeString(Calendar.getInstance().getTime(), Formatter.YYMMDDHHMMSS.pattern);
    }

    /**
     * 当前时间字符串 日期格式 "yyyy-MM-dd"
     *
     * @param time 时间
     * @return String
     */
    public static String toDataString(Object time) {
        return toTimeString(time, Formatter.YYYY_MM_DD.pattern);
    }

    /**
     * 对象转化为时间 Date 或 Timestamp
     *
     * @param time 时间
     * @return String
     */
    public static String toTimeString(Object time) {
        return toTimeString(time, Formatter.YYYY_MM_DD_HH_MM_SS.pattern);
    }

    /**
     * 对象转化为时间  java.sql.Timestamp
     *
     * @param time 时间
     * @return Timestamp
     */
    public static java.sql.Timestamp toTimestamp(Object time) {
        Long currTime = toTimeMillis(time);
        if (null == currTime) {
            return null;
        }
        return new java.sql.Timestamp(currTime);
    }

    /**
     * 对象转化为时间 Timestamp
     *
     * @return Timestamp
     */
    public static Timestamp toNowTimestamp() {
        return toTimestamp(Calendar.getInstance().getTime());
    }



    /**
     * 时间常用表达
     */
    public static class Expressing {

        /**
         * 当前时间的最早时间00:00
         *
         * @param type 取值英文String: 【day 当天， week 当周， month 当月，year 当年， lastWeek 上周， lastMonth 上月， lastYear 上一年】
         * @param time 可不传，默认当前时间
         * @return Timestamp
         */
        public static java.sql.Timestamp toMorning(String type, Object... time) {
			if (null == time || time.length == 0) {
                // 当前时间
                time = new Date[]{Calendar.getInstance().getTime()};
            }
            Timestamp timestamp = toTimestamp(time[0]);
            if ("day".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return toTimestamp(cal.getTime());
            }
            if ("week".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            if ("month".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            if ("year".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), 0, 0, 0, 0, 0);
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
                cal.set(Calendar.HOUR_OF_DAY, 0);

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            if ("lastWeek".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                // 减去一周
                cal.add(Calendar.WEEK_OF_MONTH, -1);

                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }

            if ("lastMonth".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));

                // 减去一月
                cal.add(Calendar.MONTH, -1);

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            
            if ("lastYear".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), 0, 0, 0, 0, 0);
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));

                // 减去一年
                cal.add(Calendar.YEAR, -1);

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }

            return toTimestamp(Calendar.getInstance().getTime());
        }

        /**
         * 当前时间的最后晚上时间24:00(第二天)，如：本周最晚时间，本月最晚时间 Timestamp
         *
         * @param type 取值英文String: 【day 当天， week 当周， month 当月，year 当年， lastWeek 上周， lastMonth 上月， lastYear 上一年】
         * @param time 不传值，默认当前时间
         * @return Timestamp
         */
        public static Timestamp toNight(String type, Object... time) {
			if (null == time || time.length == 0) {
                // 当前时间
                time = new Date[]{Calendar.getInstance().getTime()};
            }
            Timestamp timestamp = toTimestamp(time[0]);
            if ("day".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(Calendar.HOUR_OF_DAY, 24);
                
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return toTimestamp(cal.getTime());
            }
            if ("week".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                cal.add(Calendar.DAY_OF_WEEK, 7);

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            if ("month".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 24);

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            if ("year".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), 0, 0, 0, 0, 0);
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
                cal.set(Calendar.HOUR_OF_DAY, 24);

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            if ("lastWeek".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                cal.add(Calendar.DAY_OF_WEEK, 7);

                // 减去一周
                cal.add(Calendar.WEEK_OF_MONTH, -1);

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            if ("lastMonth".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 24);

                // 减去一月
                cal.add(Calendar.MONTH, -1);

                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            
            if ("lastYear".equalsIgnoreCase(type)) {
                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
                cal.setTime(timestamp);
                cal.set(cal.get(Calendar.YEAR), 0, 0, 0, 0, 0);
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
                
                // 减去一年
                cal.add(Calendar.YEAR, -1);
              
                cal.set(Calendar.HOUR_OF_DAY, 24);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return toTimestamp(cal.getTime());
            }
            return toTimestamp(Calendar.getInstance().getTime());
        }


    }

    /**
     * 农历
     */
    public static class ChinaeseLunar {

    }

    /**
     * 日期常用格式
     */
    public static class Formatter {
        /**
         * 常量 日期格式 "yyyy"             4位长度
         */
        public static final String PATTERN_YYYY = "yyyy";
        public static final Formatter YYYY = new Formatter(PATTERN_YYYY);
        /**
         * 常量 日期格式 "HH:mm"            5位长度
         */
        public static final String PATTERN_HH_MM = "HH:mm";
        public static final Formatter HH_MM = new Formatter(PATTERN_HH_MM);
        /**
         * 常量 日期格式 "yyyyMM"           6位长度
         */
        public static final String PATTERN_YYYYMM = "yyyyMM";
        public static final Formatter YYYYMM = new Formatter(PATTERN_YYYYMM);
        /**
         * 常量 日期格式 "yyyy-MM"          7位长度
         */
        public static final String PATTERN_YYYY_MM = "yyyy-MM";
        public static final Formatter YYYY_MM = new Formatter(PATTERN_YYYY_MM);
        /**
         * 常量 日期格式 "yy-MM-dd"         8位长度
         */
        public static final String PATTERN_YY_MM_DD = "yy-MM-dd";
        public static final Formatter YY_MM_DD = new Formatter(PATTERN_YY_MM_DD);
        /**
         * 常量 日期格式 "yyyy-MM-dd"            10位长度
         */
        public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";
        public static final Formatter YYYY_MM_DD = new Formatter(PATTERN_YYYY_MM_DD);
        /**
         * 常量 日期格式 "yyMMddHHmmss"          12位长度
         */
        public static final String PATTERN_YYMMDDHHMMSS = "yyMMddHHmmss";
        public static final Formatter YYMMDDHHMMSS = new Formatter(PATTERN_YYMMDDHHMMSS);
        /**
         * 常量 日期格式 "yyyyMMddHHmm"          12位长度
         */
        public static final String PATTERN_YYYYMMDDHHMM = "yyyyMMddHHmm";
        public static final Formatter YYYYMMDDHHMM = new Formatter(PATTERN_YYYYMMDDHHMM);
        /**
         * 常量 日期格式 "yyyyMMddHHmmss"        14位长度
         */
        public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
        public static final Formatter YYYYMMDDHHMMSS = new Formatter(PATTERN_YYYYMMDDHHMMSS);
        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm"      16位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
        public static final Formatter YYYY_MM_DD_HH_MM = new Formatter(PATTERN_YYYY_MM_DD_HH_MM);
        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm:ss"   19位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
        public static final Formatter YYYY_MM_DD_HH_MM_SS = new Formatter(PATTERN_YYYY_MM_DD_HH_MM_SS);

        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm:ss.SSS"  21位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.S";
        public static final Formatter YYYY_MM_DD_HH_MM_SS_S = new Formatter(PATTERN_YYYY_MM_DD_HH_MM_SS_S);

        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm:ss.SS"  22位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_SS = "yyyy-MM-dd HH:mm:ss.SS";
        public static final Formatter YYYY_MM_DD_HH_MM_SS_SS = new Formatter(PATTERN_YYYY_MM_DD_HH_MM_SS_SS);

        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm:ss.SSS"  23位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
        public static final Formatter YYYY_MM_DD_HH_MM_SS_SSS = new Formatter(PATTERN_YYYY_MM_DD_HH_MM_SS_SSS);

        final public String pattern;
        final private DateTimeFormatter dateTimeFmt;

        private Formatter(String pattern) {
            this.pattern = pattern;
//            this.dateTimeFmt = DateTimeFormatter.ofPattern(this.pattern);
            this.dateTimeFmt = new DateTimeFormatterBuilder().appendPattern(this.pattern)
                               .parseDefaulting(ChronoField.YEAR_OF_ERA, 1970)
                    .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
//                    .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                    .toFormatter();
        }

        public static Formatter valueOf(String pattern) {
            if (YYYY.pattern.equalsIgnoreCase(pattern)) {
                return YYYY;
            } else if (HH_MM.pattern.equalsIgnoreCase(pattern)) {
                return HH_MM;
            } else if (YYYYMM.pattern.equalsIgnoreCase(pattern)) {
                return YYYYMM;
            } else if (YYYY_MM.pattern.equalsIgnoreCase(pattern)) {
                return YYYY_MM;
            } else if (YY_MM_DD.pattern.equalsIgnoreCase(pattern)) {
                return YY_MM_DD;
            } else if (YYYY_MM_DD.pattern.equalsIgnoreCase(pattern)) {
                return YYYY_MM_DD;
            } else if (YYMMDDHHMMSS.pattern.equalsIgnoreCase(pattern)) {
                return YYMMDDHHMMSS;
            } else if (YYYYMMDDHHMMSS.pattern.equalsIgnoreCase(pattern)) {
                return YYYYMMDDHHMMSS;
            } else if (YYYY_MM_DD_HH_MM.pattern.equalsIgnoreCase(pattern)) {
                return YYYY_MM_DD_HH_MM;
            } else if (YYYY_MM_DD_HH_MM_SS.pattern.equalsIgnoreCase(pattern)) {
                return YYYY_MM_DD_HH_MM_SS;
            } else if (YYYY_MM_DD_HH_MM_SS_S.pattern.equalsIgnoreCase(pattern)) {
                return YYYY_MM_DD_HH_MM_SS_S;
            } else if (YYYY_MM_DD_HH_MM_SS_SS.pattern.equalsIgnoreCase(pattern)) {
                return YYYY_MM_DD_HH_MM_SS_SS;
            } else if (YYYY_MM_DD_HH_MM_SS_SSS.pattern.equalsIgnoreCase(pattern)) {
                return YYYY_MM_DD_HH_MM_SS_SSS;
            }
            return new Formatter(pattern);
        }

        /**
         * 根据数据长度判断使用的Formatter
         *
         * @param source 日期数据
         * @return Formatter
         */
        public static Formatter valueOfSourceLength(String source) {
//
//			if (data.indexOf('年') > 0 && data.indexOf('月') > 0 && data.indexOf('日') > 0) {
//				return YYYY_MM_DD_3;
//			}

            // 4位
            if (YYYY.pattern.length() == source.length()) {
                return YYYY;
                // 5位
            } else if (HH_MM.pattern.length() == source.length()) {
                if (source.indexOf(':') > 0) {
                    return HH_MM;
                }
                // 6位
            } else if (YYYYMM.pattern.length() == source.length()) {
                return YYYYMM;
                // 7位
            } else if (YYYY_MM.pattern.length() == source.length()) {
                return YYYY_MM;
                // 8位
            } else if (YY_MM_DD.pattern.length() == source.length()) {
                return YY_MM_DD;
                // 10位
            } else if (YYYY_MM_DD.pattern.length() == source.length()) {
                if (source.indexOf('-') > 0) {
                    return YYYY_MM_DD;
                }
                return YYYY_MM_DD;
                // 12位
            } else if (YYMMDDHHMMSS.pattern.length() == source.length()) {
                return YYMMDDHHMMSS;
                // 14位
            } else if (YYYYMMDDHHMMSS.pattern.length() == source.length()) {
                return YYYYMMDDHHMMSS;
                // 16位
            } else if (YYYY_MM_DD_HH_MM.pattern.length() == source.length()) {
                return YYYY_MM_DD_HH_MM;
                // 19位
            } else if (YYYY_MM_DD_HH_MM_SS.pattern.length() == source.length()) {
                return YYYY_MM_DD_HH_MM_SS;
                // 21 23位
            } else if (YYYY_MM_DD_HH_MM_SS_S.pattern.length() == source.length()) {
                return YYYY_MM_DD_HH_MM_SS_S;
            } else if (YYYY_MM_DD_HH_MM_SS_SS.pattern.length() == source.length()) {
                return YYYY_MM_DD_HH_MM_SS_SS;
            } else if (YYYY_MM_DD_HH_MM_SS_SSS.pattern.length() == source.length()) {
                return YYYY_MM_DD_HH_MM_SS_SSS;
            }
            return YYYY_MM_DD_HH_MM_SS_SSS;
        }

        public String print(Date date, Locale locale) {
            return getDateFormat(locale).format(date);
        }

        /**
         * Parses text from the beginning of the given string to produce a date.
         * The method may not use the entire text of the given string.
         *
         * @param source A <code>String</code> whose beginning should be parsed.
         * @return A <code>Date</code> parsed from the string.
         * @throws ParseException if the beginning of the specified string
         *                        cannot be parsed.
         */
        public Date parse(String source, Locale locale) throws ParseException {
            return getDateFormat(locale).parse(source);
        }


        private DateFormat getDateFormat(Locale locale) {
            return new SimpleDateFormat(this.pattern, locale);
        }
        /**
         *
         * @param source
         * @param zoneId
         * @return
         * @throws ParseException
         */
        public Date parse2(String source, TimeZone timeZone) throws ParseException {
            LocalDateTime localDateTime = LocalDateTime.parse(source, dateTimeFmt);
            return Date.from(localDateTime.atZone(timeZone.toZoneId()).toInstant());
        }

    }


}
