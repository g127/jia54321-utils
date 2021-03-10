package com.jia54321.utils.entity;

import com.jia54321.utils.JsonHelper;
import com.jia54321.utils.Kv;
import com.jia54321.utils.entity.query.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DynamicEntityTest {

    @Test
    public void test() {
        DynamicEntity entity = newDynamicEntity("10", "{ \"TYPE_ID\":\"1001\"}", null);

        SqlBuilder sqlBuilder = new SqlBuilder();

        CrudTableDesc userTable = new CrudTableDesc();
        userTable.setTableDesc(entity.getTableDesc());
        userTable.setColumnProps(entity.getItems());



        SqlContent test =  sqlBuilder.buildInsertSql(userTable);
        System.out.println(test);


        test =  sqlBuilder.buildUpdateSql(userTable);
        System.out.println(test);

        test =  sqlBuilder.buildDeleteSql(userTable);
        System.out.println(test);

        // 构造GET查询 buildGetSql
        test =  sqlBuilder.buildGetSql(userTable);

        System.out.println(test);

        // 查询
        SimpleCondition sc = SimpleConditionFactory.createByAnd(Kv.init().set("EQ_TYPE_MK","SYS").set("EQ_TYPE_ENTITY_NAME","USER"), 1, 20);
        QueryContent qc = QueryContentFactory.createQueryContent(sc);
        // 构造查询 buildQueryCondition
        SqlContent querySqlContent = sqlBuilder.buildQueryCondition(userTable, qc.getConditions(), qc.getSorts());

        test = querySqlContent;

        System.out.println(test);

        System.out.println(JsonHelper.toJson(entity));
    }

    public DynamicEntity newDynamicEntity(String typeId, String jsonItems, Map<String, Object> extItems){
        if (jsonItems == null || "".equals(jsonItems)) {
            jsonItems = "{}";
        }

        DynamicEntity entity = JsonHelper.cast(
                String.format("{\"typeId\":\"%s\",\"items\": %s }", typeId, jsonItems), DynamicEntity.class);

        if (entity.getItems() == null) {
            entity.setItems(new LinkedHashMap<>());
        }

        if(extItems != null && extItems.size() > 0) {
            entity.getItems().putAll(extItems);
        }

        return entity;
    }

}