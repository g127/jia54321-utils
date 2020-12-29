package com.jia54321.utils.oss.image;

import com.jia54321.utils.EnvHelper;
import com.jia54321.utils.IOUtil;
import com.jia54321.utils.JsonHelper;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * 图片测试类
 */
public class ImageTest {
    /** 日志 */
    static final Logger log = LoggerFactory.getLogger(ImageTest.class);
    /** java临时目录 */
    private final String tmpdir = System.getProperty("java.io.tmpdir");
    /** 测试图片resize的文件 */
    private final String imageTestResize ="image-test-resize.jpg";
    /** 测试图片info的文件 */
    private final String imageTestInfo ="image-test-info.jpg";

    /**
     * 前置
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // 测试图片的文件名称
        String[] tmpFiles = new String[]{ imageTestResize, imageTestInfo};
        // 循环
        for (String temp : tmpFiles) {
            // ==========================================
            // 图片路径，默认在classpath中，需要拷贝到临时目录中处理
            // ==========================================
            // 输入
            String classPathImage = '/' + getClass().getPackage().getName() + '/' + temp;
            // 输出路径
            String outPath = tmpdir + '/' + temp;
            // 不存在就拷贝
            if(!IOUtil.contentIsAvailable(outPath)) {
                InputStream clsInputStream = null;
                try {
                    clsInputStream = EnvHelper.getInputStream('/' + getClass().getPackage().getName() + '/' + temp);
                    IOUtil.copyByPath(clsInputStream, outPath);
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    IOUtil.closeQuietly(clsInputStream);
                }
            }
        }
    }

    @Test
    public void resizeV1() {
        // 测试样例
        String[] resizeV1s = new String[]{ "100x100", "100x100_fitCenter", "100x100_fitXY", "100x100_centerCrop"};
        // 循环执行4种样例
        for (String resizeV1: resizeV1s ) {
            File in = new File(tmpdir + '/' + imageTestResize);
            String outPath = tmpdir + '/' + resizeV1 + '-' + imageTestResize;
            File out =  new File(outPath);
            Image.resizeV1(resizeV1, in, out);
            Assert.assertTrue("true", IOUtil.contentIsAvailable(outPath));
        }
    }

    @Test
    public void info() {
        File in = new File(tmpdir + '/' + imageTestInfo);
        Map<String, Object> info = Image.info(in);
        Assert.assertTrue("true", info.size() > 0);
        System.out.println(JsonHelper.toJSONString(info, true));
    }
}