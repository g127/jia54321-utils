package com.jia54321.utils.doc.dbTable;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础表格数据
 */
@Data
public class BasicTable {
    private String tableName;
    private String tableComment;
    private List<TableColumn> columns;
    private List<TableIndex> indexes;
    private List<TableForeignKey> foreignKeys;

    // 表格显示配置
    private TableStyle style = TableStyle.STANDARD;

    public BasicTable(String tableName, String tableComment) {
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.columns = new ArrayList<>();
        this.indexes = new ArrayList<>();
        this.foreignKeys = new ArrayList<>();
    }

    public void addColumn(String name, String type, boolean nullable,
                          String defaultValue, String comment, boolean primaryKey) {
        TableColumn column = new TableColumn();
        column.setName(name);
        column.setType(type);
        column.setNullable(nullable);
        column.setDefaultValue(defaultValue);
        column.setComment(comment);
        column.setPrimaryKey(primaryKey);
        columns.add(column);
    }
}



