package com.jia54321.utils.entity.query;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.*;
import com.jia54321.utils.cache.ICache;
import com.jia54321.utils.cache.caffeine.CaffeineCache;


/**
 * 表描述对象
 * @author G
 */
public class CrudTableDesc {

	public static final  LoadingCache<String, ITableDesc> CACHE = CacheBuilder.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)// 给定时间内没有被读/写访问，则回收。
			.refreshAfterWrite(10, TimeUnit.MINUTES)// 给定时间内没有被读/写访问，则回收。
			.expireAfterAccess(1, TimeUnit.HOURS)// 缓存过期时间和redis缓存时长一样
			.maximumSize(1000)// 设置缓存个数
			.removalListener(new RemovalListener<String, ITableDesc>() {
				@Override
				public void onRemoval(RemovalNotification<String, ITableDesc> notification) {
				}
			}).build( new CacheLoader<String, ITableDesc>() {
				@Override
				public ITableDesc load(String key) throws Exception {
					switch (key) {
						case "10": return SYS_ENTITY_TYPE;
						case "1001": return SYS_USER;
						case "1002": return SYS_ROLE;
						case "1003": return SYS_MENU;
						case "1004": return SYS_USER_GROUP;
						case "1005": return SYS_DICT;
						case "1006": return SYS_CLASSIFY;
						case "object": return SYS_OBJECT;
						case "1010": return SYS_USER_ROLE;
						case "1014": return SYS_USER_USERGROUP;
						case "1023": return SYS_ROLE_MENU;
						default:
							return null;
					}
				}
			});

	//=========================================================================================

	/** SYS_ENTITY_TYPE 实体定义表 */
	public static final ITableDesc SYS_ENTITY_TYPE = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("ENTITY_TYPE");
			this.setTypeDisplayName("实体定义表");
			this.setTypeId("10");
			this.setTypeAliasId("entityType");
			this.setTypeOpts(9L);
			this.setTypePkName("TYPE_ID");
		}
	};

	/** SYS_ENTITY_ITEM 实体项目表 */
	public static final ITableDesc SYS_ENTITY_ITEM = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("ENTITY_ITEM");
			this.setTypeDisplayName(" 实体项目表");
			this.setTypeId("11");
			this.setTypeAliasId("entityItem");
			this.setTypeOpts(9L);
			this.setTypePkName("ITEM_ID");
		}
	};

	/** 1001 SYS_USER 系统用户表 */
	public static final ITableDesc SYS_USER = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("USER");
			this.setTypeDisplayName(" 系统用户表");
			this.setTypeId("1001");
			this.setTypeOpts(9L);
			this.setTypePkName("USER_ID");
		}
	};

	/** 1002 SYS_ROLE 系统角色表 */
	public static final ITableDesc SYS_ROLE = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("ROLE");
			this.setTypeDisplayName("系统角色表");
			this.setTypeId("1002");
			this.setTypeOpts(9L);
			this.setTypePkName("ROLE_ID");
		}
	};

	/** 1003 SYS_MENU 系统菜单表 */
	public static final ITableDesc SYS_MENU = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("MENU");
			this.setTypeDisplayName("系统菜单表");
			this.setTypeId("1003");
			this.setTypeOpts(9L);
			this.setTypePkName("MENU_ID");
		}
	};

	/** 1004 SYS_USER_GROUP 系统菜单表 */
	public static final ITableDesc SYS_USER_GROUP = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("USER_GROUP");
			this.setTypeDisplayName("系统单位");
			this.setTypeId("1004");
			this.setTypeOpts(9L);
			this.setTypePkName("USER_GROUP_ID");
		}
	};

	/** 1005 SYS_DICT 系统菜单表 */
	public static final ITableDesc SYS_DICT = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("DICT");
			this.setTypeDisplayName("系统字典");
			this.setTypeId("1005");
			this.setTypeOpts(9L);
			this.setTypePkName("DICT_ID");
		}
	};

	/** 1006 SYS_CLASSIFY 系统分类表 */
	public static final ITableDesc SYS_CLASSIFY = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("CLASSIFY");
			this.setTypeDisplayName("系统分类表");
			this.setTypeId("1006");
			this.setTypeOpts(9L);
			this.setTypePkName("CLASSIFY_ID");
		}
	};

	/** 1010 SYS_OBJECT 系统文件对象表 */
	public static final ITableDesc SYS_OBJECT = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("OBJECT");
			this.setTypeDisplayName("系统文件对象表");
			this.setTypeId("object");
			this.setTypeOpts(9L);
			this.setTypePkName("OBJECT_ID");
		}
	};
//=========================================================================================
	/** 1012 SYS_OBJECT 系统用户角色关联  */
	public static final ITableDesc SYS_USER_ROLE = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("USER_ROLE");
			this.setTypeDisplayName("系统文件对象表");
			this.setTypeId("1010");
			this.setTypeOpts(9L);
			this.setTypePkName("ID");
		}
	};
	/** 1014 SYS_USER_USERGROUP 系统用户组织关联  */
	public static final ITableDesc SYS_USER_USERGROUP = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("USER_USERGROUP");
			this.setTypeDisplayName("系统用户组织关联表");
			this.setTypeId("1014");
			this.setTypeOpts(9L);
			this.setTypePkName("ID");
		}
	};
	/** 1023 SYS_ROLE_MENU 系统角色菜单关联  */
	public static final ITableDesc SYS_ROLE_MENU = new ITableDesc() {
		{
			this.setTypeMk("SYS_");
			this.setTypeEntityName("ROLE_MENU");
			this.setTypeDisplayName("系统角色菜单关联表");
			this.setTypeId("1023");
			this.setTypeOpts(9L);
			this.setTypePkName("ID");
		}
	};
//=========================================================================================
	/**表格描述  */
	private ITableDesc          tableDesc;
	/**主键值  */
	private Object              primaryValue;
	/**属性名称值集合  */
	private Map<String, Object> columnProps = new HashMap<String, Object>();

	//=========================================================================================
	public ITableDesc getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(ITableDesc tableDesc) {
		this.tableDesc = tableDesc;
	}

	public Object getPrimaryValue() {
		return primaryValue;
	}

	public void setPrimaryValue(Object primaryValue) {
		this.primaryValue = primaryValue;
	}

	public Map<String, Object> getColumnProps() {
		return columnProps;
	}

	public void setColumnProps(Map<String, Object> columnProps) {
		this.columnProps = columnProps;
	}

	/**
	 * 设置属性
	 * @param propertyName 属性名
	 * @param value        属性值
	 */
	public void setColumn(String columnName, Object value) {
		columnProps.put(columnName.toUpperCase(), value);
	}

	/**
	 * 获取属性
	 * @param propertyName 属性名
	 * @return value 属性值
	 */
	public Object getColumn(String columnName) {
		return columnProps.get(columnName.toUpperCase());
	}

	/**
	 * 表属性的遍历器
	 * @return 表属性的遍历器
	 */
	public Iterator<Map.Entry<String, Object>> iteratorColumnProps() {
		return columnProps.entrySet().iterator();
	}

}
