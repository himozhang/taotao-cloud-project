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
package com.taotao.cloud.dubbo.monitor;

import com.taotao.cloud.common.utils.log.LogUtil;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.config.spring.context.DubboBootstrapStartStopListenerSpringAdapter;
import org.apache.dubbo.monitor.Monitor;
import org.apache.dubbo.monitor.support.AbstractMonitorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义监控工厂
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-08 10:19:23
 */
public class CustomMonitorFactory extends AbstractMonitorFactory {
	@Override
	protected Monitor createMonitor(URL url) {
		LogUtil.info("Dubbo CustomMonitorFactory getExecutor activate ------------------------------");
		LogUtil.info(url.toFullString());
		return new CustomMonitor();
	}


	public static class CustomMonitor implements Monitor {

		@Override
		public URL getUrl() {
			return null;
		}

		@Override
		public boolean isAvailable() {
			return false;
		}

		@Override
		public void destroy() {

		}

		@Override
		public void collect(URL statistics) {
			LogUtil.info("CustomMonitor collect activate ------------------------------");
			LogUtil.info(statistics.toFullString());
		}

		@Override
		public List<URL> lookup(URL query) {
			LogUtil.info("CustomMonitor lookup activate ------------------------------");
			LogUtil.info(query.toFullString());

			return new ArrayList<>();
		}
	}
}
