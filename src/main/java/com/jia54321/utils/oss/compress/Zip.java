package com.jia54321.utils.oss.compress;

import com.jia54321.utils.IOUtil;
import com.jia54321.utils.JsonHelper;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * zip压缩类
 */
public class Zip {

    /**
     * zip压缩
     *
     * @param baseName 基础路径，可为空
     * @param subs     文件列表
     * @param newNames 文件名列表
     * @param tempFile 输出文件
     * @return 输出文件
     */
    public File zipFile(String baseName, File[] subs, String[] newNames, File tempFile) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
            zipFile(baseName, subs, newNames, zos);
        } catch (Exception e) {
        } finally {
            IOUtil.closeQuietly(zos);
        }
        return tempFile;
    }

    /**
     * zip压缩
     *
     * @param baseName 基础路径，可为空
     * @param subs     文件列表
     * @param newNames 文件名列表
     * @param zos      ZipOutputStream
     * @throws IOException 异常
     */
    private void zipFile(String baseName, File[] subs, String[] newNames, ZipOutputStream zos) throws IOException {
        // 基础路径，可为空
        if (null == baseName) {
            baseName = "";
        }
        // 循环每个文件
        for (int i = 0; i < subs.length; i++) {
            File f = subs[i];
            String name = f.getName();
            if (null != newNames && i < newNames.length && !JsonHelper.isEmpty(newNames[i])) {
                name = newNames[i];
            }
            if (f.exists()) {
                String n = String.format("%s/%s", baseName, name).replaceAll("//", "/");
                while (n.startsWith("/") && n.length() > 0) {
                    n = n.substring(1);
                }
                zos.putNextEntry(new ZipEntry(n));
                FileInputStream fis = new FileInputStream(f);
                byte[] buffer = new byte[1024];
                int r = 0;
                while ((r = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, r);
                }
                fis.close();
            }
        }
    }
}
