/*
 * Copyright 2002-2021 the original author or authors.
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
package com.taotao.cloud.web;

import com.taotao.cloud.core.properties.AsyncProperties;
import com.taotao.cloud.web.properties.DozerProperties;
import com.taotao.cloud.web.properties.EncryptProperties;
import com.taotao.cloud.web.properties.FilterProperties;
import com.taotao.cloud.web.properties.IdempotentProperties;
import com.taotao.cloud.web.properties.LimitProperties;
import com.taotao.cloud.web.properties.XssProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * PropertiesAutoConfiguration
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-03 08:03:14
 */
@EnableConfigurationProperties({
		FilterProperties.class,
		AsyncProperties.class,
		DozerProperties.class,
		IdempotentProperties.class,
		XssProperties.class,
		LimitProperties.class,
		EncryptProperties.class
})
public class PropertiesAutoConfiguration {

}
