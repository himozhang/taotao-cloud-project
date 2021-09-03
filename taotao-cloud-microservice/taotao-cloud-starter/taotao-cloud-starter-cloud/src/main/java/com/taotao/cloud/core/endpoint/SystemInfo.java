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
package com.taotao.cloud.core.endpoint;

import org.springframework.boot.actuate.endpoint.jmx.annotation.JmxEndpoint;

/**
 * SystemInfo 
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 21:06:46
 */
public class SystemInfo implements SystemInfoMBean {

	@Override
	public int getCpuCore() {
		return Runtime.getRuntime().availableProcessors();
	}
	@Override
	public long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	@Override
	public void shutdown() {
		System.exit(0);
	}
}
