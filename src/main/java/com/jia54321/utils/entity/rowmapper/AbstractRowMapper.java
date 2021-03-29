package com.jia54321.utils.entity.rowmapper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;

import com.jia54321.utils.CamelNameUtil;
import com.jia54321.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author G
 *
 * @param <T>
 */
public abstract class AbstractRowMapper<T>  {
	
	private static Logger log = LoggerFactory.getLogger(AbstractRowMapper.class);

	protected Class<T> type;

	protected Method[] methods;

	private ResultSetMetaData resultSetMetaData = null;
	private int columnCount = 0;

	public AbstractRowMapper() {
		ParameterizedType parameterizedType = null;
		for (Class<?> clazz =  getClass(); null != clazz &&  null != getClass().getSuperclass() ; clazz = clazz.getSuperclass()) {
//			if(clazz.getSuperclass().getName().equals(AbstractRowMapper.class.getName())) {
				Type type = getClass().getGenericSuperclass();
				if(type instanceof ParameterizedType)  {
					parameterizedType = (ParameterizedType) type;
					this.type = (Class<T>) parameterizedType.getActualTypeArguments()[0];
					this.methods = this.type.getClass().getMethods();
					break;
				}
//			}
		}
	}

	public T mapRow(ResultSet rs, int rowNum) throws SQLException
	{
		T entity = newInstance();

		if(resultSetMetaData == null) {
			resultSetMetaData = rs.getMetaData(); // 获取键名
		}

		columnCount = resultSetMetaData.getColumnCount();  // 获取行的数量

		for (int i = 1; i <= columnCount; i++) {
			try {
				String columnName = resultSetMetaData.getColumnName(i);
				String camelLowerCaseColumnName = CamelNameUtil.underlineToCamelLowerCase(columnName);
				String methodName = "set" + Character.toUpperCase(camelLowerCaseColumnName.charAt(0)) + camelLowerCaseColumnName.substring(1);
				Object columnValue = rs.getObject(i);
				//
				ClassUtils.invokeBeanWriteMethodIfAvailable(entity, camelLowerCaseColumnName, columnValue);

			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
		return entity;
	}

	/**
	 * 构造实体对象
	 * @return
	 */
	public T newInstance() {
		try {
			return this.type.newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}


}
