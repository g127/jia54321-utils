/**
 * MIT License
 * 
 * Copyright (c) 2009-present GuoGang and other contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.jia54321.utils;

import java.awt.image.BufferedImage;

/**
 * 图片处理
 * @author 郭罡
 */
public class BufferedImageUtil {
	public static enum FillType {
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
		FIT_XY };

	/**
	 * 
	 * @param bufferedImage
	 * @param t
	 * @param width
	 * @param height
	 * @return BufferedImage
	 */
	public static BufferedImage ps(BufferedImage bufferedImage, FillType t, int width, int height){
		if(t == FillType.FIT_CENTER){
			return fitCenter(bufferedImage, width, height, true);
		} else if(t == FillType.CENTER_CROP){
			return centerCrop(bufferedImage, width, height, true);
		} else if(t == FillType.FIT_XY){
			return centerCrop(bufferedImage, width, height, false);
		} else {
			return centerCrop(bufferedImage, width, height, true);
		}
	}
	
	private static BufferedImage fitCenter(BufferedImage bufferedImage, int width, int height, boolean equalProportion) {  
	   int sWidth=bufferedImage.getWidth();  
	   int sHeight=bufferedImage.getHeight();  
	   int diffWidth=0;  
	   int diffHeight=0;  
	   if(equalProportion){  
	       if((double)sWidth/width>(double)sHeight/height) {  
	           int height2=width*sHeight/sWidth;  
	           diffHeight=(height-height2)/2;  
	       }else if((double)sWidth/width<(double)sHeight/height) {  
	           int width2=height*sWidth/sHeight;  
	           diffWidth=(width-width2)/2;  
	       }  
	   }  
	   BufferedImage nbufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);  
	   nbufferedImage.getGraphics().fillRect(0,0,width,height);//填充整个屏幕  
	   nbufferedImage.getGraphics().drawImage(bufferedImage, diffWidth, diffHeight, width-diffWidth*2, height-diffHeight*2, null); // 绘制缩小后的图  
	   return nbufferedImage;
	}
	
	private static BufferedImage centerCrop(BufferedImage bufferedImage, int width, int height, boolean equalProportion) {  
		   int sWidth=bufferedImage.getWidth();  
		   int sHeight=bufferedImage.getHeight();  
		   int diffWidth=0;  
		   int diffHeight=0;  
//		   double scale = 1f;
		   if(equalProportion){  
		       if((double)sWidth/width>(double)sHeight/height) {  
		           int width2=height*sWidth/sHeight;  
		           diffWidth=(width-width2)/2; 
		       }else if((double)sWidth/width<(double)sHeight/height) {  
		           int height2=width*sHeight/sWidth;  
		           diffHeight=(height-height2)/2;  
		       }  
		   }
		   BufferedImage nbufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);  
		   nbufferedImage.getGraphics().fillRect(0,0,width,height);//填充整个屏幕  
		   nbufferedImage.getGraphics().drawImage(bufferedImage, diffWidth, diffHeight, width-diffWidth*2, height-diffHeight*2, null); // 绘制缩小后的图  
		   return nbufferedImage;
		}

}
