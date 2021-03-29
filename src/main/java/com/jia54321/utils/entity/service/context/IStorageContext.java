package com.jia54321.utils.entity.service.context;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.jia54321.utils.entity.IStorageConstants;
import com.jia54321.utils.entity.query.SimpleCondition;

/**
 * 存储服务上下文
 * @author G
 */
public interface IStorageContext extends IStorageConstants {
	/**
	 * 查询文件对象
	 * @param id
	 * @return
	 */
	Map<String,Object> getObject(String id);
	
	/**
	 * 条件查询文件对象
	 * @param sc
	 * @return 文件对象列表
	 */
	List<Map<String,Object>> simpleQuery(String typeId, SimpleCondition sc);
	
	/**
	 * 条件查询文件对象
	 * @param sc
	 * @return 文件对象列表
	 */
	List<Map<String,Object>> queryObjects(List<SimpleCondition> sc);
	
	/**
	 * 条件查询文件对象数量
	 * @param sc
	 * @return 文件对象列表
	 */
	long count(List<SimpleCondition> sc);
	
	/**
	 * 存储
	 * @param input
	 * @param jsonItems
	 * @param extItems
	 * @return
	 */
	Map<String,Object> putObject(InputStream input, String jsonItems, Map<String, Object> extItems);
	
	/**
	 * mv命令来为文件或目录改名或将文件由一个目录移入另一个目录中 
	 * @param sc  查询条件
	 * @param targetOverItems
	 * @return
	 */
	List<Map<String,Object>> mvObjects(List<SimpleCondition> sc, Map<String, Object> targetOverItems);
	
	/**
	 * 覆盖对象的属性
	 * @param targetOverItems 需要覆盖的属性
	 * @return
	 */
	List<Map<String, Object>> upObjects(List<Map<String, Object>> targetOverItems);
	
	/**
	 * 复制文件对象
	 * @param sc  查询条件
	 * @param targetOverItems  待覆盖的属性
	 * @return 复制的对象结果。
	 */
	List<Map<String,Object>> cpObjects(List<SimpleCondition> sc, Map<String, Object> targetOverItems);
	
	/**
	 * 删除文件对象
	 * @param sc
	 * @return
	 */
	List<Map<String,Object>> delObjects(List<SimpleCondition> sc);
	
	/**
	 * 缩略图生成
	 * @param lstExtItems
	 * @param resizeImg  10x10
	 * @return
	 */
	Map<String,Object> resizeImgsByResult(List<Map<String, Object>> lstExtItems, String resizeImg);
	
	/**
	 * 压缩文件
	 * @param lstExtItems
	 * @param compressType  zip
	 * @return
	 */
	Map<String,Object> compressByResult(List<Map<String, Object>> lstExtItems, String compressType);
	
	/**
	 * 命令行<br/>
	 * 1. info  存储服务信息概览
	 * @param cmd
	 * @return 
	 */
	Object cmd(String cmd);
	
	/**
	 * 是否存在网络资源
	 * @param url
	 * @return
	 */
	Boolean isFileResourceAvailable(String url);
}
