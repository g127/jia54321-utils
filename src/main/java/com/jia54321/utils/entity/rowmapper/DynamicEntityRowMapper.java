package com.jia54321.utils.entity.rowmapper;

import com.jia54321.utils.JsonHelper;
import com.jia54321.utils.entity.IDynamicEntity;
import com.jia54321.utils.entity.MetaItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public class DynamicEntityRowMapper extends AbstractRowMapper<IDynamicEntity> {

    @Override
    public IDynamicEntity mapRow(ResultSet rs, int paramInt)
            throws SQLException {
        boolean metaItemExistPk = false;
        IDynamicEntity targetDynamicEntity = newInstance();
        for (Iterator<Map.Entry<String, MetaItem>> iter = targetDynamicEntity.iteratorMetaItems(); iter.hasNext();) {
            Map.Entry<String, MetaItem> e =  iter.next();
            MetaItem metaItem = e.getValue();
            Object val = rs.getObject(metaItem.getItemColName());
            targetDynamicEntity.set(e.getValue().getItemColName(), val);
            //如果存在主键定义
            if(metaItem.getItemColName().equals(targetDynamicEntity.getTableDesc().getTypePkName())){
                metaItemExistPk  = true;
            }
        }

        //处理PK
        if(!metaItemExistPk) {
            boolean isPkUseDbAutoIncreaseId = targetDynamicEntity.getTableDesc().isPkUseDbAutoIncreaseId();
            boolean isPkUseVarcharId = targetDynamicEntity.getTableDesc().isPkUseVarcharId();
            boolean isPkUseNumberId = targetDynamicEntity.getTableDesc().isPkUseNumberId();

            Object val = rs.getObject(targetDynamicEntity.getTableDesc().getTypePkName());
            if( isPkUseVarcharId){
                val =  JsonHelper.toStr(val,"");
            }else{
                val =  JsonHelper.toLong(val,0L);
            }
            targetDynamicEntity.set(targetDynamicEntity.getTableDesc().getTypePkName(), val);
        }

        return targetDynamicEntity;
    }

}
