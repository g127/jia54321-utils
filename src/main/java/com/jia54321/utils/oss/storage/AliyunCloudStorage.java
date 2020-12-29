package com.jia54321.utils.oss.storage;

import com.jia54321.utils.oss.Oss;
import com.jia54321.utils.oss.OssConfig;
import com.aliyun.oss.OSSClient;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 阿里云存储
 */
public class AliyunCloudStorage extends Oss {
    private OSSClient client;

    public AliyunCloudStorage(OssConfig config){
        this.config = config;

        //初始化
        init();
    }

    private void init(){
        client = new OSSClient(config.getAliyunEndPoint(), config.getAliyunAccessKeyId(),
                config.getAliyunAccessKeySecret());
    }

    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            client.putObject(config.getAliyunBucketName(), path, inputStream);
        } catch (Exception e){
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        }

        return config.getAliyunDomain() + "/" + path;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getAliyunPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getAliyunPrefix(), suffix));
    }
}
