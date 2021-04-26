package com.jia54321.utils.entity.impl;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;


import com.google.common.base.Joiner;
import com.jia54321.utils.CamelNameUtil;
import com.jia54321.utils.ClassUtils;
import com.jia54321.utils.EnvHelper;
import com.jia54321.utils.JsonHelper;
import com.jia54321.utils.entity.service.context.IEntityEnvContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Env
 * @author gg
 * @date 2019-08-25
 */

public class EntityEnvContext implements IEntityEnvContext {
	private static Logger log = LoggerFactory.getLogger(EntityEnvContext.class);
	/** isWindows. */
	public static boolean isWindows = EnvHelper.IS_WINDOWS;
	/** isLinux. */
	public static boolean isLinux = EnvHelper.IS_LINUX;
	/** isMac. */
	public static boolean isMac = EnvHelper.IS_MAC;

	/** is64 */
	public static boolean is64 = EnvHelper.IS_ARCH64;

	public static String USER_HOME = System.getProperties().getProperty("user.home");
	
	/** . */
	private static Properties props = new Properties();
//	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

	public EntityEnvContext() {
		init();
	}

	public void init() {
		props = EnvHelper.loadByClassPath(Charset.forName("ISO-8859-1"), 
				// 推送相关default
				"/entity-conf/notification/notification-default.properties",
				// 推送相关
				"/notification/notification.properties",
				// redis相关default
				"/entity-conf/redis-default.properties",
				// redis
				"/redis.properties",
				// 全局
				"/application.properties" //
		);
	}
//
//	private ApplicationContext getApplicationContext() {
//		return SpringUtils.getApplicationContext();
//	}
//
//	private String getEnvironmentProperty(String key, String defaultValue) {
//		String val = defaultValue;
//		try {
//			val = SpringUtils.getApplicationContext().getEnvironment().getProperty(key, defaultValue);
//		} catch (Exception e) {
//			log.error("getEnvironmentProperty", e);
//		}
//		return val;
//	}

	/**
	 * 获取环境配置项
	 * @param key key
	 * @return value
	 */
	@Override
	public String getProperty(String key) {
		return getPropertyWithValue(key, null);
	}

	/**
	 * 获取环境配置项
	 * @param key key
	 * @param defValue defValue
	 * @return value
	 */
	@Override
	public String getPropertyWithValue(String key, String defValue) {
		final String val;
		if (JsonHelper.isEmpty(key)) {
			val = defValue;
			if (log.isDebugEnabled()) {
				log.debug(String.format("%s=%s (key is empty, Get form Defult Value)", key, val));
			}
//		} else if (null != getEnvironmentProperty(key, null)) {
//			val = getEnvironmentProperty(key, "");
//			if (log.isDebugEnabled()) {
//				log.debug(String.format("%s=%s (Get form Spring Environment)", key, val));
//			}
		} else if (null != props && !JsonHelper.isEmpty(props.getProperty(key))) {
			val = props.getProperty(key);
			if (log.isDebugEnabled()) {
				log.debug(String.format("%s=%s (Get form Properties File)", key, val));
			}
		} else {
			val = defValue;
			if (null != defValue && log.isDebugEnabled()) {
				log.debug(String.format("%s=%s (Not Found key, Get form Defult Value)", key, val));
			}
		}
		return val;
	}
	
	/**
	 * 获取配置项
	 * 
	 * @param prefix
	 *            prefix
	 * @return Properties
	 */
	@Override
	public final Properties getProperties(String prefix) {
		final Properties subProps = new Properties();
		for (Entry<Object, Object> entry : props.entrySet()) {
			if (entry.getKey() instanceof String && ((String) entry.getKey()).indexOf(prefix) == 0) {
				// avoid .
				String key = String.valueOf(entry.getKey()).substring(prefix.length());
				if (key.indexOf(".") == 0) {
					key = key.substring(1);
				}
				subProps.put(key, String.valueOf(entry.getValue()));
			}
		}
		return subProps;
	}
	
	/**
	 * 获取配置项
	 * 
	 * @param prefix
	 *            prefix
	 * @return Properties
	 */
	@Override
	public final Map<String, Object> getPropertiesByMap(String prefix){
		final Map<String, Object> subMap = new LinkedHashMap<String, Object>();
		final Properties subProps = getProperties(prefix);
		for (Entry<Object, Object> entry : subProps.entrySet()) {
			subMap.put(String.valueOf(entry.getKey()), entry.getValue());
		}
		return subMap;
	}
	
	@Override
	public boolean isTrue(String key) {
		return Boolean.valueOf(getPropertyWithValue(key, "false"));
	}

	@Override
	public String envStorageRootPath(String... relativePath) {
		// 获取 Root
		String root = getProperty("entity.storage.url");
		// 尝试其他方式，兼容老代码
		if (null == root || "".equals(root)) {
			root = getPropertyWithValue("uploadPath", "/DevProjectFiles/ws-root/fileResources/");
		}

		// 无法获取
		if (null == root || "".equals(root)) {
			throw new NullPointerException("无法获取StorageRootPath，请检查。");
		}

		// Mac 路径优化，如果为苹果系统，而路径出现 D盘之类的，重设到用户主目录下
		if (isMac && root.indexOf(":") > -1) {
			root = USER_HOME + "/" + root.substring(root.indexOf(":") + 1);
			if (log.isInfoEnabled()) {
				log.info("MAC StorageRootPath优化，优化后路径：" + root);
			}
		}

		if (!JsonHelper.isEmpty(relativePath)) {
			// 拼接和替换操作
			root = root + '/' + Joiner.on('/').skipNulls().join(relativePath);
		} 
		
		// 二次处理
		try {
			// 进行replace操作
			root = root.replaceAll("////", "//").replaceAll("///", "//");
			// 进行replace操作
			root = root.replace("//", "/");
			
			// 不存在路径 建立目录
			File isDir = new File(root);
            if (!isDir.isDirectory()) {
                isDir.mkdirs();
            }
			
		} catch (Throwable e) {
			if (log.isDebugEnabled()) {
				log.debug("root路径二次处理异常", e);
			}
		}
		
		return root;
	}

	@Override
	public String envTempRootPath(String... relativePath) {
		// String sysTemp = System.getProperty("java.io.tmpdir");
		// return sysTemp;
		if (JsonHelper.isEmpty(relativePath)) {
			return envStorageRootPath("temp");
		}

		return envStorageRootPath("temp", Joiner.on('/').skipNulls().join(relativePath));
	}

	@Override
	public String envWebRootPath(String... relativePath) {
		String webRoot = null;
		HttpServletRequest request = EnvHelper.requestContext().getRequest();
		if (null == request) {
			webRoot = null;
		} else {
			webRoot = request.getContextPath();
		}

		if (null == webRoot || "".equals(webRoot)) {
			webRoot = getProperty("entity.web.root");
		}
		if (null == webRoot || "".equals(webRoot)) {
			webRoot = getPropertyWithValue("webRoot", "/");
		}

		if (null == webRoot || "".equals(webRoot)) {
			throw new NullPointerException("无法获取webRoot，请检查。");
		}

		if (!JsonHelper.isEmpty(relativePath)) {
			for (int i = 0; i < relativePath.length; i++) {
				if (relativePath[i].indexOf('/') < 0) {
					relativePath[i] = JsonHelper.encodeUrl(relativePath[i]);
				} else {
					relativePath[i] = JsonHelper.encodeUrl(relativePath[i]).replaceAll("%2F", "/");
				}
			}
			// 拼接和替换操作
			webRoot = webRoot + '/' + Joiner.on('/').skipNulls().join(relativePath);
			// 进行replace操作
			webRoot = webRoot.replaceAll("////", "//").replaceAll("///", "//");
			// 进行replace操作
			webRoot = webRoot.replace("//", "/");
		}

		return webRoot;
	}

	@Override
	public String envWebRealPath(String... relativePath) {
		String realPath = null;

		HttpServletRequest request = EnvHelper.requestContext().getRequest();
		if (null == request) {
			realPath = null;
		} else {
			realPath = request.getServletContext().getRealPath("/");
		}

		if (!JsonHelper.isEmpty(relativePath)) {
			// 拼接和替换操作
			realPath = realPath + '/' + Joiner.on('/').skipNulls().join(relativePath);
			// 进行replace操作
			realPath = realPath.replaceAll("////", "//").replaceAll("///", "//");
			// 进行replace操作
			realPath = realPath.replace("//", "/");
		}

		return realPath;

	}

	@Override
	public String envAppPkgName() {
		return getPropertyWithValue("appPkgName", getClass().getPackage().getName());
	}

//	/**
//	 * 根据serviceName获取服务对象
//	 *
//	 * @param serviceName
//	 * @return null 或 Object
//	 */
//	private Object getTargetIfAvailable(final String serviceName) {
//		Object serviceObject = null;
//		// 获取参数
//		Class<?> paramClass = null;
//		String innerBeanName = null;
//		try {
//			// 获取参数
//			paramClass = ClassUtils.resolveClassName(serviceName, null);
//			// 转换内部BeanName
//			innerBeanName = getInnerBeanName(serviceName);
//			// 获取服务对象
//			serviceObject = getApplicationContext().getBean(innerBeanName, paramClass);
//		} catch (Throwable t1) {
//			if (log.isDebugEnabled()) {
//				log.debug(
//						String.format("无法获取Entity范围[%s]对象", innerBeanName),
//						t1.getMessage());
//			}
//			if (null == serviceObject) {
//				try {
//					// 获取全局服务对象
//					serviceObject = getApplicationContext().getBean(paramClass);
//				} catch (Throwable t2) {
//					if (log.isErrorEnabled()) {
//						log.error(
//								String.format("无法获取全局范围[%s]对象", serviceName),
//								t2.getMessage());
//					}
//				}
//			}
//		}
//		return serviceObject;
//	}
//
//	/**
//	 * 获取内部 beanName.
//	 *
//	 * @param serviceName
//	 *            服务名
//	 * @return 内部名
//	 */
//	private String getInnerBeanName(final String serviceName) {
//		final int index = serviceName.lastIndexOf('.');
//		final String interfaceName = serviceName.substring(index + 1);
//		return String.format("service.%s", interfaceName);
//	}
//
//	/**
//	 *
//	 * @param target
//	 * @param methodName
//	 * @return
//	 */
//	private Method getMethodIfAvailable(final Object target,
//			final String methodName) {
//		Set<Method> candidates = new HashSet<Method>(1);
//		Method[] methods = target.getClass().getMethods();
//		for (Method method : methods) {
//			if (methodName.equals(method.getName())) {
//				candidates.add(method);
//			}
//		}
//		if (candidates.size() == 1) {
//			return (Method) candidates.iterator().next();
//		}
//		return null;
//	}
//
//	private Object[] getMethodArgsIfAvailable(final Method method,
//			final String params) {
//		final Type[] argTypes = method.getGenericParameterTypes();
//		return JsonHelper.cast(params, argTypes);
//	}

	/**
	 * 调用服务
	 * 
	 * @param fullName
	 *            全名
	 * @param params
	 *            参数
	 * @return 返回结果
	 */
	@Override
	public Object call(String fullName, String params) {
//		// 日志
//		if (log.isDebugEnabled()) {
//			log.debug(String.format("fullName=%s, params=%s", fullName, params));
//		}
//
//		try {
//			// 返回对象
//			Object resultOject = null;
//			// 全名
//			final String[] fullNames = splitServiceNameAndMethodName(fullName);
//			// 服务名，方法名
//			final String serviceName = fullNames[0], methodName = fullNames[1];
//			// 调用对象实例
//			Object target = getTargetIfAvailable(serviceName);
//			// 方法
//			Method method = getMethodIfAvailable(target, methodName);
//			// 参数
//			Object[] args = getMethodArgsIfAvailable(method, params);
//
//			ReflectionUtils.makeAccessible(method);
//			// 反射调用
//			// resultOject =
//			// com.topflames.entity.crud.service.types.ClassUtils.invokeMethodIfAvailable(target,
//			// methodName, args);
//			resultOject = ReflectionUtils.invokeMethod(method, target, args);
//			// 返回结果
//			return resultOject;
//		} catch (Exception e) {
//			String msg = String.format("调用异常。 call fullName(params)=%s(%s)", fullName, params);
//			if (log.isDebugEnabled()) {
//				log.debug(msg, e);
//			}
//			throw new RuntimeException(msg, e);
//		}
		return null;
	}

	@Override
	public Object invokeUpdateField(Object clazz, String updateField,
			String pk, Object v) {
		String className = "";
		if (clazz instanceof Class) {
			className = ((Class<?>) clazz).getName();
		} else if (clazz instanceof String) {
			className = (String) clazz;
		}

		// 首字母大写
		StringBuilder upperCaseField = new StringBuilder().append(
				Character.toUpperCase(updateField.charAt(0))).append(
				updateField.substring(1));

		Object[] args = new Object[] { pk, v };

		return call(String.format("%s.update%s", className, upperCaseField.toString()), JsonHelper.toJSONString(args));
	}

	/**
	 * 获取登陆服务对象 UserId
	 * 
	 * @return null 或 Object Map<String, Object> userInfo
	 */
	@Override
	public Object loginUserPropIfAvailable(String prop) {
		String propName = CamelNameUtil.underlineToCamelLowerCase(prop);
		
		Map<String, Object> userInfo = loginUserInfoIfAvailable();
		
		if(null == userInfo) {
			return null;
		}
		
		if (null != userInfo.get(propName)) {
			return userInfo.get(propName);
		} else {
			return userInfo.get(prop);
		}
	}
	
	/**
	 * 根据serviceName获取登陆服务对象
	 * 每个属性 为 下划线转驼峰	(首字母小写)
	 * @return null 或 Object Map<String, Object> userInfo
	 */
	@Override
	public Map<String, Object> loginUserInfoIfAvailable() {
		Map<String, Object> userInfo = new HashMap<String, Object>();
//		try {
//			Object user = SecurityUtils.getSubject().getPrincipal();
//
//			// 会话通用属性
//			final String[] commProps = new String[] {//
//					"id", "name",// 1           /* 新版本字段 用户ID，用户名称new field */
//					"userId", "userName",// 2   /* 老版本字段 用户ID，用户名称new field */
//					"loginName",// 3            /* 登录名 */
//					"userType",// 4             /* 用户类型  */					
//					"host",// 5                 /* 登录IP */
//					//
//					"wxUnionId",// 6            /* wxUnionId  */
//					"wxOpenId",// 7             /* wxOpenId */
//					"iosPushTokenId",// 8       /* iosPushTokenId */
//					"androidPushTokenId",// 9   /* androidPushTokenId */
//					//
//					"position", //              /* 职务 */
//			};
//			// 会话扩展属性
//			final String[] extProps = new String[] { 	//		
////					"masterGroup", //
////					"roleList", //
////					"groupList", //
////					"menuList", //
////					"ext" //
//					};
//
//			String[] allProps = StringUtils.concatenateStringArrays(commProps, extProps);
//
//			for (String propName : allProps) {
////				String key = CamelNameUtil.camelToUnderline(propName)
////						.toUpperCase();
//				String key = CamelNameUtil.underlineToCamelLowerCase(propName);
//				Object val = com.topflames.entity.crud.service.types.ClassUtils
//						.invokeBeanReadMethodIfAvailable(user, propName);
//				userInfo.put(key, val);
//			}
//
//			if(userInfo.get("ext") instanceof Map) {
//				( (Map<String, Object>)userInfo.get("ext") ).remove("password");
//			}
//		} catch (Throwable e) {
//			if (log.isTraceEnabled()) {
//				log.trace("通用用户信息未知", e);
//			}
//		}
		return userInfo;
	}



	/**
	 * 获取类名和方法名.
	 * 
	 * @param serviceName
	 *            服务名
	 * @return [0] 类名 [1] 方法名
	 */
	private String[] splitServiceNameAndMethodName(final String serviceName) {
		final int index = serviceName.lastIndexOf('.');
		final String className = serviceName.substring(0, index);
		final String methodName = serviceName.substring(index + 1);
		return new String[] { className, methodName };
	}
}
