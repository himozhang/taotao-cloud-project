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
package com.taotao.cloud.web.configuration;

import com.taotao.cloud.common.constant.StarterName;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.redis.repository.RedisRepository;
import com.taotao.cloud.web.limit.GuavaLimitAspect;
import com.taotao.cloud.web.limit.LimitAspect;
import com.taotao.cloud.web.properties.LimitProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * LimitConfiguration
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 21:28:08
 */
@AutoConfiguration
@EnableConfigurationProperties({LimitProperties.class})
@ConditionalOnProperty(prefix = LimitProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class LimitAutoConfiguration implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		LogUtil.started(LimitAutoConfiguration.class, StarterName.WEB_STARTER);
	}

	@Bean
	@ConditionalOnBean({RedisRepository.class})
	public LimitAspect limitAspect(RedisRepository redisRepository) {
		return new LimitAspect(redisRepository);
	}

	@Bean
	@ConditionalOnClass({com.google.common.util.concurrent.RateLimiter.class})
	public GuavaLimitAspect guavaLimitAspect(){
		return new GuavaLimitAspect();
	}
}
