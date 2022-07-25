package com.jia54321.utils;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * 日志功能测试用例
 */
public class DateUtilTest {
    static final Logger log = LoggerFactory.getLogger(DateUtilTest.class);

    private List<TestTime> testTimeList = new ArrayList<>();

    class TestTime {
        String inputTime;
        //
        String expectedBeginDay;
        String expectedBeginWeek;
        String expectedBeginMonth;
        String expectedBeginQuarter;
        String expectedBeginQ1;
        String expectedBeginQ2;
        String expectedBeginQ3;
        String expectedBeginQ4;
        String expectedBeginHalfYear;
        String expectedBeginH1;
        String expectedBeginH2;
        String expectedBeginYear;
        //
        String expectedBeginYesterday;
        String expectedBeginLastWeek;
        String expectedBeginLastMonth;
        String expectedBeginLastQuarter;
        String expectedBeginLastHalfYear;
        String expectedBeginLastYear;
        //
        String expectedEndDay;
        String expectedEndWeek;
        String expectedEndMonth;
        String expectedEndQuarter;
        String expectedEndQ1;
        String expectedEndQ2;
        String expectedEndQ3;
        String expectedEndQ4;
        String expectedEndHalfYear;
        String expectedEndH1;
        String expectedEndH2;
        String expectedEndYear;
        //
        String expectedEndYesterday;
        String expectedEndLastWeek;
        String expectedEndLastMonth;
        String expectedEndLastQuarter;
        String expectedEndLastHalfYear;
        String expectedEndLastYear;

        public TestTime setInputTime(String inputTime) {
            this.inputTime = inputTime;
            return this;
        }

        public TestTime setExpectedBeginDay(String expectedBeginDay) {
            this.expectedBeginDay = expectedBeginDay;
            return this;
        }

        public TestTime setExpectedBeginWeek(String expectedBeginWeek) {
            this.expectedBeginWeek = expectedBeginWeek;
            return this;
        }

        public TestTime setExpectedBeginMonth(String expectedBeginMonth) {
            this.expectedBeginMonth = expectedBeginMonth;
            return this;
        }

        public TestTime setExpectedBeginQuarter(String expectedBeginQuarter) {
            this.expectedBeginQuarter = expectedBeginQuarter;
            return this;
        }

        public TestTime setExpectedBeginQ1(String expectedBeginQ1) {
            this.expectedBeginQ1 = expectedBeginQ1;
            return this;
        }

        public TestTime setExpectedBeginQ2(String expectedBeginQ2) {
            this.expectedBeginQ2 = expectedBeginQ2;
            return this;
        }

        public TestTime setExpectedBeginQ3(String expectedBeginQ3) {
            this.expectedBeginQ3 = expectedBeginQ3;
            return this;
        }

        public TestTime setExpectedBeginQ4(String expectedBeginQ4) {
            this.expectedBeginQ4 = expectedBeginQ4;
            return this;
        }

        public TestTime setExpectedBeginHalfYear(String expectedBeginHalfYear) {
            this.expectedBeginHalfYear = expectedBeginHalfYear;
            return this;
        }

        public TestTime setExpectedBeginH1(String expectedBeginH1) {
            this.expectedBeginH1 = expectedBeginH1;
            return this;
        }

        public TestTime setExpectedBeginH2(String expectedBeginH2) {
            this.expectedBeginH2 = expectedBeginH2;
            return this;
        }

        public TestTime setExpectedBeginYear(String expectedBeginYear) {
            this.expectedBeginYear = expectedBeginYear;
            return this;
        }
        //
        public TestTime setExpectedBeginYesterday(String expectedBeginYesterday) {
            this.expectedBeginYesterday = expectedBeginYesterday;
            return this;
        }

        public TestTime setExpectedBeginLastWeek(String expectedBeginLastWeek) {
            this.expectedBeginLastWeek = expectedBeginLastWeek;
            return this;
        }

        public TestTime setExpectedBeginLastMonth(String expectedBeginLastMonth) {
            this.expectedBeginLastMonth = expectedBeginLastMonth;
            return this;
        }

        public TestTime setExpectedBeginLastQuarter(String expectedBeginLastQuarter) {
            this.expectedBeginLastQuarter = expectedBeginLastQuarter;
            return this;
        }

        public TestTime setExpectedBeginLastHalfYear(String expectedBeginLastHalfYear) {
            this.expectedBeginLastHalfYear = expectedBeginLastHalfYear;
            return this;
        }

        public TestTime setExpectedBeginLastYear(String expectedBeginLastYear) {
            this.expectedBeginLastYear = expectedBeginLastYear;
            return this;
        }

        //

        public TestTime setExpectedEndDay(String expectedEndDay) {
            this.expectedEndDay = expectedEndDay;
            return this;
        }

        public TestTime setExpectedEndWeek(String expectedEndWeek) {
            this.expectedEndWeek = expectedEndWeek;
            return this;
        }

        public TestTime setExpectedEndMonth(String expectedEndMonth) {
            this.expectedEndMonth = expectedEndMonth;
            return this;
        }

        public TestTime setExpectedEndQuarter(String expectedEndQuarter) {
            this.expectedEndQuarter = expectedEndQuarter;
            return this;
        }

        public TestTime setExpectedEndQ1(String expectedEndQ1) {
            this.expectedEndQ1 = expectedEndQ1;
            return this;
        }

        public TestTime setExpectedEndQ2(String expectedEndQ2) {
            this.expectedEndQ2 = expectedEndQ2;
            return this;
        }

        public TestTime setExpectedEndQ3(String expectedEndQ3) {
            this.expectedEndQ3 = expectedEndQ3;
            return this;
        }

        public TestTime setExpectedEndQ4(String expectedEndQ4) {
            this.expectedEndQ4 = expectedEndQ4;
            return this;
        }

        public TestTime setExpectedEndHalfYear(String expectedEndHalfYear) {
            this.expectedEndHalfYear = expectedEndHalfYear;
            return this;
        }

        public TestTime setExpectedEndH1(String expectedEndH1) {
            this.expectedEndH1 = expectedEndH1;
            return this;
        }

        public TestTime setExpectedEndH2(String expectedEndH2) {
            this.expectedEndH2 = expectedEndH2;
            return this;
        }

        public TestTime setExpectedEndYear(String expectedEndYear) {
            this.expectedEndYear = expectedEndYear;
            return this;
        }

        public TestTime setExpectedEndYesterday(String expectedEndYesterday) {
            this.expectedEndYesterday = expectedEndYesterday;
            return this;
        }

        public TestTime setExpectedEndLastWeek(String expectedEndLastWeek) {
            this.expectedEndLastWeek = expectedEndLastWeek;
            return this;
        }

        public TestTime setExpectedEndLastMonth(String expectedEndLastMonth) {
            this.expectedEndLastMonth = expectedEndLastMonth;
            return this;
        }

        public TestTime setExpectedEndLastQuarter(String expectedEndLastQuarter) {
            this.expectedEndLastQuarter = expectedEndLastQuarter;
            return this;
        }


        public TestTime setExpectedEndLastHalfYear(String expectedEndLastHalfYear) {
            this.expectedEndLastHalfYear = expectedEndLastHalfYear;
            return this;
        }

        public TestTime setExpectedEndLastYear(String expectedEndLastYear) {
            this.expectedEndLastYear = expectedEndLastYear;
            return this;
        }
    }

    @Before
    public void setUp() throws Exception {
        testTimeList.clear();
        //
        testTimeList.add(new TestTime()
                //
                .setInputTime("2021-08-12 08:01:12")
                //
                .setExpectedBeginDay("2021-08-12 00:00:00.0")
                .setExpectedBeginWeek("2021-08-09 00:00:00.0")
                .setExpectedBeginMonth("2021-08-01 00:00:00.0")
                .setExpectedBeginQuarter("2021-07-01 00:00:00.0")
                .setExpectedBeginQ1("2021-01-01 00:00:00.0")
                .setExpectedBeginQ2("2021-04-01 00:00:00.0")
                .setExpectedBeginQ3("2021-07-01 00:00:00.0")
                .setExpectedBeginQ4("2021-10-01 00:00:00.0")
                .setExpectedBeginHalfYear("2021-07-01 00:00:00.0")
                .setExpectedBeginH1("2021-01-01 00:00:00.0")
                .setExpectedBeginH2("2021-07-01 00:00:00.0")
                .setExpectedBeginYear("2021-01-01 00:00:00.0")
                //
                .setExpectedBeginYesterday("2021-08-11 00:00:00.0")
                .setExpectedBeginLastWeek("2021-08-02 00:00:00.0")
                .setExpectedBeginLastMonth("2021-07-01 00:00:00.0")
                .setExpectedBeginLastQuarter("2021-04-01 00:00:00.0")
                .setExpectedBeginLastHalfYear("2021-01-01 00:00:00.0")
                .setExpectedBeginLastYear("2020-01-01 00:00:00.0")
                //
                //
                .setExpectedEndDay("2021-08-13 00:00:00.0")
                .setExpectedEndWeek("2021-08-16 00:00:00.0")
                .setExpectedEndMonth("2021-09-01 00:00:00.0")
                .setExpectedEndQuarter("2021-10-01 00:00:00.0")
                .setExpectedEndQ1("2021-04-01 00:00:00.0")
                .setExpectedEndQ2("2021-07-01 00:00:00.0")
                .setExpectedEndQ3("2021-10-01 00:00:00.0")
                .setExpectedEndQ4("2022-01-01 00:00:00.0")
                .setExpectedEndHalfYear("2022-01-01 00:00:00.0")
                .setExpectedEndH1("2021-07-01 00:00:00.0")
                .setExpectedEndH2("2022-01-01 00:00:00.0")
                .setExpectedEndYear("2022-01-01 00:00:00.0")
                //
                .setExpectedEndYesterday("2021-08-12 00:00:00.0")
                .setExpectedEndLastWeek("2021-08-09 00:00:00.0")
                .setExpectedEndLastMonth("2021-08-01 00:00:00.0")
                .setExpectedEndLastQuarter("2021-07-01 00:00:00.0")
                .setExpectedEndLastHalfYear("2021-07-01 00:00:00.0")
                .setExpectedEndLastYear("2021-01-01 00:00:00.0")
        );

        testTimeList.add(new TestTime()
                //
                .setInputTime("2021-05-12 08:01:12")
                //
                .setExpectedBeginDay("2021-05-12 00:00:00.0")
                .setExpectedBeginWeek("2021-05-10 00:00:00.0")
                .setExpectedBeginMonth("2021-05-01 00:00:00.0")
                .setExpectedBeginQuarter("2021-04-01 00:00:00.0")
                .setExpectedBeginQ1("2021-01-01 00:00:00.0")
                .setExpectedBeginQ2("2021-04-01 00:00:00.0")
                .setExpectedBeginQ3("2021-07-01 00:00:00.0")
                .setExpectedBeginQ4("2021-10-01 00:00:00.0")
                .setExpectedBeginHalfYear("2021-01-01 00:00:00.0")
                .setExpectedBeginH1("2021-01-01 00:00:00.0")
                .setExpectedBeginH2("2021-07-01 00:00:00.0")
                .setExpectedBeginYear("2021-01-01 00:00:00.0")
                //
                .setExpectedBeginYesterday("2021-05-11 00:00:00.0")
                .setExpectedBeginLastWeek("2021-05-03 00:00:00.0")
                .setExpectedBeginLastMonth("2021-04-01 00:00:00.0")
                .setExpectedBeginLastQuarter("2021-01-01 00:00:00.0")
                .setExpectedBeginLastHalfYear("2020-07-01 00:00:00.0")
                .setExpectedBeginLastYear("2020-01-01 00:00:00.0")
                //
                //
                .setExpectedEndDay("2021-05-13 00:00:00.0")
                .setExpectedEndWeek("2021-05-17 00:00:00.0")
                .setExpectedEndMonth("2021-06-01 00:00:00.0")
                .setExpectedEndQuarter("2021-07-01 00:00:00.0")
                .setExpectedEndQ1("2021-04-01 00:00:00.0")
                .setExpectedEndQ2("2021-07-01 00:00:00.0")
                .setExpectedEndQ3("2021-10-01 00:00:00.0")
                .setExpectedEndQ4("2022-01-01 00:00:00.0")
                .setExpectedEndHalfYear("2021-07-01 00:00:00.0")
                .setExpectedEndH1("2021-07-01 00:00:00.0")
                .setExpectedEndH2("2022-01-01 00:00:00.0")
                .setExpectedEndYear("2022-01-01 00:00:00.0")
                //
                .setExpectedEndYesterday("2021-05-12 00:00:00.0")
                .setExpectedEndLastWeek("2021-05-10 00:00:00.0")
                .setExpectedEndLastMonth("2021-05-01 00:00:00.0")
                .setExpectedEndLastQuarter("2021-04-01 00:00:00.0")
                .setExpectedEndLastHalfYear("2021-01-01 00:00:00.0")
                .setExpectedEndLastYear("2021-01-01 00:00:00.0")
        );
    }

    @Test
    public void expressingToMorning() {
        for (TestTime t: testTimeList) {
            // 当前时间 当天 开始时间
            assertEquals("当天 开始时间", t.expectedBeginDay, DateUtil.Expressing.toMorning("day", t.inputTime).toString());
            // 当前时间 本周 开始时间 按周一计算
            assertEquals("本周 开始时间", t.expectedBeginWeek, DateUtil.Expressing.toMorning("week",t.inputTime).toString());
            // 当前时间 本月 开始时间
            assertEquals("本月 开始时间", t.expectedBeginMonth, DateUtil.Expressing.toMorning("month",t.inputTime).toString());
            // 当前时间 本季度 开始时间
            assertEquals("本季 开始时间", t.expectedBeginQuarter, DateUtil.Expressing.toMorning("quarter",t.inputTime).toString());
            assertEquals("本年1季 开始时间", t.expectedBeginQ1, DateUtil.Expressing.toMorning("q1",t.inputTime).toString());
            assertEquals("本年2季 开始时间", t.expectedBeginQ2, DateUtil.Expressing.toMorning("q2",t.inputTime).toString());
            assertEquals("本年3季 开始时间", t.expectedBeginQ3, DateUtil.Expressing.toMorning("q3",t.inputTime).toString());
            assertEquals("本年4季 开始时间", t.expectedBeginQ4, DateUtil.Expressing.toMorning("q4",t.inputTime).toString());
            // 当前时间 本半年 开始时间
            assertEquals("这半年 开始时间", t.expectedBeginHalfYear, DateUtil.Expressing.toMorning("halfYear",t.inputTime).toString());
            assertEquals("本年上半年 开始时间", t.expectedBeginH1, DateUtil.Expressing.toMorning("h1",t.inputTime).toString());
            assertEquals("本年下半年 开始时间", t.expectedBeginH2, DateUtil.Expressing.toMorning("h2",t.inputTime).toString());
            // 当前时间 本年 开始时间
            assertEquals("本年 开始时间", t.expectedBeginYear, DateUtil.Expressing.toMorning("year",t.inputTime).toString());

            // 当前时间 昨天 开始时间
            assertEquals("昨天 开始时间", t.expectedBeginYesterday, DateUtil.Expressing.toMorning("yesterday",t.inputTime).toString());
            // 当前时间 上周 开始时间
            assertEquals("上周 开始时间", t.expectedBeginLastWeek, DateUtil.Expressing.toMorning("lastWeek",t.inputTime).toString());
            // 当前时间 上个月 开始时间
            assertEquals("上月 开始时间", t.expectedBeginLastMonth, DateUtil.Expressing.toMorning("lastMonth",t.inputTime).toString());
            // 当前时间 上个季度 开始时间
            assertEquals("上季 开始时间", t.expectedBeginLastQuarter, DateUtil.Expressing.toMorning("lastQuarter",t.inputTime).toString());
            // 当前时间 上个半年 开始时间
            assertEquals("上个半年 开始时间", t.expectedBeginLastHalfYear, DateUtil.Expressing.toMorning("lastHalfYear",t.inputTime).toString());
            // 当前时间 去年 开始时间
            assertEquals("去年 开始时间", t.expectedBeginLastYear, DateUtil.Expressing.toMorning("lastYear",t.inputTime).toString());
        }

        // 仅仅有年月场景
        // ===================================================================================================================
        // 当前时间 当天 开始时间
        assertEquals("2018-08-01 00:00:00.0", DateUtil.Expressing.toMorning("day","2018-08").toString());
        // 当前时间 本周 开始时间 按周一计算
        assertEquals("2018-07-30 00:00:00.0", DateUtil.Expressing.toMorning("week","2018-08").toString());
        // 当前时间 本月 开始时间
        assertEquals("2018-08-01 00:00:00.0", DateUtil.Expressing.toMorning("month","2018-08").toString());
        // 当前时间 本季度 开始时间
        assertEquals("2018-07-01 00:00:00.0", DateUtil.Expressing.toMorning("quarter","2018-08").toString());
        // 当前时间 本年 开始时间
        assertEquals("2018-01-01 00:00:00.0", DateUtil.Expressing.toMorning("year","2018-08").toString());
        // ===================================================================================================================
        // 未传入时间时，避免空指针
        DateUtil.Expressing.toMorning("day").toString();
        DateUtil.Expressing.toMorning("week").toString();
        DateUtil.Expressing.toMorning("month").toString();
        DateUtil.Expressing.toMorning("quarter").toString();
        DateUtil.Expressing.toMorning("year").toString();
    }

    @Test
    public void expressingToNight() {
        for (TestTime t: testTimeList) {
            // 当前时间 当天 结束时间（不包含）
            assertEquals("当天 结束时间（不包含）", t.expectedEndDay, DateUtil.Expressing.toNight("day",t.inputTime).toString());
            // 当前时间 本周 结束时间（不包含）
            assertEquals("本周 结束时间（不包含）", t.expectedEndWeek, DateUtil.Expressing.toNight("week",t.inputTime).toString());
            // 当前时间 本月 结束时间（不包含）
            assertEquals("本月 结束时间（不包含）", t.expectedEndMonth, DateUtil.Expressing.toNight("month",t.inputTime).toString());
            // 当前时间 本季度 结束时间（不包含）
            assertEquals("本季 结束时间（不包含）", t.expectedEndQuarter, DateUtil.Expressing.toNight("quarter",t.inputTime).toString());
            assertEquals("本年1季 结束时间（不包含）", t.expectedEndQ1, DateUtil.Expressing.toNight("q1",t.inputTime).toString());
            assertEquals("本年2季 结束时间（不包含）", t.expectedEndQ2, DateUtil.Expressing.toNight("q2",t.inputTime).toString());
            assertEquals("本年3季 结束时间（不包含）", t.expectedEndQ3, DateUtil.Expressing.toNight("q3",t.inputTime).toString());
            assertEquals("本年4季 结束时间（不包含）", t.expectedEndQ4, DateUtil.Expressing.toNight("q4",t.inputTime).toString());
            // 当前时间 本半年 结束时间（不包含）
            assertEquals("这半年 结束时间（不包含）", t.expectedEndHalfYear, DateUtil.Expressing.toNight("halfYear",t.inputTime).toString());
            assertEquals("本年上半年 结束时间（不包含）", t.expectedEndH1, DateUtil.Expressing.toNight("h1",t.inputTime).toString());
            assertEquals("本年下半年 结束时间（不包含）", t.expectedEndH2, DateUtil.Expressing.toNight("h2",t.inputTime).toString());

            // 当前时间 本年 结束时间（不包含）
            assertEquals("本年 结束时间（不包含）", t.expectedEndYear, DateUtil.Expressing.toNight("year",t.inputTime).toString());

            // 当前时间 昨天 结束时间（不包含）
            assertEquals("昨天 结束时间（不包含）", t.expectedEndYesterday, DateUtil.Expressing.toNight("yesterday",t.inputTime).toString());
            // 当前时间 上周 结束时间（不包含）
            assertEquals("上周 结束时间（不包含）", t.expectedEndLastWeek, DateUtil.Expressing.toNight("lastWeek",t.inputTime).toString());
            // 当前时间 上月 结束时间（不包含）
            assertEquals("上月 结束时间（不包含）", t.expectedEndLastMonth, DateUtil.Expressing.toNight("lastMonth",t.inputTime).toString());
            // 当前时间 上季度 结束时间（不包含）
            assertEquals("上季 结束时间（不包含）", t.expectedEndLastQuarter, DateUtil.Expressing.toNight("lastQuarter",t.inputTime).toString());
            // 当前时间 上个半年 结束时间（不包含）
            assertEquals("前个半年 结束时间（不包含）", t.expectedEndLastHalfYear, DateUtil.Expressing.toNight("lastHalfYear",t.inputTime).toString());
            // 当前时间 去年 结束时间（不包含）
            assertEquals("上年 结束时间（不包含）", t.expectedEndLastYear, DateUtil.Expressing.toNight("lastYear",t.inputTime).toString());
        }

        // 仅仅有年月场景
        // ===================================================================================================================

        // 0 点
        assertEquals("2021-08-02 00:00:00.0", DateUtil.Expressing.toNight("day","2021-08").toString());
        // 周一
        assertEquals("2021-08-02 00:00:00.0", DateUtil.Expressing.toNight("week","2021-08").toString());
        // 1号
        assertEquals("2021-09-01 00:00:00.0", DateUtil.Expressing.toNight("month","2021-08").toString());
        // 1号
        assertEquals("2022-01-01 00:00:00.0", DateUtil.Expressing.toNight("year","2021-08").toString());
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
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2022-01-01 00:00:00.000"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 22
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2022-01-01 00:00:00.00"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 21
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2022-01-01 00:00:00.0"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 19
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2022-01-01 00:00:00"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 16
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2022-01-01 00:00"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 14
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("20220101000000"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        // 12
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("220101000000"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 10
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2022-01-01"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 8
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("22-01-01"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 8
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("20220101"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 7
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2022-01"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 6
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("202201"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 5
        assertEquals("1970-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("00:00"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));

        // 4
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis("2022"), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));


        // 东8区时间
        if(TimeZone.getDefault().getID().equalsIgnoreCase("Asia/Shanghai")) {
            // 毫秒数
            assertEquals("1970-01-01 08:00:00.000",
                    DateUtil.toTimeString(DateUtil.toTimeMillis(0), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));
        }

        // TimestampDateUtil
        assertEquals("2022-01-01 00:00:00.000",
                DateUtil.toTimeString(DateUtil.toTimeMillis(Timestamp.valueOf("2022-01-01 00:00:00.000")), DateUtil.Formatter.PATTERN_YYYY_MM_DD_HH_MM_SS_SSS));


        // LocalDate
        LocalDate date = LocalDate.of(2022, 1, 2);
        String settleDate = DateUtil.toTimeString(date, DateUtil.Formatter.PATTERN_YYYY_MM_DD_PURE);

         log.info("{}", settleDate);

    }

    @Test
    public void toList() {
        Object[] testArray = new Object[] {
                DateUtil.Expressing.toList("year", "year"),
                DateUtil.Expressing.toList("halfYear", "year"),
                DateUtil.Expressing.toList("quarter", "year"),
                DateUtil.Expressing.toList("quarter", "quarter"),
                DateUtil.Expressing.toList("month", "quarter"),
                DateUtil.Expressing.toList("month", "month"),
                DateUtil.Expressing.toList("day", "month"),
                DateUtil.Expressing.toList("day", "day"),
                DateUtil.Expressing.toList("hour", "day"),
                DateUtil.Expressing.toList("week", "year"),
                DateUtil.Expressing.toList("week", "month"),
                DateUtil.Expressing.toList("week", "week"),
                DateUtil.Expressing.toList("day", "week"),

                DateUtil.Expressing.toList("year", "year", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("halfYear", "year", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("quarter", "year", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("quarter", "quarter", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("month", "quarter", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("month", "month", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("day", "month", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("day", "day", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("hour", "day", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("week", "year", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("week", "month", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("week", "week", "2022-01-01 00:00:00"),
                DateUtil.Expressing.toList("day", "week", "2022-01-01 00:00:00"),
        };
        for (Object test: testArray) {
            List<DateUtil.ExpressingTime> expressingTimeList = (List<DateUtil.ExpressingTime>)test;
            int i = 1;
            log.info("" //
               + expressingTimeList.get(0).stepType + " in " + expressingTimeList.get(0).rangeType
            );
            for (DateUtil.ExpressingTime expressingTime: expressingTimeList ) {
                log.info("" //
                        + i++ + ". "
                        + "range: [ " + DateUtil.toTimeString(expressingTime.beginTime) + ", " + DateUtil.toTimeString(expressingTime.endTime) + " )"
                        + ", statisticId: " + IdGeneration.getStatisticId(expressingTime.stepType, expressingTime.beginTime)
                );
            }
            log.info("");
        }

    }

    @Test
    public void toListNull() {
        DateUtil.Expressing.toList("day", "year");
        Object statTimePoint = "";
        DateUtil.Expressing.toList("day", "year", statTimePoint);
        statTimePoint = null;
        DateUtil.Expressing.toList("day", "year", statTimePoint);
    }

    @Test
    public void toTimeChinaWeekOfYear() {
    }

    @Test
    public void toTimeString() {
        assertEquals("2021-01-01 00:00:00", DateUtil.toTimeString("2021"));
        assertEquals("2021-01-01 00:00:00", DateUtil.toTimeString("2021-01"));
        assertEquals("2021-01-01 00:00:00", DateUtil.toTimeString("2021-01", null));
        assertEquals("2021-01-01 00:00:00", DateUtil.toTimeString("2021-01", ""));
        assertEquals("2021-01-01 00:00:00", DateUtil.toTimeString("20210101", ""));
        assertEquals("2021-01", DateUtil.toTimeString("2021-01", "yyyy-MM"));


        assertEquals("2021-01-01", DateUtil.toDateString("2021-01"));
    }

    @Test
    public void toNow() {
        DateUtil.toNow();
        DateUtil.toNow(null);
        DateUtil.toNow("HH");
        log.info(String.valueOf(JsonHelper.toInt(DateUtil.toNow("HH"), 0)));
    }

    @Test
    public void toNowDateString() {
        DateUtil.toNowDateString();
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
