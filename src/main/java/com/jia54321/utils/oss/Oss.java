package com.jia54321.utils.oss;

import com.jia54321.utils.DateUtil;
import com.jia54321.utils.IdGeneration;
import com.jia54321.utils.JsonHelper;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * 对象存储
 */
public abstract class Oss {
    /** oss存储配置信息 */
    protected OssConfig config;

    /**
     * 文件路径
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public String getPath(String prefix, String suffix) {
        //生成唯一键
        String uuid = IdGeneration.getOID().replaceAll("-", "");
        //文件路径
        String path = DateUtil.toTimeString(new Date(),"yyyyMMdd") + "/" + uuid;

        if(!JsonHelper.isEmpty(prefix)){
            path = prefix + "/" + path;
        }

        return path + suffix;
    }

    /**
     * 文件上传
     * @param data    文件字节数组
     * @param path    文件路径，包含文件名
     * @return        返回http地址
     */
    public abstract String upload(byte[] data, String path);

    /**
     * 文件上传
     * @param data     文件字节数组
     * @param suffix   后缀
     * @return         返回http地址
     */
    public abstract String uploadSuffix(byte[] data, String suffix);

    /**
     * 文件上传
     * @param inputStream   字节流
     * @param path          文件路径，包含文件名
     * @return              返回http地址
     */
    public abstract String upload(InputStream inputStream, String path);

    /**
     * 文件上传
     * @param inputStream  字节流
     * @param suffix       后缀
     * @return             返回http地址
     */
    public abstract String uploadSuffix(InputStream inputStream, String suffix);

}
