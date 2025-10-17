package com.jia54321.utils.jfinal.activerecord.generator;

import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jia54321.utils.CamelNameUtil;
import com.jia54321.utils.Helper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * 代码生成测试
 */
public class GeneratorTest {

    static final Logger log = LoggerFactory.getLogger(GeneratorTest.class);

    /**
     * 定义数据源
     * @return
     */
    public static DataSource getDataSource(String url, String username, String password) {
        DruidPlugin druidPlugin = new DruidPlugin(url, username, password);
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    /**
     * 自定义生成逻辑
     * @param packageName
     * @param includeName
     */
    public static void generate(String templateDir, DataSource ds , String projectDir, String packageName, final String includeName, Consumer<List<TableMetaExtend>> consumer) {
        Path projectPath = Paths.get(projectDir);

        // =====================================================================================================================
        // 模板目录Dir
        // =====================================================================================================================
        TabGenerator generator = new TabGenerator();
        generator.init(templateDir, packageName, projectPath.toString());

        // =====================================================================================================================

        // =====================================================================================================================
        MetaBuilder metaBuilder = new MetaBuilder(ds);
        metaBuilder.skip(
                // 不匹配的全部跳过
                tableName -> Arrays.stream(includeName.split(","))
                        .filter(Helper::isNotEmpty)
                        .noneMatch(tableName::startsWith)
        );
        metaBuilder.setDialect(new MysqlDialect());
        // 配置是否生成备注
        metaBuilder.setGenerateRemarks(true);
        //
//        metaBuilder.addTypeMapping(java.sql.Date.class, LocalDateTime.class);
//        metaBuilder.addTypeMapping(java.sql.Timestamp.class, LocalDateTime.class);
//        metaBuilder.addTypeMapping(LocalDateTime.class, LocalDateTime.class);
//        metaBuilder.addTypeMapping(LocalDate.class, LocalDate.class);
//        metaBuilder.addTypeMapping(LocalTime.class, LocalTime.class);
        // =====================================================================================================================


        // =====================================================================================================================
        long start = System.currentTimeMillis();
        List<TableMeta> tableMetas = metaBuilder.build();
        if (tableMetas.size() == 0) {
            System.out.println("TableMeta 数量为 0，不生成任何文件");
            return ;
        }

        List<TableMetaExtend> tableMetaExtends = new ArrayList<>(tableMetas.size());

        for (int i = 0; i < tableMetas.size(); i++) {
            TableMeta old           = tableMetas.get(i);
            TableMetaExtend  newObj = new TableMetaExtend();
            newObj.name             = old.name       ; // 表名
            newObj.remarks          = old.remarks    ; // 表备注
            newObj.primaryKey       = old.primaryKey ; // 主键，复合主键以逗号分隔
            newObj.columnMetas      = old.columnMetas; // 字段 meta

            // ---------
            newObj.basePackageName  = packageName;
            newObj.baseModelName    = old.baseModelName;	  // 生成的 base model 名
            newObj.baseModelContent = old.baseModelContent;	  // 生成的 base model 内容

            newObj.modelName        = old.modelName;	      // 生成的 model 名
            newObj.modelContent     = old.modelContent;		  // 生成的 model 内容

            // ---------

            newObj.colNameMaxLen         = old.colNameMaxLen        ;			// 字段名最大宽度，用于辅助生成字典文件样式
            newObj.colTypeMaxLen         = old.colTypeMaxLen        ;			// 字段类型最大宽度，用于辅助生成字典文件样式
            newObj.colDefaultValueMaxLen = old.colDefaultValueMaxLen;	// 字段默认值最大宽度，用于辅助生成字典文件样式

            // ---------
            newObj.namePrefixes          = old.name.replace(CamelNameUtil.camelToUnderline(old.modelName),"");

            newObj.serviceName          = old.modelName + "Service";

            newObj.serviceImplName      = old.modelName + "ServiceImpl";

            tableMetaExtends.add(newObj);
        }

        if(null != consumer) {
            consumer.accept(tableMetaExtends);
        } else {
            generator.generate(tableMetaExtends);
        }

        long usedTime = (System.currentTimeMillis() - start) / 1000;
        System.out.println("Generate complete in " + usedTime + " seconds.");
        // =====================================================================================================================
    }

    @Test
    public void testGen() {
        /** 当前工作目录, 执行Java程序的路径 */
        final Path userDir = new File(System.getProperty("user.dir")).toPath();

        final String templateDirName = "mybatis-plus/";
        final String templateDirName2 = "tk-mybatis/";
        final Path generatedDir = userDir.resolve("src/generated-domain/").resolve(templateDirName);
        final Path templateDir = Paths.get("generatorCodeTest", templateDirName);

        // 使用DatabaseMetaData获取mysql表的注释
        // 此时获取不到表名的注释，原因是需要在jdbc url 添加如下参数useInformationSchema=true
        generate(
                templateDir.toString(),
//                "generatorCodeTest/tk-mybatis",
                // 数据源
                getDataSource(
                        "jdbc:mysql://114.115.160.57:3306/flypark?useInformationSchema=true&useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true",
                        "root",
                        "Rd4rfv^T"
                ),
                // 生成目录
                generatedDir.toString(),
                // 包名
                "com.msf.code",
                // 表名
                "t_charging_platform,",
                null
        );
    }
}
