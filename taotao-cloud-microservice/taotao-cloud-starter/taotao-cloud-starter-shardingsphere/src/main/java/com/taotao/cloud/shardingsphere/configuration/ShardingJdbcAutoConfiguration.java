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
package com.taotao.cloud.shardingsphere.configuration;

import com.taotao.cloud.common.constant.StarterName;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.shardingsphere.algorithm.DataSourceShardingAlgorithm;
import com.taotao.cloud.shardingsphere.properties.ShardingJdbcProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

/**
 * ShardingJdbcConfiguration
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-07 20:54:47
 */
@AutoConfiguration
@EnableConfigurationProperties({ShardingJdbcProperties.class})
@ConditionalOnProperty(prefix = ShardingJdbcProperties.PREIX, name = "enabled", havingValue = "true")
public class ShardingJdbcAutoConfiguration implements
	ApplicationContextAware, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		LogUtil.started(ShardingJdbcAutoConfiguration.class, StarterName.SHARDINGSPHERE_STARTER);
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		//super.setEnvironment(context.getEnvironment());
	}

	//@ConstructorProperties({
	//	"shardingRule",
	//	"masterSlaveRule",
	//	"encryptRule",
	//	"shadowRule",
	//	"props"
	//})
	//public ShardingJdbcConfiguration(
	//	SpringBootShardingRuleConfigurationProperties shardingRule,
	//	SpringBootMasterSlaveRuleConfigurationProperties masterSlaveRule,
	//	SpringBootEncryptRuleConfigurationProperties encryptRule,
	//	SpringBootShadowRuleConfigurationProperties shadowRule,
	//	SpringBootPropertiesConfigurationProperties props) {
	//	super(shardingRule, masterSlaveRule, encryptRule, shadowRule, props);
	//}

	@Bean
	public DataSourceShardingAlgorithm dataSourceShardingAlgorithm() {
		return new DataSourceShardingAlgorithm();
	}

}
