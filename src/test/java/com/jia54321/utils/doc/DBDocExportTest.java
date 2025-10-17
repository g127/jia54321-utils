package com.jia54321.utils.doc;


import com.jia54321.utils.doc.dbTable.BasicTable;
import com.jia54321.utils.doc.dbTable.BasicTableGenerator;
import com.jia54321.utils.jfinal.activerecord.generator.GeneratorTest;
import com.jia54321.utils.jfinal.activerecord.generator.TableMetaExtend;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DBDocExportTest {

    public static List<BasicTable> toBasicTableList(List<TableMetaExtend> tableMetaExtendList){
        List<BasicTable> basicTables = new ArrayList<>();
        for (TableMetaExtend tableMetaExtend : tableMetaExtendList) {
            BasicTable basicTable = new BasicTable(tableMetaExtend.name, tableMetaExtend.remarks);
            tableMetaExtend.columnMetas.forEach(columnMeta -> basicTable.addColumn(
                    columnMeta.name,
                    columnMeta.type,
                    !Objects.equals(columnMeta.isNullable, "NO"),
                    columnMeta.defaultValue,
                    columnMeta.remarks,
                    Objects.equals(columnMeta.isPrimaryKey, "PRI")
            ));
            basicTables.add(basicTable);
        }
        return basicTables;
    }

    public static void genDoc(List<TableMetaExtend> tableMetaExtendList) {
        try {
            // 1. 准备表格数据
            List<BasicTable> basicTables = toBasicTableList(tableMetaExtendList);

            // 2.
            SimpleMarkerProcessor simpleMarkerProcessor = new SimpleMarkerProcessor(
                    new EnhancedContentInserter(new EnhancedTableGenerator(new BasicTableGenerator(),1))
            );

            // 3.
            simpleMarkerProcessor.processDocument(
                    "D:\\guogang\\DevProjectFiles\\ws-fly-tnar\\工作安排\\武汉城投停车项目\\交付文件\\design_template.docx",
                    "D:\\guogang\\DevProjectFiles\\ws-fly-tnar\\工作安排\\武汉城投停车项目\\交付文件\\gggg.docx",
                    basicTables);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /** 当前工作目录, 执行Java程序的路径 */
        final Path userDir = new File(System.getProperty("user.dir")).toPath();

        final String templateDirName = "mybatis-plus/";
        final String templateDirName2 = "tk-mybatis/";
        final Path generatedDir = userDir.resolve("src/generated-domain/").resolve(templateDirName);
        final Path templateDir = Paths.get("generatorCodeTest", templateDirName);

        // 使用DatabaseMetaData获取mysql表的注释
        // 此时获取不到表名的注释，原因是需要在jdbc url 添加如下参数useInformationSchema=true
        GeneratorTest.generate(
                templateDir.toString(),
//                "generatorCodeTest/tk-mybatis",
                // 数据源
                GeneratorTest.getDataSource(
                        "jdbc:mysql://114.115.160.57:3306/flypark?useInformationSchema=true&useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true",
                        "root",
                        "Rd4rfv^T"
                ),
                // 生成目录
                generatedDir.toString(),
                // 包名
                "com.msf.code",
                // 表名
                "t_charging,",
                DBDocExportTest::genDoc
        );
    }


}
