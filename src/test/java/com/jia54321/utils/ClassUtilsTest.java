package com.jia54321.utils;

import com.jia54321.utils.entity.query.QueryContent;
import com.jia54321.utils.entity.query.SimpleQueryContent;
import junit.framework.Assert;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class ClassUtilsTest {

    @Test
    public void isAssignableFrom() {
        Assert.assertTrue("isAssignableFrom", ClassUtils.isAssignableFrom(Integer.class, 1));
        Assert.assertTrue("isAssignableFrom", ClassUtils.isAssignableFrom("java.lang.Integer", 1));
        //queryContentOrSimpleQueryContent.getClass().getName().equals(SimpleQueryContent.class.getName())
        Assert.assertTrue("isAssignableFrom", ClassUtils.isAssignableFrom(SimpleQueryContent.class, new SimpleQueryContent<>()));
        Assert.assertTrue("isAssignableFrom", ClassUtils.isAssignableFrom(QueryContent.class, new QueryContent<>()));
    }

    @Test
    public void getSelfPropertyDescriptors() {
        Assert.assertTrue("getSelfPropertyDescriptors", ClassUtils.getSelfPropertyDescriptors(ClassUtilsTest.class).length >= 0);
    }

    @Test
    public void isPrimitiveOrWrapper() {
        Assert.assertTrue("isPrimitiveOrWrapper", ClassUtils.isPrimitiveOrWrapper(new Boolean("1").getClass()));
        Assert.assertTrue("isPrimitiveOrWrapper", ClassUtils.isPrimitiveOrWrapper(new Byte("1").getClass()));
        Assert.assertTrue("isPrimitiveOrWrapper", ClassUtils.isPrimitiveOrWrapper(new Character('1').getClass()));
        Assert.assertTrue("isPrimitiveOrWrapper", ClassUtils.isPrimitiveOrWrapper(new Double("1").getClass()));
        Assert.assertTrue("isPrimitiveOrWrapper", ClassUtils.isPrimitiveOrWrapper(new Float("1").getClass()));
        Assert.assertTrue("isPrimitiveOrWrapper", ClassUtils.isPrimitiveOrWrapper(new Integer("1").getClass()));
        Assert.assertTrue("isPrimitiveOrWrapper", ClassUtils.isPrimitiveOrWrapper(new Long("1").getClass()));
        Assert.assertTrue("isPrimitiveOrWrapper", ClassUtils.isPrimitiveOrWrapper(new Short("1").getClass()));
        Assert.assertTrue("isPrimitiveOrWrapper", ClassUtils.isPrimitiveOrWrapper(new BigInteger("1").getClass()));
    }

    @Test
    public void isTimePropType() {
        Assert.assertTrue("isTimePropType", ClassUtils.isTimePropType(new java.util.Date().getClass()));
        //
        Assert.assertFalse("isTimePropType", ClassUtils.isTimePropType(new Long("1").getClass()));
    }

}
