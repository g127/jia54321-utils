/**
 * MIT License
 * <p>
 * Copyright (c) 2009-present GuoGang and other contributors
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.jia54321.utils.oss.image;


import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.jia54321.utils.Kv;
import com.jia54321.utils.NumberUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.awt.image.BufferedImageGraphicsConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 图片处理
 * 仅支持缩放,INFO ，后续考虑水印
 * @author 郭罡
 */
public class Image {
    static final Logger log = LoggerFactory.getLogger(Image.class);

    /**
     * resizeV1 解析器
     * @param text 字符串 100x100  100x100_fitCenter 100x100_fitXY 100x100_centerCrop
     * @param input 输入文件
     * @param out 输出文件
     */
    public static void resizeV1(String text, File input, File out) {
        String resizeImgParam1Size = text;
        Image.FillType resizeImgParam2FillType = Image.FillType.FIT_XY;
        // 100x100_fitCenter
        if (text.indexOf('_') > 0) {
            String[] t = text.split("_");
            resizeImgParam1Size = t[0];
            if("fitCenter".equalsIgnoreCase(t[1])) {
                resizeImgParam2FillType = Image.FillType.FIT_CENTER;
            }
            if("fitXY".equalsIgnoreCase(t[1])) {
                resizeImgParam2FillType = Image.FillType.FIT_XY;
            }
            if("centerCrop".equalsIgnoreCase(t[1])) {
                resizeImgParam2FillType = Image.FillType.CENTER_CROP;
            }
        }

        String[] resizeImgs = resizeImgParam1Size.split("x");
        if(resizeImgs.length > 1) {
            int widthParamInt1 = NumberUtils.toInt(resizeImgs[0], 200);
            int heightParamInt2 = NumberUtils.toInt(resizeImgs[1], 200);
            final String strResizeImg = widthParamInt1 + "x" + heightParamInt2;
            final String strResizeImgParam2FillType = resizeImgParam2FillType.toString();
//            if(!input.exists()) {
//                //自定义头像 awesome-identicon
//                if (StoragePathKey.avatar.value().equals(objectPathKey)) {
//                    final String avatarDefaultPath = rootPath(StorageRoot.tmpResizeImgRoot) + File.separator + "gravatar-mm-100x100.JPEG";
//                    File avatarDefaultTempFile = IOUtil.createParentDirByFilePath(avatarDefaultPath);
//                    if(!avatarDefaultTempFile.exists()) {
//                        //默认头像从 gravatar 获取
//                        try {
//                            URL gravatar = new URL(String.format(WEB_URL_CN_GRAVATAR_IMG_2_ARGS, objectBizFormId, "100"));
//                            Thumbnails.of(gravatar).scale(1.0).toFile(avatarDefaultTempFile);
//                        } catch (MalformedURLException e) {
//                        } catch (IOException e) {
//                        }
//                    }
//                    inputPath = avatarDefaultTempFile.getAbsolutePath();
//                }
//            }
            //
            if(input.exists()) {
                if (!out.exists()) {
                    Exception buildException1 = null, buildException2 = null;
                    try {
                        BufferedImage image = ImageIO.read(input);
                        BufferedImage outImage = ps(image, resizeImgParam2FillType, widthParamInt1, heightParamInt2);
                        Thumbnails.of(outImage).size(widthParamInt1, heightParamInt2).toFile(out.getAbsolutePath());
                    } catch (Exception e) {
                        buildException1 = e;
                    }
                    if (null != buildException1) {
                        try {
                            Thumbnails.of(input.getAbsolutePath()).scale(1.0).toFile(out.getAbsolutePath());
                        } catch (Exception e) {
                            buildException2 = e;
                        }
                    }
                    if (null != buildException1 || null != buildException2) {
                        log.error("buildException1", buildException1);
                        log.error("buildException2", buildException2);
                    }
                }
            }
        }
    }


    public static Map<String, Object> info(File input) {
        Map<String, Object> info = new LinkedHashMap<>();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(input);
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    //格式化输出[directory.getName()] - tag.getTagName() = tag.getDescription()
                    //
                    info.put(tag.getTagName().replaceAll(" ","").replaceAll("/",""), Kv.init().set("value", tag.getDescription()));
                }
                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        log.error(String.format("ERROR: %s", error));
                    }
                }
            }
        } catch (Exception e) {
            log.error("error", e);
        }

        return info;
    }

    /**
     * 过滤类型
     */
    private static enum FillType {
        /**
         * CENTER 图片位于视图中间，但不执行缩放比
         */
        CENTER,
        /**
         * FIT_CENTER 缩放图片使用CENTER
         */
        FIT_CENTER,
        /**
         * CENTER_CROP 均衡的缩放图像（保持图像原始比例），使图片的两个坐标（宽、高）都大于等于 相应的视图坐标（负的内边距）。图像则位于视图的中央。
         */
        CENTER_CROP,
        /**
         * FIT_XY 缩放图片使用FILL.
         */
        FIT_XY
    }

    /**
     * ps
     * @param bufferedImage
     * @param t
     * @param width
     * @param height
     * @return BufferedImage
     */
    private static BufferedImage ps(BufferedImage bufferedImage, FillType t, int width, int height) {
		BufferedImage out = null;
		switch (t) {
			case FIT_CENTER:
				out = fitCenter(bufferedImage, width, height, true);
				break;
			case CENTER_CROP:
				out = centerCrop(bufferedImage, width, height, true);
				break;
			case FIT_XY:
				out = centerCrop(bufferedImage, width, height, false);
				break;
			default:
				out = centerCrop(bufferedImage, width, height, true);
				break;
		}
		return out;
    }

    private static BufferedImage fitCenter(BufferedImage bufferedImage, int width, int height, boolean equalProportion) {
        int sWidth = bufferedImage.getWidth();
        int sHeight = bufferedImage.getHeight();
        int diffWidth = 0;
        int diffHeight = 0;
        if (equalProportion) {
            if ((double) sWidth / width > (double) sHeight / height) {
                int height2 = width * sHeight / sWidth;
                diffHeight = (height - height2) / 2;
            } else if ((double) sWidth / width < (double) sHeight / height) {
                int width2 = height * sWidth / sHeight;
                diffWidth = (width - width2) / 2;
            }
        }
        // Java BufferedImage设置透明背景 开始
        BufferedImage nbufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImageGraphicsConfig config = BufferedImageGraphicsConfig.getConfig(nbufferedImage);
        nbufferedImage = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        // Java BufferedImage设置透明背景 结束
        //nbufferedImage.getGraphics().fillRect(0, 0, width, height);//填充整个屏幕

        nbufferedImage.getGraphics().drawImage(bufferedImage,
                diffWidth, diffHeight, width - diffWidth * 2, height - diffHeight * 2, null); // 绘制缩小后的图
        return nbufferedImage;
    }

    private static BufferedImage centerCrop(BufferedImage bufferedImage, int width, int height, boolean equalProportion) {
        int sWidth = bufferedImage.getWidth();
        int sHeight = bufferedImage.getHeight();
        int diffWidth = 0;
        int diffHeight = 0;
//		   double scale = 1f;
        if (equalProportion) {
            if ((double) sWidth / width > (double) sHeight / height) {
                int width2 = height * sWidth / sHeight;
                diffWidth = (width - width2) / 2;
            } else if ((double) sWidth / width < (double) sHeight / height) {
                int height2 = width * sHeight / sWidth;
                diffHeight = (height - height2) / 2;
            }
        }
        // Java BufferedImage设置透明背景 开始
        BufferedImage nbufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImageGraphicsConfig config = BufferedImageGraphicsConfig.getConfig(nbufferedImage);
        nbufferedImage = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        // Java BufferedImage设置透明背景 结束
        //nbufferedImage.getGraphics().fillRect(0, 0, width, height);//填充整个屏幕
        nbufferedImage.getGraphics().drawImage(bufferedImage,
                diffWidth, diffHeight, width - diffWidth * 2, height - diffHeight * 2, null); // 绘制缩小后的图
        return nbufferedImage;
    }

}
