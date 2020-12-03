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

/**
 * “BSON” is a portmanteau of the words “binary” and “JSON”. Think of BSON as a
 * binary representation of JSON (JavaScript Object Notation) documents
 * 
 * @author 郭罡
 *
 */
public abstract class BsonUtil {

	/**
	 * 将 64进制编码长度为16位的ID 转换为 16进制编码的长度为24位的ID
	 * 
	 * @param shortId
	 * @return string
	 */
	public static String unCompressObjectId(String shortId) {
		if (shortId == null || shortId.length() != 16) {
			throw new IllegalArgumentException();
		}
		StringBuilder res = new StringBuilder(24);
		char[] str = shortId.toCharArray();
		for (int i = 0; i < str.length; i += 2) {
			int pre = char2Int(str[i]), end = char2Int(str[i + 1]);
			res.append(int2Char((pre >> 2)));
			res.append(int2Char(((pre & 3) << 2) + (end >> 4)));
			res.append(int2Char(end & 15));
		}
		return res.toString();
	}

	/**
	 * * 将 16进制编码的长度为24位的ID 转换为 64进制编码长度为16位的ID
	 * 
	 * @param objectId
	 * @return string
	 */
	public static String compressObjectId(String objectId) {
		if (objectId == null || objectId.length() != 24) {
			throw new IllegalArgumentException();
		}
		StringBuilder res = new StringBuilder(16);
		char[] str = objectId.toCharArray();
		for (int i = 0; i < str.length; i += 3) {
			int pre = char2Int(str[i]), mid = char2Int(str[i + 1]), end = char2Int(str[i + 2]);
			res.append(int2Char((pre << 2) + (mid >> 2)));
			res.append(int2Char(((mid & 3) << 4) + end));
		}
		return res.toString();
	}

	/**
	 * 支持64进制bit转字符 * 0～9,a～z,A～Z,-,_
	 * 
	 * @param i
	 * @return
	 */
	private static char int2Char(int i) {
		if (i >= 0 && i <= 9) {
			return (char) ('0' + i);
		} else if (i >= 10 && i <= 35) {
			return (char) ('a' + i - 10);
		} else if (i >= 36 && i <= 61) {
			return (char) ('A' + i - 36);
		} else if (i == 62) {
			return '-';
		} else if (i == 63) {
			return '_';
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * 支持64进制字符转bit * 0～9,a～z,A～Z,-,_
	 * 
	 * @param c
	 */
	private static int char2Int(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		} else if (c >= 'a' && c <= 'z') {
			return 10 + c - 'a';
		} else if (c >= 'A' && c <= 'Z') {
			return 36 + c - 'A';
		} else if (c == '-') {
			return 62;
		} else if (c == '_') {
			return 63;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	
//	public static void main(String[] args) {
//		System.out.println(BsonUtil.compressObjectId(ObjectId.get().toHexString()));
//	}
	/**
或操作符：| ，示例：5 | 3 = 0101b | 0011b = 0111b = 7
非操作符：~ ，示例： ~5 = ~0000...0101b = 1111...1010b = -6
异或操作符： ^ ，示例： 5 ^ 3 = 0101b ^ 0011b = 0110b = 6
与操作符： & ，示例： 5 & 3 = 0101b & 0011b = 0001b = 1
左移操作符：<< ，示例：5 << 35 = 0101b << ( 35%32) = 0101b << 3 = 0010 1000 = 40
算数右移：>> ，示例1： 5 >> 2 = 0101b >> 2 = 0001b = 1 ；示例2：-5 >> 2 = 1111...1011b >> 2 = 1111...1110b  = -2
逻辑右移：>>> ，示例：-5 >>> 2 = 1111...1011b >>> 2 = 0011...1110b = 1073741822
	 */
}
