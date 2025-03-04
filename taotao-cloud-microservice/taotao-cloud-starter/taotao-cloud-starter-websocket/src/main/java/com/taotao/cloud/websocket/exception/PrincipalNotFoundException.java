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
package com.taotao.cloud.websocket.exception;


import com.taotao.cloud.common.enums.ResultEnum;

/**
 * <p>Description: 无法找到 Principal 错误 </p>
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-12 11:01:18
 */
public class PrincipalNotFoundException extends WebSocketException {

	public PrincipalNotFoundException(String message) {
		super(message);
	}

	public PrincipalNotFoundException(Integer code, String message) {
		super(code, message);
	}

	public PrincipalNotFoundException(Throwable e) {
		super(e);
	}

	public PrincipalNotFoundException(String message, Throwable e) {
		super(message, e);
	}

	public PrincipalNotFoundException(Integer code, String message, Throwable e) {
		super(code, message, e);
	}

	public PrincipalNotFoundException(ResultEnum result) {
		super(result);
	}

	public PrincipalNotFoundException(ResultEnum result, Throwable e) {
		super(result, e);
	}
}
