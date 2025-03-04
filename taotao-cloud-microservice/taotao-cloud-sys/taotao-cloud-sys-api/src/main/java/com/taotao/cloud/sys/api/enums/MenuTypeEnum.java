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
package com.taotao.cloud.sys.api.enums;


/**
 * 菜单类型
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 22:14:43
 */
public enum MenuTypeEnum {

	/**
	 * 目录
	 */
	DIR(1, "目录"),
	/**
	 * 菜单
	 */
	MENU(2, "菜单"),
	/**
	 * 按钮
	 */
	BUTTON(3, "按钮");

	private final Integer code;

	private final String message;

	MenuTypeEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
