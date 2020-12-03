package com.jia54321.utils;

import com.alibaba.fastjson.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Yaml文件处理工具类
 */
@SuppressWarnings("unchecked")
public class YamlHelper {
    /**
     * Yaml字符串转成json字符串
     * @param yamlStr
     * @return
     */
    public static String yamlToJsonString(String yamlStr){
        if(JsonHelper.isEmpty(yamlStr)){
            return null;
        }

        Yaml yaml = new Yaml();
        return yaml.dump(yaml.load(yamlStr));
    }

    /**
     * yaml文件转换成Json字符串
     * @param dumpfile
     * @return
     */
    public static String yamlToJsonString(File dumpfile){
        Yaml yaml = new Yaml();
        Object result = null;
        InputStream in = null;
        try {
            in = new FileInputStream(dumpfile);
            result = yaml.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> resmap = (Map<String, Object>) result;
        return JSONObject.toJSONString(resmap);
    }

    /**
     * yaml文件流转换成Json字符串
     * @param in
     * @return
     */
    public static String yamlToJsonString(InputStream in){
        Yaml yaml = new Yaml();
        Object result = null;
        try {
            result = yaml.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> resmap = (Map<String, Object>) result;
        return JSONObject.toJSONString(resmap);
    }

    /**
     * Yaml字符串转json对象
     * @param yamlString
     * @return
     */

    @SuppressWarnings("unchecked")
    public static JSONObject convertToJson(String yamlString) {
        if(JsonHelper.isEmpty(yamlString)){
            return null;
        }
        Yaml yaml= new Yaml();
        Map<String, Object> map = (Map<String, Object>) yaml.load(yamlString);
        JSONObject jsonObject=new JSONObject(map);
        return jsonObject;
    }

    /**
     * yaml文件转换成Json对象
     * @param dumpfile
     * @return
     */
    public static JSONObject convertToJson(File dumpfile){
        Yaml yaml = new Yaml();
        Object result = null;
        InputStream in = null;
        try {
            in = new FileInputStream(dumpfile);
            result = yaml.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> resmap = (Map<String, Object>) result;
        JSONObject jsonObject=new JSONObject(resmap);
        return jsonObject;
    }

    /**
     * yaml文件流转换成Json对象
     * @param in
     * @return
     */
    public static JSONObject convertToJson(InputStream in){
        Yaml yaml = new Yaml();
        Object result = null;
        try {
            result = yaml.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> resmap = (Map<String, Object>) result;
        JSONObject jsonObject=new JSONObject(resmap);
        return jsonObject;
    }



    /**
     * Yaml字符串转Map
     * @param yamlString
     * @return
     */

    @SuppressWarnings("unchecked")
    public static Map<String,Object> convertToMap(String yamlString) {
        if(JsonHelper.isEmpty(yamlString)){
            return null;
        }
        if(yamlString.startsWith("---\n")){
            yamlString = yamlString.replaceAll("---\n","");
        }if(yamlString.contains("!ruby/hash")){
            yamlString = yamlString.replaceAll("(?i)!ruby/.*\n","\n");
        }
        Yaml yaml= new Yaml();
        Map<String,Object> map= (Map<String, Object>) yaml.load(yamlString);
        return map;
    }

    /**
     * Yaml文件转Map
     * @param dumpfile
     * @return
     */
    public static Map<String,Object> convertToMap(File dumpfile){
        Yaml yaml = new Yaml();
        Object result = null;
        InputStream in = null;
        try {
            in = new FileInputStream(dumpfile);
            result = yaml.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> resmap = (Map<String, Object>) result;
        return resmap;
    }

    /**
     * Yaml文件流转Map
     * @param in
     * @return
     */
    public static Map<String,Object> convertToMap(InputStream in){
        Yaml yaml = new Yaml();
        Object result = null;
        try {
            result = yaml.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> resmap = (Map<String, Object>) result;
        return resmap;
    }

    /**
     * Yaml字符串转Object
     * @param yamlString
     * @return
     */
    public static Object convertToObject(String yamlString) {
        if(JsonHelper.isEmpty(yamlString)){
            return null;
        }
        Yaml yaml= new Yaml();
        Object obj = yaml.load(yamlString);
        return obj;
    }

    /**
     * Yaml文件转Object
     * @param dumpfile
     * @return
     */
    public static Object convertToObject(File dumpfile){
        Yaml yaml = new Yaml();
        Object result = null;
        InputStream in = null;
        try {
            in = new FileInputStream(dumpfile);
            result = yaml.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Yaml文件流转Object
     * @param in
     * @return
     */
    public static Object convertToObject(InputStream in){
        Yaml yaml = new Yaml();
        Object result = null;
        try {
            result = yaml.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * Yaml转List
     * @param yamlString
     * @return
     */

    @SuppressWarnings("unchecked")
    public static List<String> toList(String yamlString) {
        if(JsonHelper.isEmpty(yamlString)){
            return null;
        }
        Yaml yaml= new Yaml();
        List<String> list= (List<String>) yaml.load(yamlString);
        return list;
    }

//    public static void main(String[] args) {
//        File ymalFile = new File("C:\\Users\\luohui\\Desktop\\jira.yaml");
//        String jsonString = YamlHelper.yamlToJsonString(ymalFile);
//        System.out.println(jsonString);
//
//        Map<String, Object> map = YamlHelper.convertToMap(ymalFile);
//        System.out.println(map);
//    }
}

