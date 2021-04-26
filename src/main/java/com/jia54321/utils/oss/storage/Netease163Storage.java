package com.jia54321.utils.oss.storage;

import com.jia54321.utils.oss.Oss;

import java.io.InputStream;

/**
 * 网易云存储
 */
public class Netease163Storage  extends Oss {

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return null;
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        return null;
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return null;
    }
}
