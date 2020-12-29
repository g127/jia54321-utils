package com.jia54321.utils.oss.storage;

import com.jia54321.utils.oss.Oss;

import java.io.InputStream;

public class LocalStorage extends Oss {

    @Override
    public String upload(byte[] data, String path) {
        return null;
    }

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
