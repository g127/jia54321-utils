package com.jia54321.utils.entity.query;

import com.jia54321.utils.CamelNameUtil;
import com.jia54321.utils.JsonHelper;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 系统实体类型
 *
 -- mysql ddl
 CREATE TABLE `SYS_ENTITY_TYPE` (
 `TYPE_ID` varchar(40) NOT NULL COMMENT '配置项ID',
 `TYPE_ALIAS_ID` varchar(20) DEFAULT NULL COMMENT '唯一类型 别名ID',
 `TYPE_MK` varchar(10) DEFAULT NULL COMMENT '类型模块',
 `TYPE_ENTITY_NAME` varchar(30) DEFAULT NULL COMMENT '类型实体名称',
 `TYPE_DISPLAY_NAME` varchar(80) DEFAULT NULL COMMENT '类型实体显示名称',
 `TYPE_PK_NAME` varchar(80) DEFAULT NULL COMMENT '主键名称',
 `TYPE_OPTS` decimal(10,0) DEFAULT NULL COMMENT '类型选项',
 PRIMARY KEY (`TYPE_ID`)
 ) ENGINE=InnoDB DEFAULT COMMENT='系统实体类型表（SYS_ENTITY_TYPE）';
 *
 *
 */
public class ITableDesc implements Serializable {
	/**  */
	private static final long serialVersionUID = 1L;

	/**类型实体唯一类型ID  */
	private String              typeId;
	/**唯一类型 别名ID  */
	private String              typeAliasId;
	/**类型模块 */
	private String              typeMk;
	/**类型实体名称 */
	private String              typeEntityName;
	/**类型实体显示名称 */
	private String              typeDisplayName;
	/**主键名称  */
	private String              typePkName;
	/**类型选项  */
	private Long                typeOpts = 9L;

	/**实体类  */
	private Class<?>            entityClass;

	// ============================================================
	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = toStr(typeId, _BLANK).trim();
	}

	public String getTypeAliasId() {
		return typeAliasId;
	}

	public void setTypeAliasId(String typeAliasId) {
		this.typeAliasId = toStr(typeAliasId, _BLANK).trim();
	}

	public String getTypeMk() {
		return typeMk;
	}

	public void setTypeMk(String typeMk) {
		this.typeMk = toStr(typeMk, _BLANK).trim();
	}

	public String getTypeEntityName() {
		return typeEntityName;
	}

	public void setTypeEntityName(String typeEntityName) {
		this.typeEntityName = toStr(typeEntityName, _BLANK).trim();
	}

	public String getTypeDisplayName() {
		return typeDisplayName;
	}

	public void setTypeDisplayName(String typeDisplayName) {
		this.typeDisplayName =  toStr(typeDisplayName, _BLANK).trim();
	}

	public String getTypePkName() {
		return typePkName;
	}

	public void setTypePkName(String typePkName) {
		this.typePkName = toStr(typePkName, _BLANK).trim();
	}

	public Long getTypeOpts() {
		return typeOpts;
	}

	public void setTypeOpts(Long typeOpts) {
		this.typeOpts = typeOpts;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	//=========================================================================================
//
//	/**
//	 * 设置属性
//	 * @param propertyName 属性名
//	 * @param value        属性值
//	 */
//	public void setColumn(String columnName, Object value) {
//		columnProps.put(columnName.toUpperCase(), value);
//	}
//
//	/**
//	 * 获取属性
//	 * @param propertyName 属性名
//	 * @return value 属性值
//	 */
//	public Object getColumn(String columnName) {
//		return columnProps.get(columnName.toUpperCase());
//	}
//
//	/**
//	 * 表属性的遍历器
//	 * @return 表属性的遍历器
//	 */
//	public Iterator<Map.Entry<String, Object>> iteratorColumnProps() {
//		return columnProps.entrySet().iterator();
//	}

	//=========================================================================================

	/**
	 * 获取表名称
	 * @return 表名称
	 */
	public String getTableName() {

		Long value = getTypeOpts();
//	    is( 2, value ) && !is( 4, value )  = 表名由 模块 + 类型标识  组成, 例如： SYS_1001
//	   !is( 2, value ) &&  is( 4, value )  = 表名由 模块 + 实体名称  组成, 例如： SYS_USER
//	    is( 2, value ) &&  is( 4, value )  = 表名由 模块 + 实体名称 + 类型标识  组成, 例如： SYS_USER_1001

		String typeMk = getTypeMk();

		String typeEntityName = getTypeEntityName();

		if( typeMk != null &&  value != null ) {

			if( typeMk.length() > 1 && typeMk.lastIndexOf('_') != typeMk.length() - 1  ) {
				typeMk = typeMk + '_';
			}

			if( is( 2, value ) &&  is( 4, value ) ) {
				// 模块名TypeMk + 类型实体名称TypeEntityName
				return (typeMk + CamelNameUtil.camelToUnderline(typeEntityName)).toLowerCase();
			}
			if( !is( 2, value ) &&  is( 4, value ) ) {
				// 模块名TypeMk + 唯一类型IDTypeId
				return typeMk + getTypeId();
			}

			// 默认
			return (typeMk + CamelNameUtil.camelToUnderline(typeEntityName)).toLowerCase();
		}
		// 默认
		return null;
	}

	/**
	 * 主键是否 为数据库自动 组成
	 * @return true or false
	 */
	public Boolean isPkUseDbAutoIncreaseId() {
		return !is( 8, getTypeOpts() ) &&  is(16, getTypeOpts());
	}

	/**
	 * 主键是否 主键程序生成VARCHAR 类型
	 * @return true or false
	 */
	public Boolean isPkUseVarcharId() {
		return is( 8, getTypeOpts() ) && !is(16, getTypeOpts());
	}

	/**
	 * 主键是否 主键程序生成NUMBER 类型
	 * @return true or false
	 */
	public Boolean isPkUseNumberId() {
		return !is( 8, getTypeOpts() ) && !is(16, getTypeOpts());
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ITableDesc.class.getSimpleName() + "[", "]")
				.add("tableName='" + getTableName() + "'")
				.add("typeId='" + String.valueOf(typeId) + "'")
				.add("typeAliasId='" + String.valueOf(typeAliasId) + "'")
				.add("typeMk='" + String.valueOf(typeMk) + "'")
				.add("typeEntityName='" + String.valueOf(typeEntityName) + "'")
				.add("typeDisplayName='" + String.valueOf(typeDisplayName) + "'")
				.add("typePkName='" + String.valueOf(typePkName) + "'")
				.add("typeOpts=" + String.valueOf(typeOpts))
				.add("isPkUseDbAutoIncreaseId=" + isPkUseDbAutoIncreaseId())
				.add("isPkUseVarcharId=" + isPkUseVarcharId())
				.add("isPkUseNumberId=" + isPkUseNumberId())
				.add("entityClass=" + String.valueOf(entityClass))
				.toString();
	}

	/**
	 * toStr
	 * @param o   输入对象
	 * @param defVal 默认值
	 * @return string类型值
	 */
	private static String toStr(Object o, String defVal) {
		if (null == o || "".equals(o)) {
			return defVal;
		}
		if ("null".equals(o)  || "(null)".equals(o) || "undefined".equals(o) ) {
			return defVal;
		}
		return String.valueOf(o);
	}

	/**
	 * 判断标志位.
	 * 二进制标志位 1 2 4 8 16 32 64
	 * 1  对象表名         可选项_对象定义为预置的，不允许更改.
	 *                    is( 1, value ) = 预置的表
	 * 2  对象表名         存储对象名(表名)组成可选项  是否包含 实体类型ID<code>typeId</code>.
	 * 4  对象表名         存储对象名(表名)组成可选项  是否包含 实体实体名<code>typeEntityName</code>.
	 *                    is( 2, value ) && !is( 4, value )  = 表名由 模块 + 类型标识  组成, 例如： SYS_1001
	 *                   !is( 2, value ) &&  is( 4, value )  = 表名由 模块 + 实体名称  组成, 例如： SYS_USER
	 *                    is( 2, value ) &&  is( 4, value )  = 表名由 模块 + 实体名称 + 类型标识  组成, 例如： SYS_USER_1001
	 * 8  对象主键         存储对象(表)的主键 可选项_正常为VARCHAR，反之为NUMBER 类型的自定义值.
	 * 16 对象主键         存储对象(表)的主键 可选项_正常为DB自动生成，反之不是.
	 *                   !is( 8, value ) &&  is(16, value) = 主键DB自增
	 *                    is( 8, value ) && !is(16, value) = 主键程序生成VARCHAR 类型
	 *                   !is( 8, value ) && !is(16, value) = 主键程序生成NUMBER 类型
	 * 32 对象类型ID       存储对象(表)的类型ID 可选项_正常为ObjectId，反之为Varchar 类型的用户自定义值.
	 *                    is( 32, value ) = ObjectId
	 *
	 * @param flag  标志
	 * @param curValue  当前值
	 * @return 返回是否
	 */
	private static boolean is(final int flag, final Long curValue) {
		return (curValue.intValue() & flag) == flag;
	}

	/**
	 * 空白
	 */
	private static final String _BLANK = "";
}
