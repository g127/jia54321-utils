package com.jia54321.utils.cache;

import com.jia54321.utils.MailTest;
import com.jia54321.utils.cache.caffeine.CaffeineCache;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class ICacheTest {
    static final Logger log = LoggerFactory.getLogger(ICacheTest.class);

    private static Long timeToLiveSeconds = 24 * 60 * 60L;  // 一天
    private static Long timeToIdleSeconds = 0L;
    public static final ICache CACHE = CaffeineCache.register("Test", timeToLiveSeconds, timeToIdleSeconds);

    @Test
    public void getCacheName() {
        String key = "1";
        String val = "output";
        try {
            if( null == CACHE.get(key) ) {
                CACHE.put(key, val);
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(String.valueOf(CACHE.get(key)));
                Thread.sleep(4000);
            }
            Thread.sleep(timeToIdleSeconds * 1000 - 1000);

            Assert.assertEquals(val, String.valueOf(CACHE.get(key)));

            Thread.sleep(timeToIdleSeconds * 1000 - 1000);
            Assert.assertEquals("null", String.valueOf(CACHE.get(key)));

            Thread.sleep(timeToLiveSeconds * 1000 + 1000);
            Assert.assertEquals("null", String.valueOf(CACHE.get(key)));

        } catch (Exception e) {
            log.error("getCacheName", e);
        }
    }

    @Test
    public void clear() {
    }

    @Test
    public void get() {
    }

    @Test
    public void testGet() {
    }

    @Test
    public void keys() {
    }

    @Test
    public void put() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void putTemporary() {
    }

    @Test
    public void ttl() {
    }

    @Test
    public void getTimeToLiveSeconds() {
    }

    @Test
    public void getTimeToIdleSeconds() {
    }
}