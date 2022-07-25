package com.jia54321.utils;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * 连续值的范围相关工具类
 */
public class RangeUtil {

    // 转换
    private final static Function<double[], BigDecimal[]> doubleToBigDecimals = new Function<double[], BigDecimal[]>() {
        @Override
        public BigDecimal[] apply(double[] doubles) {
            BigDecimal[] v = new BigDecimal[doubles.length];
            for (int i = 0; i < doubles.length; i++) {
                v[i] = BigDecimal.valueOf(doubles[i]);
            }
            return v;
        }
    };
    // 转换
    private final static Function<long[], BigDecimal[]> longToBigDecimals = new Function<long[], BigDecimal[]>() {
        @Override
        public BigDecimal[] apply(long[] doubles) {
            BigDecimal[] v = new BigDecimal[doubles.length];
            for (int i = 0; i < doubles.length; i++) {
                v[i] = BigDecimal.valueOf(doubles[i]);
            }
            return v;
        }
    };

    /**
     * <b>查找样本索引</b> <br/>
     * 在连续的标准值数组stds中，查找样本值val 的区间索引（ 索引取值 正负0.5表示 ） <br/>
     * @param val  样本值 double
     * @param callback 回调函数
     * @param stds 连续的标准值数组 格式为 double[]
     * @return callback处理后的结果
     */
    public static Object idxCallback(double val, Function<Float, Object> callback, double... stds) {
        return callback.apply(idx(BigDecimal.valueOf(val), doubleToBigDecimals.apply(stds)));
    }

    /**
     * <b>查找样本索引</b> <br/>
     * 在连续的标准值数组stds中，查找样本值val 的区间索引（ 索引取值 正负0.5表示 ） <br/>
     * @param val  样本值 long
     * @param callback 回调函数
     * @param stds 连续的标准值数组 格式为 long[]
     * @return callback处理后的结果
     */
    public static Object idxCallback(long val, Function<Float, Object> callback, long... stds) {
        return callback.apply(idx(BigDecimal.valueOf(val), longToBigDecimals.apply(stds)));
    }

    /**
     * <b>查找样本索引</b> <br/>
     * 在连续的标准值数组stds中，查找样本值val 的区间索引（ 索引取值 正负0.5表示 ） <br/>
     * @param val  样本值 BigDecimal
     * @param callback 回调函数
     * @param stds 连续的标准值数组 格式为 BigDecimal[]
     * @return callback处理后的结果
     */
    public static Object idxCallback(BigDecimal val, Function<Float, Object> callback, BigDecimal... stds) {
        return callback.apply(idx(val, stds));
    }


    /**
     * <b>查找样本索引</b> <br/>
     * 在连续的标准值数组stds中，查找样本值val 的区间索引（ 索引取值 正负0.5表示 ） <br/>
     * <code>
     *     stds[0...N-1] :  A(i) <br/>
     *     idx:  Idx(i-0.5), A(i), Idx(i+0.5) <br/>
     * </code>
     * 例如：标准值数组stds为 [ 100, 200]  <br/>
     * <code>
     *     样本val 为 50 （小于stds[0]） 时，返回 -0.5 <br/>
     *     样本val 为 100 （等于stds[0]） 时，返回 0   <br/>
     *     样本val 为 150 （大于stds[0],小于stds[1]） 时，返回 0.5 <br/>
     *     样本val 为 200 （等于stds[1]）时，返回 1.0 <br/>
     *     样本val 为 250 （大于stds[1]）时，返回 1.5 <br/>
     * </code>
     * @param val  样本值 BigDecimal
     * @param stds 连续的标准值数组 格式为 BigDecimal[]
     * @return idx 返回 float 形式索引表示, 如 -0.5, 0, 0.5 之类
     */
    public static float idx(BigDecimal val, BigDecimal... stds) {
        // 初始化索引值，最大索引值
        float initIdx = 0 - 0.5f;
        float maxIdx = initIdx + stds.length - 1 + 0.5f ;
        // 区间范围索引
        float rangeIdx = maxIdx; //默认为最大索引
        // 比较值
        float compare = 0f;

        // 是否为递增stds ，简单判断前两位值 , 即 第1位小于第2位 true  ，反之 false
        // [100, 200]为增量数组，  [200, 100]为减量数组
        boolean increaseStds = stds.length >= 2 ? ( stds[0].compareTo(stds[1]) == -1 ) :true;

        // ---------------------------------
        // 简单二分查找 开始
        // ---------------------------------
        int start = 0;
        int end = stds.length - 1;
        while (start <= end) {
            int mid = start + (end - start) / 2;
            // 比较
            compare = val.compareTo(stds[mid])/2f;
            if (compare == 0f) {  // 相等
                rangeIdx = mid + compare;
                break;
            }

            if (  ( compare < 0f && increaseStds ) || ( compare > 0f && !increaseStds) ) {  // 小于且增量数组  或者 大于且减量数组
                // end 向前移动 <=
                end = mid - 1;
                // 跳出前，设置索引值
                if(start > end) {
                    rangeIdx = mid + compare;
                    break;
                }
            }

            if ( (compare > 0f && increaseStds ) || ( compare < 0f && !increaseStds) ) { // 大于且增量数组 或者 小于且减量数组
                // start 向后移动 =>
                start = mid + 1;
                // 跳出前，设置索引值
                if(start > end) {
                    rangeIdx = mid + compare;
                    break;
                }
            }
        }
        // ---------------------------------
        // 简单二分查找 结束
        // ---------------------------------
        return rangeIdx;

//        // std值遍历，从小到大递增 o(n) 待优化
//        for (int i = 0; i < stds.length; i++) {
//            // 比较
//            pos = val.compareTo(stds[i])/2f;
//            // 结果为 小于或等于
//            if ( pos <= 0 ) {
//                rangeIdx = initIdx + i + pos;
//                break;
//            }
//            // 结果为 大于，进入下次循环
//
//            // 最后,多比较一次
//            if( i == stds.length - 1 ) {
//                if (pos > 0) {
//                    rangeIdx = initIdx + i + pos ;
//                    break;
//                }
//            }
//        }
//        return rangeIdx;
    }
}
