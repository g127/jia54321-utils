package com.jia54321.utils;

import junit.framework.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * 测试用例
 */
public class RangeUtilTest {

    // 转换
    private final static Function<double[], BigDecimal[]> toBigDecimals = new Function<double[], BigDecimal[]>() {
        @Override
        public BigDecimal[] apply(double[] doubles) {
            BigDecimal[] v = new BigDecimal[doubles.length];
            for (int i = 0; i < doubles.length; i++) {
                v[i] = BigDecimal.valueOf(doubles[i]);
            }
            return v;
        }
    };

    @Test
    public void idxCallback_time_begin_end() {
        // 开始时间
        long begin = System.currentTimeMillis();
        // 结束时间  延后 1 小时
        long end  = System.currentTimeMillis() + 1 * 60 * 60 * 1000L;

        // 计算 某个时间点 在 开始时间，结束时间 的位置索引
        Function<Long, String> calcRangeVal = v -> {
            //
            return (String)RangeUtil.idxCallback(v , (idx) -> {
                // 按需处理5个值 [-0.5, 0, 0.5, 1, 1.5 ]
                if( idx == -0.5f ) {
                    return "小于开始时间";
                } else if( idx == 0f ) {
                    return "等于开始时间";
                } else if( idx == 0.5f ) {
                    return "大于开始时间且小于结束时间";
                } else if( idx == 1f ) {
                    return "等于结束时间";
                } else if( idx == 1.5f ) {
                    return "大于结束时间";
                }
                return "";
            } , new long[]{ begin, end });
        };

        //
        Assert.assertEquals("", "小于开始时间", calcRangeVal.apply((begin - 1 )));
        //
        Assert.assertEquals("", "等于开始时间", calcRangeVal.apply((begin ) ));
        //
        Assert.assertEquals("", "大于开始时间且小于结束时间", calcRangeVal.apply((begin + 1 ) ));
        //
        Assert.assertEquals("", "等于结束时间", calcRangeVal.apply((end  )));
        //
        Assert.assertEquals("", "大于结束时间", calcRangeVal.apply((end + 1) ));
    }

    @Test
    public void idxCallback_water_lake_phosphorus() {
        // 标准值 总磷(湖、库) <= 小于等于 最低 1类开始
        double[] std_water_lake_phosphorus  = { 0.01, 0.025, 0.05, 0.1, 0.2 };
        BigDecimal[] stds  = toBigDecimals.apply(std_water_lake_phosphorus);

        // 计算 总磷 在(湖、库)的等级
        // 参考《地表水环境质量标准（GB3838-2002)》表1  http://www.cnemc.cn/
        Function<String, String> water_lake_phosphorus = v -> {
            //
            return (String)RangeUtil.idxCallback(new BigDecimal(v) , (idx) -> {
                if( idx <= 0f ) {
                    return "1";
                } else if(idx <= 1f ) {
                    return "2";
                } else if(idx <= 2f ) {
                    return "3";
                } else if(idx <= 3f ) {
                    return "4";
                } else if(idx <= 4f ) {
                    return "5";
                }
                return "6";
            } , stds);
        };

        //
        Assert.assertEquals("", "1", water_lake_phosphorus.apply("0.01"));
        //
        Assert.assertEquals("", "2", water_lake_phosphorus.apply("0.020"));
        //
        Assert.assertEquals("", "2", water_lake_phosphorus.apply("0.025"));
        //
        Assert.assertEquals("", "3", water_lake_phosphorus.apply("0.030"));
    }

    @Test
    public void idx() {
        //
        BigDecimal[] stds = toBigDecimals.apply(new double[]{ 100 });

        //
        for (int i = 0; i < stds.length ; i++) {
            junit.framework.Assert.assertEquals("-", new Float(i - 0.5), RangeUtil.idx(stds[i].subtract(BigDecimal.ONE) ,stds));
            junit.framework.Assert.assertEquals("=", new Float(i), RangeUtil.idx(stds[i] ,stds));
            junit.framework.Assert.assertEquals("+", new Float(i + 0.5), RangeUtil.idx(stds[i].add(BigDecimal.ONE) ,stds));
        }

        stds = toBigDecimals.apply(new double[]{ 100, 200 });
        //
        for (int i = 0; i < stds.length ; i++) {

            junit.framework.Assert.assertEquals("-", new Float(i - 0.5), RangeUtil.idx(stds[i].subtract(BigDecimal.ONE) ,stds));
            junit.framework.Assert.assertEquals("=", new Float(i), RangeUtil.idx(stds[i] ,stds));
            junit.framework.Assert.assertEquals("+", new Float(i + 0.5), RangeUtil.idx(stds[i].add(BigDecimal.ONE) ,stds));

        }

        stds = toBigDecimals.apply(new double[]{ 100, 200, 300 });
        //
        for (int i = 0; i < stds.length ; i++) {

            junit.framework.Assert.assertEquals("-", new Float(i - 0.5), RangeUtil.idx(stds[i].subtract(BigDecimal.ONE) ,stds));
            junit.framework.Assert.assertEquals("=", new Float(i), RangeUtil.idx(stds[i] ,stds));
            Assert.assertEquals("+", new Float(i + 0.5), RangeUtil.idx(stds[i].add(BigDecimal.ONE) ,stds));

        }
    }

}
