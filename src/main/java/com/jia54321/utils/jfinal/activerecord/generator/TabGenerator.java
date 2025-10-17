package com.jia54321.utils.jfinal.activerecord.generator;

import com.jfinal.kit.Kv;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabGenerator extends AbstractTemplateGenerator<TableMetaExtend>  {

    /**
     * 切分，返回实体名称
     *  eg: t_user_detail ==> UserDetail
     * @param tabName 表名
     * @param skipUnderscoreNum 跳过的下划线数量
     * @return
     */
    public String getEntityName(String tabName, int skipUnderscoreNum) {
        if(tabName.matches("[a-zA-Z]+[_][a-zA-Z0-9_]+]")) {
            throw new RuntimeException("表名不符合规范 <==" + tabName);
        }

        String skipPrefixTabName = tabName;
        for (String skipPrefix : this.getUselessTablePrefixList()) {
            if (Objects.nonNull(skipPrefix) && tabName.startsWith(skipPrefix)) {
                skipPrefixTabName = tabName.substring(skipPrefix.length());
                // 满足一种即可退出
                break;
            }
        }


        // t_user_detail ==> stream:  t, user, detail
        return Stream.of(skipPrefixTabName.split("_")).skip(skipUnderscoreNum)
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining());
    }

    /**
     * 切分，并返回名称
     * name$ext_template.jf
     *  eg: entity$java_template.jf ==> Entity.java
     *  eg: service$java_template.jf ==> Service.java
     *  eg: service_impl$java_template.jf ==> ServiceImpl.java
     *  eg: mapper$xml_template.jf ==> Mapper.xml
     * @param templateFileName 返回相对路径 relativize
     * @return stream
     */
    public String getSuffixName(String templateFileName) {
        if(templateFileName.indexOf("_template.jf") <= 0) {
            throw new RuntimeException("模版名不符合规范 <==" + templateFileName);
        }
        // String templateFileName = templatePath.toFile().getName();
        String[] unitNames = templateFileName.split("_");
        // stream 去掉最后一条
        // service_impl$java_template.jf ==> service_impl$java  ==> ServiceImpl.java
        return Stream.of(unitNames).limit(unitNames.length -1)
                // 首字母大写
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining()).replace('$', '.');
    }

    /**
     * 按照 模版路径 => 返回java文件的包路径
     *  service/impl/service_impl$java_template.jf
     *  ==> .service.impl
     * @param templatePath 模版路径
     * @return java文件的包路径
     */
    public String getClassPackageName(Path templatePath) {
        // service/impl/service_impl$java_template.jf
        // ==> .service.impl
        String subPkgName = "." + templatePath.getParent().toString().replaceAll("[\\\\/]", ".");

        return getPackageName() + ( subPkgName.length() > 1 ? subPkgName : "");
    }

    @Override
    public List<String[]> genMultiFileMap(TableMetaExtend tableMeta) {
        List<String[]> multiFiles = new ArrayList<>();

        Kv data = Kv.by("basePackageName", getPackageName());
        data.set("generateChainSetter", false);
        data.set("tableMeta", tableMeta);
        data.set("tableMetaExtend", tableMeta);

        // 返回相对路径 relativize
        List<Path> templatePaths = findClasspathFiles(getTemplateDir(), p -> p.toString().endsWith(".jf"));

        Path multiFileDir = Paths.get(getOutputDir(), getPackageName().replaceAll("\\.","/"));

        for (Path templatePath : templatePaths) {
            // t_user_detail
            String tabName = tableMeta.name;
            // eg: t_user_detail ==> UserDetail
            String entityName = getEntityName(tabName, 0);
            String entityObjectName =  Character.toLowerCase(entityName.charAt(0)) + entityName.substring(1);
            // eg: service_impl$java_template.jf ==> ServiceImpl.java
            String suffixName = getSuffixName(templatePath.toFile().getName());
            // eg: UserDetail + ServiceImpl.java ==> UserDetailServiceImpl.java
            String fileNameWithExt = entityName + suffixName;
            // UserDetail + ServiceImpl.java ==> UserDetailServiceImpl
            String className = entityName + suffixName.split("\\.")[0];
            // service/impl/service_impl$java_template.jf ==> .service.impl
            String classPackageName = getClassPackageName(templatePath);

            // 设置到本次的模版变量中
            data.set("packageName", classPackageName );
            data.set("entityName", entityName );
            data.set("entityObjectName", entityObjectName);
            data.set("className", className );
            // 文件默认注释
            data.set("tableName", tableMeta.name );
            // 文件默认注释
            data.set("comment", tableMeta.remarks );

            String file = multiFileDir.resolve(templatePath.getParent()).resolve(fileNameWithExt).toString();
            String content = engine.getTemplate(getTemplateDir() + "/" + templatePath).renderToString(data);
            multiFiles.add(new String[]{file, content});
        }

        return multiFiles;
    }
}
