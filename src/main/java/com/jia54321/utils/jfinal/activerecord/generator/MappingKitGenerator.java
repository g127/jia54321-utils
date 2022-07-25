package com.jia54321.utils.jfinal.activerecord.generator;

import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class MappingKitGenerator {

    protected Engine engine;
    protected String template = "/com/jfinal/plugin/activerecord/generator/mapping_kit_template.jf";

    protected String mappingKitPackageName;
    protected String mappingKitOutputDir;
    protected String mappingKitClassName = "_MappingKit";

    public MappingKitGenerator(String mappingKitPackageName, String mappingKitOutputDir) {
        this.mappingKitPackageName = mappingKitPackageName;
        this.mappingKitOutputDir = mappingKitOutputDir;

        initEngine();
    }

    protected void initEngine() {
        engine = new Engine();
        engine.setToClassPathSourceFactory();
        engine.addSharedMethod(new StrKit());
    }

    /**
     * 使用自定义模板生成 MappingKit
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    public void setMappingKitOutputDir(String mappingKitOutputDir) {
        if (StrKit.notBlank(mappingKitOutputDir)) {
            this.mappingKitOutputDir = mappingKitOutputDir;
        }
    }

    public String getMappingKitOutputDir() {
        return mappingKitOutputDir;
    }

    public void setMappingKitPackageName(String mappingKitPackageName) {
        if (StrKit.notBlank(mappingKitPackageName)) {
            this.mappingKitPackageName = mappingKitPackageName;
        }
    }

    public String getMappingKitPackageName() {
        return mappingKitPackageName;
    }

    public void setMappingKitClassName(String mappingKitClassName) {
        if (StrKit.notBlank(mappingKitClassName)) {
            this.mappingKitClassName = StrKit.firstCharToUpperCase(mappingKitClassName);
        }
    }

    public String getMappingKitClassName() {
        return mappingKitClassName;
    }

    public void generate(List<TableMetaExtend> tableMetaExtend) {
        System.out.println("Generate MappingKit file ...");
        System.out.println("MappingKit Output Dir: " + mappingKitOutputDir);

        Kv data = Kv.by("mappingKitPackageName", mappingKitPackageName);
        data.set("mappingKitClassName", mappingKitClassName);
        data.set("tableMetas", tableMetaExtend);
        data.set("tableMetaExtends", tableMetaExtend);
        if(tableMetaExtend.size() > 0){
            data.set("basePackageName", tableMetaExtend.get(0).basePackageName);
        }

        String ret = engine.getTemplate(template).renderToString(data);
        writeToFile(ret);
    }

    /**
     * _MappingKit.java 覆盖写入
     */
    protected void writeToFile(String ret) {
        File dir = new File(mappingKitOutputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String             target = mappingKitOutputDir + File.separator + mappingKitClassName + ".java";
        OutputStreamWriter osw    = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(target), "UTF-8");
            osw.write(ret);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (osw != null) {
                try {osw.close();} catch (IOException e) {
                    LogKit.error(e.getMessage(), e);}
            }
        }
    }
}
