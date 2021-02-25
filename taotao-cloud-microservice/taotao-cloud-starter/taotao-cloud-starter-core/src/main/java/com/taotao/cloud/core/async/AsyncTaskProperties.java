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
package com.taotao.cloud.core.async;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 异步任务Properties
 *
 * @author dengtao
 * @version 1.0.0
 * @since 2020/7/24 08:22
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "taotao.cloud.core.async.task")
public class AsyncTaskProperties {

	/**
	 * 线程池维护线程的最小数量
	 */
	private int corePoolSize = 10;

	/**
	 * 线程池维护线程的最大数量
	 */
	private int maxPoolSiz = 200;

	/**
	 * 队列最大长度
	 */
	private int queueCapacity = 300;

	/**
	 * 线程池前缀
	 */
	private String threadNamePrefix = "taotao-cloud-executor-";
}
