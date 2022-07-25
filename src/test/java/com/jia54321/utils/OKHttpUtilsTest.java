package com.jia54321.utils;

import org.junit.Test;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

public class OKHttpUtilsTest {
    /**
     * 杭州城市大脑 对接服务商（创泰科技停车云 ）ChuangTaiApi 参见接口文档V2.1
     * api:场库系统 --> 城市大脑停车系统
     * mq: 城市大脑停车系统 --> 通过定义对应的 topic 主题给车场消费
     */
    class ChuangTaiProperties {
        /** baseUrl */
        String baseUrl = "http://220.191.209.248:9100";

        /**  mq: 01. 车辆签约信息下发(MQ:departurePayInfo) Topic : hzcity/v2/departurePayInfo/{accessID}/{parkingCode} */
        String departurePayInfo = "hzcity/v2/departurePayInfo/{accessID}/{parkingCode}";

        /** api: 02. 车辆签约信息确认(confirmDeparturePayInfo)  */
        String confirmDeparturePayInfo = "${baseUrl}/api/v2/cp/confirmDeparturePayInfo";

        /** api: 03. 车辆签约信息查询(getDeparturePayInfo) */
        String getDeparturePayInfo = "${baseUrl}/api/v2/cp/getDeparturePayInfo";

        /** api: 04. 停车场申请支付扣款(applyPayment) */
        String applyPayment = getBaseUrl() + "/api/v2/cp/applyPayment";

        /**  mq: 05. 车辆账单信息查询(MQ:fee)  Topic : hzcity/v2/fee/{accessID}/{parkingCode} */
        String fee = "hzcity/v2/fee/{accessID}/{parkingCode}";

        /** api: 06. 车辆账单信息上报(uploadFee) */
        String uploadFee = getBaseUrl() + "/api/v2/cp/uploadFee";

        /**  mq: 07. 账单支付扣费结果通知(MQ:payResult) */
        String payResult = "hzcity/v2/payResult/{accessID}/{parkingCode}";

        /** api: 08. 账单支付扣费结果确认(confirmPayResult) */
        String confirmPayResult = getBaseUrl() + "/api/v2/cp/confirmPayResult";

        /** api: 09. 账单支付扣费结果查询(getPayResult) */
        String getPayResult = getBaseUrl() + "/api/v2/cp/getPayResult";

        /** api: 10. 异常账单上报(uploadAbnormalBill)  */
        String uploadAbnormalBill = getBaseUrl() + "/api/v2/cp/uploadAbnormalBill";

        /** api: 11. 车场先离后缴账单按天对账(checkDailyBill) */
        String checkDailyBill = getBaseUrl() + "/api/v2/cp/checkDailyBill";

        public ChuangTaiProperties(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }
    @Test
    public void execute() throws IOException {
//        okhttp3.Request request = new okhttp3.Request.Builder().url(ChuangTaiProperties.getDeparturePayInfo)
//                .post(okhttp3.RequestBody.create(OKHttpUtils.JSON, ""))
//                .build();
//        okhttp3.Response response = OKHttpUtils.execute(request);
    }

    @Test
    public void executePost() {
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${","}");

        ChuangTaiProperties chuangTaiProperties = new ChuangTaiProperties("http://localhost:8080");
        Map<String, String> map = Collections.singletonMap("baseUrl","http://localhost:8080");
//        Kv.init().set("${baseUrl}","http://localhost:8080")
        System.out.println( helper.replacePlaceholders(chuangTaiProperties.getDeparturePayInfo, map::get));
        map = Collections.singletonMap("baseUrl","http://localhost:8081");
        System.out.println( helper.replacePlaceholders(chuangTaiProperties.getDeparturePayInfo, map::get));
    }

    @Test
    public void enqueue() {
    }

    @Test
    public void testEnqueue() {
    }
}
