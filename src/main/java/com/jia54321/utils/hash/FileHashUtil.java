package com.jia54321.utils.hash;

import com.jia54321.utils.IOUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;



/**
 *
 * @author G
 *
 */
public class FileHashUtil {

	public static final String MD5 = "MD5";
	public static final String SHA1 = "SHA-1";

	private static byte[] createChecksum(String filename, String algorithm) {
		InputStream fis = null;

		try {
			fis = new FileInputStream(filename);

	        byte[] buffer = new byte[1024];
	        MessageDigest complete = MessageDigest.getInstance(algorithm);
	        int numRead;

	        do {
	            numRead = fis.read(buffer);
	            if (numRead > 0) {
	                complete.update(buffer, 0, numRead);
	            }
	        } while (numRead != -1);

	        return complete.digest();
		} catch (Exception e) {
			throw new RuntimeException("error", e);
		} finally {
			IOUtil.closeQuietly(fis);
		}
    }

    public static String getChecksum(String filename, String algorithm) {
         byte[] b = createChecksum(filename, algorithm);
         String result = "";

         for (int i=0; i < b.length; i++) {
             result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
         }
         return result;
    }

	public static String getMD5Checksum(String filename) {
		return getChecksum(filename, MD5);
	}

	public static String getSHA1Checksum(String filename) {
		return getChecksum(filename, SHA1);
	}

	public static boolean checksumMD5(String filename, String sum)
			throws Exception {
		return getMD5Checksum(filename).equalsIgnoreCase(sum);
	}

	public static String toMd5(String data) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data.getBytes());
			StringBuffer buf = new StringBuffer();
			byte[] bits = md.digest();
			for (int i = 0; i < bits.length; i++) {
				int a = bits[i];
				if (a < 0) {
                    a += 256;
                }
				if (a < 16) {
                    buf.append("0");
                }
				buf.append(Integer.toHexString(a));
			}
			return buf.toString();
		} catch (Exception e) {
		}
		return null;
	}

	public static String toSha1(String data) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(data.getBytes());
			StringBuffer buf = new StringBuffer();
			byte[] bits = md.digest();
			for (int i = 0; i < bits.length; i++) {
				int a = bits[i];
				if (a < 0) {
                    a += 256;
                }
				if (a < 16) {
                    buf.append("0");
                }
				buf.append(Integer.toHexString(a));
			}
			return buf.toString();
		} catch (Exception e) {
		}
		return null;
	}

//
//
//
//    public static void main(String args[]) {
//        try {
//        	long a = System.currentTimeMillis();
//            System.out.println(getMD5Checksum("D:\\DevProjectFiles\\ws-java20150210\\svn\\t\\ttt.txt"));
//            long b = System.currentTimeMillis();
//         	 System.out.println(b-a);
//            System.out.println(getSHA1Checksum("D:\\DevProjectFiles\\ws-java20150210\\svn\\t\\ttt.txt"));
//            long converter = System.currentTimeMillis();
//         	 System.out.println(converter-b);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
