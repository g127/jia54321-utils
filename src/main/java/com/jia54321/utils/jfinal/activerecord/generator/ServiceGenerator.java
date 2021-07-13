package com.jia54321.utils.jfinal.activerecord.generator;


import com.jfinal.kit.JavaKeyword;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceGenerator {

    protected Engine engine;
    protected String template = "/com/jia54321/utils/jfinal/activerecord/generator/service_template.jf";

    protected String modelPackageName;
    protected String servicePackageName;
    protected String serviceOutputDir;

    protected JavaKeyword javaKeyword = JavaKeyword.me;

    /**
     * 针对 Model 中七种可以自动转换类型的 getter 方法，调用其具有确定类型返回值的 getter 方法
     * 享用自动类型转换的便利性，例如 getInt(String)、getStr(String)
     * 其它方法使用泛型返回值方法： get(String)
     * 注意：jfinal 3.2 及以上版本 Model 中的六种 getter 方法才具有类型转换功能
     */
    @SuppressWarnings("serial")
    protected Map<String, String> getterTypeMap = new HashMap<String, String>() {{
        put("java.lang.String", "getStr");
        put("java.lang.Integer", "getInt");
        put("java.lang.Long", "getLong");
        put("java.lang.Double", "getDouble");
        put("java.lang.Float", "getFloat");
        put("java.lang.Short", "getShort");
        put("java.lang.Byte", "getByte");

        // 新增两种可自动转换类型的 getter 方法
        put("java.util.Date", "getDate");
        put("java.time.LocalDateTime", "getLocalDateTime");
    }};

    public ServiceGenerator(String modelPackageName, String servicePackageName, String serviceOutputDir) {
        if (StrKit.isBlank(servicePackageName)) {
            throw new IllegalArgumentException("servicePackageName can not be blank.");
        }
        if (servicePackageName.contains("/") || servicePackageName.contains("\\")) {
            throw new IllegalArgumentException("servicePackageName error : " + servicePackageName);
        }
        if (StrKit.isBlank(serviceOutputDir)) {
            throw new IllegalArgumentException("serviceOutputDir can not be blank.");
        }

        this.modelPackageName = modelPackageName;
        this.servicePackageName = servicePackageName;
        this.serviceOutputDir = serviceOutputDir;

        initEngine();
    }

    protected void initEngine() {
        engine = new Engine();
        engine.setToClassPathSourceFactory();	// 从 class path 内读模板文件
        engine.addSharedMethod(new StrKit());
        engine.addSharedObject("getterTypeMap", getterTypeMap);
        engine.addSharedObject("javaKeyword", javaKeyword);
    }

    /**
     * 使用自定义模板生成 Service Impl
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    public void generate(List<TableMetaExtend> tableMetaExtends) {
        System.out.println("Generate service model ...");
        System.out.println("Service Output Dir: " + serviceOutputDir);

        for (TableMetaExtend tableMetaExtend : tableMetaExtends) {
            genServiceImplContent(tableMetaExtend);
        }
        writeToFile(tableMetaExtends);
    }

    protected void genServiceImplContent(TableMetaExtend tableMetaExtend) {
        Kv data = Kv.by("servicePackageName", servicePackageName);
        data.set("modelPackageName", modelPackageName);
        data.set("tableMeta", tableMetaExtend);
        data.set("tableMetaExtend", tableMetaExtend);
        tableMetaExtend.serviceContent = engine.getTemplate(template).renderToString(data);
    }

    protected void writeToFile(List<TableMetaExtend> tableMetaExtends) {
        try {
            for (TableMetaExtend tableMetaExtend : tableMetaExtends) {
                writeToFile(tableMetaExtend);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * base model 覆盖写入
     */
    protected void writeToFile(TableMetaExtend tableMetaExtend) throws IOException {
        File dir = new File(serviceOutputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String target = serviceOutputDir + File.separator + tableMetaExtend.serviceName + ".java";
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(target), "UTF-8");
            osw.write(tableMetaExtend.serviceContent);
        }
        finally {
            if (osw != null) {
                osw.close();
            }
        }
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public String getServiceOutputDir() {
        return serviceOutputDir;
    }
}
