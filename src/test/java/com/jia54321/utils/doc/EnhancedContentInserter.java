package com.jia54321.utils.doc;

import com.jia54321.utils.doc.dbTable.*;
import com.jia54321.utils.doc.wordCfg.TableTitleConfig;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 增强的内容插入器 - 支持带标题的表格
 */
@AllArgsConstructor
public class EnhancedContentInserter {

    private EnhancedTableGenerator enhancedTableGenerator;

    /**
     * 在标记位置插入带标题的完整表结构
     */
    public void insertTableWithTitle(XWPFDocument document, MarkerPosition position,
                                     BasicTable tableData, TableTitleConfig titleConfig) {
        switch (position.getType()) {
            case PARAGRAPH:
                insertTableWithTitleAtParagraph(document, position, tableData, titleConfig);
                break;
            case TABLE_CELL:
                insertTableWithTitleInTableCell(position, tableData, titleConfig);
                break;
            default:
                throw new IllegalArgumentException("不支持的标记位置类型: " + position.getType());
        }
    }

    /**
     * 在段落标记位置插入带标题的表格
     */
    private void insertTableWithTitleAtParagraph(XWPFDocument document, MarkerPosition position,
                                                 BasicTable tableData, TableTitleConfig titleConfig) {
        XWPFParagraph markerParagraph = position.getParagraph();

        // 清空标记段落
        clearParagraph(markerParagraph);

//        // 将标记段落转换为标题
//        convertMarkerToTitle(markerParagraph, tableData, titleConfig);

        // 在标题后插入表格内容
        insertTableContentAfterParagraph(document, markerParagraph, tableData, titleConfig);
    }

    /**
     * 将标记段落转换为标题
     */
    private void convertMarkerToTitle(XWPFParagraph paragraph, BasicTable tableData,
                                      TableTitleConfig titleConfig) {
        // 清空段落
        clearParagraph(paragraph);

        // 设置段落对齐
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraph.setSpacingBefore(200);
        paragraph.setSpacingAfter(100);

        // 构建标题文本
        String titleText = buildSimpleTitle(tableData, titleConfig);

        // 创建标题
        XWPFRun titleRun = paragraph.createRun();
        titleRun.setText(titleText);
        titleRun.setBold(true);
        titleRun.setFontSize(12);
        titleRun.setFontFamily("宋体");
    }

    /**
     * 构建简化的标题文本（用于段落内）
     */
    private String buildSimpleTitle(BasicTable tableData, TableTitleConfig titleConfig) {
        StringBuilder titleBuilder = new StringBuilder();

        // 添加编号
        if (titleConfig.isAutoNumbering()) {
            titleBuilder.append(titleConfig.getPrefix())
                    .append(enhancedTableGenerator.getTableCounter())
                    .append(titleConfig.getSuffix())
                    .append(" ");
        }

        // 添加标题文本
        if (titleConfig.getTitle() != null) {
            titleBuilder.append(titleConfig.getTitle()).append(" ");
        }

        // 添加表名
        titleBuilder.append(tableData.getTableName());

        // 添加表注释
        if (tableData.getTableComment() != null && !tableData.getTableComment().isEmpty()) {
            titleBuilder.append(" - ").append(tableData.getTableComment());
        }

        return titleBuilder.toString();
    }

    /**
     * 在段落后插入表格内容
     */
    private void insertTableContentAfterParagraph(XWPFDocument document, XWPFParagraph anchorParagraph,
                                                  BasicTable tableData, TableTitleConfig titleConfig) {
        // 获取锚点段落位置
        int anchorIndex = getParagraphIndex(document, anchorParagraph);
        if (anchorIndex == -1) return;

        // 生成带标题的完整表格内容
        List<IBodyElement> tableElements = enhancedTableGenerator.generateCompleteTableWithTitles(
                document, tableData, titleConfig);

        // 在锚点段落后插入所有元素
        int insertPosition = anchorIndex + 1;
        for (IBodyElement element : tableElements) {
            insertBodyElementAtPosition(document, insertPosition, element);
            insertPosition++;
        }

        // 在最后添加空行分隔
        insertEmptyParagraphAtPosition(document, insertPosition);
    }

    /**
     * 在表格单元格中插入带标题的表结构
     */
    private void insertTableWithTitleInTableCell(MarkerPosition position, BasicTable tableData,
                                                 TableTitleConfig titleConfig) {
        XWPFTableCell cell = position.getTableCell();

        // 清空单元格
        clearTableCell(cell);

        // 添加带标题的格式化内容
        addTitledContentToCell(cell, tableData, titleConfig);
    }

    /**
     * 在单元格中添加带标题的格式化内容
     */
    private void addTitledContentToCell(XWPFTableCell cell, BasicTable tableData,
                                        TableTitleConfig titleConfig) {
        XWPFParagraph paragraph = cell.addParagraph();

        // 添加标题
        XWPFRun titleRun = paragraph.createRun();
        String titleText = buildSimpleTitle(tableData, titleConfig);
        titleRun.setText(titleText);
        titleRun.setBold(true);
        titleRun.setFontSize(10);
        titleRun.addBreak();
        titleRun.addBreak();

        // 添加列信息
        addColumnInfoToCell(paragraph, tableData);

        // 添加索引信息
        if (!tableData.getIndexes().isEmpty()) {
            addIndexInfoToCell(paragraph, tableData);
        }

        // 添加外键信息
        if (!tableData.getForeignKeys().isEmpty()) {
            addForeignKeyInfoToCell(paragraph, tableData);
        }
    }

    // 以下辅助方法与之前相同，保持不变
    private void clearParagraph(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
    }

    private void clearTableCell(XWPFTableCell cell) {
        List<XWPFParagraph> paragraphs = cell.getParagraphs();
        for (int i = paragraphs.size() - 1; i >= 0; i--) {
            cell.removeParagraph(i);
        }
    }

    private int getParagraphIndex(XWPFDocument document, XWPFParagraph paragraph) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (int i = 0; i < paragraphs.size(); i++) {
            if (paragraphs.get(i) == paragraph) {
                return i;
            }
        }
        return -1;
    }

    private void insertBodyElementAtPosition(XWPFDocument document, int position, IBodyElement element) {
        try {
            List<IBodyElement> bodyElements = document.getBodyElements();
            if (position <= bodyElements.size()) {
                bodyElements.add(position, element);
            } else {
                bodyElements.add(element);
            }
        } catch (Exception e) {
            // 如果插入失败，忽略继续
        }
    }

    private void insertEmptyParagraphAtPosition(XWPFDocument document, int position) {
        try {
            XWPFParagraph emptyPara = document.createParagraph();
            emptyPara.createRun().addBreak();
        } catch (Exception e) {
            // 忽略错误
        }
    }

    private void addColumnInfoToCell(XWPFParagraph paragraph, BasicTable tableData) {
        XWPFRun titleRun = paragraph.createRun();
        titleRun.setText("列信息:");
        titleRun.setBold(true);
        titleRun.setFontSize(9);
        titleRun.addBreak();

        for (TableColumn column : tableData.getColumns()) {
            XWPFRun columnRun = paragraph.createRun();
            String pkMark = column.isPrimaryKey() ? " (主键)" : "";
            String aiMark = column.isAutoIncrement() ? " (自增)" : "";
            columnRun.setText("  • " + column.getName() + ": " + column.getDisplayType() + pkMark + aiMark);
            columnRun.setFontSize(8);

            if (column.getComment() != null && !column.getComment().isEmpty()) {
                columnRun.setText(" - " + column.getComment());
            }
            columnRun.addBreak();
        }
        paragraph.createRun().addBreak();
    }

    private void addIndexInfoToCell(XWPFParagraph paragraph, BasicTable tableData) {
        XWPFRun titleRun = paragraph.createRun();
        titleRun.setText("索引信息:");
        titleRun.setBold(true);
        titleRun.setFontSize(9);
        titleRun.addBreak();

        for (TableIndex index : tableData.getIndexes()) {
            XWPFRun indexRun = paragraph.createRun();
            indexRun.setText("  • " + index.getName() + ": " + index.getDisplayType() +
                    " (" + String.join(", ", index.getColumns()) + ")");
            indexRun.setFontSize(8);
            indexRun.addBreak();
        }
        paragraph.createRun().addBreak();
    }

    private void addForeignKeyInfoToCell(XWPFParagraph paragraph, BasicTable tableData) {
        XWPFRun titleRun = paragraph.createRun();
        titleRun.setText("外键关系:");
        titleRun.setBold(true);
        titleRun.setFontSize(9);
        titleRun.addBreak();

        for (TableForeignKey fk : tableData.getForeignKeys()) {
            XWPFRun fkRun = paragraph.createRun();
            fkRun.setText("  • " + fk.getColumnName() + " → " +
                    fk.getReferencedTableName() + "." + fk.getReferencedColumnName());
            fkRun.setFontSize(8);
            fkRun.addBreak();
        }
    }
}
