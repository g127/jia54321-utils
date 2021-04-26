package com.jia54321.utils.oss.storage;

import com.jia54321.utils.IOUtil;
import com.jia54321.utils.oss.Oss;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 本地存储
 */
public class LocalStorage extends Oss {

    private String prefix = "";

    private String httpPath = "";
    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(prefix, suffix));
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        IOUtil.copyByPath(inputStream, path);
        return httpPath + path;
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(prefix, suffix));
    }

    @Override
    public String getPath(String prefix, String suffix) {
        return super.getPath(prefix, suffix);
    }
}
