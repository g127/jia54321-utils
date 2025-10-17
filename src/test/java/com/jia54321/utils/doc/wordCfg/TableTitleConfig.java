package com.jia54321.utils.doc.wordCfg;

import lombok.Data;

/**
 * 表格标题配置
 */
@Data
public class TableTitleConfig {
    private String title;           // 标题文本
    private boolean showTitle = true; // 是否显示标题
    private TitleStyle style = TitleStyle.STANDARD; // 标题样式
    private boolean autoNumbering = true; // 是否自动编号
    private int titleLevel = 2;     // 标题级别 (1-6)
    private String prefix = "表";   // 编号前缀
    private String suffix = "";     // 编号后缀

    public static TableTitleConfig defaultConfig() {
        TableTitleConfig config = new TableTitleConfig();
        config.setTitle("表结构");
        config.setShowTitle(true);
        config.setStyle(TitleStyle.STANDARD);
        config.setAutoNumbering(true);
        config.setTitleLevel(2);
        config.setPrefix("表");
        return config;
    }

    public static TableTitleConfig create(String title) {
        TableTitleConfig config = new TableTitleConfig();
        config.setTitle(title);
        config.setShowTitle(false);
        return config;
    }
}

