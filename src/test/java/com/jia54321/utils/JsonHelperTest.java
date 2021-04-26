package com.jia54321.utils;

import junit.framework.Assert;
import org.checkerframework.checker.units.qual.A;
import org.easymock.EasyMock;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonHelperTest {

    @Test
    public void toJson() {
        Assert.assertTrue("toJson",  JsonHelper.toJson(new Object()).length() > 0);
    }

    @Test
    public void fromJson() {
        Assert.assertTrue("fromJson",  JsonHelper.fromJson("{}", Object.class) != null);
    }

    @Test
    public void fromJsonAsLinkedHashMap() {
        Assert.assertTrue("fromJsonAsLinkedHashMap",  JsonHelper.fromJsonAsLinkedHashMap("{}") != null);
    }

    @Test
    public void fromJsonAsHashMap() {
        Assert.assertTrue("fromJsonAsHashMap",  JsonHelper.fromJsonAsLinkedHashMap("{}") != null);
    }

    @Test
    public void fromJsonToList() {
        Assert.assertTrue("fromJsonToList",  JsonHelper.fromJsonToList("[]", String.class).size() == 0);
    }

    @Test
    public void fromJsonToHashMapList() {
        Assert.assertTrue("fromJsonToHashMapList",  JsonHelper.fromJsonToHashMapList("[]").size() == 0);
    }

    @Test
    public void fromJsonToLinkedHashMapList() {
        Assert.assertTrue("fromJsonToLinkedHashMapList",  JsonHelper.fromJsonToLinkedHashMapList("[]").size() == 0);
    }

    @Test
    public void fromJsonToMapList() {
        Assert.assertTrue("fromJsonToMapList",  JsonHelper.fromJsonToMapList("[]").size() == 0);
    }

    @Test
    public void toJSONString() {
        Assert.assertTrue("fromJsonToMapList",  JsonHelper.toJSONString(new Object()).length() > 0);
        Assert.assertTrue("fromJsonToMapList",  JsonHelper.toJSONString(new Object(), true).length() > 0);
    }

    @Test
    public void cast() {
    }

    @Test
    public void testCast() {
    }

    @Test
    public void parseObject() {
    }

    @Test
    public void parseArray() {
    }

    @Test
    public void toMapList() {
    }

    @Test
    public void overrideObject() {
    }

    @Test
    public void toFilePrintSize() {
    }

    @Test
    public void isEmpty() {
    }

    @Test
    public void toInt() {
    }

    @Test
    public void toLong() {
    }

    @Test
    public void toFloat() {
    }

    @Test
    public void toDouble() {
    }

    @Test
    public void toHexString() {
    }

    @Test
    public void toRadixString() {
    }

    @Test
    public void toBigDecimal() {
    }

    @Test
    public void toStr() {
    }

    @Test
    public void toSplitAsLinkedHashSet() {
        // 1,2,3
        String[] test1 = new String[]{"3","2","1"};
        String test1String = "3,2,1";
        LinkedHashSet<String> xxxSet = JsonHelper.toSplitAsLinkedHashSet(test1String,",");
        assertArrayEquals(test1, xxxSet.toArray());
    }

    @Test
    public void toBoolean() {
    }

    @Test
    public void addStringToArray() {
    }

    @Test
    public void concatenateStringArrays() {
    }

    @Test
    public void getParametersStartingWith() {
        HttpServletRequest request = (HttpServletRequest) EasyMock.createMock(HttpServletRequest.class);
        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        parameterMap.put("userId", new String[]{ "1"});
        parameterMap.put("se_LIKE_userId", new String[]{ "1"});
        parameterMap.put("se_EQ_age", new String[]{ "1"});
        EasyMock.expect(request.getParameterMap()).andReturn(parameterMap).once();   //期望使用参数
        EasyMock.replay(request);//保存期望结果
        Map<String, Object> searchMap = JsonHelper.getParametersStartingWith(request, "se_");
        assertNotNull("LIKE_userId",  searchMap.get("LIKE_userId"));
        assertNotNull("EQ_age",  searchMap.get("EQ_age"));
    }

    @Test
    public void encodeParameterStringWithPrefix() {
    }

    @Test
    public void encodeUrl() {
    }

    @Test
    public void decodeUrl() {
    }

    @Test
    public void encodeUnicode() {
    }

    @Test
    public void decodeUnicode() {
    }

    @Test
    public void encodeBase64() {
    }

    @Test
    public void decodeBase64() {
    }
}
