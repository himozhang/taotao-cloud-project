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
package com.taotao.cloud.dingtalk.annatations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DingerTokenId
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:18:15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@Documented
public @interface DingerTokenId {

	/**
	 * 价值
	 *
	 * @return {@link String }
	 * @since 2022-07-06 15:18:15
	 */
	String value();

	/**
	 * 秘密
	 *
	 * @return {@link String }
	 * @since 2022-07-06 15:18:15
	 */
	String secret() default "";

	/**
	 * 解密密钥
	 *
	 * @return {@link String }
	 * @since 2022-07-06 15:18:16
	 */
	String decryptKey() default "";
}
