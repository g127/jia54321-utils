package com.jia54321.utils.doc;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.util.List;

/**
 * 表格宽度设置工具类
 */
public class TableWidthUtils {

    // 页面宽度设置（单位：TWIPS）
    private static final int PAGE_WIDTH_TWIPS = 12240; // 8.5英寸 * 1440 TWIPS/英寸
    private static final int USABLE_PAGE_WIDTH = 8000; // 可用页面宽度

    /**
     * 设置表格固定宽度
     */
    public void setTableFixedWidth(XWPFTable table, int totalWidth) {
        if (table == null) return;

        // 获取表格的CTTbl
        CTTbl ctTbl = table.getCTTbl();
        CTTblPr tblPr = ctTbl.getTblPr();
        if (tblPr == null) {
            tblPr = ctTbl.addNewTblPr();
        }

        // 设置表格宽度
        CTTblWidth tblWidth = tblPr.getTblW();
        if (tblWidth == null) {
            tblWidth = tblPr.addNewTblW();
        }
        tblWidth.setW(BigInteger.valueOf(totalWidth));
        tblWidth.setType(STTblWidth.DXA); // 使用DXA单位（TWIPS）

        // 设置表格布局为固定
        CTTblLayoutType layoutType = tblPr.getTblLayout();
        if (layoutType == null) {
            layoutType = tblPr.addNewTblLayout();
        }
        layoutType.setType(STTblLayoutType.FIXED);
    }

    /**
     * 设置表格列宽
     */
    public void setColumnWidths(XWPFTable table, int[] columnWidths) {
        if (table == null || columnWidths == null) return;

        // 确保表格有足够的列
        int columnCount = table.getRow(0).getTableCells().size();
        if (columnWidths.length != columnCount) {
            throw new IllegalArgumentException("列宽数组长度必须与表格列数一致");
        }

        // 设置每列宽度
        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
            setColumnWidth(table, colIndex, columnWidths[colIndex]);
        }
    }

    /**
     * 设置单列宽度
     */
    private void setColumnWidth(XWPFTable table, int colIndex, int width) {
        // 获取表格的网格列定义
        CTTblGrid grid = table.getCTTbl().getTblGrid();
        if (grid == null) {
            grid = table.getCTTbl().addNewTblGrid();
        }

        // 确保有足够的网格列
        List<CTTblGridCol> gridCols = grid.getGridColList();
        while (gridCols.size() <= colIndex) {
            grid.addNewGridCol();
        }

        // 设置列宽
        CTTblGridCol gridCol = gridCols.get(colIndex);
        gridCol.setW(BigInteger.valueOf(width));
    }

    /**
     * 获取列信息表格的标准列宽
     */
    public int[] getStandardColumnsTableWidths() {
        // 7列：序号、字段名、数据类型、可空、默认值、主键、字段说明
        return new int[] {
                800   ,   // 序号
                1600  ,  // 字段名
                800   ,   // 类型
                800   ,   // 可空
                800   ,   // 默认
                800   ,   // 主键
                2400   // 说明
        }; // 总宽度：8000 TWIPS
    }

    /**
     * 获取索引表格的标准列宽
     */
    public int[] getStandardIndexesTableWidths() {
        // 5列：索引名称、索引类型、包含列、唯一性、索引说明
        return new int[] {
                1800,  // 索引名称
                1200,  // 索引类型
                2000,  // 包含列
                800,   // 唯一性
                4000   // 索引说明
        }; // 总宽度：8000 TWIPS
    }

    /**
     * 获取外键表格的标准列宽
     */
    public int[] getStandardForeignKeysTableWidths() {
        // 6列：外键名称、字段名、引用表、引用字段、更新规则、删除规则
        return new int[] {
                1500,  // 外键名称
                1200,  // 字段名
                1500,  // 引用表
                1500,  // 引用字段
                1000,  // 更新规则
                3100   // 删除规则
        }; // 总宽度：8000 TWIPS
    }

    /**
     * 设置表格自动适应页面宽度
     */
    public void setTableAutoFit(XWPFTable table) {
        if (table == null) return;

        CTTbl ctTbl = table.getCTTbl();
        CTTblPr tblPr = ctTbl.getTblPr();
        if (tblPr == null) {
            tblPr = ctTbl.addNewTblPr();
        }

        // 设置表格宽度为100%
        CTTblWidth tblWidth = tblPr.getTblW();
        if (tblWidth == null) {
            tblWidth = tblPr.addNewTblW();
        }
        tblWidth.setW(BigInteger.valueOf(5000)); // 50% 页面宽度
        tblWidth.setType(STTblWidth.PCT); // 百分比

        // 设置自动布局
        CTTblLayoutType layoutType = tblPr.getTblLayout();
        if (layoutType == null) {
            layoutType = tblPr.addNewTblLayout();
        }
        layoutType.setType(STTblLayoutType.AUTOFIT);
    }

    /**
     * 设置表格单元格文本自动换行
     */
    public void setCellTextWrapping(XWPFTableCell cell, boolean wrap) {
        if (cell == null) return;

        CTTc ctTc = cell.getCTTc();
        CTTcPr tcPr = ctTc.getTcPr();
        if (tcPr == null) {
            tcPr = ctTc.addNewTcPr();
        }

        // 设置不自动调整大小（允许文本换行）
        CTOnOff noWrap = tcPr.getNoWrap();
        if (noWrap == null) {
            noWrap = tcPr.addNewNoWrap();
        }
        if (!wrap) {
            noWrap.setVal(STOnOff1.ON);
        } else {
            tcPr.unsetNoWrap(); // 允许换行
        }
    }

    /**
     * 设置整个表格的文本换行
     */
    public void setTableTextWrapping(XWPFTable table, boolean wrap) {
        if (table == null) return;

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                setCellTextWrapping(cell, wrap);
            }
        }
    }

    /**
     * 计算适合页面宽度的表格总宽度
     */
    public int calculateOptimalTableWidth(int margin) {
        return USABLE_PAGE_WIDTH - (margin * 2);
    }

    /**
     * 根据内容自动计算列宽（简单算法）
     */
    public int[] calculateAutoColumnWidths(XWPFTable table) {
        if (table == null || table.getNumberOfRows() == 0) {
            return getStandardColumnsTableWidths();
        }

        int colCount = table.getRow(0).getTableCells().size();
        int[] maxTextLengths = new int[colCount];

        // 遍历所有行，找到每列的最大文本长度
        for (XWPFTableRow row : table.getRows()) {
            for (int i = 0; i < colCount && i < row.getTableCells().size(); i++) {
                XWPFTableCell cell = row.getCell(i);
                String text = cell.getText();
                if (text != null) {
                    maxTextLengths[i] = Math.max(maxTextLengths[i], text.length());
                }
            }
        }

        // 根据最大文本长度计算列宽
        int totalChars = 0;
        for (int length : maxTextLengths) {
            totalChars += Math.max(length, 5); // 最小5个字符
        }

        int[] widths = new int[colCount];
        int totalWidth = USABLE_PAGE_WIDTH;

        for (int i = 0; i < colCount; i++) {
            double ratio = (double) Math.max(maxTextLengths[i], 5) / totalChars;
            widths[i] = (int) (totalWidth * ratio);
        }

        return widths;
    }
}