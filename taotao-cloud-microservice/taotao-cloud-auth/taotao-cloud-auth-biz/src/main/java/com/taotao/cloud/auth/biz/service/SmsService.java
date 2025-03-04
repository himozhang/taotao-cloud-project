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
package com.taotao.cloud.auth.biz.service;

import com.taotao.cloud.auth.api.dubbo.IDubboClientService;
import com.taotao.cloud.auth.api.dubbo.response.DubboClientRes;
import com.taotao.cloud.common.constant.RedisConstant;
import com.taotao.cloud.auth.biz.exception.CloudAuthenticationException;
import com.taotao.cloud.redis.repository.RedisRepository;
import com.taotao.cloud.sms.common.service.NoticeService;
import com.taotao.cloud.sms.common.service.VerificationCodeService;
import com.taotao.cloud.sys.api.dubbo.IDubboDictService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SmsService
 *
 * @author shuigedeng
 * @version 2022.03
 * @since 2021/12/21 20:52
 */
@Service
@DubboService(interfaceClass = IDubboClientService.class)
public class SmsService implements IDubboClientService {

	@Autowired(required = false)
	private NoticeService noticeService;
	@Autowired(required = false)
	private VerificationCodeService verificationCodeService;
	@Autowired
	private RedisRepository redisRepository;

	public boolean sendSms(String phoneNumber) {
		String code = verificationCodeService.find(phoneNumber, "");

		boolean result = noticeService.send(
			null,
			""
		);

		// 添加发送日志
		redisRepository
			.setExpire(RedisConstant.SMS_KEY_PREFIX + phoneNumber, code.toLowerCase(), 120);

		return true;
	}

	public boolean checkSms(String code, String phoneNumber) {
		String key = RedisConstant.SMS_KEY_PREFIX + phoneNumber;
		if (!redisRepository.exists(key)) {
			throw CloudAuthenticationException.throwError("手机验证码不合法");
		}

		Object captcha = redisRepository.get(key);
		if (captcha == null) {
			throw CloudAuthenticationException.throwError("手机验证码已失效");
		}
		if (!code.toLowerCase().equals(captcha)) {
			throw CloudAuthenticationException.throwError("手机验证码错误");
		}

		return true;
	}

	@Override
	public DubboClientRes findById(Long id) {
		return new DubboClientRes();
	}
}
