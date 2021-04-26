package com.jia54321.utils.entity.service.context;

import java.util.Collection;

import com.jia54321.utils.entity.IEntityType;
import com.jia54321.utils.entity.MetaItem;
import com.jia54321.utils.entity.query.ITableDesc;

/**
 * DDL服务上下文 .DDL(Data Definition Language 数据定义语言)用于操作对象和对象的属性
 * @author G
 */
public interface IDdlContext {

    /**
     * 返回实体定义
     * @param typeId
     * @return 对象
     */
    public IEntityType desc(String typeId);

    /**
    * 根据指定的表描述和元数据重建被管理的表, 并导入表定义
    * @param table 表描述对象
    * @param metaItems 元数据对象列表
    * @return 成功、失败
    */
	public String rebuildAsImportData(ITableDesc table, Collection<MetaItem> metaItems) ;
}
