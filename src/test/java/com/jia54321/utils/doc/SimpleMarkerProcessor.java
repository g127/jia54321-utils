package com.jia54321.utils.doc;

import com.jia54321.utils.doc.dbTable.BasicTable;
import com.jia54321.utils.doc.wordCfg.PositionType;
import com.jia54321.utils.doc.wordCfg.TableTitleConfig;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 简化的标记处理器 - 使用正确的方法
 */
@AllArgsConstructor
public class SimpleMarkerProcessor {

    private EnhancedContentInserter contentInserter;

    /**
     * 处理文档中的所有标记
     */
    public void processDocument(String templatePath, String outputPath, List<BasicTable> tables) {
        try (XWPFDocument document = new XWPFDocument(new FileInputStream(templatePath))) {

            // 处理标记
            processMarkers(document, tables);

            // 保存文档
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }

            System.out.println("文档处理完成: " + outputPath);

        } catch (Exception e) {
            throw new RuntimeException("处理文档失败", e);
        }
    }

    /**
     * 处理所有标记
     */
    private void processMarkers(XWPFDocument document, List<BasicTable> tables) {
        // 处理单个表标记
        for (BasicTable table : tables) {
            String marker = "{{TABLE:" + table.getTableName().toUpperCase() + "}}";
            processSingleMarker(document, marker, table);
        }

        // 处理所有表标记
        processAllTablesMarker(document, "{{ALL_TABLES}}", tables);

        // 处理数据库信息标记
        processDatabaseInfoMarker(document, "{{DATABASE_INFO}}", tables);
    }

    /**
     * 处理单个标记
     */
    private void processSingleMarker(XWPFDocument document, String marker, BasicTable table) {
        List<MarkerPosition> positions = findMarkers(document, marker);

        for (MarkerPosition position : positions) {

//            contentReplacer.replaceMarkerWithContent(document, position, table);
            contentInserter.insertTableWithTitle(document, position, table, TableTitleConfig.create(table.getTableComment()));

//            // 对于段落标记，在段落后插入表格
//            if (position.getType() == PositionType.PARAGRAPH) {
//                contentReplacer.insertTablesAfterParagraph(document, position.getParagraph(), table);
//            }
        }
    }

    /**
     * 处理所有表标记
     */
    private void processAllTablesMarker(XWPFDocument document, String marker, List<BasicTable> tables) {
        List<MarkerPosition> positions = findMarkers(document, marker);

        for (MarkerPosition position : positions) {
            // 替换标记内容
            replaceWithAllTablesInfo(document, position, tables);

            // 在段落后插入所有表格
            if (position.getType() == PositionType.PARAGRAPH) {
                for (BasicTable table : tables) {
                   // contentReplacer.insertTablesAfterParagraph(document, position.getParagraph(), table);
                    contentInserter.insertTableWithTitle(document, position, table, TableTitleConfig.create(table.getTableComment()));

//                    // 在表格间添加空行
//                    contentReplacer.addEmptyParagraph(document);
                }
            }
        }
    }

    /**
     * 处理数据库信息标记
     */
    private void processDatabaseInfoMarker(XWPFDocument document, String marker, List<BasicTable> tables) {
        List<MarkerPosition> positions = findMarkers(document, marker);

        for (MarkerPosition position : positions) {
            replaceWithDatabaseInfo(position, tables);
        }
    }

    /**
     * 查找文档中的标记
     */
    private List<MarkerPosition> findMarkers(XWPFDocument document, String marker) {
        List<MarkerPosition> positions = new ArrayList<>();

        // 在段落中查找
        findInParagraphs(document, marker, positions);

        // 在表格中查找
        findInTables(document, marker, positions);

        return positions;
    }

    private void findInParagraphs(XWPFDocument document, String marker, List<MarkerPosition> positions) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();

        for (int i = 0; i < paragraphs.size(); i++) {
            XWPFParagraph paragraph = paragraphs.get(i);
            String text = paragraph.getText();

            if (text != null && text.contains(marker)) {
                MarkerPosition position = new MarkerPosition();
                position.setType(PositionType.PARAGRAPH);
                position.setParagraphIndex(i);
                position.setMarkerText(marker);
                position.setParagraph(paragraph);
                positions.add(position);
            }
        }
    }

    private void findInTables(XWPFDocument document, String marker, List<MarkerPosition> positions) {
        List<XWPFTable> tables = document.getTables();

        for (int tableIndex = 0; tableIndex < tables.size(); tableIndex++) {
            XWPFTable table = tables.get(tableIndex);

            for (int rowIndex = 0; rowIndex < table.getNumberOfRows(); rowIndex++) {
                XWPFTableRow row = table.getRow(rowIndex);

                for (int cellIndex = 0; cellIndex < row.getTableCells().size(); cellIndex++) {
                    XWPFTableCell cell = row.getCell(cellIndex);
                    String text = cell.getText();

                    if (text != null && text.contains(marker)) {
                        MarkerPosition position = new MarkerPosition();
                        position.setType(PositionType.TABLE_CELL);
                        position.setTableIndex(tableIndex);
                        position.setRowIndex(rowIndex);
                        position.setCellIndex(cellIndex);
                        position.setMarkerText(marker);
                        position.setTableCell(cell);
                        positions.add(position);
                    }
                }
            }
        }
    }

    /**
     * 替换为所有表信息
     */
    private void replaceWithAllTablesInfo(XWPFDocument document, MarkerPosition position,
                                          List<BasicTable> tables) {
        if (position.getType() != PositionType.PARAGRAPH) return;

        XWPFParagraph paragraph = position.getParagraph();

        // 清空段落
        clearParagraph(paragraph);

        // 添加信息
        XWPFRun run = paragraph.createRun();
        run.setText("数据库包含 " + tables.size() + " 个表");
        run.setBold(true);
        run.setFontSize(12);
    }

    /**
     * 替换为数据库信息
     */
    private void replaceWithDatabaseInfo(MarkerPosition position, List<BasicTable> tables) {
        if (position.getType() != PositionType.PARAGRAPH) return;

        XWPFParagraph paragraph = position.getParagraph();

        // 清空段落
        clearParagraph(paragraph);

        // 添加数据库信息
        XWPFRun run = paragraph.createRun();
        int totalColumns = tables.stream().mapToInt(t -> t.getColumns().size()).sum();
        int totalIndexes = tables.stream().mapToInt(t -> t.getIndexes().size()).sum();
        int totalForeignKeys = tables.stream().mapToInt(t -> t.getForeignKeys().size()).sum();

        String info = String.format(
                "数据库统计: %d 个表, %d 个字段, %d 个索引, %d 个外键关系",
                tables.size(), totalColumns, totalIndexes, totalForeignKeys
        );

        run.setText(info);
        run.setFontSize(11);
    }

    /**
     * 清空段落
     */
    private void clearParagraph(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
    }

}