package com.jia54321.utils.entity.service.context;

import java.util.List;
import java.util.Map;

import com.jia54321.utils.entity.DynamicEntity;
import com.jia54321.utils.entity.query.QueryContent;
import com.jia54321.utils.entity.query.SimpleCondition;
import com.jia54321.utils.entity.query.SimpleQueryContent;


/**
 * 动态实体服务上下文
 * @author G
 */
public interface IDynamicEntityContext {
	
	/**
	 * 创建一个String类型的ID,24位长
	 * @return
	 */
	String createId();
	
	/**
	 * 创建一个String类型的ID，4位
	 * @return
	 */
	String createShortId();
	
	/**
	 * 创建一个 DynamicEntity 对象，辅助
	 * @param typeId "object"
	 * @param jsonItems  {"A":"A"}
	 * @param extItems extItems
	 * @return 实体
	 */
	DynamicEntity newDynamicEntity(String typeId, String jsonItems, Map<String, Object> extItems);
	
	
	/**
	 * 插入
	 * @param entity 实体属性
	 * @return 主键值
	 */
	String insert(DynamicEntity entity);
	
	/**
	 * 批量插入
	 * @param entity 实体属性
	 * @return 主键值
	 */
	String batchInsert(List<DynamicEntity> list);
	
	/**
	 * 复制
	 * @param typeId
	 * @param sc  查询条件
	 * @param targetOverItems 目标需要覆盖的属性。
	 * @return 复制结果对象。
	 */
	QueryContent<DynamicEntity> copy(String typeId, QueryContent<DynamicEntity> qc, Map<String, Object> targetItems);
	
	/**
	 * 复制
	 * @param typeId
	 * @param sc  查询条件
	 * @param targetOverItems 目标需要覆盖的属性。
	 * @return 复制结果对象。
	 */
	SimpleQueryContent<Map<String, Object>> copySimpleList(String typeId, SimpleCondition sc, Map<String, Object> targetItems);
	
	/**
	 * 更新
	 * @param entity
	 * @return
	 */
	Integer update(DynamicEntity entity);
	
	/**
	 * 按照条件更新
	 * @param typeId
	 * @param qc  查询条件
	 * @param updateItems 目标需要更新的属性。
	 * @return
	 */
	Integer updateByCondition(String typeId, QueryContent<DynamicEntity> qc, Map<String, Object> updateItems);
	
	/**
	 * 按照条件更新
	 * @param typeId
	 * @param sc  查询条件
	 * @param updateItems 目标需要更新的属性。
	 * @return
	 */
	Integer updateBySimpleCondition(String typeId, SimpleCondition sc, Map<String, Object> updateItems);
	
	/**
	 * 按照条件更新
	 * @param typeId
	 * @param conds 查询条件
	 * @param updateItems 目标需要更新的属性。
	 * @return
	 */
	Integer updateByMapCondition(String typeId, Map<String, Object> conds, Map<String, Object> updateItems);
	
	/**
	 * 删除
	 * @param typeId
	 * @param gids
	 * @return
	 */
	Integer[] delete(String typeId, String gids);
	
	/**
	 * 获取
	 * @param typeId
	 * @param id
	 * @return
	 */
	DynamicEntity get(final String typeId, final String id);
	
	/**
	 * 标准查询
	 * @param typeId
	 * @param queryContent
	 * @return
	 */
	QueryContent<DynamicEntity> query(String typeId, QueryContent<DynamicEntity> queryContent);
	
	/**
	 * 标准查询
	 * @param queryContent
	 * @return List<QueryContent<DynamicEntity>> 
	 */
	List<QueryContent<DynamicEntity>> multiQuery(List<QueryContent<DynamicEntity>> queryContent);
	
	/**
	 * 简单查询
	 * @param typeId
	 * @param sc
	 * @return
	 */
	SimpleQueryContent<Map<String, Object>> simpleQuery(String typeId, SimpleCondition sc);
	
	/**
	 * 简单查询
	 * @param sc
	 * @return
	 */
	List<SimpleQueryContent<Map<String, Object>>> multiSimpleQuery(SimpleCondition... sc);
	
	/**
	 * 简单查询
	 * @param typeId
	 * @param scJson
	 * @return
	 */
	SimpleQueryContent<Map<String, Object>> findAllByJson(String typeId, String scJson);
	
	/**
	 * 简单查询一个
	 * @param typeId
	 * @param scJson
	 * @return
	 */
	Map<String, Object> findOneByJson(String typeId, String scJson);
}
