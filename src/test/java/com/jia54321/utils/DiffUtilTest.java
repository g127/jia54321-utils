package com.jia54321.utils;

import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * 测试用例
 */
public class DiffUtilTest {

    @Test
    public void diffList_Integer() {
        List<Integer> baseList = Lists.newArrayList(1, 2, 3, 4, 5);
        List<Integer> targetList = null;
        DiffUtil.DiffResult diffResult = null;

        targetList =  Lists.newArrayList(1, 2, 3, 4, 5);
        diffResult = DiffUtil.diffList(baseList, targetList, i -> i, (i,j) -> i - j );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 0);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 0);


        targetList = Lists.newArrayList(1, 2, 3, 4, 5, 6 );
        diffResult = DiffUtil.diffList(baseList, targetList, i -> i, (i,j) -> i - j );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 1);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 0);


        targetList = Lists.newArrayList(1, 2, 3, 4 );
        diffResult = DiffUtil.diffList(baseList, targetList, i -> i, (i,j) -> i - j );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 0);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 1);
    }


    @Test
    public void diffList_String() {
        List<String> baseList = Lists.newArrayList("1", "2", "3", "4", "5");
        List<String> targetList = null;
        DiffUtil.DiffResult diffResult = null;

        targetList =  Lists.newArrayList("1", "2", "3", "4", "5");
        diffResult = DiffUtil.diffList(baseList, targetList, i -> i, (i,j) -> i.compareTo(j) );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 0);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 0);


        targetList = Lists.newArrayList("1", "2", "3", "4", "5", "6");
        diffResult = DiffUtil.diffList(baseList, targetList, i -> i, (i,j) -> i.compareTo(j) );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 1);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 0);


        targetList = Lists.newArrayList("1", "2", "3", "4");
        diffResult = DiffUtil.diffList(baseList, targetList, i -> i, (i,j) -> i.compareTo(j) );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 0);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 1);
    }


    @Test
    public void diffList_Map() {
        List<Map<String, Object>> baseList = Lists.newArrayList(Kv.init().set("key", 1), Kv.init().set("key", 2), Kv.init().set("key", 3));
        List<Map<String, Object>> targetList = null;
        DiffUtil.DiffResult diffResult = null;

        Function<Map<String, Object>, Object> primaryKeyExtractor = i -> i.get("key");
        Comparator<Map<String, Object>> elementComparator = (i,j) -> JsonHelper.toInt(i.get("key"),0) - JsonHelper.toInt(j.get("key"),0);

        targetList =   Lists.newArrayList(Kv.init().set("key", 1), Kv.init().set("key", 2), Kv.init().set("key", 3));
        diffResult = DiffUtil.diffList(baseList, targetList, primaryKeyExtractor, elementComparator );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 0);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 0);


        targetList =   Lists.newArrayList(Kv.init().set("key", 1), Kv.init().set("key", 2), Kv.init().set("key", 3), Kv.init().set("key", 4));
        diffResult = DiffUtil.diffList(baseList, targetList, primaryKeyExtractor, elementComparator );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 1);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 0);


        targetList =   Lists.newArrayList(Kv.init().set("key", 1), Kv.init().set("key", 2));
        diffResult = DiffUtil.diffList(baseList, targetList, primaryKeyExtractor, elementComparator );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 0);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 1);


        targetList =   Lists.newArrayList(Kv.init().set("key", 1), Kv.init().set("key", 2), Kv.init().set("key", 4));
        diffResult = DiffUtil.diffList(baseList, targetList, primaryKeyExtractor, elementComparator );
        Assert.assertTrue("getAddedList", diffResult.getAddedList().size() == 1);
        Assert.assertTrue("getChangedList", diffResult.getChangedList().size() == 0);
        Assert.assertTrue("getDeletedList", diffResult.getDeletedList().size() == 1);
    }


}
