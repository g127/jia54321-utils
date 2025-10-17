package com.jia54321.utils.doc;

import com.jia54321.utils.doc.dbTable.BasicTable;
import com.jia54321.utils.doc.dbTable.BasicTableGenerator;
import com.jia54321.utils.doc.wordCfg.TableTitleConfig;
import com.jia54321.utils.doc.wordCfg.TitleStyle;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 增强的表格生成器 - 支持表格标题
 */
@AllArgsConstructor
public class EnhancedTableGenerator {

    private BasicTableGenerator basicTableGenerator;

    private int tableCounter = 1; // 表格计数器，用于自动编号

    /**
     * 生成带标题的完整表结构（列+索引+外键）
     */
    public List<IBodyElement> generateCompleteTableWithTitles(XWPFDocument document, BasicTable tableData,
                                                              TableTitleConfig mainTitleConfig) {
        List<IBodyElement> elements = new ArrayList<>();

        // 1. 生成主标题
        XWPFParagraph mainTitle = createTableTitle(document, tableData, mainTitleConfig, null);
        elements.add(mainTitle);

        // 2. 生成列信息子标题和表格
        if (mainTitleConfig.isShowTitle()) {
            TableTitleConfig columnsTitleConfig = createSubTitleConfig("列信息");
            XWPFParagraph columnsTitle = createSubTitle(document, columnsTitleConfig);
            elements.add(columnsTitle);
        }

        XWPFTable columnsTable = basicTableGenerator.generateColumnsTable(document, tableData);
        elements.add(columnsTable);

        // 3. 生成索引子标题和表格（如果有索引）
        if (!tableData.getIndexes().isEmpty()) {
            elements.add(createEmptyParagraph(document));

            if (mainTitleConfig.isShowTitle()) {
                TableTitleConfig indexesTitleConfig = createSubTitleConfig("索引信息");
                XWPFParagraph indexesTitle = createSubTitle(document, indexesTitleConfig);
                elements.add(indexesTitle);
            }

            XWPFTable indexesTable = basicTableGenerator.generateIndexesTable(document, tableData);
            elements.add(indexesTable);
        }

        // 4. 生成外键子标题和表格（如果有外键）
        if (!tableData.getForeignKeys().isEmpty()) {
            elements.add(createEmptyParagraph(document));

            if (mainTitleConfig.isShowTitle()) {
                TableTitleConfig fkTitleConfig = createSubTitleConfig("外键关系");
                XWPFParagraph fkTitle = createSubTitle(document, fkTitleConfig);
                elements.add(fkTitle);
            }

            XWPFTable fkTable = basicTableGenerator.generateForeignKeysTable(document, tableData);
            elements.add(fkTable);
        }

        // 增加表格计数器
        tableCounter++;

        return elements;
    }

    /**
     * 创建表格主标题
     */
    private XWPFParagraph createTableTitle(XWPFDocument document, BasicTable tableData,
                                           TableTitleConfig config, String subTitle) {
        XWPFParagraph titleParagraph = document.createParagraph();

        // 设置段落样式
        applyTitleStyle(titleParagraph, config);

        // 构建标题文本
        String titleText = buildTitleText(tableData, config, subTitle);

        // 创建标题运行文本
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(titleText);
        applyTitleRunStyle(titleRun, config);

        return titleParagraph;
    }

    /**
     * 创建子标题
     */
    private XWPFParagraph createSubTitle(XWPFDocument document, TableTitleConfig config) {
        XWPFParagraph subTitleParagraph = document.createParagraph();

        // 子标题样式：左对齐，稍小字体
        subTitleParagraph.setAlignment(ParagraphAlignment.LEFT);
        subTitleParagraph.setSpacingBefore(100);
        subTitleParagraph.setSpacingAfter(50);

        XWPFRun subTitleRun = subTitleParagraph.createRun();
        subTitleRun.setText(config.getTitle());
        subTitleRun.setBold(false);
        subTitleRun.setFontSize(11);
        subTitleRun.setFontFamily("宋体");

        return subTitleParagraph;
    }

    /**
     * 构建标题文本
     */
    private String buildTitleText(BasicTable tableData, TableTitleConfig config, String subTitle) {
        StringBuilder titleBuilder = new StringBuilder();

        // 添加自动编号
        if (config.isAutoNumbering()) {
            titleBuilder.append(config.getPrefix())
                    .append(tableCounter)
                    .append(config.getSuffix())
                    .append(" ");
        }

        // 添加主标题
        if (config.getTitle() != null) {
            titleBuilder.append(config.getTitle()).append(" ");
        }

        // 添加表名
        titleBuilder.append(tableData.getTableName());

        // 添加表注释
        if (tableData.getTableComment() != null && !tableData.getTableComment().isEmpty()) {
            titleBuilder.append(" (").append(tableData.getTableComment()).append(")");
        }

        // 添加子标题
        if (subTitle != null && !subTitle.isEmpty()) {
            titleBuilder.append(" - ").append(subTitle);
        }

        return titleBuilder.toString();
    }

    /**
     * 应用标题段落样式
     */
    private void applyTitleStyle(XWPFParagraph paragraph, TableTitleConfig config) {
        // 设置对齐方式
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        // 设置间距
        switch (config.getTitleLevel()) {
            case 1:
                paragraph.setSpacingBefore(400);
                paragraph.setSpacingAfter(200);
                paragraph.setIndentationLeft(0);
                break;
            case 2:
                paragraph.setSpacingBefore(300);
                paragraph.setSpacingAfter(150);
                paragraph.setIndentationLeft(0);
                break;
            default:
                paragraph.setSpacingBefore(200);
                paragraph.setSpacingAfter(100);
                paragraph.setIndentationLeft(100);
                break;
        }
    }

    /**
     * 应用标题文本样式
     */
    private void applyTitleRunStyle(XWPFRun run, TableTitleConfig config) {
        // 设置字体
        run.setFontFamily("宋体");

        // 根据标题级别设置字体大小
        switch (config.getTitleLevel()) {
            case 1:
                run.setFontSize(14);
                break;
            case 2:
                run.setFontSize(12);
                break;
            default:
                run.setFontSize(11);
                break;
        }

        // 应用样式
        switch (config.getStyle()) {
            case STANDARD:
                run.setBold(false);
                break;
            case BOLD:
                run.setBold(true);
                break;
            case UNDERLINE:
                run.setBold(true);
                run.setUnderline(UnderlinePatterns.SINGLE);
                break;
            case COLORFUL:
                run.setBold(true);
                run.setColor("2E86C1"); // 蓝色
                break;
            case MINIMAL:
                // 简约样式，不加粗
                break;
        }
    }

    /**
     * 创建子标题配置
     */
    private TableTitleConfig createSubTitleConfig(String title) {
        TableTitleConfig config = new TableTitleConfig();
        config.setTitle(title);
        config.setShowTitle(true);
        config.setAutoNumbering(false);
        config.setStyle(TitleStyle.STANDARD);
        config.setTitleLevel(3);
        return config;
    }

    /**
     * 创建空段落（用于间距）
     */
    private XWPFParagraph createEmptyParagraph(XWPFDocument document) {
        XWPFParagraph emptyParagraph = document.createParagraph();
        XWPFRun run = emptyParagraph.createRun();
        run.setText("");
        return emptyParagraph;
    }

    /**
     * 重置表格计数器（开始新文档时调用）
     */
    public void resetTableCounter() {
        this.tableCounter = 1;
    }

    /**
     * 获取当前表格计数
     */
    public int getTableCounter() {
        return this.tableCounter;
    }
}