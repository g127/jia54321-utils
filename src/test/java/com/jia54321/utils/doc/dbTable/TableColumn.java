package com.jia54321.utils.doc.dbTable;

import lombok.Data;

/**
 * 表格列定义
 */
@Data
public class TableColumn {
    private String name;
    private String type;
    private boolean nullable;
    private String defaultValue;
    private String comment;
    private boolean primaryKey;
    private boolean autoIncrement;

    public String getDisplayType() {
        return type + (nullable ? "" : " NOT NULL");
    }
}