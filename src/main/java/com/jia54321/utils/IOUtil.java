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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * 通用的的IO流操纵类
 * <p>
 * 这个类提供了静态的实用方法，提供输入/输出操作。
 * 在源 (<code>InputStream</code>, <code>Reader</code>, <code>String</code> 和
 * <code>byte[]</code>) 和目标 (<code>OutputStream</code>, <code>Writer</code>,
 * <code>String</code> and <code>byte[]</code>)之间使用缓冲区复制.
 * </p>
 * <p>对与<code>copy</code> 方法, 容许设置一个buffer size，默认为4k.
 * </p>
 * <p>除非另有说明，这些<code>copy</code>方法是不会清空或者关闭流.
 * </p>
 * <p>对于 字节变换到字符(byte-to-char)的方法, 可设置编码(否则，使用平台默认编码).
 * </p>
 * <p><code>copy</code> 方法使用内部缓存. 因此每必要再用一次 <code>Buffered*</code> streams包装过的流。
 * </p>
 *
 *
 * @author 郭罡
 * @since JDK1.4
 * @description 2009-7-31 郭罡 新建
 */
public class IOUtil {

    /**默认缓存大小,单位：字节*/
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * 私有构造，以防止实例化
     */
//    private IOUtil() {
//    }

    ///////////////////////////////////////////////////////////////
    // 核心复制方法
    ///////////////////////////////////////////////////////////////
    /**
     * 复制字节从 <code>InputStream</code> 到 <code>OutputStream</code>.
     * @param bufferSize 内部缓冲值
     */
    public static void copy(final InputStream input, final OutputStream output, final int bufferSize)
            throws IOException {
        final byte[] buffer = new byte[bufferSize];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    /**
     * 复制 chars 从 <code>Reader</code> 到 <code>Writer</code>.
     */
    public static void copy(final Reader input, final Writer output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 复制 chars 从 <code>Reader</code> 到 <code>Writer</code>.
     * @param bufferSize 内部缓冲值
     */
    public static void copy(final Reader input, final Writer output, final int bufferSize)
            throws IOException {
        final char[] buffer = new char[bufferSize];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush();
    }

    ///////////////////////////////////////////////////////////////
    // 衍生的复制方法
    // InputStream -> *
    ///////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////
    // InputStream -> Writer

    /**
     * 复制 同时将<code>InputStream</code>中的字节转化字符的并写到<code>Writer</code>.
     * 使用平台默认编码 (byte-to-char).
     */
    public static void copy(final InputStream input, final Writer output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 复制 同时将<code>InputStream</code>中的字节转化字符的并写到<code>Writer</code>.
     * 使用平台默认编码 (byte-to-char).
     * @param bufferSize 内部缓冲值
     */
    public static void copy(final InputStream input, final Writer output, final int bufferSize)
            throws IOException {
        final InputStreamReader in = new InputStreamReader(input);
        copy(in, output, bufferSize);
    }

    /**
     * 复制 同时将<code>InputStream</code>中的字节转化字符的并写到<code>Writer</code>.
     * @param encoding 编码集.
     */
    public static void copy(final InputStream input, final Writer output, final String encoding)
            throws IOException {
        final InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output);
    }

    /**
     * 复制 同时将<code>InputStream</code>中的字节转化字符的并写到<code>Writer</code>.
     * @param encoding 编码集.
     * @param bufferSize 内部缓冲值
     */
    public static void copy(final InputStream input, final Writer output, final String encoding,
            final int bufferSize) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output, bufferSize);
    }

    ///////////////////////////////////////////////////////////////
    // InputStream -> String

    /**
     * 以<code>String</code>形式返回从 <code>InputStream</code> 中获取内容.
     * 使用平台默认编码 (byte-to-char).
     */
    public static String toString(final InputStream input) throws IOException {
        return toString(input, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 以<code>String</code>形式返回从 <code>InputStream</code> 中获取内容.
     * 使用平台默认编码 (byte-to-char).
     * @param bufferSize 内部缓冲值
     */
    public static String toString(final InputStream input, final int bufferSize) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, bufferSize);
        return sw.toString();
    }

    /**
     * 以<code>String</code>形式返回从 <code>InputStream</code> 中获取内容.
     * @param encoding 编码集.
     */
    public static String toString(final InputStream input, final String encoding)
            throws IOException {
        return toString(input, encoding, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 以<code>String</code>形式返回从 <code>InputStream</code> 中获取内容.
     * @param encoding 编码集.
     * @param bufferSize 内部缓冲值
     */
    public static String toString(final InputStream input, final String encoding,
            final int bufferSize) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, encoding, bufferSize);
        return sw.toString();
    }

    ///////////////////////////////////////////////////////////////
    // InputStream -> byte[]

    public static byte[] toByteArrayByPath(final String inputPath) throws IOException {
    	return toByteArrayByPath(inputPath, DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * 以<code>String</code>形式返回从 <code>InputStream</code> 中获取内容.
     */
    public static byte[] toByteArrayByPath(final String inputPath, final int bufferSize) throws IOException {
    	File f1 = new File(inputPath);// parent1 = new File(f1.getParent());
    	InputStream input = null;
    	try {
    		if (!f1.exists()) {
    			return new byte[] {};
    		}

    		input = new FileInputStream(inputPath);
    		return toByteArray(input, DEFAULT_BUFFER_SIZE);
		} catch(IOException e){
			throw new RuntimeException(e);
		} finally {
			closeQuietly(input);
		}
    }
    
    /**
     * 以<code>String</code>形式返回从 <code>InputStream</code> 中获取内容.
     */
    public static byte[] toByteArray(final InputStream input) throws IOException {
        return toByteArray(input, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 以<code>String</code>形式返回从 <code>InputStream</code> 中获取内容.
     * @param bufferSize 内部缓冲值
     */
    public static byte[] toByteArray(final InputStream input, final int bufferSize)
            throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, bufferSize);
        return output.toByteArray();
    }

    ///////////////////////////////////////////////////////////////
    // 衍生的复制方法
    // Reader -> *
    ///////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////
    // Reader -> OutputStream
    /**
     * 获取<code>Reader</code>中的字符，以字节方式写到<code>OutputStream</code>,
     * 然后清空 <code>OutputStream</code>.
     */
    public static void copy(final Reader input, final OutputStream output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 获取<code>Reader</code>中的字符，以字节方式写到<code>OutputStream</code>,
     * 然后清空 <code>OutputStream</code>.
     * @param bufferSize 内部缓冲值
     */
    public static void copy(final Reader input, final OutputStream output, final int bufferSize)
            throws IOException {
        final OutputStreamWriter out = new OutputStreamWriter(output);
        copy(input, out, bufferSize);
        // 注意: 这里会清空.
        out.flush();
    }

    ///////////////////////////////////////////////////////////////
    // Reader -> String
    /**
     * 以<code>String</code>形式返回从 <code>Reader</code> 中获取内容.
     */
    public static String toString(final Reader input) throws IOException {
        return toString(input, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 以<code>String</code>形式返回从 <code>Reader</code> 中获取内容.
     * @param bufferSize 内部缓冲值
     */
    public static String toString(final Reader input, final int bufferSize) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, bufferSize);
        return sw.toString();
    }

    ///////////////////////////////////////////////////////////////
    // Reader -> byte[]
    /**
     * 以<code>byte[]</code>形式返回从 <code>Reader</code> 中获取内容.
     */
    public static byte[] toByteArray(final Reader input) throws IOException {
        return toByteArray(input, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 以<code>byte[]</code>形式返回从 <code>Reader</code> 中获取内容.
     * @param bufferSize 内部缓冲值
     */
    public static byte[] toByteArray(final Reader input, final int bufferSize) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, bufferSize);
        return output.toByteArray();
    }

    ///////////////////////////////////////////////////////////////
    // 衍生的复制方法
    // String -> *
    ///////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////
    // String -> OutputStream

    /**
     * 获取<code>String</code>中的字符，以字节方式写到<code>OutputStream</code>,
     * 然后清空 <code>OutputStream</code>.
     */
    public static void copy(final String input, final OutputStream output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 获取<code>String</code>中的字符，以字节方式写到<code>OutputStream</code>,
     * 然后清空 <code>OutputStream</code>.
     * @param bufferSize 内部缓冲值
     */
    public static void copy(final String input, final OutputStream output, final int bufferSize)
            throws IOException {
        final StringReader in = new StringReader(input);
        final OutputStreamWriter out = new OutputStreamWriter(output);
        copy(in, out, bufferSize);
        // 注意: 这里会清空.
        out.flush();
    }

    ///////////////////////////////////////////////////////////////
    // String -> Writer

    /**
     * 获取<code>String</code>中的字符，写到<code>Writer</code>中
     */
    public static void copy(final String input, final Writer output) throws IOException {
        output.write(input);
    }

    ///////////////////////////////////////////////////////////////
    // String -> byte[]
    /**
     * 以<code>byte[]</code>形式返回 <code>String</code> 中的内容.
     */
    public static byte[] toByteArray(final String input) throws IOException {
        return toByteArray(input, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 以<code>byte[]</code>形式返回 <code>String</code> 中的内容.
     * @param bufferSize 内部缓冲值
     */
    public static byte[] toByteArray(final String input, final int bufferSize) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, bufferSize);
        return output.toByteArray();
    }

    ///////////////////////////////////////////////////////////////
    // 衍生的复制方法
    // byte[] -> *
    ///////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////
    // byte[] -> Writer

    /**
     * 复制 将<code>byte[]</code>转化字符的并写到<code>Writer</code>.
     * 使用平台默认编码 (byte-to-char).
     */
    public static void copy(final byte[] input, final Writer output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 复制 将<code>byte[]</code>转化字符的并写到<code>Writer</code>.
     * 使用平台默认编码 (byte-to-char).
     * @param bufferSize 内部缓冲值
     */
    public static void copy(final byte[] input, final Writer output, final int bufferSize)
            throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(input);
        copy(in, output, bufferSize);
    }

    /**
     * 复制 将<code>byte[]</code>转化字符的并写到<code>Writer</code>.
     * @param encoding 编码集.
     */
    public static void copy(final byte[] input, final Writer output, final String encoding)
            throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(input);
        copy(in, output, encoding);
    }

    /**
     * 复制 将<code>byte[]</code>转化字符的并写到<code>Writer</code>.
     * @param encoding 编码集.
     * @param bufferSize 内部缓冲值
     */
    public static void copy(final byte[] input, final Writer output, final String encoding,
            final int bufferSize) throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(input);
        copy(in, output, encoding, bufferSize);
    }

    ///////////////////////////////////////////////////////////////
    /**
     * createFileByPath创建输出路径的父目录 <code>outPath</code>.
     */
    public static File createParentDirByFilePath(final String outPath) {
		File f = new File(outPath);
   		if (!f.exists()) {
   			createDirPath(f.getParent());
    		if (!f.getParentFile().exists()) {
    			f.getParentFile().mkdirs();
    		}
		}
   		return f;
    }

    public static File createDirPath(final String outPath) {
		//==============================================================================
		File f = new File(outPath);
   		if (!f.isDirectory()) {
   			f.mkdirs();
		}
		//==============================================================================
   		return f;
    }
    
    /**
     * 复制  <code>inputPath</code> 到 <code>outPath</code>.<br/>
     * 如果 inputPath 为 "", 仅仅创建输出路径的父目录
     */
    public static void copyByPath(final String inputPath, final String outPath) {
    	InputStream input = null;
    	OutputStream output = null;
    	try {
    		if (null == inputPath  || "".equals(inputPath)  || !(new File(inputPath)).exists()) {
    			return ;
    		}
    		
    		File f2 = createParentDirByFilePath(outPath);
       		if (!f2.exists()) {
       			f2.createNewFile();
    		}
    		input = new FileInputStream(inputPath);
    		output = new FileOutputStream(outPath);
    		copy(input, output, DEFAULT_BUFFER_SIZE);
//      } catch(FileNotFoundException e){
//    		throw new RuntimeException(e);
		} catch(IOException e){
			throw new RuntimeException(e);
		} finally {
			closeQuietly(input);
			closeQuietly(output);
		}
    	return ;
    }
    
    /**
     * 复制 <code>inputPath</code> 到 <code>OutputStream</code>.
     */
    public static void copyByPath(final String inputPath, final OutputStream output) {
		File f1 = new File(inputPath);// parent1 = new File(f1.getParent());
    	InputStream input = null;
    	try {
    		if (!f1.exists()) {
    			return ;
    		}

    		input = new FileInputStream(inputPath);
    		copy(input, output, DEFAULT_BUFFER_SIZE);
//    	} catch(FileNotFoundException e){
//    		throw new RuntimeException(e);
		} catch(IOException e){
			throw new RuntimeException(e);
		} finally {
			closeQuietly(input);
		}
    }
    
    /**
     * 复制 <code>InputStream</code> 到 <code>outPath</code>.
     */
    public static void copyByPath(final InputStream input, final String outPath) {
    	File f2 = new File(outPath), parent2 = new File(f2.getParent());
    	OutputStream output = null;
    	try {
    		if (!parent2.exists()) {
    			parent2.mkdirs();
    		}
       		if (!f2.exists()) {
       			f2.createNewFile();
    		}
       		
    		output = new FileOutputStream(outPath);
    		copy(input, output, DEFAULT_BUFFER_SIZE);
//        	} catch(FileNotFoundException e){
//    		throw new RuntimeException(e);
		} catch(IOException e){
			f2.delete();
			throw new RuntimeException(e);
		} finally {
			closeQuietly(input);
			closeQuietly(output);
		}
    }
    
    /**
     * 复制 <code>input</code> 到 <code>outPath</code>.
     */
    public static void copyByPath(final byte[] input, final String outPath) {
    	final ByteArrayInputStream in = new ByteArrayInputStream(input);
    	copyByPath(in, outPath);
    }
    ///////////////////////////////////////////////////////////////
    // byte[] -> String

    /**
     * 以String形式返回 <code>byte[]</code> 中的内容.
     * 使用平台默认编码 (byte-to-char).
     */
    public static String toString(final byte[] input) throws IOException {
        return toString(input, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 以String形式返回 <code>byte[]</code> 中的内容.
     * 使用平台默认编码 (byte-to-char).
     * @param bufferSize 内部缓冲值
     */
    public static String toString(final byte[] input, final int bufferSize) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, bufferSize);
        return sw.toString();
    }

    /**
     * 以String形式返回 <code>byte[]</code> 中的内容.
     * @param encoding 编码集.
     */
    public static String toString(final byte[] input, final String encoding) throws IOException {
        return toString(input, encoding, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 以String形式返回 <code>byte[]</code> 中的内容.
     * @param encoding 编码集.
     * @param bufferSize 内部缓冲值
     */
    public static String toString(final byte[] input, final String encoding, final int bufferSize)
            throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, encoding, bufferSize);
        return sw.toString();
    }

    ///////////////////////////////////////////////////////////////
    // byte[] -> OutputStream

    /**
     * 复制 从<code>byte[]</code>中获取字节并写到<code>OutputStream</code>.
     */
    public static void copy(final byte[] input, final OutputStream output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 复制 从<code>byte[]</code>中获取字节并写到<code>OutputStream</code>.
     * @param bufferSize 内部缓冲值
     */
    public static void copy(final byte[] input, final OutputStream output, final int bufferSize)
            throws IOException {
        output.write(input);
    }

    /**
     * 判断两个流中的内容是否相等
     *
     * @param input1 the first stream
     * @param input2 the second stream
     * @return true 流的内容相等或双方都不存在，否则为false
     */
    public static boolean contentEquals(final InputStream input1, final InputStream input2)
            throws IOException {
        final InputStream bufferedInput1 = new BufferedInputStream(input1);
        final InputStream bufferedInput2 = new BufferedInputStream(input2);

        int ch = bufferedInput1.read();
        while (-1 != ch) {
            final int ch2 = bufferedInput2.read();
            if (ch != ch2) {
                return false;
            }
            ch = bufferedInput1.read();
        }

        final int ch2 = bufferedInput2.read();
        return -1 == ch2;
    }

    /**
     * 文件是否有内容
     * @param localOrUrl
     * @param webroot 可选项
     * @return true 有内容，否则为false
     */
	public static Boolean contentIsAvailable(String localOrUrl, String... webroot) {
		if (null == localOrUrl || "".equals(localOrUrl)) {
			return false;
		}
		
		// 本地文件
		File localFIle = null;
		try {
			localOrUrl = localOrUrl.replaceAll("\\\\\\\\", "/");
			localOrUrl = localOrUrl.replaceAll("\\\\", "/");
			localOrUrl = localOrUrl.replaceAll("//", "/");
			localFIle = new File(localOrUrl);
			if (localFIle.isFile()) {
				return true;
			}
		} catch (Exception e) {
		} 
		
		// 本地文件
		InputStream netFileInputStream = null;
		try {
			localOrUrl = localOrUrl.replaceAll("\\\\\\\\", "/");
			localOrUrl = localOrUrl.replaceAll("\\\\", "/");
			localOrUrl = localOrUrl.replaceAll("//", "/");
			netFileInputStream = new FileInputStream(localOrUrl);
			if (null != netFileInputStream) {
				return true;
			}
		} catch (Exception e) {
		} finally {
			closeQuietly(netFileInputStream);
		}
		
		try {
			if (null != webroot && webroot.length == 1
					&& null != webroot[0] && !"".equals(webroot[0])) {
				if (localOrUrl.indexOf(webroot[0]) < 0) {
					localOrUrl = localOrUrl.replaceAll("\\\\\\\\", "/");
					localOrUrl = localOrUrl.replaceAll("\\\\", "/");
					localOrUrl = localOrUrl.replaceAll("//", "/");
					localOrUrl = webroot[0] + "/" + localOrUrl;
				}
			}
			URL url = new URL(localOrUrl);
			URLConnection urlConn = url.openConnection();
			netFileInputStream = urlConn.getInputStream();
			if (null != netFileInputStream) {
				return true;
			}
		} catch (Exception e) {
		} finally {
			closeQuietly(netFileInputStream);
		}
		return false;
	}
    // ----------------------------------------------------------------------
    // closeQuietlyXXX()
    // ----------------------------------------------------------------------

    /**
     * 关闭输入流. 输入流可空，任何IOException的将被忽略。
     *
     * @param inputStream 要关闭的流.
     */
    public static void closeQuietly(InputStream inputStream) {
        if (inputStream == null) {
            return;
        }

        try {
            inputStream.close();
        } catch (IOException ex) {
            // ignore
        }
    }

    /**
     * 关闭输出流. 输出流可空，任何IOException的将被忽略。
     *
     * @param outputStream 要关闭的流.
     */
    public static void closeQuietly(OutputStream outputStream) {
        if (outputStream == null) {
            return;
        }

        try {
            outputStream.close();
        } catch (IOException ex) {
            // ignore
        }
    }

    /**
     * 关闭 reader. reader可空，任何IOException的将被忽略。
     *
     * @param reader 要关闭的reader.
     */
    public static void closeQuietly(Reader reader) {
        if (reader == null) {
            return;
        }

        try {
            reader.close();
        } catch (IOException ex) {
            // ignore
        }
    }

    /**
     * 关闭 writer. writer可空，任何IOException的将被忽略。
     *
     * @param writer 要关闭的writer.
     */
    public static void closeQuietly(Writer writer) {
        if (writer == null) {
            return;
        }

        try {
            writer.close();
        } catch (IOException ex) {
            // ignore
        }
    }
}
