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
package com.taotao.cloud.common.enums;

/**
 * DelFlagEnum 
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 20:03:32
 */
public enum DelFlagEnum implements BaseEnum {
	/**
	 * 正常
	 */
	NORMAL(1, "正常"),
	/**
	 * 删除
	 */
	DELETE(2, "删除");

	private final Integer value;
	private final String description;

	DelFlagEnum(Integer value, String description) {
		this.value = value;
		this.description = description;
	}

	public int getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String getNameByCode(int code) {
		for (DelFlagEnum result : DelFlagEnum.values()) {
			if (result.getCode() == code) {
				return result.name().toLowerCase();
			}
		}
		return null;
	}

	@Override
	public Integer getCode() {
		return value;
	}
}
