package com.jia54321.utils.doc.dbTable;

import com.jia54321.utils.doc.TableWidthUtils;
import com.jia54321.utils.doc.wordCfg.TableType;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;
/**
 * 修正后的 BasicTableGenerator - 使用正确的 POI API
 */
@Component
public class BasicTableGenerator {

    TableWidthUtils widthUtils = new TableWidthUtils();

    /**
     * 生成列信息表格 - 修正版本
     */
    public XWPFTable generateColumnsTable(XWPFDocument document, BasicTable tableData) {
        // 创建表格 - 正确的方式
        XWPFTable table = document.createTable();

        // 先创建表头行
        XWPFTableRow headerRow = table.getRow(0); // 表格创建时默认有一行
        if (headerRow == null) {
            headerRow = table.createRow();
        }

        // 确保表头行有足够的单元格
        ensureCellCount(headerRow, 7);

        // 设置表头
        setHeaderCell(headerRow.getCell(0), "序号");
        setHeaderCell(headerRow.getCell(1), "字段名");
        setHeaderCell(headerRow.getCell(2), "数据类型");
        setHeaderCell(headerRow.getCell(3), "可空");
        setHeaderCell(headerRow.getCell(4), "默认值");
        setHeaderCell(headerRow.getCell(5), "主键");
        setHeaderCell(headerRow.getCell(6), "字段说明");

        // 添加数据行
        int rowNum = 1;
        for (TableColumn column : tableData.getColumns()) {
            XWPFTableRow dataRow = table.createRow();
            ensureCellCount(dataRow, 7);

            setDataCell(dataRow.getCell(0), String.valueOf(rowNum));
            setDataCell(dataRow.getCell(1), column.getName());
            setDataCell(dataRow.getCell(2), column.getDisplayType());
            setDataCell(dataRow.getCell(3), column.isNullable() ? "是" : "否");
            setDataCell(dataRow.getCell(4), column.getDefaultValue() != null ? column.getDefaultValue() : "");
            setDataCell(dataRow.getCell(5), column.isPrimaryKey() ? "✓" : "");
            setDataCell(dataRow.getCell(6), column.getComment() != null ? column.getComment() : "");

            rowNum++;
        }

        // 设置表格固定宽度
        applyFixedWidthToTable(table, TableType.COLUMNS);

        // 应用表格样式
        applyTableStyle(table, tableData.getStyle());

        return table;
    }

    /**
     * 生成索引信息表格 - 修正版本
     */
    public XWPFTable generateIndexesTable(XWPFDocument document, BasicTable tableData) {
        XWPFTable table = document.createTable();

        // 创建表头行
        XWPFTableRow headerRow = table.getRow(0);
        if (headerRow == null) {
            headerRow = table.createRow();
        }
        ensureCellCount(headerRow, 5);

        // 设置表头
        setHeaderCell(headerRow.getCell(0), "索引名称");
        setHeaderCell(headerRow.getCell(1), "索引类型");
        setHeaderCell(headerRow.getCell(2), "包含列");
        setHeaderCell(headerRow.getCell(3), "唯一性");
        setHeaderCell(headerRow.getCell(4), "索引说明");

        // 添加数据行
        int rowNum = 1;
        for (TableIndex index : tableData.getIndexes()) {
            XWPFTableRow dataRow = table.createRow();
            ensureCellCount(dataRow, 5);

            setDataCell(dataRow.getCell(0), index.getName());
            setDataCell(dataRow.getCell(1), index.getDisplayType());
            setDataCell(dataRow.getCell(2), String.join(", ", index.getColumns()));
            setDataCell(dataRow.getCell(3), Boolean.TRUE.equals(index.getNonUnique()) ? "否" : "是");
            setDataCell(dataRow.getCell(4), index.getComment() != null ? index.getComment() : "");

            rowNum++;
        }

        applyTableStyle(table, tableData.getStyle());
        return table;
    }

    /**
     * 生成外键信息表格 - 修正版本
     */
    public XWPFTable generateForeignKeysTable(XWPFDocument document, BasicTable tableData) {
        XWPFTable table = document.createTable();

        // 创建表头行
        XWPFTableRow headerRow = table.getRow(0);
        if (headerRow == null) {
            headerRow = table.createRow();
        }
        ensureCellCount(headerRow, 6);

        // 设置表头
        setHeaderCell(headerRow.getCell(0), "外键名称");
        setHeaderCell(headerRow.getCell(1), "字段名");
        setHeaderCell(headerRow.getCell(2), "引用表");
        setHeaderCell(headerRow.getCell(3), "引用字段");
        setHeaderCell(headerRow.getCell(4), "更新规则");
        setHeaderCell(headerRow.getCell(5), "删除规则");

        // 添加数据行
        int rowNum = 1;
        for (TableForeignKey fk : tableData.getForeignKeys()) {
            XWPFTableRow dataRow = table.createRow();
            ensureCellCount(dataRow, 6);

            setDataCell(dataRow.getCell(0), fk.getName());
            setDataCell(dataRow.getCell(1), fk.getColumnName());
            setDataCell(dataRow.getCell(2), fk.getReferencedTableName());
            setDataCell(dataRow.getCell(3), fk.getReferencedColumnName());
            setDataCell(dataRow.getCell(4), convertRule(fk.getUpdateRule()));
            setDataCell(dataRow.getCell(5), convertRule(fk.getDeleteRule()));

            rowNum++;
        }

        applyTableStyle(table, tableData.getStyle());
        return table;
    }

    /**
     * 确保行有足够数量的单元格
     */
    private void ensureCellCount(XWPFTableRow row, int requiredCellCount) {
        int currentCellCount = row.getTableCells().size();

        // 如果单元格不够，添加新的
        while (currentCellCount < requiredCellCount) {
            row.createCell();
            currentCellCount++;
        }

        // 如果单元格过多，移除多余的（通常不会发生）
        while (currentCellCount > requiredCellCount) {
            row.removeCell(currentCellCount - 1);
            currentCellCount--;
        }
    }

    /**
     * 设置表头单元格样式
     */
    private void setHeaderCell(XWPFTableCell cell, String text) {
        // 清空单元格内容
        for (int i = cell.getParagraphs().size() - 1; i >= 0; i--) {
            cell.removeParagraph(i);
        }

        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontFamily("宋体");
        run.setFontSize(10);

        // 设置单元格背景色
        cell.setColor("D3D3D3"); // 浅灰色背景
    }

    /**
     * 设置数据单元格样式
     */
    private void setDataCell(XWPFTableCell cell, String text) {
        // 清空单元格内容
        for (int i = cell.getParagraphs().size() - 1; i >= 0; i--) {
            cell.removeParagraph(i);
        }

        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun run = paragraph.createRun();
        run.setText(text != null ? text : "");
        run.setFontFamily("宋体");
        run.setFontSize(9);
    }

    /**
     * 转换外键规则为中文描述
     */
    private String convertRule(String rule) {
        if (rule == null) return "RESTRICT";

        switch (rule) {
            case "CASCADE": return "级联";
            case "SET NULL": return "设为NULL";
            case "SET DEFAULT": return "设为默认值";
            case "RESTRICT": return "限制";
            case "NO ACTION": return "无操作";
            default: return rule;
        }
    }

    /**
     * 应用表格样式
     */
    private void applyTableStyle(XWPFTable table, TableStyle style) {
        switch (style) {
            case STANDARD:
                applyStandardStyle(table);
                break;
            case MINIMAL:
                applyMinimalStyle(table);
                break;
            case COLORFUL:
                applyColorfulStyle(table);
                break;
            case COMPACT:
                applyCompactStyle(table);
                break;
        }
    }

    private void applyStandardStyle(XWPFTable table) {
        // 标准样式：交替行颜色
        for (int i = 0; i < table.getNumberOfRows(); i++) {
            XWPFTableRow row = table.getRow(i);
            if (i > 0 && i % 2 == 0) { // 偶数数据行
                for (XWPFTableCell cell : row.getTableCells()) {
                    cell.setColor("F5F5F5"); // 浅灰色背景
                }
            }
        }
    }

    private void applyMinimalStyle(XWPFTable table) {
        // 简约样式：只有表头有背景色
        // 表头已经在setHeaderCell中设置了背景色
    }

    private void applyColorfulStyle(XWPFTable table) {
        // 彩色样式：表头彩色，主键列特殊标记
        XWPFTableRow headerRow = table.getRow(0);
        for (XWPFTableCell cell : headerRow.getTableCells()) {
            cell.setColor("4A86E8"); // 蓝色表头
            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setColor("FFFFFF"); // 白色文字
                }
            }
        }
    }

    private void applyCompactStyle(XWPFTable table) {
        // 紧凑样式：小字体
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        run.setFontSize(8); // 更小的字体
                    }
                }
            }
        }
    }


    /**
     * 应用固定宽度到表格
     */
    private void applyFixedWidthToTable(XWPFTable table, TableType tableType) {
        // 设置表格总宽度
        widthUtils.setTableFixedWidth(table, 8000);

        // 设置列宽
        switch (tableType) {
            case COLUMNS:
                widthUtils.setColumnWidths(table, widthUtils.getStandardColumnsTableWidths());
                break;
            case INDEXES:
                widthUtils.setColumnWidths(table, widthUtils.getStandardIndexesTableWidths());
                break;
            case FOREIGN_KEYS:
                widthUtils.setColumnWidths(table, widthUtils.getStandardForeignKeysTableWidths());
                break;
        }

        // 允许文本换行
        widthUtils.setTableTextWrapping(table, true);
    }
}