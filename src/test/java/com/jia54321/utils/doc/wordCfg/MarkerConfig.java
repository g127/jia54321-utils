package com.jia54321.utils.doc.wordCfg;

import lombok.Data;
import org.apache.poi.ss.usermodel.TableStyle;

@Data
public class MarkerConfig {
    private String markerPattern;
    private MarkerType type;
    private String tableName; // 对于单个表标记
    private ContentType contentType;
    private TableStyle style;

    public static MarkerConfig allTables() {
        MarkerConfig config = new MarkerConfig();
        config.setMarkerPattern("{{ALL_TABLES}}");
        config.setType(MarkerType.ALL_TABLES);
        config.setContentType(ContentType.TABLES);
        return config;
    }

    public static MarkerConfig singleTable(String tableName) {
        MarkerConfig config = new MarkerConfig();
        config.setMarkerPattern("{{TABLE:" + tableName.toUpperCase() + "}}");
        config.setType(MarkerType.SINGLE_TABLE);
        config.setTableName(tableName);
        config.setContentType(ContentType.TABLE);
        return config;
    }

    public static MarkerConfig databaseInfo() {
        MarkerConfig config = new MarkerConfig();
        config.setMarkerPattern("{{DATABASE_INFO}}");
        config.setType(MarkerType.DATABASE_INFO);
        config.setContentType(ContentType.TEXT);
        return config;
    }
}
