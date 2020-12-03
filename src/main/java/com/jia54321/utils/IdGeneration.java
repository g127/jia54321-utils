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
}
