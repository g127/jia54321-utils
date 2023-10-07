package com.jia54321.utils.doc;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.process.ProcessConfig;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.hikaricp.HikariCpPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

public class DBDocExportTest {

    /**
     * 会员 定义数据源
     * @return
     */
    public static DataSource getDataSource() {
        DruidPlugin druidPlugin = new DruidPlugin(
                "jdbc:mysql://106.14.150.97:31529/flypark?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8",
                "park",
                "g&!BKilp$Mrl4k#h");
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    public static DataSource getDataSource2() {
        // 数据源
        HikariCpPlugin hikariCpPlugin = new HikariCpPlugin(
                "jdbc:mysql://106.14.150.97:31529/flypark?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8",
                "park",
                "g&!BKilp$Mrl4k#h");
        hikariCpPlugin.start();
        return hikariCpPlugin.getDataSource();
    }

    public static DataSource getDataSource4() {
        // 数据源
        HikariCpPlugin hikariCpPlugin = new HikariCpPlugin(
                "jdbc:mysql://127.0.0.1:3306/finance?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8",
                "park",
                "park123");
        hikariCpPlugin.start();
        return hikariCpPlugin.getDataSource();
    }

    public static void main(String[] args) {

        DataSource dataSource = getDataSource4();
        // 1、生成文件配置
        EngineConfig engineConfig = EngineConfig.builder()
                // 生成文件路径
                .fileOutputDir("D:/1111111111")
                // 打开目录
                .openOutputDir(false)
                // 文件类型
                .fileType(EngineFileType.WORD)
                // 生成模板实现
                .produceType(EngineTemplateType.freemarker).build();

        // 忽略表名
        List<String> ignoreTableName = Arrays.asList("test");
        // 忽略表前缀
        List<String> ignorePrefix    = Arrays.asList("test_", "test");
        // 忽略表后缀
        List<String> ignoreSuffix = Arrays.asList("_test", "test");

        // 2、配置想要忽略的表
        ProcessConfig processConfig = ProcessConfig.builder().ignoreTableName(ignoreTableName)
                .ignoreTablePrefix(ignorePrefix).ignoreTableSuffix(ignoreSuffix).build();

        // 3、生成文档配置（包含以下自定义版本号、描述等配置连接）
        Configuration config = Configuration.builder().version("1.0.0").description("数据库文档").dataSource(dataSource)
                .engineConfig(engineConfig).produceConfig(processConfig).build();

        // 4、执行生成
        new DocumentationExecute(config).execute();
    }


}
