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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.util.Assert;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 环境参数工具类
 * @author 郭罡
 */
public class EnvHelper {
	/** 日志 */
	private static Logger LOG = LoggerFactory.getLogger(EnvHelper.class);

	/** is Windows. */
	public static boolean IS_WINDOWS = System.getProperties().getProperty("os.name").toUpperCase()
			.indexOf("WINDOWS") != -1;
	/** is Linux. */
	public static boolean IS_LINUX = System.getProperties().getProperty("os.name").toUpperCase().indexOf("LINUX") != -1;
	/** is Mac. */
	public static boolean IS_MAC = System.getProperties().getProperty("os.name").toUpperCase().indexOf("MAC") != -1;
	/** 是否为64位环境 */
	public static boolean IS_ARCH64 = System.getProperties().getProperty("os.arch").toUpperCase().indexOf("64") != -1;
	/** user home */
	public static String USER_HOME = System.getProperties().getProperty("user.home");

	/**
	 * 加载多个classpath路径下的资源文件
	 * @param charset
	 * @param propertiesFilePath
	 * @return Properties
	 */
	public static Properties loadByClassPath(Charset charset, String... propertiesFilePath) {
		List<String> fileList = new ArrayList<String>(20);
		Collections.addAll(fileList, propertiesFilePath);
		if (charset == null) {
			charset = Charset.forName("ISO-8859-1");
		}
		Properties props = new Properties();
		for (String classPathFile : fileList) {
			String cnt = load(charset, classPathFile);
			if (null == cnt || "".equals(cnt)) {
				continue;
			}
			StringReader sr = new StringReader(cnt);
			try {
				props.load(sr);
			} catch (IOException e) {
				props = new Properties();
			} finally {
				sr.close();
			}
		}
		return props;
	}
	
	/**
	 * 加载资源文件，并返回string内容
	 * @param classPath 路径
	 * @param charset   字符
	 * @return  字符串
	 */
	private static String load(Charset charset, String classPath) {
		String cnt = "";
		InputStream in = null;
		try {
			in = getInputStream(classPath);
			if (in != null) {
				if (null == charset || !Charset.isSupported(charset.toString())) {
					cnt = IOUtil.toString(in, Charset.forName("utf-8").toString());
				} else {
					cnt = IOUtil.toString(in, charset.toString());
				}
			} 
		} catch (IOException e) {
			LOG.error(String.format("classpath:%s Load Error.", classPath), e);
		} finally {
			IOUtil.closeQuietly(in);
		}
		return cnt;
	}
	
	/**
	 * 根据classPath 获取对应的InputStream
	 * @param classPath  classPath
	 * @return InputStream
	 * @throws IOException
	 */
	public static InputStream getInputStream(String classPath) throws IOException {
		InputStream in = null;
		String pathToUse = "";
		if (classPath.startsWith("/")) {
			pathToUse = classPath.substring(1);
		}
		URL url = getDefaultClassLoader().getResource(pathToUse);
		if (url != null) {
			in = url.openStream();
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("classpath:%s Found.", classPath));
			}
		} else {
			if(LOG.isWarnEnabled()) {
				LOG.warn(String.format("classpath:%s Not Found.", classPath));
			}
		}
		return in;
	}
	
	public static String loadFileAsUtf8StringByClassPath(String classPath) {
		return load(Charset.forName("utf-8"), classPath);
	}

	/**
	 * Return the default ClassLoader to use: typically the thread context
	 * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
	 * class will be used as fallback.
	 * <p>
	 * Call this method if you intend to use the thread context ClassLoader in a
	 * scenario where you clearly prefer a non-null ClassLoader reference: for
	 * example, for class path resource loading (but not necessarily for
	 * {@code Class.forName}, which accepts a {@code null} ClassLoader reference
	 * as well).
	 * 
	 * @return the default ClassLoader (only {@code null} if even the system
	 *         ClassLoader isn't accessible)
	 * @see Thread#getContextClassLoader()
	 * @see ClassLoader#getSystemClassLoader()
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtils.class.getClassLoader();
			if (cl == null) {
				// getClassLoader() returning null indicates the bootstrap
				// ClassLoader
				try {
					cl = ClassLoader.getSystemClassLoader();
				} catch (Throwable ex) {
					// Cannot access system ClassLoader - oh well, maybe the
					// caller can live with null...
				}
			}
		}
		return cl;
	}
//
//	/**
//	 * 返回 ServletRequestAttributes <br/>
//     * 注意需配合监听器RequestContextListener 或者过滤器RequestContextFilter使用
//	 * @return ServletRequestAttributes
//	 */
//	public static ServletRequestAttributes requestContext() {
//		ServletRequestAttributes requestContext = null;
//		try {
////			 要在web.xml增加如下监听器:
////			 <!--
////			     ContextLoaderListener 如果仅注册RequestContextListener，在进入到SpringServlet之前，只有Request，没有Response
////			     会导致Filter中无法获取到Response
////			 -->
////			 <listener>
////			 <listener-class>org.springframework.web.context.request.RequestContextListener</listener-class>
////			 </listener>
////			 <!--
////		     RequestContextFilter 确保在最前面使用，相比 RequestContextListener 多了下面逻辑
////  			ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
////		     -->
////			<filter>
////				<filter-name>requestContextFilter</filter-name>
////				<filter-class>org.springframework.web.filter.RequestContextFilter</filter-class>
////			</filter>
////          同时避免在非Request线程中使用，会导致无法获取Request
//			requestContext = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
//			Assert.notNull(requestContext, "无法获取web上下文，请检查监听器, 或是在非Request线程中调用");
//		} catch (Exception e) {
//			if (LOG.isErrorEnabled()) {
//				LOG.error("无法获取web上下文，请检查监听器, 或是在非Request线程中调用", e);
//			}
//		}
//		return requestContext;
//	}
}
