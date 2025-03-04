/*
 * Copyright (c) 2018-2022 the original author or authors.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.sms.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.redis.repository.RedisRepository;
import com.taotao.cloud.sms.common.model.VerificationCodeTypeGenerate;
import com.taotao.cloud.sms.common.properties.SmsProperties;
import com.taotao.cloud.sms.common.properties.VerificationCodeMemoryRepositoryProperties;
import com.taotao.cloud.sms.common.properties.VerificationCodeProperties;
import com.taotao.cloud.sms.common.repository.VerificationCodeMemoryRepository;
import com.taotao.cloud.sms.common.repository.VerificationCodeRedisRepository;
import com.taotao.cloud.sms.common.repository.VerificationCodeRepository;
import com.taotao.cloud.sms.common.service.CodeGenerate;
import com.taotao.cloud.sms.common.service.NoticeService;
import com.taotao.cloud.sms.common.service.VerificationCodeService;
import com.taotao.cloud.sms.common.service.impl.DefaultCodeGenerate;
import com.taotao.cloud.sms.common.service.impl.DefaultVerificationCodeService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 验证码服务配置
 *
 * @author shuigedeng
 */
@AutoConfiguration(after = SmsAutoConfiguration.class)
@ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties({VerificationCodeProperties.class, VerificationCodeMemoryRepositoryProperties.class})
public class VerificationCodeAutoConfiguration {

	/**
	 * 创建默认验证码生成
	 *
	 * @param properties 验证码配置
	 * @return 默认验证码生成
	 */
	@Bean
	@ConditionalOnMissingBean
	public CodeGenerate defaultCodeGenerate(VerificationCodeProperties properties) {
		return new DefaultCodeGenerate(properties);
	}

	/**
	 * 创建手机验证码服务
	 *
	 * @param repository                           验证码储存接口
	 * @param properties                           验证码配置
	 * @param noticeService                        短信通知服务
	 * @param codeGenerate                         验证码生成
	 * @param verificationCodeTypeGenerateProvider 验证码类型生成
	 * @return 手机验证码服务
	 */
	@Bean
	@ConditionalOnMissingBean
	public VerificationCodeService verificationCodeService(
		VerificationCodeRepository repository,
		VerificationCodeProperties properties,
		NoticeService noticeService,
		CodeGenerate codeGenerate,
		ObjectProvider<VerificationCodeTypeGenerate> verificationCodeTypeGenerateProvider) {

		return new DefaultVerificationCodeService(repository,
			properties,
			noticeService,
			codeGenerate,
			verificationCodeTypeGenerateProvider.getIfUnique());
	}

	/**
	 * 验证码内存储存配置实现
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = VerificationCodeProperties.PREFIX, name = "repository", havingValue = "memory")
	public VerificationCodeRepository verificationCodeMemoryRepository(
		VerificationCodeMemoryRepositoryProperties config) {
		VerificationCodeRepository repository = new VerificationCodeMemoryRepository(config);
		LogUtil.debug("create VerificationCodeRepository: Memory");
		return repository;
	}

	/**
	 * 验证码redis储存配置实现
	 */
	@Bean
	@ConditionalOnBean(RedisRepository.class)
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = VerificationCodeProperties.PREFIX, name = "repository", havingValue = "redis", matchIfMissing = true)
	public VerificationCodeRepository verificationCodeRedisRepository(
		RedisRepository redisRepository,
		ObjectMapper objectMapper) {
		VerificationCodeRepository repository = new VerificationCodeRedisRepository(
			redisRepository,
			objectMapper);
		LogUtil.debug("create VerificationCodeRepository: Redis");
		return repository;
	}
}
