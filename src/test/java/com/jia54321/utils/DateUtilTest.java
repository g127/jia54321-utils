package com.jia54321.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class DateUtilTest {
//    static final Logger log = LoggerFactory.getLogger(DateUtilTest.class);

    @Test
    public void expressingToMorning() {
        // 2018-08-12 星期天
        // 当前时间 0 点  当天
        assertEquals("2018-08-12 00:00:00.0", DateUtil.Expressing.toMorning("day","2018-08-12 08:01:12.1").toString());
        // 当前时间 周一  本周
        assertEquals("2018-08-06 00:00:00.0", DateUtil.Expressing.toMorning("week","2018-08-12 08:01:12").toString());
        // 当前时间 1号  本月
        assertEquals("2018-08-01 00:00:00.0", DateUtil.Expressing.toMorning("month","2018-08-12 08:01:12").toString());
        // 当前时间 1号  本年
        assertEquals("2018-01-01 00:00:00.0", DateUtil.Expressing.toMorning("year","2018-08-12 08:01:12").toString());

        // 当前时间 上周一
        assertEquals("2018-07-30 00:00:00.0", DateUtil.Expressing.toMorning("lastWeek","2018-08-12 08:01:12").toString());
        // 当前时间 上月1号
        assertEquals("2018-07-01 00:00:00.0", DateUtil.Expressing.toMorning("lastMonth","2018-08-12 08:01:12").toString());
        // 当前时间 上月1号
        assertEquals("2017-01-01 00:00:00.0", DateUtil.Expressing.toMorning("lastYear","2018-08-12 08:01:12").toString());

        // 仅仅有年月场景
        // ===================================================================================================================
        // 当前时间 0 点
        assertEquals("2018-08-01 00:00:00.0", DateUtil.Expressing.toMorning("day","2018-08").toString());
        // 当前时间 周一
        assertEquals("2018-07-30 00:00:00.0", DateUtil.Expressing.toMorning("week","2018-08").toString());
        // 当前时间 1号
        assertEquals("2018-08-01 00:00:00.0", DateUtil.Expressing.toMorning("month","2018-08").toString());
        // 当前时间 1号
        assertEquals("2018-01-01 00:00:00.0", DateUtil.Expressing.toMorning("year","2018-08").toString());
        // ===================================================================================================================
        // 未传入时间时，避免空指针
        DateUtil.Expressing.toMorning("day").toString();
        DateUtil.Expressing.toMorning("week").toString();
        DateUtil.Expressing.toMorning("month").toString();
        DateUtil.Expressing.toMorning("year").toString();
    }

    @Test
    public void expressingToNight() {
        // 当前时间 24 点，第二天 0点
        assertEquals("2018-08-13 00:00:00.0", DateUtil.Expressing.toNight("day","2018-08-12 08:01:12").toString());
        // 当前时间 下周一 0点
        assertEquals("2018-08-13 00:00:00.0", DateUtil.Expressing.toNight("week","2018-08-12 08:01:12").toString());
        // 当前时间 下一月 0点
        assertEquals("2018-09-01 00:00:00.0", DateUtil.Expressing.toNight("month","2018-08-12 08:01:12").toString());
        // 当前时间 1号  本年
        assertEquals("2019-01-01 00:00:00.0", DateUtil.Expressing.toNight("year","2018-08-12 08:01:12").toString());

        // 当前时间 上周最后一天 本周第一天
        assertEquals("2018-08-06 00:00:00.0", DateUtil.Expressing.toNight("lastWeek","2018-08-12 08:01:12").toString());
        // 当前时间 上月最后一天 本月第一天
        assertEquals("2018-08-01 00:00:00.0", DateUtil.Expressing.toNight("lastMonth","2018-08-12 08:01:12").toString());
        // 当前时间 去年最后一天 今年第一天
        assertEquals("2018-01-01 00:00:00.0", DateUtil.Expressing.toNight("lastYear","2018-08-12 08:01:12").toString());

        // 仅仅有年月场景
        // ===================================================================================================================

        // 0 点
        assertEquals("2018-08-02 00:00:00.0", DateUtil.Expressing.toNight("day","2018-08").toString());
        // 周一
        assertEquals("2018-08-06 00:00:00.0", DateUtil.Expressing.toNight("week","2018-08").toString());
        // 1号
        assertEquals("2018-09-01 00:00:00.0", DateUtil.Expressing.toNight("month","2018-08").toString());
        // 1号
        assertEquals("2019-01-01 00:00:00.0", DateUtil.Expressing.toNight("year","2018-08").toString());
        // ===================================================================================================================

        // 未传入时间时，避免空指针
        DateUtil.Expressing.toNight("day").toString();
        DateUtil.Expressing.toNight("week").toString();
        DateUtil.Expressing.toNight("month").toString();
        DateUtil.Expressing.toNight("year").toString();
        // ===================================================================================================================

    }
    @Test
    public void toTimeMillis() {
        // 23
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2019-01-01 00:00:00.000"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 22
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2019-01-01 00:00:00.00"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 21
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2019-01-01 00:00:00.0"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 19
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2019-01-01 00:00:00"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 16
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2019-01-01 00:00"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 14
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("20190101000000"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 12
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("190101000000"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 10
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2019-01-01"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 8
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("19-01-01"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 7
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2019-01"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 6
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("201901"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 5
        assertEquals("1970-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("00:00"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 4
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2019"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));


        // 东8区时间
        if(TimeZone.getDefault().getID().equalsIgnoreCase("Asia/Shanghai")) {
            // 毫秒数
            assertEquals("1970-01-01 08:00:00.000",
                    DateUtil.toTimeString(DateUtil.toTimeMillis(0), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        }

        // TimestampDateUtil
        assertEquals("2019-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis(Timestamp.valueOf("2019-01-01 00:00:00.000")), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

    }

    @Test
    public void toTimeChinaWeekOfYear() {
    }

    @Test
    public void toTimeString() {

    }

    @Test
    public void toNow() {
    }

    @Test
    public void toNowDataString() {
    }

    @Test
    public void toNowTimeString() {
    }

    @Test
    public void toNowStampString() {
    }

    @Test
    public void toDataString() {
    }

    @Test
    public void testToTimeString() {
    }

    @Test
    public void toTimestamp() {
    }

    @Test
    public void toNowTimestamp() {
    }
}