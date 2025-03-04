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
package com.taotao.cloud.core.configuration;

import com.taotao.cloud.common.constant.StarterName;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.core.endpoint.CustomHealthEndPoint;
import com.taotao.cloud.core.endpoint.RequestMappingEndPoint;
import com.taotao.cloud.core.endpoint.indicator.CustomHealthIndicator;
import com.taotao.cloud.core.endpoint.CustomMbeanRegistrar;
import com.taotao.cloud.core.endpoint.mbean.MBeanDemo;
import com.taotao.cloud.core.endpoint.CustomEndPoint;
import com.taotao.cloud.core.properties.EndpointProperties;
import javax.management.MalformedObjectNameException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * EndPointConfiguration
 *
 * @author shuigedeng
 * @version 2022.03
 * @since 2021/04/02 10:25
 */
@AutoConfiguration
@EnableConfigurationProperties({EndpointProperties.class})
@ConditionalOnProperty(prefix = EndpointProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class EndPointAutoConfiguration implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		LogUtil.started(EndPointAutoConfiguration.class, StarterName.CORE_STARTER);
	}

	@Bean
	public CustomHealthIndicator customHealthIndicator() {
		return new CustomHealthIndicator();
	}

	@Bean
	public CustomHealthEndPoint myEndPoint() {
		return new CustomHealthEndPoint();
	}

	@Bean
	public CustomEndPoint taoTaoCloudEndPoint() {
		return new CustomEndPoint();
	}

	@Bean
	public RequestMappingEndPoint requestMappingEndPoint(){
		return new RequestMappingEndPoint();
	}

	@Bean
	public MBeanDemo mBeanDemo() {
		return new MBeanDemo();
	}

	@Bean
	public CustomMbeanRegistrar customMbeanRegistrar() throws MalformedObjectNameException {
		return new CustomMbeanRegistrar();
	}
}
