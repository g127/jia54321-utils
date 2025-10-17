package com.jia54321.utils.doc.dbTable;


import lombok.Data;

/**
 * 外键信息
 */
@Data
public class TableForeignKey {
    private String name;
    private String columnName;
    private String referencedTableName;
    private String referencedColumnName;
    private String updateRule; // CASCADE, SET NULL, RESTRICT, NO ACTION
    private String deleteRule;

    public String getDisplayRule() {
        return "ON UPDATE " + updateRule + " ON DELETE " + deleteRule;
    }
}