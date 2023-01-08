package com.jia54321.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

public class Helper extends DateUtil {

    private static final Logger log = LoggerFactory.getLogger(Helper.class);

    /** 空字符串 "" */
    public static final String STRING_EMPTY = "";

    /**
     * 文件大小 自动转换 B KB MB GB
     * @param size 文件大小
     * @return 文件大小
     */
    public static String toFilePrintSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        double value = (double) size;
        if (value < 1024) {
            return String.valueOf(value) + "B";
        } else {

            value = BigDecimal.valueOf( value / 1024 ).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (value < 1024) {
            return String.valueOf(value) + "KB";
        } else {
            value = BigDecimal.valueOf( value / 1024 ).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        if (value < 1024) {
            return String.valueOf(value) + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            value = BigDecimal.valueOf( value / 1024 ).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return String.valueOf(value) + "GB";
        }
    }


    /**
     * 切分总数量
     * @param splitNum  切分数量
     * @param list      总列表
     * @param <T>       实体类型
     * @return          子列表
     */
    public static <T> List<List<T>> toSplitList(int splitNum, List<T> list) {
        List<List<T>> splitList = new LinkedList<>();
        // groupFlag >= 1
        int groupFlag = list.size() % splitNum == 0 ? (list.size() / splitNum) : (list.size() / splitNum + 1);
        for (int j = 1; j <= groupFlag; j++) {
            if ((j * splitNum) <= list.size()) {
                splitList.add(list.subList(j * splitNum - splitNum, j * splitNum));
            } else if ((j * splitNum) > list.size()) {
                splitList.add(list.subList(j * splitNum - splitNum, list.size()));
            }
        }
        return splitList;
    }


    /**
     * <p>可判断字符是否为空  , 以下字符都将判断为空：</p>
     * <pre>
     *   null , "" , "null", "(null)", "undefined"
     * </pre>
     * <p>可判断java.util.Map是否为空 </p>
     * <p>可判断java.util.Collection是否为空 </p>
     * @param o input object
     * @return true or false
     */
    public static boolean isEmpty(Object o) {
        if(null == o || STRING_EMPTY.equals(o)) {
            return true;
        }
        if("null".equals(o)  || "(null)".equals(o) || "undefined".equals(o) ) {
            return true;
        }
        if(o instanceof Collection) {
            return ((Collection<?>)o).isEmpty();
        }
        if(o instanceof Map) {
            return ((Map<?,?>)o).isEmpty();
        }

        // 数组元素是否为空判断
        if (o instanceof Object[]) {
            return ((Object[]) o).length == 0;
        } else if (o instanceof boolean[]) {
            return ((boolean[]) o).length == 0;
        } else if (o instanceof byte[]) {
            return ((byte[]) o).length == 0;
        } else if (o instanceof char[]) {
            return ((char[]) o).length == 0;
        } else if (o instanceof double[]) {
            return ((double[]) o).length == 0;
        } else if (o instanceof float[]) {
            return ((float[]) o).length == 0;
        } else if (o instanceof int[]) {
            return ((int[]) o).length == 0;
        } else if (o instanceof long[]) {
            return ((long[]) o).length == 0;
        } else if (o instanceof short[]) {
            return ((short[]) o).length == 0;
        }
        return false;
    }

    public static boolean isNotEmpty(Object o) {
        return ! isEmpty(o);
    }

    /**
     * <p>Convert a <code>String</code> to an <code>int</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, the default value is returned.</p>
     *
     * <pre>
     *   NumberUtils.toInt(null, 1) = 1
     *   NumberUtils.toInt("", 1)   = 1
     *   NumberUtils.toInt("1", 0)  = 1
     * </pre>
     *
     * @param o  the string to convert, may be null
     * @param defVal  the default value
     * @return the int represented by the string, or the default if conversion fails
     */
    public static Integer toInteger(Object o, Integer defVal) {
        if (o == null) {
            return defVal;
        }
        if (o instanceof String) {
            try {
                return Integer.parseInt((String) o);
            } catch (final NumberFormatException nfe) {
                return defVal;
            }
        }
        if (o instanceof byte[]) {
            byte[] res = (byte[]) o;
            if (res.length == 4) {
                return (res[0] & 0x00ff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);
            }
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        return defVal;
    }

    public static int toInt(Object o, int defVal) {
        return toInteger(o, defVal);
    }

    public static Long toLong(Boolean condition, Object o, Long defVal) {
        return condition? toLong(o, defVal) : defVal;
    }

    public static Long toLong(Object o, Long defVal) {
        String str = String.valueOf(o);
        if (str == null) {
            return defVal;
        }
        try {
            return Long.parseLong(str);
        } catch (final NumberFormatException nfe) {
            return defVal;
        }
    }

    public static Float toFloat(Object o, Float defVal) {
        String str = String.valueOf(o);
        if (str == null) {
            return defVal;
        }
        try {
            return Float.parseFloat(str);
        } catch (final NumberFormatException nfe) {
            return defVal;
        }
    }

    public static Double toDouble(Object o, Double defVal) {
        String str = String.valueOf(o);
        if (str == null) {
            return defVal;
        }
        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException nfe) {
            return defVal;
        }
    }

    /**
     * 对象转化为 BigDecimal
     * @param o
     * @param defVal
     * @return BigDecimal
     */
    public static BigDecimal toBigDecimal(Object o, BigDecimal defVal) {
        BigDecimal val = defVal;
        if (o == null) {
            return val;
        }
        try {
            val = new BigDecimal(String.valueOf(o));
        } catch (Exception e) {
            val = defVal;
        }
        return val;
    }

    /**
     * <p>以下字符都将被默认值<code>defVal<、code>取代：</p>
     * <pre>
     *   null , "" , "null", "(null)", "undefined"
     * </pre>
     * @param o  输入
     * @param defVal 默认值
     * @return boolean
     */
    public static boolean toBoolean(Object o, Boolean defVal) {
        return Boolean.valueOf(toStr(o, defVal.toString())).booleanValue();
    }

    /**
     * <p>以下字符都将被默认值<code>defVal<、code>取代：</p>
     * <pre>
     *   null , "" , "null", "(null)", "undefined"
     * </pre>
     * <pre>
     *   支持获取 javax.servlet.ServletRequest.getInputStream（utf-8）的字符串
     * </pre>
     * <pre>
     *   支持获取 InputStream（utf-8）的字符串
     * </pre>
     * @param o  输入
     * @param defVal
     * @return string
     */
    public static String toStr(Object o, String defVal) {
        return toStr(o, defVal, "utf-8");
    }

    private static String toStr(Object o, String defVal, String chartset) {
        if (isEmpty(o)) {
            return defVal;
        }
//		if(o instanceof javax.servlet.ServletRequest) {
//			try {
//				return toStr(((javax.servlet.ServletRequest) o).getInputStream(), defVal, chartset);
//			} catch (Exception e) {
//				// log
//				if (log.isDebugEnabled()) {
//					log.debug("解析ServletRequest.getInputStream的字符串失败", e);
//				}
//				return defVal;
//			}
//		}
        if(o instanceof InputStream) {
            InputStream in = (InputStream) o;
            List<Byte> byteList = new LinkedList<>();
            ReadableByteChannel channel = null;
            try {
                channel = Channels.newChannel(in);
                Byte[] bytes = new Byte[0];
                ByteBuffer byteBuffer = ByteBuffer.allocate(9600);
                while (channel.read(byteBuffer) != -1) {
                    byteBuffer.flip();// 为读取做好准备
                    while (byteBuffer.hasRemaining()) {
                        // builder.append((char)byteBuffer.get());
                        byteList.add(byteBuffer.get());
                    }
                    byteBuffer.clear();// 为下一次写入做好准备
                }
                bytes = byteList.toArray(new Byte[byteList.size()]);
                byte[] bytes1 = new byte[bytes.length];

                for (int i = 0; i < bytes.length; i++) {
                    bytes1[i] = bytes[i].byteValue();
                }

                return toStr(new String(bytes1, chartset), defVal, chartset);
            } catch (Exception e) {
                // log
                if (log.isDebugEnabled()) {
                    log.debug("解析InputStream的字符串失败", e);
                }
                return defVal;
            } finally {
                try {
                    if( null != channel) {
                        channel.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return String.valueOf(o);
    }

    /**
     * <p>处理String分隔符，转化为Set</p>
     * @param o 对象
     * @param separator 分隔符
     * @return LinkedHashSet
     */
    public static LinkedHashSet<String> toSplitAsLinkedHashSet(Object o, String separator) {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        String[] values = toStr(o, STRING_EMPTY).split(separator);
        for (String v: values ) {
            result.add(toStr(v, STRING_EMPTY).trim());
        }
        return result;
//		return  Sets.newLinkedHashSet(Splitter.on(separator).trimResults().splitToList(toStr(o, "")));
    }


    /**
     * 整数
     * @param o object
     * @return string
     */
    public static String toHexString(Object o) {
        return toRadixString(o, 16);
    }

    /**
     * 整数,最大36进制
     * @param o
     * @return string
     */
    public static String toRadixString(Object o, int radix) {
        BigDecimal n = toBigDecimal(o, null);
        if (null == n) {
            return null;
        }
        return Long.toString(n.longValue(), radix).toUpperCase();
    }


    //---------------------------------------------------------------------
    // Convenience methods for working with String arrays
    //---------------------------------------------------------------------

    /**
     * Append the given String to the given String array, returning a new array
     * consisting of the input array contents plus the given String.
     * @param array the array to append to (can be {@code null})
     * @param str the String to append
     * @return the new array (never {@code null})
     */
    public static String[] addStringToArray(String[] array, String str) {
        if (isEmpty(array)) {
            return new String[] {str};
        }
        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = str;
        return newArr;
    }

    /**
     * Concatenate the given String arrays into one,
     * with overlapping array elements included twice.
     * <p>The order of elements in the original arrays is preserved.
     * @param array1 the first array (can be {@code null})
     * @param array2 the second array (can be {@code null})
     * @return the new array ({@code null} if both given arrays were {@code null})
     */
    public static String[] concatenateStringArrays(String[] array1, String[] array2) {
        if (isEmpty(array1)) {
            return array2;
        }
        if (isEmpty(array2)) {
            return array1;
        }
        String[] newArr = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, newArr, 0, array1.length);
        System.arraycopy(array2, 0, newArr, array1.length, array2.length);
        return newArr;
    }
}