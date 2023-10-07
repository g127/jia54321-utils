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
package com.jia54321.utils.idgeneration;

import com.jia54321.utils.Assert;
import com.jia54321.utils.Helper;
import com.jia54321.utils.clock.SystemTimer;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An object that generates IDs. This is broken into a separate class in case we
 * ever want to support multiple worker threads per process
 */

public class IdWorker {
	/**
	 * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
	 */
	private static final long twepoch = 1288834974657L;
	/**
	 * 机器标识位数
	 */
	private final long workerIdBits = 5L;
	private final long datacenterIdBits = 5L;
	private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
	private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	/**
	 * 毫秒内自增位
	 */
	private final long sequenceBits = 12L;
	private final long workerIdShift = sequenceBits;
	private final long datacenterIdShift = sequenceBits + workerIdBits;
	/**
	 * 时间戳左移动位
	 */
	private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);

	private final long workerId;

	/**
	 * 数据标识 ID 部分
	 */
	private final long datacenterId;
	/**
	 * 并发控制
	 */
	private long sequence = 0L;
	/**
	 * 上次生产 ID 时间戳
	 */
	private long lastTimestamp = -1L;
	/**
	 * IP 地址
	 */
	private InetAddress inetAddress;


	public IdWorker(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
		this.datacenterId = getDatacenterId(maxDatacenterId);
		this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);
	}

	/**
	 * 
	 * @param workerId
	 * @param datacenterId
	 */
	public IdWorker(long workerId, long datacenterId) {
		Assert.isFalse(workerId > maxWorkerId || workerId < 0,
				String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		Assert.isFalse(datacenterId > maxDatacenterId || datacenterId < 0,
				String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
		this.workerId = workerId;
		this.datacenterId = datacenterId;
	}


	/**
	 * 获取 maxWorkerId
	 */
	protected long getMaxWorkerId(long datacenterId, long maxWorkerId) {
		StringBuilder mpid = new StringBuilder();
		mpid.append(datacenterId);
		String name = ManagementFactory.getRuntimeMXBean().getName();
		if (Helper.isNotEmpty(name)) {
			/*
			 * GET jvmPid
			 */
			mpid.append(name.split("@")[0]);
		}
		/*
		 * MAC + PID 的 hashcode 获取16个低位
		 */
		return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
	}

	/**
	 * 数据标识id部分
	 */
	protected long getDatacenterId(long maxDatacenterId) {
		long id = 0L;
		try {
			if (null == this.inetAddress) {
				this.inetAddress = InetAddress.getLocalHost();
			}
			NetworkInterface network = NetworkInterface.getByInetAddress(this.inetAddress);
			if (null == network) {
				id = 1L;
			} else {
				byte[] mac = network.getHardwareAddress();
				if (null != mac) {
					id = ((0x000000FF & (long) mac[mac.length - 2]) | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
					id = id % (maxDatacenterId + 1);
				}
			}
		} catch (Exception e) {
			// logger.warn(" getDatacenterId: " + e.getMessage());
			throw new RuntimeException(e);
		}
		return id;
	}

	public long getDatacenterId() {
		return datacenterId;
	}

	public long getWorkerId() {
		return workerId;
	}

	/**
	 * 获取下一个 ID
	 *
	 * @return 下一个 ID
	 */
	public synchronized long nextId() {
		long timestamp = timeGen();
		//闰秒
		if (timestamp < lastTimestamp) {
			long offset = lastTimestamp - timestamp;
			if (offset <= 5) {
				try {
					wait(offset << 1);
					timestamp = timeGen();
					if (timestamp < lastTimestamp) {
						throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
			}
		}

		if (lastTimestamp == timestamp) {
			// 相同毫秒内，序列号自增
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				// 同一毫秒的序列数已经达到最大
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			// 不同毫秒内，序列号置为 1 - 2 随机数
			sequence = ThreadLocalRandom.current().nextLong(1, 3);
		}

		lastTimestamp = timestamp;

		// 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
		return ((timestamp - twepoch) << timestampLeftShift)
				| (datacenterId << datacenterIdShift)
				| (workerId << workerIdShift)
				| sequence;
	}

	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * 
	 * @return
	 */
	protected long timeGen() {
		return SystemTimer.currentTimeMillis();
	}


	/**
	 * 反解id的时间戳部分
	 */
	public static long parseIdTimestamp(long id) {
		return (id>>22)+twepoch;
	}
}