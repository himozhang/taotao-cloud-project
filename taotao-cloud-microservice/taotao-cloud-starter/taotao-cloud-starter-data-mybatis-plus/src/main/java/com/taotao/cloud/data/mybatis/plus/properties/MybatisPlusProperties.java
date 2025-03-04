/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.taotaocloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.data.mybatis.plus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * MybatisPlusAutoFillProperties
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-04 07:44:25
 */
@RefreshScope
@ConfigurationProperties(prefix = MybatisPlusProperties.PREFIX)
public class MybatisPlusProperties {

	public static final String PREFIX = "taotao.cloud.data.mybatis-plus";

	private Boolean enabled = false;

	private Boolean sqlLogEnable = true;

	private Boolean sqlCollectorEnable = true;

	private Boolean cipherEnable = true;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getSqlLogEnable() {
		return sqlLogEnable;
	}

	public void setSqlLogEnable(Boolean sqlLogEnable) {
		this.sqlLogEnable = sqlLogEnable;
	}

	public Boolean getSqlCollectorEnable() {
		return sqlCollectorEnable;
	}

	public void setSqlCollectorEnable(Boolean sqlCollectorEnable) {
		this.sqlCollectorEnable = sqlCollectorEnable;
	}

	public Boolean getCipherEnable() {
		return cipherEnable;
	}

	public void setCipherEnable(Boolean cipherEnable) {
		this.cipherEnable = cipherEnable;
	}
}
