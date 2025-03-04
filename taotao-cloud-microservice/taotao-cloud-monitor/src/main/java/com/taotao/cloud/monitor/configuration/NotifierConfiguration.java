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
package com.taotao.cloud.monitor.configuration;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.common.utils.date.DateUtil;
import com.taotao.cloud.dingtalk.entity.DingerRequest;
import com.taotao.cloud.dingtalk.enums.MessageSubType;
import com.taotao.cloud.dingtalk.model.DingerSender;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

/**
 * NotifierConfiguration
 *
 * @author shuigedeng
 * @version 2022.03
 * @since 2021/12/01 10:01
 */
@Configuration
public class NotifierConfiguration {

	@Bean
	public DingDingNotifier dingDingNotifier(InstanceRepository repository) {
		return new DingDingNotifier(repository);
	}

	public static class DingDingNotifier extends AbstractStatusChangeNotifier {

		private final String[] ignoreChanges = new String[]{"UNKNOWN:UP", "DOWN:UP", "OFFLINE:UP"};

		@Autowired
		private DingerSender sender;

		public DingDingNotifier(InstanceRepository repository) {
			super(repository);
		}

		@Override
		protected boolean shouldNotify(InstanceEvent event, Instance instance) {
			if (!(event instanceof InstanceStatusChangedEvent)) {
				return false;
			} else {
				InstanceStatusChangedEvent statusChange = (InstanceStatusChangedEvent) event;
				String from = this.getLastStatus(event.getInstance());
				String to = statusChange.getStatusInfo().getStatus();
				return Arrays.binarySearch(this.ignoreChanges, from + ":" + to) < 0
					&& Arrays.binarySearch(this.ignoreChanges, "*:" + to) < 0
					&& Arrays.binarySearch(this.ignoreChanges, from + ":*") < 0;
			}
		}

		@Override
		protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
			String serviceName = instance.getRegistration().getName();
			String serviceUrl = instance.getRegistration().getServiceUrl();

			StringBuilder str = new StringBuilder();
			str.append("taotaocloud微服务监控 \n");
			str.append("[时间戳]: ").append(DateUtil.format(LocalDateTime.now(), DateUtil.DEFAULT_DATE_TIME_FORMAT)).append("\n");
			str.append("[服务名] : ").append(serviceName).append("\n");
			str.append("[服务ip]: ").append(serviceUrl).append("\n");

			return Mono.fromRunnable(() -> {
				if (event instanceof InstanceStatusChangedEvent) {
					String status = ((InstanceStatusChangedEvent) event).getStatusInfo()
						.getStatus();
					switch (status) {
						// 健康检查没通过
						case "DOWN" -> str.append("[服务状态]: ").append(status).append("(")
							.append("健康检未通过").append(")").append("\n");

						// 服务离线
						case "OFFLINE" -> str.append("[服务状态]: ").append(status).append("(")
							.append("服务离线").append(")").append("\n");

						//服务上线
						case "UP" -> str.append("[服务状态]: ").append(status).append("(")
							.append("服务上线").append(")").append("\n");

						// 服务未知异常
						case "UNKNOWN" -> str.append("[服务状态]: ").append(status).append("(")
							.append("服务未知异常").append(")").append("\n");

						default -> str.append("[服务状态]: ").append(status).append("(")
							.append("服务未知异常").append(")").append("\n");
					}

					Map<String, Object> details = ((InstanceStatusChangedEvent) event).getStatusInfo()
						.getDetails();
					str.append("[服务详情]: ").append(JsonUtil.toJSONString(details));

					sender.send(
						MessageSubType.TEXT,
						DingerRequest.request(str.toString()));
				}
			});
		}
	}
}
