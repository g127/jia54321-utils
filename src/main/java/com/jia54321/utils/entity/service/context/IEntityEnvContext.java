package com.jia54321.utils.entity.service.context;

import java.util.Map;
import java.util.Properties;

/**
 * 环境服务上下文
 * @author G
 *
 */
public interface IEntityEnvContext {
	/**
	 * 获取环境配置项
	 * @param key key
	 * @return value
	 */
	public abstract String getProperty(String key);

	/**
	 * 获取环境配置项
	 * @param key key
	 * @param defValue defValue
	 * @return value
	 */
	public abstract String getPropertyWithValue(String key, String defValue);
	
	/**
	 * 获取环境配置项
	 * @param prefix prefix
	 * @return Properties
	 */
	public abstract Properties getProperties(String prefix);
	
	/**
	 * 获取环境配置项
	 * @param prefix prefix
	 * @return Map<String, String>
	 */
	public abstract Map<String, Object> getPropertiesByMap(String prefix);
	
	/**
	 * 是否为真
	 * @param key
	 * @return true or false
	 */
	public abstract boolean isTrue(String key);
	
	/**
	 * 获取 存储根路径 FileResource, 如果传入参数，会将参数拼接到 “存储根路径” 后。 root + relativePath
	 * <p>处理方式 root = root + '/' + Joiner.on('/').skipNulls().join(relativePath) </p>
	 * @param relativePath relativePath 拼接路径，每个路径首位不要出现 /
	 * @return
	 */
	public abstract String envStorageRootPath(String... relativePath);
	
	/**
	 * 获取临时文件根路径 FileTempPath, 如如果传入参数，会将参数拼接到 这个路径 后。 root + temp + relativePath
	 * <p>处理方式 root = root + '/' + Joiner.on('/').skipNulls().join(relativePath); </p>
	 * @param relativePath relativePath 拼接路径，每个路径首位不要出现 /
	 * @return
	 */
	public abstract String envTempRootPath(String... relativePath);
	
	/**
	 * 获取web根路径 contextPath, 如果传入参数，会将参数拼接到 这个路径 后。 ctxPath + relativePath
	 * <p>处理方式 webRoot = webRoot + '/' + Joiner.on('/').skipNulls().join(relativePath); </p>
	 * @param relativePath relativePath 拼接路径，每个路径首位不要出现 /
	 * @return
	 */
	public abstract String envWebRootPath(String... relativePath);
	
	/**
	 * 获取web根路径 servletContext().getRealPath(), 如果传入参数，会将参数拼接到 这个路径 后。 realPath + relativePath
	 * @param relativePath
	 * @return
	 */
	public String envWebRealPath(String... relativePath);
	
	/**
	 * 获取包名
	 * @return
	 */
	public abstract String envAppPkgName();

	/**
	 * 调用服务
	 * @param fullName 全名
	 * @param params   参数
	 * @return 返回结果
	 */
	public abstract Object call(String fullName, String params);
	
	/**
	 * 调用特定的update服务  clazz.updateField(pk, v)
	 * @param clazz
	 * @param updateField
	 * @param pk
	 * @param v
	 * @return
	 */
	public abstract Object invokeUpdateField(Object clazz, String updateField, String pk, Object v);

	/**
	 * 获取登陆服务对象 的 属性
	 * @return null 或 Object Map<String, Object> userInfo
	 */
	public abstract Object loginUserPropIfAvailable(String prop);

	/**
	 * 根据serviceName获取登陆服务对象
	 * @return null 或 Object Map<String, Object> userInfo
	 */
	public abstract Map<String, Object> loginUserInfoIfAvailable();

}