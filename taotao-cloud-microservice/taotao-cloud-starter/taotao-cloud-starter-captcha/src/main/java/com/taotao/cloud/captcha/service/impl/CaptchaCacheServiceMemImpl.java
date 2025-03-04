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
package com.taotao.cloud.captcha.service.impl;

import com.taotao.cloud.captcha.service.CaptchaCacheService;
import com.taotao.cloud.captcha.util.CacheUtil;

/**
 * CaptchaCacheServiceMemImpl 
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-04 07:41:39
 */
public class CaptchaCacheServiceMemImpl implements CaptchaCacheService {

	@Override
	public void set(String key, String value, long expiresInSeconds) {
		CacheUtil.set(key, value, expiresInSeconds);
	}

	@Override
	public boolean exists(String key) {
		return CacheUtil.exists(key);
	}

	@Override
	public void delete(String key) {
		CacheUtil.delete(key);
	}

	@Override
	public String get(String key) {
		return CacheUtil.get(key);
	}

	@Override
	public Long increment(String key, long val) {
		Long ret = Long.parseLong(CacheUtil.get(key)) + val;
		CacheUtil.set(key, ret + "", 0);
		return ret;
	}

	@Override
	public String type() {
		return "local";
	}
}
