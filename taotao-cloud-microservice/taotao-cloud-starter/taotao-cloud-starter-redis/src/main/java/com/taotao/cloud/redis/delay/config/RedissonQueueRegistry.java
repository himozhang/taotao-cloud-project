package com.taotao.cloud.redis.delay.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RedissonQueueRegistry {

	private final Map<String, QueueRegistryInfo> registryInfoContainer = new ConcurrentHashMap<>(8);

	protected void registerQueueInfo(String queueName, QueueRegistryInfo queueInfo) {
		if (queueInfo == null) {
			return;
		}
		this.registryInfoContainer.put(queueName, queueInfo);
	}

	public QueueRegistryInfo getRegistryInfo(String queueName) {
		return this.registryInfoContainer.get(queueName);
	}

}
