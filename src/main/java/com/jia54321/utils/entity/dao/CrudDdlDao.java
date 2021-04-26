package com.jia54321.utils.entity.dao;

import java.util.*;
import java.util.function.Function;


import com.jia54321.utils.DiffUtil;
import com.jia54321.utils.JsonHelper;
import com.jia54321.utils.entity.Database;
import com.jia54321.utils.entity.IEntityType;
import com.jia54321.utils.entity.MetaItem;
import com.jia54321.utils.entity.MetaItemType;
import com.jia54321.utils.entity.converter.CrudTableConverter;
import com.jia54321.utils.entity.jdbc.IEntityTemplate;
import com.jia54321.utils.entity.query.CrudTableDesc;
import com.jia54321.utils.entity.query.ITableDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * CrudDdlDao dll
 * @author gg
 * @date 2019-08-25
 */
public class CrudDdlDao {
	/** . */
	private static Logger log = LoggerFactory.getLogger(CrudDdlDao.class);
	/**
	 * IEntityTemplate
	 */
	protected IEntityTemplate template;

	public CrudDdlDao(IEntityTemplate template) {
		this.template = template;
	}

	public IEntityType desc(String typeId) {
		return this.template.getEntityTypeAndItems(typeId);
	}

	/**
	 * 重建表
	 * @param typeId 类型
	 * @return
	 */
	public String rebuild(ITableDesc tableDesc, Collection<MetaItem> metaItems, String... excludeMetaItems) {
		//
		String excludes = null;
		if (null != excludeMetaItems) {
//			JsonHelper.addStringToArray()
//			excludes = String.format(",%s,", JsonHelper.arrayToCommaDelimitedString(excludeMetaItems)).toUpperCase();
		}

		String tableName = tableDesc.getTableName();

		List<MetaItem> baseMetaItems =
				this.template.getEntityItemsByTabName(tableDesc.getTypeId(), tableName);
		List<MetaItem> targetMetaItems = new ArrayList<MetaItem>(metaItems);


		boolean existTable = ( null != baseMetaItems ) && ( baseMetaItems.size() > 0 );

		if( !existTable) {

			String primaryName = tableDesc.getTypePkName();
			if( tableDesc.isPkUseDbAutoIncreaseId() ){
				this.template.executeSql(String.format(CrudDdlDaoConst.SQL_CREATE_TAB_BY_NUMERIC_PK, tableName, primaryName, primaryName));
				this.template.executeSql(String.format(CrudDdlDaoConst.SQL_CREATE_NUMERIC_PK_AUTO_INCREMENT, tableName, primaryName));
			} else if(tableDesc.isPkUseVarcharId()){
				this.template.executeSql(String.format(CrudDdlDaoConst.SQL_CREATE_TAB_BY_VARCHAR_PK, tableName, primaryName, primaryName));
			} else if(tableDesc.isPkUseNumberId()){
				this.template.executeSql(String.format(CrudDdlDaoConst.SQL_CREATE_TAB_BY_NUMERIC_PK, tableName, primaryName, primaryName));
			}

//			if(jdbcTemplate.getDatabase() == Database.mysql){
				// 表注释
				this.template.executeSql(String.format(CrudDdlDaoConst.MYSQL_SQL_TABLE_COMMENT, tableName, tableDesc.getTypeDisplayName()));
				// 表引擎MyISAM ，fixed bug 解决Innodb不同查询session间，查询数据延时问题
				// 具体描述是两个session 一个做插入，一个做查询，查询不到应该是 Innodb引擎下持久化有一定延时.
				// 未定位出明显原因，使用MyISAM暂时处理。
				this.template.executeSql(String.format(CrudDdlDaoConst.MYSQL_SQL_ADD_ENGINE_MYISAM, tableName));
				//

				// 创建时间
				this.template.executeSql(String.format(CrudDdlDaoConst.MYSQL_SQL_ADD_COL_TAB_DATETIME, tableName, "CREATE_TIME", "创建时间"));
				this.template.executeSql(String.format(CrudDdlDaoConst.MYSQL_SQL_COL_COMMENT_DATETIME, tableName, "CREATE_TIME", "创建时间"));

//			}else if(jdbcTemplate.getDatabase() == Database.oracle){
//				// 表注释
//				this.template.executeSql(String.format(CrudDdlDaoConst.ORACLE_SQL_TABLE_COMMENT, tableName, table.getTypeDisPlayName()));
//
//				// 创建时间
//				this.template.executeSql(String.format(CrudDdlDaoConst.ORACLE_SQL_ADD_COL_TAB_DATETIME, tableName, "CREATE_TIME"));
//				this.template.executeSql(String.format(CrudDdlDaoConst.ORACLE_SQL_COL_COMMENT, tableName, "CREATE_TIME", "创建时间"));
//
//
//			}
		}


		// 集合比较
		DiffUtil.DiffResult<MetaItem> diffResult = DiffUtil.diffList(baseMetaItems, targetMetaItems,
				new Function<MetaItem, Object>() {
					@Override
					public String apply(MetaItem metaItem) {
						return metaItem.getItemColName();
					}
				}, new Comparator<MetaItem>() {
					@Override
					public int compare(MetaItem o1, MetaItem o2) {
						return 0;
					}
				});
		//
		List<MetaItem> actualMetaItems = diffResult.getAddedList();





		log.debug(String.format("rebuildTable, actualMetaItems size is %s", actualMetaItems.size()));

//		if (this.template.getDatabase() == Database.mysql) {
			for (MetaItem metaItem : actualMetaItems) {
				// 忽略字段
				if (null != excludes && excludes.indexOf(String.format(",%s,", metaItem.getItemColName())) > -1) {
					//
					continue;
				}

				// 数字类型
				if (MetaItemType.NUMBER == metaItem.getItemType()) {
					this.template.executeSql(String.format(CrudDdlDaoConst.MYSQL_SQL_ADD_COL_TAB_NUMBER,
							tableName, metaItem.getItemColName().toUpperCase(),
							metaItem.getItemLen(), metaItem.getItemDec(),
							metaItem.getItemColDesc()));

				}
				// 字符类型
				if (MetaItemType.VARCHAR == metaItem.getItemType()) {
					this.template.executeSql(String.format(CrudDdlDaoConst.MYSQL_SQL_ADD_COL_TAB_VARCHAR,
							tableName, metaItem.getItemColName().toUpperCase(),
							metaItem.getItemLen(),
							metaItem.getItemColDesc()));

				}
				// 时间类型
				if (MetaItemType.TIME == metaItem.getItemType()) {
					this.template.executeSql(String.format(CrudDdlDaoConst.MYSQL_SQL_ADD_COL_TAB_DATETIME,
							tableName, metaItem.getItemColName().toUpperCase(),
							metaItem.getItemColDesc()));

				}

			}
//		}
//		else if (jdbcTemplate.getDatabase() == Database.oracle) {
//			for (MetaItem metaItem : actualMetaItems) {
//				// 忽略字段
//				if (null != excludes && excludes.indexOf(String.format(",%s,", metaItem.getItemColName())) > -1) {
//					//
//					continue;
//				}
//
//				// 数字类型
//				if (MetaItemType.NUMBER == metaItem.getItemType()) {
//					this.template.executeSql(String.format(CrudDdlDaoConst.ORACLE_SQL_ADD_COL_TAB_NUMBER,
//							tableName, metaItem.getItemColName().toUpperCase(),
//							metaItem.getItemLen(), metaItem.getItemDec()));
//				}
//				// 字符类型
//				if (MetaItemType.VARCHAR == metaItem.getItemType()) {
//					this.template.executeSql(String.format(CrudDdlDaoConst.ORACLE_SQL_ADD_COL_TAB_VARCHAR,
//							tableName, metaItem.getItemColName().toUpperCase(),
//							metaItem.getItemLen()));
//				}
//				// 时间类型
//				if (MetaItemType.TIME == metaItem.getItemType()) {
//					this.template.executeSql(String.format(CrudDdlDaoConst.ORACLE_SQL_ADD_COL_TAB_DATETIME,
//							tableName, metaItem.getItemColName().toUpperCase()));
//				}
//
//				this.template.executeSql(String.format(CrudDdlDaoConst.ORACLE_SQL_COL_COMMENT,
//						tableName, metaItem.getItemColName().toUpperCase(), metaItem.getItemColDesc()));
//			}
//		}

		return "";
	}

}
