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

package com.taotao.cloud.common.utils;


import org.apache.commons.io.Charsets;

/**
 * url处理工具类
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 19:41:13
 */
public class UrlUtil extends org.springframework.web.util.UriUtils {

	/**
	 * encode
	 *
	 * @param source source
	 * @return sourced String
	 */
	public static String encode(String source) {
		return UrlUtil.encode(source, Charsets.UTF_8);
	}

	/**
	 * decode
	 *
	 * @param source source
	 * @return decoded String
	 */
	public static String decode(String source) {
		return UrlUtil.decode(source, Charsets.UTF_8);
	}
}
