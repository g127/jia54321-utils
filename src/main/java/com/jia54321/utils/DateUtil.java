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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期工具类
 *
 * @author gg
 * @create 2019-07-31
 */
public class DateUtil {
    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);
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
        } else if (obj instanceof Calendar) {
            time = ( (Calendar) obj ).getTime();
        } else if (obj instanceof String) {
            String srcTime = ((String) obj).trim();
            try {
                time = Formatter.valueOfSourceLength(srcTime).parse2(srcTime, TimeZone.getDefault());
            } catch (DateTimeException e) {
                //log
                if (log.isDebugEnabled()) {
                    log.debug("解析时间字符串失败", e);
                }
            }
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else if (obj instanceof LocalDateTime) {
            return  ((LocalDateTime) obj).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        } else if (obj instanceof LocalDate) {
            return  ((LocalDate) obj).atStartOfDay().atZone(ZoneOffset.of("+8")).toInstant().toEpochMilli();
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

        if (null == dateFormat || 0 == dateFormat.length || null == dateFormat[0] ||  "".equals(dateFormat[0])) {
            dateFormat = new String[] { Formatter.YYYY_MM_DD_HH_MM_SS.pattern };
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
        if (null == dateFormat || 0 == dateFormat.length || null == dateFormat[0] ||  "".equals(dateFormat[0]) ) {
            dateFormat = new String[]{Formatter.YYYY_MM_DD_HH_MM_SS.pattern};
        }
        return toTimeString(Calendar.getInstance().getTime(), dateFormat[0]);
    }

    /**
     * 当前时间字符串 日期格式 "yyyy-MM-dd"
     *
     * @return String
     */
    public static String toNowDateString() {
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
    public static String toDateString(Object time) {
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
     * 对象转化为时间  java.time.LocalDateTime
     *
     * @param time 时间
     * @return LocalDateTime
     */
    public static java.time.LocalDateTime toLocalDateTime(Object time) {
        Long currTime = toTimeMillis(time);
        if (null == currTime) {
            return null;
        }
        return new java.sql.Timestamp(currTime).toLocalDateTime();
    }

    /**
     * 对象转化为时间  java.time.LocalDate
     *
     * @param time 时间
     * @return LocalDate
     */
    public static java.time.LocalDate toLocalDate(Object time) {
        Long currTime = toTimeMillis(time);
        if (null == currTime) {
            return null;
        }
        return new java.sql.Timestamp(currTime).toLocalDateTime().toLocalDate();
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
     * 时间区间内的步长时间
     */
    public static class ExpressingTime {
        /** 时间区间类型 */
        public final String rangeType;
        /** 时间步长类型 */
        public final String stepType;
        /** 步长开始时间 */
        public long beginTime;
        /** 步长结束时间 */
        public long endTime;
        public ExpressingTime(String rangeType, String stepType) {
            this.rangeType = rangeType;
            this.stepType = stepType;
        }
    }
    /**
     * 时间常用表达
     */
    public static class Expressing {

        /**
         * 按年，月，日，小时，季度，周的步长，计算xxxx年内的最早时间00:00列表
         * @param stepType 步长类型
         * @param rangeType 范围类型
         * @param timePoint 时间点（某范围内的时间点）
         * @return 列表
         */
        public static List<DateUtil.ExpressingTime> toList(String stepType, String rangeType, Object... timePoint) {
            Timestamp beginRangeTime = DateUtil.Expressing.toBegin(rangeType, timePoint);
            Timestamp endRangeTime = DateUtil.Expressing.toEnd(rangeType, timePoint);
            return toList(stepType, beginRangeTime, endRangeTime);
        }

        /**
         * 按年，月，日，小时，季度，周的步长，计算xxxx年内的最早时间00:00列表
         * @param stepType 步长类型
         * @param beginRangeTime 开始时间点（某范围内的时间点）
         * @param endRangeTime   结束时间点（某范围内的时间点）
         * @return 列表
         */
        public static List<ExpressingTime> toList(String stepType, Timestamp beginRangeTime, Timestamp endRangeTime) {
            List<ExpressingTime> result = null;
            Calendar begin = Calendar.getInstance();
            begin.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
            begin.setTime(beginRangeTime);
            begin.set(Calendar.MILLISECOND, 0);
            begin.set(Calendar.SECOND, 0);
            begin.set(Calendar.MINUTE, 0);

            Calendar end = Calendar.getInstance();
            end.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
            end.setTime(endRangeTime);
            end.set(Calendar.MILLISECOND, 0);
            end.set(Calendar.SECOND, 0);
            end.set(Calendar.MINUTE, 0);

            String key = toTimeString(begin) + " ~ " + toTimeString(end) + " " + stepType;

            int[] step = new int[]{ Calendar.DAY_OF_YEAR, 1 };
            switch (stepType) {
                case "year":
                    step = new int[]{ Calendar.MONTH, 12 };
                    break;
                case "month":
                    step = new int[]{ Calendar.MONTH, 1 };
                    break;
                case "day":
                    step = new int[]{ Calendar.DAY_OF_YEAR, 1 };
                    break;
                case "hour":
                    step = new int[]{ Calendar.HOUR_OF_DAY, 1 };
                    break;
                case "halfYear":
                    step = new int[]{ Calendar.MONTH, 6 };
                    break;
                case "quarter":
                    step = new int[]{ Calendar.MONTH, 3 };
                    break;
                case "week":
                    // 设定为周一
                    if(begin.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                        begin.add(Calendar.DAY_OF_WEEK, 7); // 下周
                        begin.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 设定为下周一
                    }
                    step = new int[]{ Calendar.DAY_OF_YEAR, 7 };
                    break;
                default:
                    Assert.isTrue(false, "stepType 取值有误");
                    break;
            }

            result = new ArrayList<>();
            ExpressingTime expressingTime = null;
            Calendar i = (Calendar)begin.clone();
            while ( i.before(end) ) {
                expressingTime = new ExpressingTime("", stepType);
                expressingTime.beginTime = i.getTimeInMillis();
                i.add(step[0], step[1]);
                expressingTime.endTime = i.getTimeInMillis();
                result.add(expressingTime);
            }
            return result;
        }

        /**
         * 当前时间的最早时间00:00
         *
         * @param type 取值英文String: 【
         *             day 当天
         *             week 当周
         *             month 当月
         *             quarter 当季度
         *             q1 本年1季度
         *             q2 本年2季度
         *             q3 本年3季度
         *             q4 本年4季度
         *             halfYear 上半年/下半年
         *             h1 本年上半年
         *             h2 本年下半年
         *             year 当年
         *
         *             yesterday 昨天
         *             lastWeek 上周
         *             lastMonth 上月
         *             lastQuarter 上季度
         *             lastHalfYear 前一半年
         *             lastYear 上一年
         *             】
         * @param time 可不传，默认当前时间
         * @return Timestamp
         */
        public static java.sql.Timestamp toBegin(String type, Object... time) {
            if ( null == time || time.length == 0 || null == time[0] ||  "".equals(time[0])) {
                // 当前时间
                time = new Date[]{Calendar.getInstance().getTime()};
            }
            Timestamp timestamp = toTimestamp(time[0]);
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
            cal.setTime(timestamp);
            cal.set(Calendar.MILLISECOND, 0);
            if ("day".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                //
                return toTimestamp(cal.getTime());
            }
            if ("week".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                //
                return toTimestamp(cal.getTime());
            }
            if ("month".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), 1, 0, 0, 0);
                //
                return toTimestamp(cal.getTime());
            }
            if ("quarter".equalsIgnoreCase(type)) {
                // 实际月份从0开始，除以3取整，然后在乘以3 。
                // [ 0，1，2，3，4，5，6，7，8，9，10，11 ] =>  [ 0，0，0，3，3，3，6，6，6，9，9，9 ]
                // 对应 1，4, 7, 10 四个季度起始月份
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) / 3 * 3, 1, 0, 0, 0);
                //
                return toTimestamp(cal.getTime());
            }
            if ("q1".equalsIgnoreCase(type) || "q2".equalsIgnoreCase(type) || "q3".equalsIgnoreCase(type) || "q4".equalsIgnoreCase(type) ) {
                // [ 1，2，3，4 ] =>  [ 0，3，6，9 ]
                cal.set(cal.get(Calendar.YEAR), Integer.parseInt(type.charAt(1) + "") * 3 - 3, 1, 0, 0, 0);
                //
                return toTimestamp(cal.getTime());
            }
            if ("halfYear".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) / 6 * 6, 1, 0, 0, 0);
                //
                return toTimestamp(cal.getTime());
            }
            if ("h1".equalsIgnoreCase(type) || "h2".equalsIgnoreCase(type)) {
                // [ 1，2 ] =>  [ 0，6 ]
                cal.set(cal.get(Calendar.YEAR), Integer.parseInt(type.charAt(1) + "") * 6 - 6, 1, 0, 0, 0);
                //
                return toTimestamp(cal.getTime());
            }
            if ("year".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
                //
                return toTimestamp(cal.getTime());
            }
            //
            //
            if ("yesterday".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                // 减去1天
                cal.add(Calendar.DATE, -1);
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastWeek".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                // 减去7天
                cal.add(Calendar.DATE, -7);
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastMonth".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
                // 减去1月
                cal.add(Calendar.MONTH, -1);
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastQuarter".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) / 3 * 3, 1, 0, 0, 0);
                // 减去3月
                cal.add(Calendar.MONTH, -3);
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastHalfYear".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) / 6 * 6, 1, 0, 0, 0);
                // 减去6月
                cal.add(Calendar.MONTH, -6);
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastYear".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
                // 减去一年
                cal.add(Calendar.YEAR, -1);
                //
                return toTimestamp(cal.getTime());
            }

            return toTimestamp(Calendar.getInstance().getTime());
        }

        /**
         * 当前时间的最后晚上时间24:00(第二天)，如：本周最晚时间，本月最晚时间 Timestamp
         *
         * @param type 取值英文String: 【
         *             day 当天
         *             week 当周
         *             month 当月
         *             quarter 当季度
         *             q1 本年1季度
         *             q2 本年2季度
         *             q3 本年3季度
         *             q4 本年4季度
         *             halfYear 上半年/下半年
         *             h1 本年上半年
         *             h2 本年下半年
         *             year 当年
         *
         *             yesterday 昨天
         *             lastWeek 上周
         *             lastMonth 上月
         *             lastQuarter 上季度
         *             lastHalfYear 前一半年
         *             lastYear 上一年
         *             】
         * @param time 不传值，默认当前时间
         * @return Timestamp
         */
        public static Timestamp toEnd(String type, Object... time) {
            if ( null == time || time.length == 0 || null == time[0] ||  "".equals(time[0])) {
                // 当前时间
                time = new Date[]{Calendar.getInstance().getTime()};
            }
            Timestamp timestamp = toTimestamp(time[0]);
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
            cal.setTime(timestamp);
            cal.set(Calendar.MILLISECOND, 0);
            if ("day".equalsIgnoreCase(type)) {
                // 重新设定时间
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                // 增加1天
                cal.add(Calendar.DATE, 1);
                //
                return toTimestamp(cal.getTime());
            }
            if ("week".equalsIgnoreCase(type)) {
                // 重新设定时间
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                // 设定周一
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                // 增加1周
                cal.add(Calendar.DATE, 7);
                //
                return toTimestamp(cal.getTime());
            }
            if ("month".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                // 增加1月
                cal.add(Calendar.MONTH, 1);
                //
                return toTimestamp(cal.getTime());
            }
            if ("quarter".equalsIgnoreCase(type)) {
                // 实际月份从0开始，除以3取整，然后在乘以3 。
                // [0，1，2，3，4，5，6，7，8，9，10，11] => [0，0，0，3，3，3，6，6，6，9，9，9]
                cal.set(cal.get(Calendar.YEAR),  cal.get(Calendar.MONTH) / 3 * 3 , 1, 0, 0, 0);
                // 增加3个月
                cal.add(Calendar.MONTH, 3);
                //
                return toTimestamp(cal.getTime());
            }
            if ("q1".equalsIgnoreCase(type) || "q2".equalsIgnoreCase(type) || "q3".equalsIgnoreCase(type) || "q4".equalsIgnoreCase(type) ) {
                // [ 1，2，3，4 ] =>  [ 0，3，6，9 ]
                cal.set(cal.get(Calendar.YEAR), Integer.parseInt(type.charAt(1) + "") * 3 - 3, 1, 0, 0, 0);
                // 增加3个月
                cal.add(Calendar.MONTH, 3);
                //
                return toTimestamp(cal.getTime());
            }
            if ("halfYear".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) / 6 * 6, 1, 0, 0, 0);
                // 增加6个月
                cal.add(Calendar.MONTH, 6);
                //
                return toTimestamp(cal.getTime());
            }
            if ("h1".equalsIgnoreCase(type) || "h2".equalsIgnoreCase(type)) {
                // [ 1，2 ] =>  [ 0，6 ]
                cal.set(cal.get(Calendar.YEAR), Integer.parseInt(type.charAt(1) + "") * 6 - 6, 1, 0, 0, 0);
                // 增加6个月
                cal.add(Calendar.MONTH, 6);
                //
                return toTimestamp(cal.getTime());
            }
            if ("year".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                // 增加1年
                cal.add(Calendar.YEAR, 1);
                //
                return toTimestamp(cal.getTime());
            }
            //
            //
            if ("yesterday".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                // 昨天结束时间 同时也为 今天开始时间
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastWeek".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 设定周一
                // 上周结束时间 同时也为 本周开始时间
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastMonth".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
                // 上月结束时间 同时也为 本月开始时间
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastQuarter".equalsIgnoreCase(type)) {
                // 实际月份从0开始，除以3取整，然后在乘以3 。
                // [0，1，2，3，4，5，6，7，8，9，10，11] =>  [0，0，0，3，3，3，6，6，6，9，9，9]
                // 对应 1，4, 7, 10 四个季度起始月份
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) / 3 * 3, 1, 0, 0, 0);
                // 上季度结束时间 同时也为 本季度开始时间
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastHalfYear".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) / 6 * 6, 1, 0, 0, 0);
                // 前半年度结束时间 同时也为 本半年度开始时间
                //
                return toTimestamp(cal.getTime());
            }
            if ("lastYear".equalsIgnoreCase(type)) {
                cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
                // 去年结束时间 同时也为 本年开始时间
                //
                return toTimestamp(cal.getTime());
            }
            return toTimestamp(Calendar.getInstance().getTime());
        }



        public static java.sql.Timestamp toMorning(String type, Object... time) {
            return toBegin(type, time);
        }

        public static Timestamp toNight(String type, Object... time) {
            return toEnd(type, time);
        }

    }

    /**
     * 农历
     */
    public static class ChinaeseLunar {
        /** * 實用於公曆 1901 年至 2100 年之間的 200 年 *  * See also:
         *  1. http://www.herongyang.com/year_big5/
         * 2. http://www.herongyang.com/year_big5/calendar.html
         * 3. http://www.herongyang.com/year_big5/program.html */
    }

    /**
     * 日期常用格式
     */
    public static class Formatter {
        /**
         * 常量 日期格式 "yyyy"             4位长度
         */
        public static final String PATTERN_YYYY = "yyyy";
        /** 常量 日期格式 "yyyy"             4位长度 */
        public static final Formatter YYYY = new Formatter(PATTERN_YYYY);
        /**
         * 常量 日期格式 "HH:mm"            5位长度
         */
        public static final String PATTERN_HH_MM = "HH:mm";
        /** 常量 日期格式 "HH:mm"            5位长度 */
        public static final Formatter HH_MM = new Formatter(PATTERN_HH_MM);
        /**
         * 常量 日期格式 "yyyyMM"           6位长度
         */
        public static final String PATTERN_YYYYMM = "yyyyMM";
        /** 常量 日期格式 "yyyyMM"           6位长度 */
        public static final Formatter YYYYMM = new Formatter(PATTERN_YYYYMM);
        /**
         * 常量 日期格式 "yyyy-MM"          7位长度
         */
        public static final String PATTERN_YYYY_MM = "yyyy-MM";
        /** 常量 日期格式 "yyyy-MM"          7位长度 */
        public static final Formatter YYYY_MM = new Formatter(PATTERN_YYYY_MM);
        /**
         * 常量 日期格式 "yy-MM-dd"         8位长度
         */
        public static final String PATTERN_YY_MM_DD_NORM = "yy-MM-dd";
        /** 常量 日期格式 "yy-MM-dd"         8位长度 */
        public static final Formatter YY_MM_DD_NORM = new Formatter(PATTERN_YY_MM_DD_NORM);

        /**
         * 常量 日期格式 "yy-MM-dd"         8位长度
         */
        public static final String PATTERN_YY_MM_DD = PATTERN_YY_MM_DD_NORM;
        /** 常量 日期格式 "yy-MM-dd"         8位长度 */
        public static final Formatter YY_MM_DD = YY_MM_DD_NORM;
        /**
         * 常量 日期格式 "yyyyMMdd"         8位长度
         */
        public static final String PATTERN_YYYY_MM_DD_PURE = "yyyyMMdd";
        /** 常量 日期格式 "yyyyMMdd"         8位长度 */
        public static final Formatter YYYY_MM_DD_PURE = new Formatter(PATTERN_YYYY_MM_DD_PURE);

        /**
         * 常量 日期格式 "yyyy-MM-dd"            10位长度
         */
        public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";
        /** 常量 日期格式 "yyyy-MM-dd"            10位长度 */
        public static final Formatter YYYY_MM_DD = new Formatter(PATTERN_YYYY_MM_DD);

        /**
         * 常量 日期格式 "yyMMddHHmmss"          12位长度
         */
        public static final String PATTERN_YYMMDDHHMMSS = "yyMMddHHmmss";
        /** 常量 日期格式 "yyMMddHHmmss"          12位长度 */
        public static final Formatter YYMMDDHHMMSS = new Formatter(PATTERN_YYMMDDHHMMSS);

        /**
         * 常量 日期格式 "yyyyMMddHHmm"          12位长度
         */
        public static final String PATTERN_YYYYMMDDHHMM = "yyyyMMddHHmm";
        /** 常量 日期格式 "yyyyMMddHHmm"          12位长度 */
        public static final Formatter YYYYMMDDHHMM = new Formatter(PATTERN_YYYYMMDDHHMM);

        /**
         * 常量 日期格式 "yyyyMMddHHmmss"        14位长度
         */
        public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
        /** 常量 日期格式 "yyyyMMddHHmmss"        14位长度 */
        public static final Formatter YYYYMMDDHHMMSS = new Formatter(PATTERN_YYYYMMDDHHMMSS);

        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm"      16位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
        /** 常量 日期格式 "yyyy-MM-dd HH:mm"      16位长度 */
        public static final Formatter YYYY_MM_DD_HH_MM = new Formatter(PATTERN_YYYY_MM_DD_HH_MM);

        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm:ss"   19位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
        /** 常量 日期格式 "yyyy-MM-dd HH:mm"      16位长度 */
        public static final Formatter YYYY_MM_DD_HH_MM_SS = new Formatter(PATTERN_YYYY_MM_DD_HH_MM_SS);

        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm:ss.SSS"  21位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.S";
        /** 常量 日期格式 "yyyy-MM-dd HH:mm"      16位长度 */
        public static final Formatter YYYY_MM_DD_HH_MM_SS_S = new Formatter(PATTERN_YYYY_MM_DD_HH_MM_SS_S);

        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm:ss.SS"  22位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_SS = "yyyy-MM-dd HH:mm:ss.SS";
        /** 常量 日期格式 "yyyy-MM-dd HH:mm"      22位长度 */
        public static final Formatter YYYY_MM_DD_HH_MM_SS_SS = new Formatter(PATTERN_YYYY_MM_DD_HH_MM_SS_SS);

        /**
         * 常量 日期格式 "yyyy-MM-dd HH:mm:ss.SSS"  23位长度
         */
        public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
        /** 常量 日期格式 "yyyy-MM-dd HH:mm:ss.SSS"  23位长度 */
        public static final Formatter YYYY_MM_DD_HH_MM_SS_SSS = new Formatter(PATTERN_YYYY_MM_DD_HH_MM_SS_SSS);

        /** 模式配置 */
        final public String pattern;
        /** DateTimeFormatter格式字符串 */
        final private DateTimeFormatter dateTimeFmt;

        /** */
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
            } else if (YY_MM_DD_NORM.pattern.equalsIgnoreCase(pattern)) {
                return YY_MM_DD_NORM;
            } else if (YYYY_MM_DD_PURE.pattern.equalsIgnoreCase(pattern)) {
                return YYYY_MM_DD_PURE;
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
            } else if (YY_MM_DD_NORM.pattern.length() == source.length()) {
                if(source.indexOf("-") == -1) {
                    return YYYY_MM_DD_PURE;
                }
                if(source.indexOf("-") == 2 && source.indexOf("-") == 5) {
                    return YY_MM_DD_NORM;
                }
                return YY_MM_DD_NORM;
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
         * The text is parsed using the formatter, returning a date-time.
         * @param source A <code>String</code> whose beginning should be parsed.
         * @param timeZone
         * @return A <code>Date</code> parsed from the string.
         * @throws DateTimeException
         */
        public Date parse2(String source, TimeZone timeZone) throws DateTimeException {
            LocalDateTime localDateTime = LocalDateTime.parse(source, dateTimeFmt);
            return Date.from(localDateTime.atZone(timeZone.toZoneId()).toInstant());
        }

    }


}
