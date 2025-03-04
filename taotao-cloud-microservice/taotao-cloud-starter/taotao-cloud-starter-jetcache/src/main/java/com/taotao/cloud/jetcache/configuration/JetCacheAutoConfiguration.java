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
package com.taotao.cloud.jetcache.configuration;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.support.SpringConfigProvider;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.jetcache.enhance.JetcacheCacheManager;
import com.taotao.cloud.jetcache.enhance.JetCacheCreateCacheFactory;
import com.taotao.cloud.jetcache.properties.JetCacheProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

/**
 * jetcache 配置
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-03 09:50:26
 */
@EnableCreateCacheAnnotation
@AutoConfiguration(after = com.alicp.jetcache.autoconfigure.JetCacheAutoConfiguration.class)
@EnableConfigurationProperties(JetCacheProperties.class)
//@EnableMethodCache(basePackages = {"com.taotao.cloud.*.biz.service.impl", "com.taotao.cloud.captcha.support"})
@ConditionalOnProperty(prefix = JetCacheProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class JetCacheAutoConfiguration implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Bean
	public JetCacheCreateCacheFactory jetCacheCreateCacheFactory(
		SpringConfigProvider springConfigProvider) {
		JetCacheCreateCacheFactory factory = new JetCacheCreateCacheFactory(springConfigProvider);
		LogUtil.info("Bean [Jet Cache Create Cache Factory] Auto Configure.");
		return factory;
	}

	@Bean(name = "jetCacheCacheManager")
	@ConditionalOnMissingBean
	public CacheManager jetCacheCacheManager(
		JetCacheCreateCacheFactory jetCacheCreateCacheFactory, JetCacheProperties cacheProperties) {
		JetcacheCacheManager jetcacheCacheManager = new JetcacheCacheManager(
			jetCacheCreateCacheFactory, cacheProperties);
		jetcacheCacheManager.setAllowNullValues(cacheProperties.getAllowNullValues());
		LogUtil.info("Bean [Jet Cache  Cache Manager] Auto Configure.");
		return jetcacheCacheManager;
	}


}
