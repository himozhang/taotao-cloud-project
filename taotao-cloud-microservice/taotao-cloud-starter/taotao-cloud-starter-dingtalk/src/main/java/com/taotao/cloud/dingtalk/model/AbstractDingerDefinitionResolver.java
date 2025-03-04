/*
 * Copyright (c) ©2015-2021 Jaemon. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.dingtalk.model;


import static com.taotao.cloud.dingtalk.enums.ExceptionEnum.DINGER_REPEATED_EXCEPTION;
import static com.taotao.cloud.dingtalk.enums.ExceptionEnum.METHOD_DEFINITION_EXCEPTION;

import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.dingtalk.annatations.AsyncExecute;
import com.taotao.cloud.dingtalk.annatations.DingerConfiguration;
import com.taotao.cloud.dingtalk.constant.DingerConstant;
import com.taotao.cloud.dingtalk.entity.DingerMethod;
import com.taotao.cloud.dingtalk.enums.DingerDefinitionType;
import com.taotao.cloud.dingtalk.enums.DingerType;
import com.taotao.cloud.dingtalk.exception.DingerException;
import com.taotao.cloud.dingtalk.listeners.DingerListenersProperty;
import com.taotao.cloud.dingtalk.utils.DingerUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * AbsDingerDefinitionResolver
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:21:56
 */
public abstract class AbstractDingerDefinitionResolver<T> extends DingerListenersProperty implements
	DingerDefinitionResolver<T> {

	/**
	 * dinger消息类型和对应生成器映射关系
	 */
	private Map<String, Class<? extends DingerDefinitionGenerator>> dingerDefinitionGeneratorMap;
	/**
	 * 方法参数名称解析
	 */
	protected ParameterNameDiscoverer parameterNameDiscoverer;

	public AbstractDingerDefinitionResolver() {
		this.dingerDefinitionGeneratorMap = new HashMap<>();
		this.parameterNameDiscoverer = new DingerParameterNameDiscoverer();

		for (DingerDefinitionType dingerDefinitionType : DingerDefinitionType.dingerDefinitionTypes) {
			dingerDefinitionGeneratorMap.put(
				dingerDefinitionType.dingerType() + DingerConstant.SPOT_SEPERATOR +
					dingerDefinitionType.messageMainType() + DingerConstant.SPOT_SEPERATOR +
					dingerDefinitionType.messageSubType(),
				dingerDefinitionType.dingerDefinitionGenerator()
			);
		}

	}

	protected DingerConfig dingerConfiguration(Class<?> dingerClass) {
		DingerConfig dingerConfig = new DingerConfig();

		if (dingerClass.isAnnotationPresent(DingerConfiguration.class)) {
			DingerConfiguration dingerConfiguration = dingerClass.getAnnotation(
				DingerConfiguration.class);
			String tokenId = dingerConfiguration.tokenId();
			if (DingerUtils.isNotEmpty(tokenId)) {
				dingerConfig.setTokenId(tokenId);
				dingerConfig.setDecryptKey(dingerConfiguration.decryptKey());
				dingerConfig.setSecret(dingerConfiguration.secret());
			}
		}

		if (dingerClass.isAnnotationPresent(AsyncExecute.class)) {
			dingerConfig.setAsyncExecute(true);
		}
		return dingerConfig;
	}


	/**
	 * 注册Dinger Definition
	 *
	 * @param dingerName          dingerName
	 * @param source              source
	 * @param dingerDefinitionKey dingerDefinitionKey
	 * @param dingerConfiguration Dinger层配置DingerConfig
	 * @param dingerMethod        方法参数信息和方法中的泛型信息
	 */
	void registerDingerDefinition(
		String dingerName, Object source,
		String dingerDefinitionKey,
		DingerConfig dingerConfiguration,
		DingerMethod dingerMethod
	) {
		for (DingerType dingerType : enabledDingerTypes) {
			DingerConfig defaultDingerConfig = defaultDingerConfigs.get(dingerType);
			if (dingerConfiguration == null) {
				LogUtil.debug("dinger={} not open and skip the corresponding dinger registration.",
					dingerType);
				continue;
			}
			String keyName = dingerType + DingerConstant.SPOT_SEPERATOR + dingerName;
			String key = dingerType + DingerConstant.SPOT_SEPERATOR + dingerDefinitionKey;
			Class<? extends DingerDefinitionGenerator> dingerDefinitionGeneratorClass =
				dingerDefinitionGeneratorMap.get(key);
			if (dingerDefinitionGeneratorClass == null) {
//                throw new DingerException(ExceptionEnum.DINGERDEFINITIONTYPE_UNDEFINED_KEY, key);
				LogUtil.debug("当前key=%s在DingerDefinitionType中没定义", key);
				continue;
			}

			DingerDefinitionGenerator dingerDefinitionGenerator = DingerDefinitionGeneratorFactory.get(
				dingerDefinitionGeneratorClass.getName()
			);
			DingerDefinition dingerDefinition = dingerDefinitionGenerator.generator(
				new DingerDefinitionGeneratorContext(keyName, source)
			);

			if (dingerDefinition == null) {
				LogUtil.debug("keyName={} dinger[{}] format is illegal.", keyName,
					dingerDefinitionKey);
				continue;
			}

			if (Container.INSTANCE.contains(keyName)) {
				throw new DingerException(DINGER_REPEATED_EXCEPTION, keyName);
			}

			if (dingerMethod.check()) {
				throw new DingerException(METHOD_DEFINITION_EXCEPTION,
					dingerMethod.getMethodName());
			}
			dingerDefinition.setMethodParams(dingerMethod.getMethodParams());
			dingerDefinition.setGenericIndex(dingerMethod.getParamTypes());

			// DingerConfig Priority： `@DingerText | @DingerMarkdown | XML` > `@DingerConfiguration` > `***.yml | ***.properties`
			dingerDefinition.dingerConfig()
				.merge(dingerConfiguration)
				.merge(defaultDingerConfig);

			Container.INSTANCE.put(keyName, dingerDefinition);
			LogUtil.debug("dinger definition={} has been registed.", keyName);
		}
	}

	/**
	 * Container for DingerDefinition
	 */
	protected enum Container {
		INSTANCE;
		private Map<String, DingerDefinition> container;

		Container() {
			this.container = new HashMap<>(128);
		}

		/**
		 * get DingerDefinition
		 *
		 * @param key key
		 * @return
		 */
		DingerDefinition get(String key) {
			return container.get(key);
		}

		/**
		 * set DingerDefinition
		 *
		 * @param key              key
		 * @param dingerDefinition dingerDefinition
		 */
		void put(String key, DingerDefinition dingerDefinition) {
			container.put(key, dingerDefinition);
		}

		/**
		 * whether contains key
		 *
		 * @param key key
		 * @return true or false
		 */
		boolean contains(String key) {
			return container.containsKey(key);
		}
	}

	protected static void clear() {
		Container.INSTANCE.container.clear();
	}
}
