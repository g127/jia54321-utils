package com.jia54321.utils.clock;

import com.jia54321.utils.DateUtilTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class SystemTimerTest {
    static final Logger log = LoggerFactory.getLogger(DateUtilTest.class);

    @Test
    public void addTimerListener() throws InterruptedException {
        AtomicInteger tt = new AtomicInteger();
        SystemTimer.addTimerListener(t -> {
            log.info( "" + (tt.getAndIncrement()) + "==" + t);
        });
       // Thread.sleep(1000);
        for (int i = 0; i < 100; i++) {
            //log.info(String.valueOf(SystemTimer.currentTimeMillis()));
        }
        Thread.sleep(2000);
        log.info("addTimerListener");
    }


    @Test
    public void removeTimerListener() throws InterruptedException {
        SystemTimer.removeTimerListener();
    }

}