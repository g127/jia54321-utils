package com.jia54321.utils.jfinal.activerecord.generator;

import com.jfinal.kit.JavaKeyword;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jia54321.utils.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractTemplateGenerator<S extends TableMeta> implements ITemplateGenerator<S> {

    static final Logger log = LoggerFactory.getLogger(AbstractTemplateGenerator.class);

    protected Engine engine;

    protected String templateDir;
    protected String packageName;
    protected String outputDir;

    public String getTemplateDir() {
        return templateDir;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void init(String templateDir, String packageName, String outputDir) {
        if (StrKit.isBlank(templateDir)) {
            throw new IllegalArgumentException("templateDir can not be blank.");
        }
        if (StrKit.isBlank(packageName)) {
            throw new IllegalArgumentException("packageName can not be blank.");
        }
        if (packageName.contains("/") || packageName.contains("\\")) {
            throw new IllegalArgumentException("packageName error : " + packageName);
        }
        if (StrKit.isBlank(outputDir)) {
            throw new IllegalArgumentException("outputDir can not be blank.");
        }

        this.templateDir = templateDir;
        this.packageName = packageName;
        this.outputDir = outputDir;

        initEngine();
    }

    protected void initEngine() {
        /**
         * 针对 Model 中七种可以自动转换类型的 getter 方法，调用其具有确定类型返回值的 getter 方法
         * 享用自动类型转换的便利性，例如 getInt(String)、getStr(String)
         * 其它方法使用泛型返回值方法： get(String)
         * 注意：jfinal 3.2 及以上版本 Model 中的六种 getter 方法才具有类型转换功能
         */
        Map<String, String> getterTypeMap = new HashMap<>() ;
        getterTypeMap.put("java.lang.String", "getStr");
        getterTypeMap.put("java.lang.Integer", "getInt");
        getterTypeMap.put("java.lang.Long", "getLong");
        getterTypeMap.put("java.lang.Double", "getDouble");
        getterTypeMap.put("java.lang.Float", "getFloat");
        getterTypeMap.put("java.lang.Short", "getShort");
        getterTypeMap.put("java.lang.Byte", "getByte");
        // 新增两种可自动转换类型的 getter 方法
        getterTypeMap.put("java.util.Date", "getDate");
        getterTypeMap.put("java.time.LocalDateTime", "getLocalDateTime");

        engine = new Engine();
        engine.setToClassPathSourceFactory();	// 从 class path 内读模板文件
        engine.addSharedMethod(new StrKit());
        engine.addSharedObject("getterTypeMap", getterTypeMap);
        engine.addSharedObject("javaKeyword", JavaKeyword.me);
    }

    @Override
    public void generate(List<S> tableMetas) {
        log.info("Generate java file ..." + getPackageName());
        log.info("Output Dir: " + getOutputDir());

        List<String[]> outputList = new ArrayList<>(tableMetas.size());
        for (S tableMeta : tableMetas) {
            // String file = Paths.get(getOutputDir() + File.separator + tableMeta.baseModelName + ".java").toString();
            outputList.addAll(genMultiFileMap(tableMeta));
        }

        for (String[] e : outputList) {
            writeToFile( Paths.get(e[0]), e[1]);
        }
    }

    abstract public List<String[]> genMultiFileMap(S tableMeta);

    /**
     * base model 覆盖写入
     */
    protected void writeToFile(Path fullFileName, String content) {
        File dir = fullFileName.getParent().toFile();

        OutputStreamWriter osw = null;
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }//
            osw = new OutputStreamWriter(new FileOutputStream(fullFileName.toString()), "UTF-8");
            osw.write(content);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
        finally {
           IOUtil.closeQuietly(osw);
        }
    }

    /**
     * 返回相对路径 relativize
     * @param directory
     * @param predicate
     * @return
     */
    public List<Path> findClasspathFiles(String directory,  Predicate<Path> predicate) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(directory);

        if (resource == null) {
            throw new RuntimeException("目录不存在: " + directory);
        }

        try{
            Path path = Paths.get(resource.toURI());

            Stream<Path> walk = Files.walk(path);

            return walk.filter(Files::isRegularFile)
                    .filter(predicate)
                    .map(path::relativize)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
