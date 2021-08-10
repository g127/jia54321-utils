package com.jia54321.utils;

import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CamelNameUtilTest {

    @Test
    public void camelToUnderline() {
        String[][] tests = new String[][]{
                // expected       input
                { "create_time", "createTime" },
                { "create_time", "CreateTime" },
                { "create_time", "Create_Time" },
                { "create_time", "CREATE_TIME" },
                { "create_time", "CREATE_TIME" },
                { "create_time", "CrEaTe_TiMe" },
                { "", "" },
        };

        for (int i = 0; i < tests.length; i++) {
            Assert.assertEquals("" + i, tests[i][0], CamelNameUtil.camelToUnderline(tests[i][1]));
        }

    }

    @Test
    public void camelToUnderlineUpperCase() {
    }

    @Test
    public void underlineToCamelLowerCase() {
    }

    @Test
    public void hyphenToCamelLowerCase() {
    }

    @Test
    public void camelToXX() {
        Object[][] tests = new Object[][]{
                { "create_time", CamelNameUtil.UNDERLINE, "createTime" },
                { "create-time", CamelNameUtil.HYPHEN, "createTime" },
                { "create-time", CamelNameUtil.HYPHEN, "CreateTime" },
        };

        for (int i = 0; i < tests.length; i++) {
            Assert.assertEquals("" + i, tests[i][0], CamelNameUtil.camelToXX((char)tests[i][1],(String)tests[i][2]));
        }
    }

    @Test
    public void xxToCamelLowerCase() {
        Object[][] tests = new Object[][]{
                { "createTime", CamelNameUtil.UNDERLINE, "create_time" },
                { "createTime", CamelNameUtil.HYPHEN, "create-time" },
                { "createTime", CamelNameUtil.HYPHEN, "Create-Time" },
                { "createTime", CamelNameUtil.HYPHEN, "CReate-Time" },
        };

        for (int i = 0; i < tests.length; i++) {
            Assert.assertEquals("" + i, tests[i][0], CamelNameUtil.xxToCamelLowerCase((char)tests[i][1],(String)tests[i][2]));
        }
    }
}
