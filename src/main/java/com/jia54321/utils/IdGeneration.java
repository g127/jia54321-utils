/**
 * MIT License
 *
 * Copyright (c) 2009-present GuoGang and other contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.jia54321.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jia54321.utils.idgeneration.IdWorker;
import com.jia54321.utils.idgeneration.ObjectId;


/**
 * 生成数据库id主键工具类
 * @author gg
 * @create 2019-08-25
 */
public class IdGeneration {
	/** 日志 */
	private static final Logger logger = LoggerFactory.getLogger(IdGeneration.class);

	public static final String PROP_PRIMARY_KEY_POLICY = "primary.key.policy"; //idWorker, UUID, ObjectId

	public static final String PROP_PRIMARY_KEY_IDWORKER_WORKERID = "primary.key.idworker.workerid";

	public static final String PROP_PRIMARY_KEY_IDWORKER_DATACENTERID = "primary.key.idworker.datacenterid";

	//
	public static String PRIMARY_KEY_POLICY = "idWorker";
    private static IdWorker idWorker = null;
	static {
		try {
			// env com.jia54321.utils.primary.key.policy
			PRIMARY_KEY_POLICY = System.getenv(IdGeneration.class.getPackage().getName() + '.' + PROP_PRIMARY_KEY_POLICY);
			// env com.jia54321.utils.primary.key.policy
            long workerId = Long.valueOf(System.getenv(IdGeneration.class.getPackage().getName() + '.' + PROP_PRIMARY_KEY_IDWORKER_WORKERID));
			// env com.jia54321.utils.primary.key.policy
			long datacenterId = Long.valueOf(System.getenv(IdGeneration.class.getPackage().getName() + '.' + PROP_PRIMARY_KEY_IDWORKER_DATACENTERID));
			idWorker = new IdWorker(1L,1L);

		} catch (Exception e) {
			PRIMARY_KEY_POLICY = "idWorker";
			idWorker = new IdWorker(1L,1L);
		}

		if (logger.isInfoEnabled()) {
			logger.info(String.format("primary.key.policy=%s", PRIMARY_KEY_POLICY));
			logger.info(String.format("primary.key.idWorker=%s,%s",
					idWorker.getWorkerId(), idWorker.getDatacenterId()));
		}
	}

	//
	public static String getStringId() {
		if ("idWorker".equalsIgnoreCase(PRIMARY_KEY_POLICY)) {
			return getSnowflakeId();
		} else if ("UUID".equalsIgnoreCase(PRIMARY_KEY_POLICY)) {
			return getStringUUID();
		} else if ("ObjectId".equalsIgnoreCase(PRIMARY_KEY_POLICY)) {
			return getOID();
		} else {
			return getSnowflakeId();
		}
	}

    /**
     * UUID
     */
	public static String getStringUUID() {
    	return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * ObjectId，mongodb方案 ,主要用于主键值的生成
     */
	public static String getOID() {
    	return ObjectId.get().toHexString();
    }

    /**
     * SnowflakeId twitter的解决方案
     */
	public static String getSnowflakeId(){
    	return String.valueOf(idWorker.getId());
    }


	/**
	 * 统计表主键， 组成形式 year(4) + month(2) + day(2) + hour(2) + halfYear(1) + quarter(1) + week(2) + type(2) <br/>
	 *  <p/>
	 *  <p>类型标志<code>type</code>: 取 year, month, day, hour, halfYear, quarter, week 之一 </p>
	 *  <p/>
	 *  <p>时间<code>time</code>: 时间数值，可用Timestamp， 或者 String表达 </p>
	 *  <p/>
	 *  <p>类型运算标志<code>typeFlags</code>: </p>
	 *  <p>计算方式</p>
	 *  <p>year * typeFlag0 + month * typeFlag1 + day * typeFlag2 + hour * typeFlag3 + halfYear * typeFlag4 + quarter  * typeFlag4 + week * typeFlag5 </p>
	 *  <p>按年，按月，按天，按小时，按季度，按周数，可如下取值</p>
	 *  <p>
	 *         <code>default0   = new int[ 1, 1, 1, 1, 0, 0, 0 ]</code> <br/>
	 *         <code>year1      = new int[ 1, 0, 0, 0, 0, 0, 0 ]</code> <br/>
	 *         <code>month2     = new int[ 1, 1, 0, 0, 0, 0, 0 ]</code> <br/>
	 *         <code>day3       = new int[ 1, 1, 1, 0, 0, 0, 0 ]</code> <br/>
	 *         <code>hour4      = new int[ 1, 1, 1, 1, 0, 0, 0 ]</code> <br/>
	 *         <code>halfYear5  = new int[ 1, 0, 0, 0, 1, 0, 0 ]</code> <br/>
	 *         <code>quarter6   = new int[ 1, 0, 0, 0, 0, 1, 0 ]</code> <br/>
	 *         <code>week7      = new int[ 1, 0, 0, 0, 0, 0, 1 ]</code> <br/>
	 *  </p>
	 *
	 * @param  type
	 *         统计类型 如 年 月  日 ，用int类型表示，且小于99 <br/>
	 *
	 * @param  time
	 *         具体时间 参见 {@link com.jia54321.utils.DateUtil#toTimestamp} <br/>

	 * @param  typeFlags
	 *         类型运算标志 ,数组[6]内取值 0|1，影响返回主键值, 可不传,默认按小时值 <br/>
	 *
	 * @return 唯一生成统计表主键
	 */
	public static String getStatisticId(String type, Object time, int... typeFlags) {
		// 原始类型运算标志
		final int[][] originalTypeFlags = new int[][] {
			new int[] { 1, 1, 1, 1, 0, 0, 0 }, // 0 默认, 取按小时的值
			new int[] { 1, 0, 0, 0, 0, 0, 0 }, // 1 按年份
			new int[] { 1, 1, 0, 0, 0, 0, 0 }, // 2 按月份
			new int[] { 1, 1, 1, 0, 0, 0, 0 }, // 3 按天数
			new int[] { 1, 1, 1, 1, 0, 0, 0 }, // 4 按小时
			new int[] { 1, 0, 0, 0, 1, 0, 0 }, // 5 按半年
			new int[] { 1, 0, 0, 0, 0, 1, 0 }, // 6 按季度
			new int[] { 1, 0, 0, 0, 0, 0, 1 }, // 7 按周数
		};
		//
		int type99 = 0;
		switch (type) {
			case "year":      // 1 按年份
				type99 = 1;
				break;
			case "month":     // 2 按月份
				type99 = 2;
				break;
			case "day":       // 3 按天数
				type99 = 3;
				break;
			case "hour":      // 4 按小时
				type99 = 4;
				break;
			case "halfYear":   // 5 按半年
				type99 = 5;
				break;
			case "quarter":   // 6 按季度
				type99 = 6;
				break;
			case "week":      // 7 按周数
				type99 = 7;
				break;
			default:
				try {
					type99 =  Integer.parseInt(type);
				} catch (final NumberFormatException nfe) {
					type99 = 0;
				}
				break;
		}
		// year(4) + month(2) + day(2) + hour(2) + halfYear(1) + quarter(1) + week(2)
		if(typeFlags == null || typeFlags.length == 0) {
			switch (type99) {
				case 1: // 1 按年份
				case 2: // 2 按月份
				case 3: // 3 按天数
				case 4: // 4 按小时
				case 5: // 5 按季度
				case 6: // 6 按季度
				case 7: // 7 按周数
					typeFlags = originalTypeFlags[type99];
					break;
				default:
					typeFlags = originalTypeFlags[0];
					break;
			}
		}
		if(typeFlags.length < 7) {
			Assert.isTrue( typeFlags.length == 7 , "typeFlags=" +  Arrays.toString(typeFlags) + ", 不足7位");
		}

		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 中国一周第一天为周一
		cal.setTime(DateUtil.toTimestamp(time));
		cal.set(Calendar.MILLISECOND, 0);
		// 各属性取值 * 计算Flag , 补足位数
		String year     = String.format("%04d", (cal.get(Calendar.YEAR)) * typeFlags[0]);
		String month    = String.format("%02d", (cal.get(Calendar.MONTH) + 1) * typeFlags[1]);
		String day      = String.format("%02d", (cal.get(Calendar.DAY_OF_MONTH)) * typeFlags[2]);
		String hour     = String.format("%02d", (cal.get(Calendar.HOUR_OF_DAY)) * typeFlags[3]);
		String halfYear = String.format("%01d", (cal.get(Calendar.MONTH) / 6 + 1) * typeFlags[4]);
		String quarter  = String.format("%01d", (cal.get(Calendar.MONTH) / 3 + 1) * typeFlags[5]);
		String week     = String.format("%02d", (cal.get(Calendar.WEEK_OF_YEAR)) * typeFlags[6]);
		//
		String typeString    = String.format("%02d", type99);

		// 统计表主键  year(4) + month(2) + day(2) + hour(2) + halfYear(1) + quarter(1) + week(2) + type(2);
		return year + month + day + hour + halfYear + quarter + week + typeString;
	}
}
