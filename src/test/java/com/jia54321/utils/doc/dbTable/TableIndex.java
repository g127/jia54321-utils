package com.jia54321.utils.doc.dbTable;

import lombok.Data;

import java.util.List;

/**
 * 索引信息
 */
@Data
public class TableIndex {
    private String name;
    private String type; // PRIMARY, UNIQUE, INDEX, FULLTEXT
    private List<String> columns;
    private Boolean nonUnique;
    private String comment;

    public String getDisplayType() {
        switch (type) {
            case "PRIMARY": return "主键索引";
            case "UNIQUE": return "唯一索引";
            case "FULLTEXT": return "全文索引";
            default: return "普通索引";
        }
    }
}
