package com.jia54321.utils;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.*;
import okhttp3.Request.Builder;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * OK Http 工具类
 * @author G
 */
public class OKHttpUtils {
	/** 日志 */
	private static Logger log = LoggerFactory.getLogger(OKHttpUtils.class);

	private final static OkHttpClient M_OK_HTTP_CLIENT;

	private final static OkHttpClient M_OK_HTTPS_CLIENT;

	private final static long DEFULTE_CONNECTION_TIMEOUT = 30L;

	static {
		M_OK_HTTP_CLIENT = new OkHttpClient().newBuilder()
				.connectTimeout(DEFULTE_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(DEFULTE_CONNECTION_TIMEOUT, TimeUnit.SECONDS).build();

		M_OK_HTTPS_CLIENT =  getTrustAllClient();
	}

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType XML = MediaType.parse("text/xml; charset=utf-8");
	public static final MediaType FORM =  MediaType.parse("application/x-www-form-urlencoded");

	private static OkHttpClient getOkHttpClient(Request request) {
		if(request.isHttps()) {
			return M_OK_HTTPS_CLIENT;
		}
		return M_OK_HTTP_CLIENT;
	}
	/**
	 * 构建一个请求体
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new okhttp3.Request.Builder();
	}

	/**
	 * 请求数据，不开启异步线程
	 *
	 * @param request 请求体
	 * @return Response
	 * @throws IOException  IOException
	 */
	public static Response execute(Request request) throws IOException {
		return getOkHttpClient(request).newCall(request).execute();
	}

	/**
	 * 请求数据，不开启异步线程
	 *
	 * @param url  请求地址
	 * @param type 媒体类型  如  OKHttpUtils.JSON
	 * @param postData  post数据
	 * @return 响应数据
	 */
	public static String executePost(String url, MediaType type, String postData) {
		try {
			return execute(
					new okhttp3.Request.Builder().url(url).post(okhttp3.RequestBody.create(type, postData)).build())
					.body().string();
		} catch (Exception e) {
			log.error(String.format("请求%s 数据 %s", url, postData), e);
			return "";
		}
	}

	/**
	 * 开启异步线程访问，访问结果自行处理
	 * @param request request
	 * @param responseCallback responseCallback
	 */
	public static void enqueue(Request request, Callback responseCallback) {
		getOkHttpClient(request).newCall(request).enqueue(responseCallback);
	}



	/**
	 * 开启异步线程访问,不对访问结果进行处理
	 *
	 * @param request request
	 */
	public static void enqueue(Request request) {
		getOkHttpClient(request).newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, Response response)
					throws IOException {
				if (!response.isSuccessful()) {
                    if (log.isErrorEnabled()) {
                        //logger.error("Unexpected code " + response);
                        log.error("Unexpected code " + response);
                    }
                }
				response.close();
			}

			@Override
			public void onFailure(Call paramCall, IOException paramIOException) {
			}
		});
	}

	/**
	 * 为HttpGet请求拼接一个参数
	 *
	 * @param url   地址
	 * @param name  参数名
	 * @param value 参数值
	 * @return string
	 */
	public static String jointURL(String url, String name, String value) {
		return url + "?" + name + "=" + value;
	}

	/**
	 * 为HttpGet请求拼接多个参数
	 *
	 * @param url   地址
	 * @param values  参数名，值对
	 * @return string
	 */
	public static String jointURL(String url, Map<String, ?> values) {
		StringBuffer result = new StringBuffer();
		result.append(url).append("?");
		Set<String> keys = values.keySet();
		for (String key : keys) {
			result.append(key).append("=").append(String.valueOf(values.get(key))).append("&");
		}
		return result.toString().substring(0, result.toString().length() - 1);
	}



	private static OkHttpsTrustManager mOkHttpsTrustManager;

	private static SSLSocketFactory createSSLSocketFactory() {
		SSLSocketFactory ssfFactory = null;
		try {
			mOkHttpsTrustManager = new OkHttpsTrustManager();
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{mOkHttpsTrustManager}, new SecureRandom());
			ssfFactory = sc.getSocketFactory();
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}

		return ssfFactory;
	}

	//实现X509TrustManager接口
	public static class OkHttpsTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			log.debug("checkClientTrusted");
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			log.debug("checkServerTrusted");
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}

	//实现HostnameVerifier接口
	private static class TrustAllHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return hostname.equalsIgnoreCase(session.getPeerHost()); //符合
//			return true;
		}
	}

	private static OkHttpClient getTrustAllClient() {
		OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
		mBuilder.sslSocketFactory(createSSLSocketFactory(), mOkHttpsTrustManager)
				.hostnameVerifier(new TrustAllHostnameVerifier());
		return mBuilder.build();
	}
}
