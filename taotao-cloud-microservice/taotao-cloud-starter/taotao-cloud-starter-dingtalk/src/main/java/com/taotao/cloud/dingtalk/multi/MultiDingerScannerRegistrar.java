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
package com.taotao.cloud.dingtalk.multi;

import static com.taotao.cloud.dingtalk.constant.DingerConstant.SPOT_SEPERATOR;
import static com.taotao.cloud.dingtalk.enums.ExceptionEnum.MULTIDINGER_ALGORITHM_EXCEPTION;
import static com.taotao.cloud.dingtalk.enums.ExceptionEnum.MULTIDINGER_ANNOTATTION_EXCEPTION;
import static com.taotao.cloud.dingtalk.enums.MultiDingerConfigContainer.GLOABL_KEY;

import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.dingtalk.constant.DingerConstant;
import com.taotao.cloud.dingtalk.model.DingerConfig;
import com.taotao.cloud.dingtalk.enums.DingerType;
import com.taotao.cloud.dingtalk.enums.ExceptionEnum;
import com.taotao.cloud.dingtalk.enums.MultiDingerConfigContainer;
import com.taotao.cloud.dingtalk.exception.DingerException;
import com.taotao.cloud.dingtalk.exception.MultiDingerRegisterException;
import com.taotao.cloud.dingtalk.listeners.DingerListenersProperty;
import com.taotao.cloud.dingtalk.annatations.EnableMultiDinger;
import com.taotao.cloud.dingtalk.annatations.MultiDinger;
import com.taotao.cloud.dingtalk.annatations.MultiHandler;
import com.taotao.cloud.dingtalk.entity.MultiDingerAlgorithmDefinition;
import com.taotao.cloud.dingtalk.entity.MultiDingerConfig;
import com.taotao.cloud.dingtalk.utils.DingerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.objenesis.instantiator.util.ClassUtils;


/**
 * MultiDingerScannerRegistrar
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:24:19
 */
public class MultiDingerScannerRegistrar
	extends DingerListenersProperty
	implements ImportBeanDefinitionRegistrar, Ordered {

	/**
	 * 算法{@link AlgorithmHandler}容器
	 *
	 * <blockquote>
	 * {<br> key: dingerClassName | {@link MultiDingerConfigContainer#GLOABL_KEY}(key) + {@link
	 * DingerConstant#SPOT_SEPERATOR} + {@link AlgorithmHandler}.simpleName<br> value: {@link
	 * MultiDingerAlgorithmDefinition}<br> }<br>
	 * </blockquote>
	 */
	protected final static Map<String, MultiDingerAlgorithmDefinition> MULTIDINGER_ALGORITHM_DEFINITION_MAP = new HashMap<>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
		BeanDefinitionRegistry registry) {
		try {
			doScanAndRegister(importingClassMetadata, registry);
		} catch (DingerException ex) {
			throw new MultiDingerRegisterException(ex.getPairs(), ex.getMessage());
		} catch (Exception ex) {
			throw new DingerException(ex, ExceptionEnum.UNKNOWN);
		} finally {
			emptyDingerClasses();
		}

	}


	private void doScanAndRegister(AnnotationMetadata importingClassMetadata,
		BeanDefinitionRegistry registry) {
		if (!importingClassMetadata.hasAnnotation(EnableMultiDinger.class.getName())) {
			LogUtil.warn("import class can't find EnableMultiDinger annotation.");
			return;
		}

		AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
			importingClassMetadata.getAnnotationAttributes(EnableMultiDinger.class.getName())
		);

		AnnotationAttributes[] value = annotationAttributes.getAnnotationArray("value");
		boolean aloneMulti = value.length == 0;
		LogUtil.info("multi dinger register and is it global register? {}.", !aloneMulti);
		// 指定多机器人配置处理逻辑
		if (aloneMulti) {
			// 处理需要执行MultiDinger逻辑的dingerClass
			List<Class<?>> dingerClasses = dingerClasses();
			if (dingerClasses.isEmpty()) {
				LogUtil.warn("dinger class is empty, so no need to deal with multiDinger.");
				return;
			}

			multiDingerHandler(registry, dingerClasses);

			if (!MultiDingerConfigContainer.INSTANCE.isEmpty()) {
				MultiDingerProperty.multiDinger = true;
			}
			// 全局多机器人配置处理逻辑
		} else {
			for (AnnotationAttributes attributes : value) {
				DingerType dinger = attributes.getEnum("dinger");
				Class<? extends DingerConfigHandler> handler = attributes.getClass("handler");

				LogUtil.debug("enable {} global multi dinger, and multiDinger handler class={}.",
						dinger, handler.getName());
				DingerConfigHandler dingerConfigHandler = BeanUtils.instantiateClass(handler);
				registerHandler(registry, dinger,
					dinger + SPOT_SEPERATOR + GLOABL_KEY, dingerConfigHandler);
				MultiDingerProperty.multiDinger = true;
			}
		}
	}


	/**
	 * 处理DingerClass绑定的MultiHandler并注册{@link AlgorithmHandler}
	 *
	 * @param registry      注册器
	 * @param dingerClasses dingerClass集
	 */
	private void multiDingerHandler(BeanDefinitionRegistry registry, List<Class<?>> dingerClasses) {

		int valid = 0;
		for (Class<?> dingerClass : dingerClasses) {
			if (dingerClass.isAnnotationPresent(MultiHandler.class)) {
				MultiHandler multiDinger = dingerClass.getAnnotation(MultiHandler.class);
				MultiDinger value = multiDinger.value();
				Class<? extends DingerConfigHandler> dingerConfigHandler = value.handler();
				String beanName = dingerConfigHandler.getSimpleName();
				// 如果DingerClass指定的MultiHandler对应的处理器为接口，则直接跳过
				if (dingerConfigHandler.isInterface()) {
					LogUtil.warn("dingerClass={} handler className={} is interface and skip.",
						dingerClass.getSimpleName(), beanName);
					continue;
				}
				String key = dingerClass.getName();
				DingerConfigHandler handler = ClassUtils.newInstance(dingerConfigHandler);
				DingerType dinger = value.dinger();

				registerHandler(registry, dinger, dinger + SPOT_SEPERATOR + key,
					handler);

				LogUtil.debug(
						"regiseter multi dinger for dingerClass={} and dingerConfigHandler={}.",
						dingerClass.getSimpleName(), beanName);
				valid++;
			}
		}

		if (valid == 0) {
			LogUtil.warn(
				"enable global multi dinger but none dinger interface be decorated with @MultiHandler.");
		}
	}


	/**
	 * 注册 AlgorithmHandler
	 *
	 * @param registry            注册器
	 * @param dinger              Dinger类型
	 * @param key                 当前dingerClass类名
	 * @param dingerConfigHandler dingerClass指定的multiHandler处理器实例
	 */
	private void registerHandler(BeanDefinitionRegistry registry, DingerType dinger, String key,
		DingerConfigHandler dingerConfigHandler) {
		String dingerConfigHandlerClassName = dingerConfigHandler.getClass().getSimpleName();

		if (!dinger.isEnabled()) {
			throw new DingerException(MULTIDINGER_ANNOTATTION_EXCEPTION, key, dinger);
		}

		// 获取当前指定算法类名, 默认四种，或使用自定义
		Class<? extends AlgorithmHandler> algorithm = dingerConfigHandler.algorithmHandler();
		// if empty? use default dinger config
		List<DingerConfig> dingerConfigs = dingerConfigHandler.dingerConfigs();
		dingerConfigs = dingerConfigs == null ? new ArrayList<>() : dingerConfigs;

		if (algorithm == null) {
			throw new DingerException(MULTIDINGER_ALGORITHM_EXCEPTION,
				dingerConfigHandlerClassName);
		}

		for (int i = 0; i < dingerConfigs.size(); i++) {
			DingerConfig dingerConfig = dingerConfigs.get(i);
			dingerConfig.setDingerType(dinger);
			if (DingerUtils.isEmpty(dingerConfig.getTokenId())) {
				throw new DingerException(ExceptionEnum.DINGER_CONFIG_HANDLER_EXCEPTION,
					dingerConfigHandlerClassName, i);
			}
		}

		// 目前只支持属性方式注入, 可优化支持构造器注入和set注入
		long injectionCnt = Arrays.stream(algorithm.getDeclaredFields())
			.filter(e -> e.isAnnotationPresent(Autowired.class)).count();
		// 如果无注入对象，直接反射算法处理器对象
		AnalysisEnum mode = AnalysisEnum.REFLECT;
		// 如果无需注入属性，直接采用反射进行实例化并注册到容器
		if (injectionCnt == 0) {
			// create algorithm instance
			AlgorithmHandler algorithmHandler = ClassUtils.newInstance(algorithm);
			MultiDingerConfigContainer.INSTANCE.put(
				key, new MultiDingerConfig(algorithmHandler, dingerConfigs)
			);
		} else {
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(
				algorithm);
			AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
			beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
			// 将当前算法注册到Spring容器中
			String beanName = key + SPOT_SEPERATOR + algorithm.getSimpleName();
			registry.registerBeanDefinition(beanName, beanDefinition);
			MULTIDINGER_ALGORITHM_DEFINITION_MAP.put(
				beanName, new MultiDingerAlgorithmDefinition(key, algorithm, dingerConfigs,
					dingerConfigHandlerClassName)
			);
			mode = AnalysisEnum.SPRING_CONTAINER;
		}
		LogUtil.debug("key={}, algorithm={} analysis through mode {}.",
				key, algorithm.getSimpleName(), mode);

	}


	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE - 1;
	}


	public enum AnalysisEnum {
		/**
		 * 反射方式
		 */
		REFLECT,
		/**
		 * Spring容器
		 */
		SPRING_CONTAINER;
	}


}
