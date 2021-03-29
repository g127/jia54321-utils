package com.jia54321.utils.entity.rowmapper;

import com.jia54321.utils.CamelNameUtil;
import com.jia54321.utils.Kv;
import com.jia54321.utils.entity.MetaItem;
import com.jia54321.utils.entity.MetaItemType;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.jia54321.utils.entity.dao.EntityTypeDaoConst.*;

public class MetaItemMapper extends AbstractRowMapper<MetaItem> {

    public final static String SQL = ENTITY_ITEM_SELECT_FROM + ENTITY_ITEM_WHERE_BY_TYPEID;

    @Override
    public MetaItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        MetaItem sourceEntity = newInstance();

        // 是否查询的 SYS_ENTITY_ITEM 表
        boolean isItemTable = rs.getMetaData().getTableName(1).equalsIgnoreCase("SYS_ENTITY_ITEM");
        int columnCount = rs.getMetaData().getColumnCount();

        ResultSetMetaData resultSetMetaData = rs.getMetaData(); // 获取键名

        columnCount = resultSetMetaData.getColumnCount();  // 获取行的数量

        Kv result = Kv.init();
        for (int i = 1; i <= columnCount; i++) {
            try {
                String columnName = resultSetMetaData.getColumnName(i);
                Object columnValue = rs.getObject(i);
                result.put(columnName, columnValue);
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
        if( !isItemTable ) {
            String itemColName = rs.getString(1);
            String itemType =  rs.getString(2);
            String itemColDesc = result.get("COMMENT", "" );

            sourceEntity.setItemId(String.format("%s%02d", sourceEntity.getTypeId(), rs.getRow()));
            sourceEntity.setItemColName(itemColName);
            sourceEntity.setItemCodeName(CamelNameUtil.underlineToCamelLowerCase(itemColName));
            sourceEntity.setItemDisplayName(sourceEntity.getItemCodeName());
            sourceEntity.setItemColDesc(itemColDesc);

            String upperCaseItemType = itemType.toUpperCase();
            if (upperCaseItemType.indexOf("CHAR")> -1 ) {
                sourceEntity.setItemType(MetaItemType.VARCHAR);
                sourceEntity.setItemCodeType("String");
            } else if (upperCaseItemType.indexOf("BLOB")> -1) {  //oracle hack
                sourceEntity.setItemType(MetaItemType.BLOB);
                sourceEntity.setItemCodeType("String");
            } else if (upperCaseItemType.indexOf("TEXT")> -1) {  //mysql hack
                sourceEntity.setItemType(MetaItemType.TEXT);
                sourceEntity.setItemCodeType("String");
            } else if(upperCaseItemType.indexOf("INT")> -1 || upperCaseItemType.indexOf("DEC")> -1 || upperCaseItemType.indexOf("NUM")> -1){
                sourceEntity.setItemType(MetaItemType.NUMBER);
                sourceEntity.setItemCodeType("BigDecimal");
            } else if(upperCaseItemType.indexOf("TIME")> -1 || upperCaseItemType.indexOf("DATE")> -1 ){
                sourceEntity.setItemType(MetaItemType.TIME);
                sourceEntity.setItemCodeType("Timestamp");
            }

            return sourceEntity;
        } else {
            // 对象映射
            return super.mapRow(rs, rowNum);
        }
    }
}
