package com.jia54321.utils;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 终极修复版OkHttp工具类（适配OkHttp3.14.9+无任何编译报错+全功能保留+高易用性）
 * 核心能力：GET/POST请求、加解密注入、设备注册适配、实例化成员变量、多实例隔离、智能重试、HTTPS支持
 * 依赖：okhttp3.14.9 + fastjson1.2.83 + commons-codec1.15 + slf4j-api1.7.36
 */
public class OkHttpUtils {
    // 全局常量（极简命名，直观可用）
    private static final Logger log = LoggerFactory.getLogger(OkHttpUtils.class);
    public static final MediaType JSON = MediaType.parse("application/json;charset=UTF-8");

    // 实例独立核心组件（保留多实例隔离能力）
    private final Config config;
    private final Ext ext;
    private OkHttpClient httpClient;
    private OkHttpClient httpsClient;

    // ====================== 构造方法（极简，支持3种实例化方式） ======================
    public OkHttpUtils() {
        this.config = new Config();
        this.ext = new DefaultExt();
        initClient();
    }

    public OkHttpUtils(Config config) {
        this.config = Objects.requireNonNull(config, "配置不能为空");
        this.ext = new DefaultExt();
        initClient();
    }

    public OkHttpUtils(Config config, Ext ext) {
        this.config = Objects.requireNonNull(config, "配置不能为空");
        this.ext = Objects.requireNonNull(ext, "扩展实现不能为空");
        initClient();
    }

    // 初始化HTTP/HTTPS客户端（核心修复：适配OkHttp3.14.9 Builder包私有问题）
    private void initClient() {
        // 1. 构建HTTP客户端：直接创建新Builder，逐行配置通用参数（无任何包私有依赖）
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(config.maxIdleConn, config.keepAliveSec, TimeUnit.SECONDS))
                .connectTimeout(config.connectSec, TimeUnit.SECONDS)
                .readTimeout(config.readSec, TimeUnit.SECONDS)
                .writeTimeout(config.writeSec, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(buildLogInterceptor());
        this.httpClient = ext.customClient(httpBuilder, false).build();

        // 2. 构建HTTPS客户端：重新创建新Builder+通用配置，避免Builder拷贝（核心修复）
        OkHttpClient.Builder httpsBuilder = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(config.maxIdleConn, config.keepAliveSec, TimeUnit.SECONDS))
                .connectTimeout(config.connectSec, TimeUnit.SECONDS)
                .readTimeout(config.readSec, TimeUnit.SECONDS)
                .writeTimeout(config.writeSec, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(buildLogInterceptor());
        // 扩展定制+HTTPS配置（默认全信任，生产可重写）
        this.httpsClient = ext.customHttps(ext.customClient(httpsBuilder, true)).build();
    }

    // 重新初始化客户端（配置/扩展修改后调用，立即生效）
    public void reInit() {
        initClient();
        log.info("OkHttp客户端已重新初始化，新配置生效");
    }

    // 构建日志拦截器（内部逻辑）
    private Interceptor buildLogInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(msg -> {
            if (log.isDebugEnabled()) log.debug("OkHttp日志：{}", msg);
        });
        interceptor.setLevel(log.isDebugEnabled() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }

    // ====================== 核心请求方法（极简命名+重载适配+全功能保留） ======================
    // GET请求4个重载（从简到繁，满足所有场景）
    public String get(String url) {
        return get(url, null, "");
    }

    public String get(String url, Map<String, String> params) {
        return get(url, params, "");
    }

    public String get(String url, Map<String, String> params, String desc) {
        return get(url, params, desc, config.retryCount, config.retryStrategy);
    }

    public String get(String url, Map<String, String> params, String desc, int retryCount, RetryStrategy retryStrategy) {
        if (url == null || url.trim().isEmpty()) {
            log.error("GET失败 | {} | URL不能为空", buildDesc(desc));
            return "";
        }
        try {
            String finalUrl = buildUrl(url, params);
            Request request = ext.customGet(new Request.Builder().url(finalUrl), finalUrl, params)
                    .get().build();
            request = ext.beforeRequest(request, desc);
            return execute(request, desc, retryCount, retryStrategy);
        } catch (Exception e) {
            log.error("GET构建失败 | {}", buildDesc(desc), e);
            return "";
        }
    }

    // POST请求4个重载（JSON体，和GET调用方式完全一致）
    public String post(String url) {
        return post(url, null, "");
    }

    public String post(String url, Map<String, Object> params) {
        return post(url, params, "");
    }

    public String post(String url, Map<String, Object> params, String desc) {
        return post(url, params, desc, config.retryCount, config.retryStrategy);
    }

    public String post(String url, Map<String, Object> params, String desc, int retryCount, RetryStrategy retryStrategy) {
        if (url == null || url.trim().isEmpty()) {
            log.error("POST失败 | {} | URL不能为空", buildDesc(desc));
            return "";
        }
        try {
            String json = JsonUtil.INSTANCE.toJson(params == null ? new HashMap<>() : params);
            RequestBody body = RequestBody.create(JSON, json);
            long contentLen = ext.calcContentLength(body);
            Request.Builder builder = ext.customPost(new Request.Builder().url(url), url, params, json)
                    .post(body);
            if (contentLen > 0) builder.header("Content-Length", String.valueOf(contentLen));
            Request request = ext.beforeRequest(builder.build(), desc);
            return execute(request, desc, retryCount, retryStrategy);
        } catch (Exception e) {
            log.error("POST构建失败 | {}", buildDesc(desc), e);
            return "";
        }
    }

    // ====================== 核心执行逻辑（内部封装，无冗余命名） ======================
    private String execute(Request request, String desc, int retryCount, RetryStrategy retryStrategy) {
        int maxRetry = Math.max(0, retryCount);
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        String standardDesc = buildDesc(desc);
        String method = request.method();
        String url = request.url().toString();

        for (int attempt = 0; attempt <= maxRetry; attempt++) {
            Response response = null;
            try {
                ext.beforeExecute(request, attempt + 1, maxRetry);
                OkHttpClient client = request.isHttps() ? httpsClient : httpClient;
                response = client.newCall(request).execute();
                long cost = System.currentTimeMillis() - startTime.get();

                String rawBody = Objects.requireNonNull(response.body()).string();
                String result = ext.afterResponse(rawBody, response, standardDesc);

                if (response.isSuccessful()) {
                    log.info("{}成功 | {} | URL：{} | 尝试：{} | 耗时：{}ms | 状态码：{}",
                            method, standardDesc, url, attempt + 1, cost, response.code());
                    log.debug("{}结果 | {} | 内容：{}", method, standardDesc, result);
                    return result;
                } else {
                    log.warn("{}失败 | {} | URL：{} | 尝试：{} | 耗时：{}ms | 状态码：{} | 原因：{}",
                            method, standardDesc, url, attempt + 1, cost, response.code(), result);
                }
            } catch (Exception e) {
                String errorMsg = ext.handleException(e, request, attempt + 1, System.currentTimeMillis() - startTime.get());
                if (attempt == maxRetry) {
                    log.error("{}最终失败 | {} | 最后尝试：{} | 异常：{}", method, standardDesc, attempt + 1, errorMsg, e);
                } else {
                    log.warn("{}异常，准备重试 | {} | 尝试：{} | 原因：{}", method, standardDesc, attempt + 1, errorMsg);
                }
            } finally {
                if (response != null) response.close();
                ext.afterExecute(request, response, attempt + 1, maxRetry);
            }

            if (attempt < maxRetry) {
                long sleep = calcRetrySleep(attempt, maxRetry, retryStrategy);
                try {
                    log.info("重试等待 | {} | 下次尝试：{} | 等待：{}ms", standardDesc, attempt + 2, sleep);
                    Thread.sleep(sleep);
                } catch (InterruptedException ie) {
                    log.warn("重试等待被中断 | {}", standardDesc);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        log.error("所有重试失败 | {} | {} | URL：{}", standardDesc, method, url);
        return "";
    }

    // ====================== 私有工具方法（内部封装，外部无感知） ======================
    // 拼接URL参数，自动UTF-8编码
    private String buildUrl(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) return url;
        StringBuilder sb = new StringBuilder(url).append(url.contains("?") ? "&" : "?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (k == null || k.trim().isEmpty()) continue;
            try {
                sb.append(URLEncoder.encode(k, StandardCharsets.UTF_8.name()))
                        .append("=")
                        .append(v == null ? "" : URLEncoder.encode(v, StandardCharsets.UTF_8.name()))
                        .append("&");
            } catch (Exception e) {
                log.warn("URL参数编码失败，跳过 | 键：{}", k);
            }
        }
        if (sb.charAt(sb.length() - 1) == '&') sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    // 构建标准化日志描述
    private String buildDesc(String desc) {
        return (desc == null || desc.trim().isEmpty()) ? "desc=无描述" : "desc=" + desc.trim();
    }

    // 计算重试等待时间（指数退避/递减）
    private long calcRetrySleep(int currentAttempt, int maxRetry, RetryStrategy strategy) {
        long base = config.baseRetryMs;
        long max = config.maxRetryMs;
        if (strategy == RetryStrategy.EXPONENTIAL) {
            long sleep = base * (1L << currentAttempt);
            return Math.min(sleep, max);
        } else {
            long step = base / maxRetry;
            long sleep = base - (step * currentAttempt);
            return Math.max(sleep, 100);
        }
    }

    // ====================== 内部类1：重试策略（极简枚举） ======================
    public enum RetryStrategy {
        EXPONENTIAL, // 指数退避
        LINEAR       // 线性递减
    }

    // ====================== 内部类2：配置类（极简字段，直接赋值） ======================
    public static class Config {
        // 超时配置（秒，默认10）
        public int connectSec = 10;
        public int readSec = 10;
        public int writeSec = 10;
        // 连接池配置
        public int maxIdleConn = 5;
        public long keepAliveSec = 300;
        // 重试配置
        public int retryCount = 3;
        public long baseRetryMs = 1000;
        public long maxRetryMs = 5000;
        public RetryStrategy retryStrategy = RetryStrategy.EXPONENTIAL;
    }

    // ====================== 内部类3：扩展接口（极简方法，修复参数冗余） ======================
    public interface Ext {
        // 自定义HTTP/HTTPS客户端构建
        OkHttpClient.Builder customClient(OkHttpClient.Builder builder, boolean isHttps);

        // 自定义HTTPS配置（默认全信任，生产重写）
        OkHttpClient.Builder customHttps(OkHttpClient.Builder builder);

        // 自定义GET请求
        Request.Builder customGet(Request.Builder builder, String url, Map<String, String> params);

        // 自定义POST请求
        Request.Builder customPost(Request.Builder builder, String url, Map<String, Object> params, String json);

        // 自定义异常处理
        String handleException(Exception e, Request request, int attempt, long cost);

        // 请求生命周期回调
        void beforeExecute(Request request, int attempt, int maxRetry);
        void afterExecute(Request request, Response response, int attempt, int maxRetry);

        // 核心加解密注入点（全保留，默认空实现）
        default Request beforeRequest(Request request, String desc) throws Exception {
            return request;
        }

        default String afterResponse(String rawBody, Response response, String desc) throws Exception {
            return rawBody;
        }

        /**
         * 计算Content-Length（修复：移除冗余Request参数，简化逻辑）
         */
        default long calcContentLength(RequestBody body) {
            if (body == null) return -1;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedSink sink = null;
            try {
                sink = Okio.buffer(Okio.sink(bos));
                body.writeTo(sink);
                sink.flush();
                return bos.toByteArray().length;
            } catch (Exception e) {
                log.warn("计算Content-Length失败，由OkHttp自动处理", e);
                return -1;
            } finally {
                try {
                    if (sink != null) sink.close();
                    bos.close();
                } catch (IOException e) {
                    log.error("关闭流失败", e);
                }
            }
        }
    }

    // ====================== 内部类4：默认扩展实现（兜底，可继承重写） ======================
    public static class DefaultExt implements Ext {
        @Override
        public OkHttpClient.Builder customClient(OkHttpClient.Builder builder, boolean isHttps) {
            return builder;
        }

        @Override
        public OkHttpClient.Builder customHttps(OkHttpClient.Builder builder) {
            try {
                X509TrustManager trustManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                    @Override
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                };
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
                builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                        .hostnameVerifier((h, s) -> true);
                log.warn("HTTPS默认全信任模式，生产环境请重写customHttps配置证书");
            } catch (Exception e) {
                log.error("HTTPS全信任配置失败", e);
            }
            return builder;
        }

        @Override
        public Request.Builder customGet(Request.Builder builder, String url, Map<String, String> params) {
            return builder;
        }

        @Override
        public Request.Builder customPost(Request.Builder builder, String url, Map<String, Object> params, String json) {
            return builder.header("Content-Type", JSON.toString());
        }

        @Override
        public String handleException(Exception e, Request request, int attempt, long cost) {
            return String.format("%s | %s | 尝试：%d | 耗时：%dms | 异常：%s：%s",
                    request.method(), request.url(), attempt, cost, e.getClass().getSimpleName(), e.getMessage());
        }

        @Override
        public void beforeExecute(Request request, int attempt, int maxRetry) {}
        @Override
        public void afterExecute(Request request, Response response, int attempt, int maxRetry) {}
    }

    // ====================== 内部类5：JSON工具（单例，适配设备注册Base64） ======================
    public static class JsonUtil {
        public static final JsonUtil INSTANCE = new JsonUtil();
        private JsonUtil() {}

        public String toJson(Object obj) {
            return obj == null ? "{}" : com.alibaba.fastjson.JSON.toJSONString(obj);
        }

        public <T> T fromJson(String json, Class<T> clazz) {
            return (json == null || json.trim().isEmpty()) ? null : com.alibaba.fastjson.JSON.parseObject(json, clazz);
        }

        public String base64Decode(String str) {
            return (str == null || str.trim().isEmpty()) ? "" : new String(Base64.decodeBase64(str), StandardCharsets.UTF_8);
        }
    }

    // ====================== 实例配置/扩展获取 ======================
    public Config getConfig() {
        return config;
    }

    public Ext getExt() {
        return ext;
    }
}