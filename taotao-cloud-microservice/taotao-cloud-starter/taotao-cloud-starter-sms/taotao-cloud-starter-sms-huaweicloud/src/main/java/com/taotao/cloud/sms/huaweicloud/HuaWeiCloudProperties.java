/*
 * Copyright (c) 2018-2022 the original author or authors.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.sms.huaweicloud;

import com.taotao.cloud.sms.common.model.AbstractHandlerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 华为云短信配置
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-27 17:50:49
 */
@RefreshScope
@ConfigurationProperties(prefix = HuaWeiCloudProperties.PREFIX)
public class HuaWeiCloudProperties extends AbstractHandlerProperties<String> {

	public static final String PREFIX = "taotao.cloud.sms.huawei";

	/**
	 * 请求地址
	 */
	private String uri;

	/**
	 * APP_Key
	 */
	private String appKey;

	/**
	 * APP_Secret
	 */
	private String appSecret;

	/**
	 * 国内短信签名通道号或国际/港澳台短信通道号
	 */
	private String sender;

	/**
	 * 签名名称
	 */
	private String signature;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
