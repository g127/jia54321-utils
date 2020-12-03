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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * DigestUtil
 * @author 郭罡
 */
public class DigestUtil {


	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex string.
	 *
	 * @param data Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(final String data) {
		return DigestUtil.md5DigestAsHex(data.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Return a hexadecimal string representation of the MD5 digest of the given bytes.
	 *
	 * @param bytes the bytes to calculate the digest over
	 * @return a hexadecimal digest string
	 */
	public static String md5Hex(final byte[] bytes) {
		return DigestUtil.md5DigestAsHex(bytes);
	}

	private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

	public static String sha1(String srcStr) {
		return hash("SHA-1", srcStr);
	}

	public static String sha256(String srcStr) {
		return hash("SHA-256", srcStr);
	}

	public static String sha384(String srcStr) {
		return hash("SHA-384", srcStr);
	}

	public static String sha512(String srcStr) {
		return hash("SHA-512", srcStr);
	}



	public static String hash(String algorithm, String srcStr) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] bytes = md.digest(srcStr.getBytes(StandardCharsets.UTF_8));
			return toHex(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toHex(byte[] bytes) {
		StringBuilder ret = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
			ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
		}
		return ret.toString();
	}

	public static boolean slowEquals(String a, String b) {
		if (a == null || b == null) {
			return false;
		}
		return slowEquals(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
	}

	public static boolean slowEquals(byte[] a, byte[] b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.length != b.length) {
			return false;
		}
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}

	/**
	 * 自定义加密 先MD5再SHA1
	 *
	 * @param data 数据
	 * @return String
	 */
	public static String encrypt(String data) {
		return sha1(md5Hex(data));
	}


	private static final String MD5_ALGORITHM_NAME = "MD5";

	private static final char[] HEX_CHARS =
			{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


	/**
	 * Calculate the MD5 digest of the given bytes.
	 * @param bytes the bytes to calculate the digest over
	 * @return the digest
	 */
	public static byte[] md5Digest(byte[] bytes) {
		return digest(MD5_ALGORITHM_NAME, bytes);
	}

	/**
	 * Calculate the MD5 digest of the given stream.
	 * @param inputStream the InputStream to calculate the digest over
	 * @return the digest
	 * @since 4.2
	 */
	public static byte[] md5Digest(InputStream inputStream) throws IOException {
		return digest(MD5_ALGORITHM_NAME, inputStream);
	}

	/**
	 * Return a hexadecimal string representation of the MD5 digest of the given bytes.
	 * @param bytes the bytes to calculate the digest over
	 * @return a hexadecimal digest string
	 */
	public static String md5DigestAsHex(byte[] bytes) {
		return digestAsHexString(MD5_ALGORITHM_NAME, bytes);
	}

	/**
	 * Return a hexadecimal string representation of the MD5 digest of the given stream.
	 * @param inputStream the InputStream to calculate the digest over
	 * @return a hexadecimal digest string
	 * @since 4.2
	 */
	public static String md5DigestAsHex(InputStream inputStream) throws IOException {
		return digestAsHexString(MD5_ALGORITHM_NAME, inputStream);
	}

	/**
	 * Append a hexadecimal string representation of the MD5 digest of the given
	 * bytes to the given {@link StringBuilder}.
	 * @param bytes the bytes to calculate the digest over
	 * @param builder the string builder to append the digest to
	 * @return the given string builder
	 */
	public static StringBuilder appendMd5DigestAsHex(byte[] bytes, StringBuilder builder) {
		return appendDigestAsHex(MD5_ALGORITHM_NAME, bytes, builder);
	}

	/**
	 * Append a hexadecimal string representation of the MD5 digest of the given
	 * inputStream to the given {@link StringBuilder}.
	 * @param inputStream the inputStream to calculate the digest over
	 * @param builder the string builder to append the digest to
	 * @return the given string builder
	 * @since 4.2
	 */
	public static StringBuilder appendMd5DigestAsHex(InputStream inputStream, StringBuilder builder) throws IOException {
		return appendDigestAsHex(MD5_ALGORITHM_NAME, inputStream, builder);
	}


	/**
	 * Create a new {@link MessageDigest} with the given algorithm.
	 * Necessary because {@code MessageDigest} is not thread-safe.
	 */
	private static MessageDigest getDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		}
		catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", ex);
		}
	}

	private static byte[] digest(String algorithm, byte[] bytes) {
		return getDigest(algorithm).digest(bytes);
	}

	private static byte[] digest(String algorithm, InputStream inputStream) throws IOException {
		MessageDigest messageDigest = getDigest(algorithm);
		if (inputStream instanceof UpdateMessageDigestInputStream){
			((UpdateMessageDigestInputStream) inputStream).updateMessageDigest(messageDigest);
			return messageDigest.digest();
		}
		else {
			final byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				messageDigest.update(buffer, 0, bytesRead);
			}
			return messageDigest.digest();
		}
	}

	private static String digestAsHexString(String algorithm, byte[] bytes) {
		char[] hexDigest = digestAsHexChars(algorithm, bytes);
		return new String(hexDigest);
	}

	private static String digestAsHexString(String algorithm, InputStream inputStream) throws IOException {
		char[] hexDigest = digestAsHexChars(algorithm, inputStream);
		return new String(hexDigest);
	}

	private static StringBuilder appendDigestAsHex(String algorithm, byte[] bytes, StringBuilder builder) {
		char[] hexDigest = digestAsHexChars(algorithm, bytes);
		return builder.append(hexDigest);
	}

	private static StringBuilder appendDigestAsHex(String algorithm, InputStream inputStream, StringBuilder builder)
			throws IOException {

		char[] hexDigest = digestAsHexChars(algorithm, inputStream);
		return builder.append(hexDigest);
	}

	private static char[] digestAsHexChars(String algorithm, byte[] bytes) {
		byte[] digest = digest(algorithm, bytes);
		return encodeHex(digest);
	}

	private static char[] digestAsHexChars(String algorithm, InputStream inputStream) throws IOException {
		byte[] digest = digest(algorithm, inputStream);
		return encodeHex(digest);
	}

	private static char[] encodeHex(byte[] bytes) {
		char[] chars = new char[32];
		for (int i = 0; i < chars.length; i = i + 2) {
			byte b = bytes[i / 2];
			chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
			chars[i + 1] = HEX_CHARS[b & 0xf];
		}
		return chars;
	}
	

/**
 * Extension of {@link java.io.InputStream} that allows for optimized
 * implementations of message digesting.
 *
 * @author Craig Andrews
 * @since 4.2
 */
abstract class UpdateMessageDigestInputStream extends InputStream {

	/**
	 * Update the message digest with the rest of the bytes in this stream.
	 * <p>Using this method is more optimized since it avoids creating new
	 * byte arrays for each call.
	 * @param messageDigest the message digest to update
	 * @throws IOException when propagated from {@link #read()}
	 */
	public void updateMessageDigest(MessageDigest messageDigest) throws IOException {
		int data;
		while ((data = read()) != -1) {
			messageDigest.update((byte) data);
		}
	}

	/**
	 * Update the message digest with the next len bytes in this stream.
	 * <p>Using this method is more optimized since it avoids creating new
	 * byte arrays for each call.
	 * @param messageDigest the message digest to update
	 * @param len how many bytes to read from this stream and use to update the message digest
	 * @throws IOException when propagated from {@link #read()}
	 */
	public void updateMessageDigest(MessageDigest messageDigest, int len) throws IOException {
		int data;
		int bytesRead = 0;
		while (bytesRead < len && (data = read()) != -1) {
			messageDigest.update((byte) data);
			bytesRead++;
		}
	}

}

}
