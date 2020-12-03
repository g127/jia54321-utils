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
 * 
 * @author 郭罡
 */
public class ByteUtil {
	
	public static void long2bytes(byte[] bb, long x) {
		bb[0] = (byte) (x >> 56);
		bb[1] = (byte) (x >> 48);
		bb[2] = (byte) (x >> 40);
		bb[3] = (byte) (x >> 32);
		bb[4] = (byte) (x >> 24);
		bb[5] = (byte) (x >> 16);
		bb[6] = (byte) (x >> 8);
		bb[7] = (byte) (x >> 0);
	}

	public static long bytes2Long(byte[] bb) { 
	       return ((((long) bb[ 0] & 0xff) << 56) 
	               | (((long) bb[ 1] & 0xff) << 48) 
	               | (((long) bb[ 2] & 0xff) << 40) 
	               | (((long) bb[ 3] & 0xff) << 32) 
	               | (((long) bb[ 4] & 0xff) << 24) 
	               | (((long) bb[ 5] & 0xff) << 16) 
	               | (((long) bb[ 6] & 0xff) << 8)
	               | (((long) bb[ 7] & 0xff) << 0)); 
	} 
 
	public static long bytes2long(byte[] b) {
		long temp = 0;
		long res = 0;
		for (int i=0;i<8;i++) {
			res <<= 8;
			temp = b[i] & 0xff;
			res |= temp;
		}
		return res;
	}
	
	public static byte[] long2bytes(long num) {
		byte[] b = new byte[8];
		for (int i=0;i<8;i++) {
			b[i] = (byte)(num>>>(56-(i*8)));
		}
		return b;
	}
 
	public static byte[] intToByteArray1(int i) {   
	  byte[] result = new byte[4];   
	  result[0] = (byte)((i >> 24) & 0xFF);
	  result[1] = (byte)((i >> 16) & 0xFF);
	  result[2] = (byte)((i >> 8) & 0xFF); 
	  result[3] = (byte)(i & 0xFF);
	  return result;
	}
// 
// public static void main(String[] args) {
//	 byte[] b = new byte[]{84, -3, 118, -65, 29, -16, -43, 16, 68, -77, -42, -84};
//	 System.out.println(bytes2Long(b));
//	 System.out.println(bytes2long(b));
//	 byte[] bb =  long2bytes(bytes2Long(b));
//	 ObjectId oo = new ObjectId(bb);
//	 System.out.println(oo);
//}
}
