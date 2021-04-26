package com.jia54321.utils.entity.impl;

import java.util.*;

import com.jia54321.utils.BsonUtil;
import com.jia54321.utils.DateUtil;
import com.jia54321.utils.IdGeneration;
import com.jia54321.utils.JsonHelper;
import com.jia54321.utils.entity.dao.CrudDao;
import com.jia54321.utils.entity.dao.DynamicEntityDao;
import com.jia54321.utils.entity.query.QueryContent;
import com.jia54321.utils.entity.query.QueryContentFactory;
import com.jia54321.utils.entity.query.SimpleCondition;
import com.jia54321.utils.entity.query.SimpleQueryContent;


import com.google.common.collect.Lists;
import com.jia54321.utils.entity.DynamicEntity;
import com.jia54321.utils.entity.IEntityType;
import com.jia54321.utils.entity.service.context.IDynamicEntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DynamicEntityContext
 * @author gg
 * @date 2019-08-25
 */
public class DynamicEntityContext implements IDynamicEntityContext {

	private static Logger log = LoggerFactory.getLogger(DynamicEntityContext.class);
	private final static String F_CREATOR_ID = "CREATOR_ID";
	private final static String F_CREATOR_NAME = "CREATOR_NAME";
	private final static String F_CREATE_TIME = "CREATE_TIME";
	private final static String F_MODIFY_TIME = "MODIFY_TIME";

	private final static String F_USER_PASSWORD = "USER_PASSWORD";
//	private final static String F_CONFIRM_USER_PASSWORD = "CONFIRM_USER_PASSWORD";
//	private final static String F_NEW_USER_PASSWORD = "NEW_USER_PASSWORD";


	private DynamicEntityDao dao;

	public DynamicEntityContext(DynamicEntityDao dao) {
		this.dao = dao;
	}

	@Override
    public String createId() {
		return IdGeneration.getStringId();
	}

	@Override
    public String createShortId() {
		return BsonUtil.compressObjectId(IdGeneration.getStringId());
	}

	@Override
	public DynamicEntity newDynamicEntity(String typeId, String jsonItems, Map<String, Object> extItems){
		DynamicEntity entity = JsonHelper.cast(
				String.format("{\"typeId\":\"%s\",\"items\": %s }", typeId, jsonItems), DynamicEntity.class);

		if (entity.getItems() == null) {
			entity.setItems(new HashMap<String, Object>());
		}

		if (entity.getItems() != null) {
			entity.getItems().putAll(extItems);
		}

		return entity;
	}

	@Override
	public String insert(DynamicEntity entity) {
		insertProcessCreatorIfAvailable(entity);
		insertProcessPwdIfAvailable(entity);
		return dao.insert(entity);
	}

	@Override
	public String batchInsert(List<DynamicEntity> list) {
		StringBuffer ids = new StringBuffer();
		for (DynamicEntity entity : list) {
			ids.append(insert(entity)).append(",");
		}
		return ids.substring(0, ids.length() - 1).toString();
	}

	@Override
	public QueryContent<DynamicEntity> copy(String typeId, QueryContent<DynamicEntity> qc, Map<String, Object> targetItems) {
		QueryContent<DynamicEntity> rqc = this.query(typeId, qc);
		List<DynamicEntity> lst =  (List<DynamicEntity>)rqc.getResult();

		for (Iterator<DynamicEntity> iterator = lst.iterator(); iterator.hasNext();) {
			DynamicEntity dynamicEntity = iterator.next();

			dynamicEntity.set(dynamicEntity.getTableDesc().getTypePkName(), null);

			dynamicEntity.getItems().putAll(targetItems);

			dynamicEntity.set(dynamicEntity.getTableDesc().getTypePkName(), this.insert(dynamicEntity));
		}
		return rqc;
	}

	@Override
	public SimpleQueryContent<Map<String, Object>> copySimpleList(String typeId, SimpleCondition sc, Map<String, Object> targetItems) {
		return QueryContentFactory.createSimpleQueryContent(this.copy(typeId, QueryContentFactory.createQueryContent(sc), targetItems));
	}

	@Override
	public Integer update(DynamicEntity entity) {
		updateProcessCreatorIfAvailable(entity);
		updateProcessPwdIfAvailable(entity);
		return dao.update(entity);
	}

	@Override
	public Integer updateByCondition(String typeId, QueryContent<DynamicEntity> qc, Map<String, Object> updateItems) {
		Integer size = 0;
		QueryContent<DynamicEntity> rqc = this.query(typeId, qc);
		List<DynamicEntity> lst =  (List<DynamicEntity>)rqc.getResult();
		for (Iterator<DynamicEntity> iterator = lst.iterator(); iterator.hasNext();) {
			DynamicEntity dynamicEntity = iterator.next();

			//更新需要主键,不用设置为Null
			//dynamicEntity.set(dynamicEntity.getTypePkName(), null);

			dynamicEntity.getItems().putAll(updateItems);

			size += update(dynamicEntity);
		}
		return size;
	}

	@Override
	public Integer updateBySimpleCondition(String typeId, SimpleCondition sc, Map<String, Object> updateItems) {
		return this.updateByCondition(typeId, QueryContentFactory.createQueryContent(sc), updateItems);
	}

	@Override
	public Integer updateByMapCondition(String typeId, Map<String, Object> conds, Map<String, Object> updateItems) {
		SimpleCondition sc = new SimpleCondition();
		sc.setSearch(conds);
		return this.updateByCondition(typeId, QueryContentFactory.createQueryContent(sc), updateItems);
	}

	@Override
	public Integer[] delete(String typeId, String ids) {
		return dao.delete(typeId, ids);
	}

	@Override
	public DynamicEntity get(String typeId, String id) {
		return dao.getDynamicEntity(typeId, id);
	}

	@Override
	public QueryContent<DynamicEntity> query(String typeId, QueryContent<DynamicEntity> queryContent) {
		return dao.query(typeId, queryContent);
	}

	@Override
	public List<QueryContent<DynamicEntity>> multiQuery(List<QueryContent<DynamicEntity>> queryContent) {
		return dao.multiQuery(Lists.newArrayList(queryContent));
	}

	@Override
	public SimpleQueryContent<Map<String, Object>> simpleQuery(String typeId, SimpleCondition sc) {
		return QueryContentFactory.createSimpleQueryContent(dao.query(typeId, QueryContentFactory.createQueryContent(sc)));
	}

	@Override
	public List<SimpleQueryContent<Map<String, Object>>> multiSimpleQuery(SimpleCondition... sc) {
		return QueryContentFactory.createSimpleQueryContents(dao.multiQuery(QueryContentFactory.createQueryContents(Lists.newArrayList(sc))));
	}

	@Override
	public SimpleQueryContent<Map<String, Object>> findAllByJson(String typeId, String scJson) {
		return simpleQuery(typeId, JsonHelper.cast(scJson, SimpleCondition.class));
	}

	@Override
	public Map<String, Object> findOneByJson(String typeId, String scJson) {
		SimpleQueryContent<Map<String, Object>> sqc = findAllByJson(typeId, scJson);
		if (null == sqc || sqc.getTotalElements() == 0L || null == sqc.getRows() || sqc.getRows().size() == 0) {
			return null;
		}
		return sqc.getRows().get(0);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	private boolean insertProcessCreatorIfAvailable(DynamicEntity entity) {
		Map<String, Object> userInfo = new LinkedHashMap<>(); // TODO
		IEntityType t = dao.create(entity.getTypeId(), null);

		if(null == t) {
			return false;
		}
		boolean hasCreatorId = (null != t.getMetaItem(F_CREATOR_ID));
		boolean emptyCreatorId = JsonHelper.isEmpty(entity.get(F_CREATOR_ID));
		if(hasCreatorId && emptyCreatorId) {
			entity.set(F_CREATOR_ID, userInfo.get("userId"));
		}

		boolean hasCreatorName = (null != t.getMetaItem(F_CREATOR_NAME));
		boolean emptyCreatorName = JsonHelper.isEmpty(entity.get(F_CREATOR_NAME));
		if(hasCreatorName && emptyCreatorName) {
			entity.set(F_CREATOR_NAME, userInfo.get("userName"));
		}

		boolean hasCreateTime = (null != t.getMetaItem(F_CREATE_TIME));
		boolean emptyCreateTime = JsonHelper.isEmpty(entity.get(F_CREATE_TIME));
		if(hasCreateTime && emptyCreateTime) {
			entity.set(F_CREATE_TIME, DateUtil.toNowTimeString());
		}

		return true;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	private boolean updateProcessCreatorIfAvailable(DynamicEntity entity) {
		IEntityType t = dao.create(entity.getTypeId(), null);

		if(null == t) {
			return false;
		}

		boolean hasModifyTime = (null != t.getMetaItem(F_MODIFY_TIME));
		boolean emptyModifyTime = JsonHelper.isEmpty(entity.get(F_MODIFY_TIME));
		if(hasModifyTime && emptyModifyTime) {
			entity.set(F_MODIFY_TIME, DateUtil.toNowTimeString());
		}

		return true;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	private boolean insertProcessPwdIfAvailable(DynamicEntity entity) {
		IEntityType t = dao.create(entity.getTypeId(), null);

		if(null == t) {
			return false;
		}

		boolean hasUserPassword = (null != t.getMetaItem(F_USER_PASSWORD));
		boolean emptyUserPassword = JsonHelper.isEmpty(entity.get(F_USER_PASSWORD));

		if(hasUserPassword && !emptyUserPassword) {
			//entity.set(F_USER_PASSWORD, "123456"); //TODO 加密
		}
		if(hasUserPassword && emptyUserPassword) {
			//entity.set(F_USER_PASSWORD, "123456"); //TODO 默认加密密码
		}

		return true;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	private boolean updateProcessPwdIfAvailable(DynamicEntity entity) {
		IEntityType t = dao.create(entity.getTypeId(), null);

		if(null == t) {
			return false;
		}

		boolean hasUserPassword = (null != t.getMetaItem(F_USER_PASSWORD));
		boolean emptyUserPassword = JsonHelper.isEmpty(entity.get(F_USER_PASSWORD));

//		boolean hasConfirmUserPassword = (null != t.getMetaItem(F_CONFIRM_USER_PASSWORD));
//		boolean emptyConfirmUserPassword = StringUtils.isEmpty(entity.get(F_CONFIRM_USER_PASSWORD));
//
//		boolean hasNewUserPassword = (null != t.getMetaItem(F_NEW_USER_PASSWORD));
//		boolean emptyNewUserPassword = StringUtils.isEmpty(entity.get(F_NEW_USER_PASSWORD));

		if(hasUserPassword && !emptyUserPassword) {
			//entity.set(F_USER_PASSWORD, "123456"); //TODO 加密
		}
		if(hasUserPassword && emptyUserPassword) {
			//entity.set(F_USER_PASSWORD, "123456"); //TODO 默认加密密码
		}

		return true;
	}

}
