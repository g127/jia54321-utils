package com.jia54321.utils.jfinal.activerecord.generator;

import com.jfinal.plugin.activerecord.generator.*;
import com.jia54321.utils.CamelNameUtil;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class Generator extends com.jfinal.plugin.activerecord.generator.Generator {

    protected BaseModelGenerator baseModelGeneratorExt;
    protected ServiceGenerator serviceGenerator;
    protected ServiceImplGenerator serviceImplGenerator;

    protected boolean generateServiceDictionary = false;
    protected boolean generateServiceImplDictionary = false;

    public Generator(DataSource dataSource,
                     String baseModelPackageName, String baseModelOutputDir, String modelPackageName, String modelOutputDir,
                     String servicePackageName, String serviceOutputDir, String serviceImplPackageName, String serviceImplOutputDir) {
        super(dataSource, baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);

        this.metaBuilder = new MetaBuilder(dataSource);
        this.baseModelGeneratorExt = new BaseModelGenerator(baseModelPackageName, baseModelOutputDir);
        this.baseModelGeneratorExt.setTemplate("/com/jia54321/utils/jfinal/activerecord/generator/base_model_template.jf");
        this.modelGenerator = modelGenerator;
        this.modelGenerator.setTemplate("/com/jia54321/utils/jfinal/activerecord/generator/model_template.jf");
        this.mappingKitGenerator = new MappingKitGenerator(modelPackageName, modelOutputDir);
        this.mappingKitGenerator.setTemplate("/com/jia54321/utils/jfinal/activerecord/generator/mapping_kit_template.jf");
        this.dataDictionaryGenerator = new DataDictionaryGenerator(dataSource, modelOutputDir);

        this.serviceGenerator = new ServiceGenerator(modelPackageName, servicePackageName, serviceOutputDir);
        this.serviceGenerator.setTemplate("/com/jia54321/utils/jfinal/activerecord/generator/service_template.jf");
        this.serviceImplGenerator = new ServiceImplGenerator(modelPackageName, servicePackageName, serviceImplPackageName, serviceImplOutputDir);
        this.serviceImplGenerator.setTemplate("/com/jia54321/utils/jfinal/activerecord/generator/service_impl_template.jf");
    }

    @Override
    public void generate() {
        if (dialect != null) {
            metaBuilder.setDialect(dialect);
        }

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

            newObj.baseModelName    = old.baseModelName;		// 生成的 base model 名
            newObj.baseModelContent = old.baseModelContent;	// 生成的 base model 内容

            newObj.modelName        = old.modelName;			// 生成的 model 名
            newObj.modelContent     = old.modelContent;		// 生成的 model 内容

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

        baseModelGeneratorExt.generate(tableMetasExtendExtend);

        if (modelGenerator != null) {
            modelGenerator.generate(tableMetas);
        }

        if (mappingKitGenerator != null) {
            mappingKitGenerator.generate(tableMetas);
        }

        if (dataDictionaryGenerator != null && generateDataDictionary) {
            dataDictionaryGenerator.generate(tableMetas);
        }

        if (serviceGenerator != null) {
            serviceGenerator.generate(tableMetasExtendExtend);
        }

        if (serviceImplGenerator != null) {
            serviceImplGenerator.generate(tableMetasExtendExtend);
        }

        long usedTime = (System.currentTimeMillis() - start) / 1000;
        System.out.println("Generate complete in " + usedTime + " seconds.");
    }

    public void setGenerateServiceDictionary(boolean generateServiceDictionary) {
        this.generateServiceDictionary = generateServiceDictionary;
    }

    public void setGenerateServiceImplDictionary(boolean generateServiceImplDictionary) {
        this.generateServiceImplDictionary = generateServiceImplDictionary;
    }
}
