package com.jia54321.utils;

import com.jia54321.utils.event.EventManagerTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;

import static org.junit.Assert.*;

public class IdGenerationTest {

    static final Logger log = LoggerFactory.getLogger(IdGenerationTest.class);
    @Test
    public void getStringId() {
        log.info(IdGeneration.getStringId());

    }

    @Test
    public void getStringUUID() {
        log.info(IdGeneration.getStringUUID());
    }

    @Test
    public void getOID() {
        log.info(IdGeneration.getOID());
    }

    @Test
    public void getSnowflakeId() {
        log.info(IdGeneration.getSnowflakeId());
    }

    @Test
    public void getStatisticId() {
        String time = DateUtil.toNowTimeString();
        System.out.println("默认:" + IdGeneration.getStatisticId("day", time));
        assertEquals("按年:", IdGeneration.getStatisticId("year",     time,1, 0, 0, 0, 0, 0, 0), IdGeneration.getStatisticId("year",     time));
        assertEquals("按月:", IdGeneration.getStatisticId("month",    time,1, 1, 0, 0, 0, 0, 0), IdGeneration.getStatisticId("month",    time));
        assertEquals("按天:", IdGeneration.getStatisticId("day",      time,1, 1, 1, 0, 0, 0, 0), IdGeneration.getStatisticId("day",      time));
        assertEquals("小时:", IdGeneration.getStatisticId("hour",     time,1, 1, 1, 1, 0, 0, 0), IdGeneration.getStatisticId("hour",     time));
        assertEquals("半年:", IdGeneration.getStatisticId("halfYear", time,1, 0, 0, 0, 1, 0, 0), IdGeneration.getStatisticId("halfYear", time));
        assertEquals("季度:", IdGeneration.getStatisticId("quarter",  time,1, 0, 0, 0, 0, 1, 0), IdGeneration.getStatisticId("quarter",  time));
        assertEquals("按周:", IdGeneration.getStatisticId("week",     time,1, 0, 0, 0, 0, 0, 1), IdGeneration.getStatisticId("week",     time));

        // 循环获取
        // 按年, 今年
        long beginYear = DateUtil.Expressing.toBegin("year").getTime();
        long endYear   = DateUtil.Expressing.toEnd("year").getTime();
        long step      = 1000 * 60 * 60 * 24; // 一天
        for (long i = beginYear; i < endYear; i += step) {
            System.out.println(""  //
                    + "统计Id: " + IdGeneration.getStatisticId("day", i) + "," //
                    + "统计时间: " + DateUtil.toDateString(i) + " ~ " + DateUtil.toDateString(i + step) //
            );
        }
    }
}
