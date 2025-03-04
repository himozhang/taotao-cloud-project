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
package com.taotao.cloud.logger.configuration;

import com.taotao.cloud.common.constant.StarterName;
import com.taotao.cloud.common.support.factory.YamlPropertySourceFactory;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.yomahub.tlog.springboot.lifecircle.TLogPropertyConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * LogbackAccessConfiguration
 *
 * @author shuigedeng
 * @version 2022.03
 * @since 2020/4/30 10:21
 */
@AutoConfiguration(before = TLogPropertyConfiguration.class)
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:tlog.yml")
public class TlogAutoConfiguration implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		LogUtil.started(TlogAutoConfiguration.class, StarterName.LOG_STARTER);
	}

}

