package com.jia54321.utils.entity.jdbc;

import com.jia54321.utils.entity.IEntityType;
import com.jia54321.utils.entity.MetaItem;
import com.jia54321.utils.entity.query.*;

import java.util.List;

public interface IEntityTemplate extends ISqlContextDao, IFuncDao, ISqlBuilderDao {

    /**
     * 创建一个 IEntityType 主键值为 Id 的对象
     * @param typeId 类型ID值
     * @param id     主键ID值
     * @return IEntityType
     */
    public IEntityType create(String typeId, String id);

    /**
     * 获取实体的描述
     * @param typeId
     * @return IEntityType
     */
    public IEntityType getEntityType(String typeId);

    /**
     * 获取实体的描述（表）
     * @param typeId
     * @return ITableDesc
     */
    public ITableDesc getITableDesc(String typeId);

    /**
     * 获取实体的属性名称集合
     * @param typeId 类型ID值
     * @return List<MetaItem>
     */
    public List<MetaItem> getEntityItems(String typeId);

    /**
     * 获取实体的描述和属性名称集合
     * @param typeId
     * @return IEntityType
     */
    public IEntityType getEntityTypeAndItems(String typeId);

    /**
     * 获取实体的属性名称集合
     * @param typeId 类型ID值
     * @param tabName 表名称
     * @return  获取实体的属性名称集合
     */
    public List<MetaItem> getEntityItemsByTabName(String typeId, String tabName);

}
