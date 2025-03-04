package com.taotao.cloud.payment.biz.kit.plugin.alipay;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.sys.api.enums.SettingEnum;
import com.taotao.cloud.sys.api.web.vo.setting.payment.AlipayPaymentSetting;

import java.util.Date;

/**
 * AliPayApiConfigKit
 *
 */
public class AliPayApiConfigKit {

    /**
     * 支付配置
     */
    static DefaultAlipayClient defaultAlipayClient;

    /**
     * 下次刷新时间
     */
    static Date nextRebuildDate;

    /**
     * 间隔时间
     */
    static Long refreshInterval = 1000 * 60 * 3L;

    /**
     * 获取支付宝支付参数
     *
     * @return
     * @throws AlipayApiException
     */
    public static synchronized DefaultAlipayClient getAliPayApiConfig() throws AlipayApiException {
        Date date = new Date();
        //如果过期，则重新构建
        if (nextRebuildDate == null || date.after(nextRebuildDate)) {
            return rebuild();
        }
        return defaultAlipayClient;
    }

    static DefaultAlipayClient rebuild() throws AlipayApiException {
        AlipayPaymentSetting setting;
        try {
            SettingService settingService = (SettingService) SpringContextUtil.getBean("settingServiceImpl");
            Setting systemSetting = settingService.get(SettingEnum.ALIPAY_PAYMENT.name());
            setting = JSONUtil.toBean(systemSetting.getSettingValue(), AlipayPaymentSetting.class);
        } catch (Exception e) {
            throw new BusinessException(ResultEnum.PAY_NOT_SUPPORT);
        }

        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");
        certAlipayRequest.setFormat("json");
        certAlipayRequest.setCharset("utf-8");
        certAlipayRequest.setSignType("RSA2");
        certAlipayRequest.setAppId(setting.getAppId());
        certAlipayRequest.setPrivateKey(setting.getPrivateKey());
        certAlipayRequest.setCertPath(setting.getCertPath());
        certAlipayRequest.setAlipayPublicCertPath(setting.getAlipayPublicCertPath());
        certAlipayRequest.setRootCertPath(setting.getRootCertPath());
        defaultAlipayClient = new DefaultAlipayClient(certAlipayRequest);
        nextRebuildDate = DateUtil.date(System.currentTimeMillis()+ refreshInterval);
        return defaultAlipayClient;
    }
}
