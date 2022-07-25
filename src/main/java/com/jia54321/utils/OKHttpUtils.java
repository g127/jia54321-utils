package com.jia54321.utils;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.logging.HttpLoggingInterceptor;
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

	/** OkHttpProperties */
	private static OkHttpProperties properties = new OkHttpProperties();

	/** MediaType JSON */
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	/** MediaType xml */
	public static final MediaType XML = MediaType.parse("text/xml; charset=utf-8");
	/** MediaType x-www-form-urlencoded */
	public static final MediaType FORM =  MediaType.parse("application/x-www-form-urlencoded");


	/** M_OK_HTTP_CLIENT */
	private static volatile OkHttpClient M_OK_HTTP_CLIENT = newClientBuilder(false).build();
	/** M_OK_HTTPS_CLIENT */
	private static volatile OkHttpClient M_OK_HTTPS_CLIENT = newClientBuilder(true).build();

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

	/**
	 * 根据构造的请求体来使用对应的 OkHttpClient
	 * @param request
	 * @return OkHttpClient
	 */
	private static OkHttpClient getOkHttpClient(Request request) {
		if(request.isHttps()) {
			return M_OK_HTTPS_CLIENT;
		}
		return M_OK_HTTP_CLIENT;
	}

	/**
	 * 创建 SSLSocketFactory
	 * @param trustManager 证书管理
	 * @return SSLSocketFactory
	 */
	private static SSLSocketFactory createSSLSocketFactory(X509TrustManager trustManager) {
		SSLSocketFactory ssfFactory = null;
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{ trustManager }, new SecureRandom());
			ssfFactory = sc.getSocketFactory();
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}

		return ssfFactory;
	}

	/**
	 * createHttpLoggingInterceptor
	 * @return
	 */
	private static HttpLoggingInterceptor createHttpLoggingInterceptor() {
		HttpLoggingInterceptor interceptor =
				new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
					@Override
					public void log(String message) {
						try {
							if(log.isDebugEnabled()) {
								log.debug(message);
							}
						} catch (Exception e) {
							if(log.isErrorEnabled()) {
								log.error(message, e);
							}
						}
					}
				});

		//包含header、body数据
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        setlevel用来设置日志打印的级别，共包括了四个级别：NONE,BASIC,HEADER,BODY
//        BASEIC:请求/响应行
//        HEADER:请求/响应行 + 头
//        BODY:请求/响应行 + 头 + 体

		return interceptor;
	}

	/**
	 * 构造一个okhttp3 的 client builder
	 * @param trustAllSimpleHttps  是否支持https
	 * @return 构造结果 OkHttpClient.Builder
	 */
	private static OkHttpClient.Builder newClientBuilder(boolean trustAllSimpleHttps) {
		OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

		builder
				// 是否开启请求的重试
				.retryOnConnectionFailure(false)
				// 连接池
				.connectionPool(new ConnectionPool(properties.getMaxIdleConnections(), properties.getKeepAliveDuration(), TimeUnit.SECONDS))
				// 连接超时
				.connectTimeout(properties.getConnectTimeout(), TimeUnit.SECONDS)
				// 读超时
				.readTimeout(properties.getReadTimeout(), TimeUnit.SECONDS)
				// 写超时
				.writeTimeout(properties.getWriteTimeout(), TimeUnit.SECONDS)
				// 设置代理
//            	.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
				// 拦截器
                .addInterceptor(createHttpLoggingInterceptor())
				.addNetworkInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
						return chain.proceed(request);
					}
				});

		// 简单的构造一个 sslSocketFactory, X509TrustManager
		if(trustAllSimpleHttps) {
			// X509TrustManager
			X509TrustManager trustManager = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					log.debug("checkClientTrusted");
				}

				@Override
				public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					log.debug("checkServerTrusted");
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			};

			builder
					// sslSocketFactory
					.sslSocketFactory(createSSLSocketFactory(trustManager), trustManager)
					// 信任所有 hostnameVerifier
//				    .hostnameVerifier((hostname, session) -> true )
					// 信任 hostnameVerifier
					.hostnameVerifier((hostname, session) -> hostname.equalsIgnoreCase(session.getPeerHost()) );
		}

		return builder;
	}

	/**
	 * OKHttp 配置属性
	 */
	public static class OkHttpProperties {
		/**
		 * 连接超时，默认 10 秒，0 表示没有超时限制
		 */
		private Integer connectTimeout = 10;

		/**
		 * 响应超时，默认 10 秒，0 表示没有超时限制
		 */
		private Integer readTimeout = 10;

		/**
		 * 写超时，默认 10 秒，0 表示没有超时限制
		 */
		private Integer writeTimeout = 10;

		/**
		 * 连接池中整体的空闲连接的最大数量，默认 5 个连接数
		 */
		private Integer maxIdleConnections = 5;

		/**
		 * 连接空闲时间最大时间，单位秒，默认 300 秒
		 */
		private Long keepAliveDuration = 300L;

		public Integer getConnectTimeout() {
			return connectTimeout;
		}

		public void setConnectTimeout(Integer connectTimeout) {
			this.connectTimeout = connectTimeout;
		}

		public Integer getReadTimeout() {
			return readTimeout;
		}

		public void setReadTimeout(Integer readTimeout) {
			this.readTimeout = readTimeout;
		}

		public Integer getWriteTimeout() {
			return writeTimeout;
		}

		public void setWriteTimeout(Integer writeTimeout) {
			this.writeTimeout = writeTimeout;
		}

		public Integer getMaxIdleConnections() {
			return maxIdleConnections;
		}

		public void setMaxIdleConnections(Integer maxIdleConnections) {
			this.maxIdleConnections = maxIdleConnections;
		}

		public Long getKeepAliveDuration() {
			return keepAliveDuration;
		}

		public void setKeepAliveDuration(Long keepAliveDuration) {
			this.keepAliveDuration = keepAliveDuration;
		}
	}
}
