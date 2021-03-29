package com.jia54321.utils.entity.converter;

import java.beans.PropertyDescriptor;
import java.util.*;

import com.jia54321.utils.Assert;
import com.jia54321.utils.ClassUtils;
import com.jia54321.utils.DateUtil;
import com.jia54321.utils.entity.query.CrudTableDesc;

import com.jia54321.utils.entity.IDynamicEntity;
import com.jia54321.utils.entity.IEntityType;
import com.jia54321.utils.entity.MetaItem;
import com.jia54321.utils.entity.MetaItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * CrudTableConverter
 */
public class CrudTableConverter implements IConverter<IEntityType, CrudTableDesc> {

	private static Logger log = LoggerFactory.getLogger(CrudTableConverter.class);

	/** 具体操作的实体类对象 */
    private Class<?>       entityClass;
    
    private PropertyDescriptor[] tagetPds;
    
    private PropertyDescriptor[] sourcePds;
    
    public CrudTableConverter() {
	}
    
	@Override
	public CrudTableDesc convert(IEntityType sourceEntity) {
		Assert.notNull(sourceEntity, "Source Entity must not be null");
		Assert.notNull(sourceEntity.getMetaItems(), "Source Entity Items must not be null");
		StringBuffer buffer = new StringBuffer();
		
//		entityClass = sourceEntity.getClass();
		CrudTableDesc tagetObject = new CrudTableDesc();
		// tableDesc
		tagetObject.setTableDesc(sourceEntity.getTableDesc());
		// (sourceEntity.getClass
		tagetObject.getTableDesc().setEntityClass(sourceEntity.getClass());
		// primaryValue
		String primaryName = sourceEntity.getTableDesc().getTypePkName();
		Object primaryValue = sourceEntity.get(primaryName);
		MetaItem primaryMetaItem = sourceEntity.getMetaItem(primaryName);
		if( null != primaryValue ){
			if(null != primaryMetaItem
					&& MetaItemType.NUMBER.equals(primaryMetaItem.getItemType())) {
				tagetObject.setPrimaryValue(primaryValue);
			}
			else if(null != primaryMetaItem
					&& MetaItemType.VARCHAR.equals(primaryMetaItem.getItemType())) {
				tagetObject.setPrimaryValue(primaryValue);
			}
			else
			{
				appendConvertLogger(buffer, sourceEntity.getTypeId(), primaryName, primaryValue, "Undefined", "Not found in metaItems");
			}
		}


		// 设置大小
		tagetObject.setColumnProps(new LinkedHashMap<>(sourceEntity.getItems().size()));

		for (Map.Entry<String, Object> entry: sourceEntity.getItems().entrySet()) {
			String propName = entry.getKey();
			Object value = entry.getValue();
			MetaItem metaItem = sourceEntity.getMetaItem(propName);
			// 存在metaItem 且为时间类型
			if(null != metaItem && MetaItemType.TIME.equals(metaItem.getItemType())){
				//TODO 优化处理时间类型
				try {
					Date time =  null;
					//兼容2017-05-17 11:11:00  2017-05-17 11:11:00.0 2017-05-17 11:11:00.999
					if (String.valueOf(value).length() >= 19 ){
						time = DateUtil.toTimestamp(String.valueOf(value).substring(0, 19));
					} else if (String.valueOf(value).length() == 16 ){
						time = DateUtil.toTimestamp(String.valueOf(value));
					} else if (String.valueOf(value).length() == 10 ){
						time = DateUtil.toTimestamp(String.valueOf(value));
					}
					tagetObject.setColumn(propName, time);
				} catch (Exception e) {
					appendConvertLogger(buffer, sourceEntity.getTypeId(), propName, value, "Time", "Parse Time Error");
				}
			} else if(null != metaItem && MetaItemType.NUMBER.equals(metaItem.getItemType())) {
				if( null == value || "".equals(value)){
					appendConvertLogger(buffer, sourceEntity.getTypeId(), propName, value, "Number", "Null Value");
				} else if (!String.valueOf(value).matches("^(-)?\\d+(\\.\\d+)?$")) {
					appendConvertLogger(buffer, sourceEntity.getTypeId(), propName, value, "Number", "Not Number");
				} else{
					tagetObject.setColumn(propName, value);
				}
			} else if( null != metaItem && MetaItemType.VARCHAR.equals(metaItem.getItemType()) ){
				tagetObject.setColumn(propName, value);
			} else if( null != metaItem && MetaItemType.TEXT.equals(metaItem.getItemType()) ){
				tagetObject.setColumn(propName, value);
			} else {
				appendConvertLogger(buffer, sourceEntity.getTypeId(), propName, value, "Undefined", "Not found in metaItems");
			}

		}

		if(buffer.length() > 0 ){
			if(log.isDebugEnabled()){
				log.debug(log.toString());
			}
		}

		if(log.isDebugEnabled()){
			log.debug(String.format("%s", tagetObject));
		}
		return tagetObject;
	}
//
//	protected String getSourceEntityPropName(String targetPropName){
//	    String sourceEntityPropName = targetPropName;
//	    if("columnProps".equalsIgnoreCase(targetPropName)){
//	    	sourceEntityPropName = "items";
//	    }
//	    if("primaryName".equalsIgnoreCase(targetPropName)){
//	    	sourceEntityPropName = "typePkName";
//	    }
//	    return sourceEntityPropName;
//	}
//
	protected StringBuffer appendConvertLogger(StringBuffer log, String typeId, String fieldName, Object val, String classTypeName, String reason){
		if(log.length() == 0){
			log.append("");
		}
		//实体： 字段： 类型： 原因：时间类型转换失败
		return log.append(String.format("\n  [%s] %s.%s(%s) %s", classTypeName, typeId, fieldName, val , reason));
	}
}
