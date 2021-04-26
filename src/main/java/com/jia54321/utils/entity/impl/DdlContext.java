package com.jia54321.utils.entity.impl;

import java.util.Collection;

import com.jia54321.utils.entity.IEntityType;
import com.jia54321.utils.entity.MetaItem;
import com.jia54321.utils.entity.dao.CrudDdlDao;
import com.jia54321.utils.entity.query.CrudTableDesc;
import com.jia54321.utils.entity.query.ITableDesc;
import com.jia54321.utils.entity.service.context.IDdlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DDL(Data Definition Language 数据定义语言)用于操作对象和对象的属性
 * @author gg
 * @date 2019-08-25
 */
public class DdlContext implements IDdlContext {

	private static Logger log = LoggerFactory.getLogger(DdlContext.class);

	private CrudDdlDao crudDdlDao;

	public DdlContext(CrudDdlDao crudDdlDao) {
		this.crudDdlDao = crudDdlDao;
	}

	/**
	 * 返回实体定义
	 * @param typeId
	 * @return 对象
	 */
	@Override
	public IEntityType desc(String typeId) {
		return crudDdlDao.desc(typeId);
	}

	@Override
	public String rebuildAsImportData(ITableDesc table, Collection<MetaItem> metaItems) {
		if (null == metaItems || metaItems.size() <= 0) {
			if (log.isInfoEnabled()) {
				log.info("未找到导入的表明细项");
			}
		}

		if (null != metaItems && metaItems.size() >= 0) {
			crudDdlDao.rebuild(table, metaItems);
		}
		return "";
	}
}
