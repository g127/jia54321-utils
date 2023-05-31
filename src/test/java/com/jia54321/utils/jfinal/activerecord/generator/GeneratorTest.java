package com.jia54321.utils.jfinal.activerecord.generator;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jia54321.utils.CamelNameUtil;
import com.jia54321.utils.IOUtil;
import com.jia54321.utils.LdapUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * 代码生成测试
 */
public class GeneratorTest {

    static final Logger log = LoggerFactory.getLogger(GeneratorTest.class);

    /**
     * 会员 定义数据源
     * @return
     */
    public static DataSource getDataSource() {
        DruidPlugin druidPlugin = new DruidPlugin(
                "jdbc:mysql://106.14.150.97:31529/msf_member?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8",
                "park",
                "g&!BKilp$Mrl4k#h");
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    /**
     * 对账 定义数据源
     * @return
     */
    public static DataSource getDataSource2() {
        DruidPlugin druidPlugin = new DruidPlugin(
                "jdbc:mysql://106.14.150.97:31529/msf_check_bill?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8",
                "park",
                "g&!BKilp$Mrl4k#h");
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    /**
     * 定义数据源  襄州测试
     * 36.134.79.20:13306
     * park
     * Park@2022**
     * @return
     */
    public static DataSource getDataSource3() {
        DruidPlugin druidPlugin = new DruidPlugin(
                "jdbc:mysql://36.134.79.20:13306/flypark?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8",
                "park",
                "Park@2022**");
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    /**
     * 定义数据源  襄州测试
     * 36.134.79.20:13306
     * park
     * Park@2022**
     * @return
     */
    public static DataSource getDataSource4() {
        DruidPlugin druidPlugin = new DruidPlugin(
                "jdbc:mysql://106.14.150.97:31529/msf_invoice?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8",
                "park",
                "g&!BKilp$Mrl4k#h");
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    /**
     * 生成代码
     */
    public String getMavenProjectDir() {
        //获取当前文件所在的路径
        String localClassPath = this.getClass().getResource("/").getPath();
        File targetFile = new File(localClassPath).getParentFile();
        String projectDir = targetFile.getParent();
        ////
        ////        "generator/java"
        return projectDir;
    }

    /**
     * 生成代码
     * @param packageName
     * @param includeName
     */
    public static void gen(final String packageName, final String includeName) {
        // 项目目录
        String projectDir = System.getProperty("user.dir");

        IOUtil.createDirPath(projectDir);

        String basePackageName = packageName;
        String baseOutputDir = projectDir + "/src/generator/java/"  + basePackageName.replace('.', '/');
        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = basePackageName + ".domain";

        // base model 所使用的包名
        String baseModelPackageName = modelPackageName + ".base";

        // base model 文件保存路径
        String baseModelOutputDir = projectDir + "/src/generator/java/" + baseModelPackageName.replace('.', '/');

        System.out.println("输出路径：" + baseModelOutputDir);

        // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
        String modelOutputDir = baseModelOutputDir + "/..";

        String mappingKitPackageName = basePackageName;
        String mappingKitOutputDir = projectDir + "/src/generator/java/" + basePackageName.replace('.', '/');
        ;

        // service 所使用的包名
        String servicePackageName = basePackageName + ".service";
        // service 文件保存路径
        String serviceOutputDir = projectDir + "/src/generator/java/" + servicePackageName.replace('.', '/');


        // serviceImpl 所使用的包名
        String serviceImplPackageName = servicePackageName + ".impl";
        // serviceImpl 文件保存路径
        String serviceImplOutputDir = serviceOutputDir + "/impl";

        DataSource dataSource = getDataSource();
        // 创建生成器
        Generator generator = new Generator(dataSource,
                basePackageName, baseOutputDir,
                baseModelPackageName, baseModelOutputDir,
                modelPackageName, modelOutputDir,
                mappingKitPackageName, mappingKitOutputDir,
                servicePackageName, serviceOutputDir,
                serviceImplPackageName, serviceImplOutputDir);

        generator.setMetaBuilder(new MetaBuilder(dataSource).skip(
                tableName -> {
                    return !tableName.startsWith(includeName);
                })
        );

        // 配置是否生成备注
        generator.setGenerateRemarks(true);

        // 设置数据库方言
        generator.setDialect(new MysqlDialect());

        // 设置是否生成链式 setter 方法，强烈建议配置成 false，否则 fastjson 反序列化会跳过有返回值的 setter 方法
        generator.setGenerateChainSetter(false);

        // 添加不需要生成的表名
        generator.addExcludedTable("adv");

        // 设置是否在 Model 中生成 dao 对象
        generator.setGenerateDaoInModel(false);

        // 设置是否生成字典文件
        generator.setGenerateDataDictionary(false);

        // 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
        generator.setRemovedTableNamePrefixes("ims_renren_shop_");


        generator.setGenerateServiceDictionary(false);
        generator.setGenerateServiceImplDictionary(false);

        // 生成
        generator.generate();
    }

    /**
     * 自定义生成逻辑
     * @param packageName
     * @param includeName
     */
    public void generate(DataSource ds , String projectDir, String packageName, final String includeName, Consumer<List<TableMetaExtend>> consumer) {
        // =====================================================================================================================
        // 定义输出目录位置
        // =====================================================================================================================
        String basePackageName = packageName;
        String srcJavaPath = "/src/main/java/";
        String baseOutputDir = projectDir + srcJavaPath + basePackageName.replace('.', '/');
        IOUtil.createDirPath(baseOutputDir);
        System.out.println("Generate path in " + projectDir );
        // =====================================================================================================================

        // =====================================================================================================================
        // 包名，模板定义
        // =====================================================================================================================
        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = basePackageName + ".domain";
        // model 文件保存路径
        String modelOutputDir = projectDir + srcJavaPath + modelPackageName.replace('.', '/');
        // model 模板
        String modelTemplateClassPath = "/generatorCodeTest/model_template.jf";
        // model 生成器
        BaseModelGenerator modelGenerator = new BaseModelGenerator(modelPackageName, modelOutputDir).setTemplate(modelTemplateClassPath);

        // model doc 所使用的包名 (MappingKit 默认使用的包名)
        String modelDocPackageName = basePackageName + ".doc";
        // model doc 文件保存路径
        String modelDocOutputDir = projectDir + srcJavaPath + modelPackageName.replace('.', '/');
        // model doc 模板
        String modelDocTemplateClassPath = "/generatorCodeTest/model_doc_template_test.jf";
        ModelDocGenerator modelDocGenerator = new ModelDocGenerator(modelDocPackageName, modelDocOutputDir).setTemplate(modelDocTemplateClassPath);
        // =====================================================================================================================

        // =====================================================================================================================
        com.jfinal.plugin.activerecord.generator.MetaBuilder metaBuilder = new MetaBuilder(ds).skip(
                tableName -> {
                    return !tableName.startsWith(includeName);
                }
        );
        metaBuilder.setDialect(new MysqlDialect());
        // 配置是否生成备注
        metaBuilder.setGenerateRemarks(true);
        // =====================================================================================================================


        // =====================================================================================================================
        long start = System.currentTimeMillis();
        List<TableMeta> tableMetas = metaBuilder.build();
        if (tableMetas.size() == 0) {
            System.out.println("TableMeta 数量为 0，不生成任何文件");
            return ;
        }

        List<TableMetaExtend> tableMetasExtendExtend = new ArrayList<>(tableMetas.size());

        for (int i = 0; i < tableMetas.size(); i++) {
            TableMeta old           = tableMetas.get(i);
            TableMetaExtend  newObj = new TableMetaExtend();
            newObj.name             = old.name       ; // 表名
            newObj.remarks          = old.remarks    ; // 表备注
            newObj.primaryKey       = old.primaryKey ; // 主键，复合主键以逗号分隔
            newObj.columnMetas      = old.columnMetas; // 字段 meta

            // ---------
            newObj.basePackageName  = basePackageName;
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

            tableMetasExtendExtend.add(newObj);
        }

        if(null != consumer) {
            consumer.accept(tableMetasExtendExtend);
        } else {
            modelGenerator.generate(tableMetasExtendExtend);
            modelDocGenerator.generate(tableMetasExtendExtend);
        }

        long usedTime = (System.currentTimeMillis() - start) / 1000;
        System.out.println("Generate complete in " + usedTime + " seconds.");
        // =====================================================================================================================
    }

    @Test
    public void testGen() {
//        DataSource ds = getDataSource();
//        String projectDir = System.getProperty("user.dir") + "/target/generated-domain/msf_member/";
//        generate(ds, projectDir, "member", "t_park");
//        generate(ds, projectDir, "member", "t_car_feesroleinfo");
//        generate(ds, projectDir, "member", "t_car_memberamountinfo_cloud");

//        DataSource ds2 = getDataSource2();
//        String projectDir2 = System.getProperty("user.dir") + "/target/generated-domain/msf_check_bill/";
//        generate(ds2, projectDir2, "checkBill", "t_check");
//        generate(ds2, projectDir2, "checkBill", "t_clear");


//        DataSource ds3 = getDataSource3();
//        String projectDir3 = System.getProperty("user.dir") + "/target/generated-domain/msf_business_center/";
//        generate(ds3, projectDir3, "msfBusinessCenter", "t_park_escape");



        DataSource ds4 = getDataSource4();
        String projectDir4 = System.getProperty("user.dir") + "/src/generated-domain/msf_invoice/";
        generate(ds4, projectDir4, "invoice", "t_invoice" , null);

    }

    @Test
    public void testGenDbDoc() {

    }
}
