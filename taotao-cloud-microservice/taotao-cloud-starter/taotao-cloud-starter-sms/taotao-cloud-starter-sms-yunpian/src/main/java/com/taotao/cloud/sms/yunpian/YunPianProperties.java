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
package com.taotao.cloud.sms.yunpian;

import com.taotao.cloud.sms.common.model.AbstractHandlerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 云片网短信配置
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-27 17:52:31
 */
@RefreshScope
@ConfigurationProperties(prefix = YunPianProperties.PREFIX)
public class YunPianProperties extends AbstractHandlerProperties<String> {
	public static final String PREFIX = "taotao.cloud.sms.yunpian";

	/**
	 * apikey
	 */
	private String apikey;

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}
}
