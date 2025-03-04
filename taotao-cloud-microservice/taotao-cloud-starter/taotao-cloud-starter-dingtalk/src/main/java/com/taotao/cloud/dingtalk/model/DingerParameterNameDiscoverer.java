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

import org.springframework.core.KotlinReflectionParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

/**
 * Default implementation of the {@link ParameterNameDiscoverer} strategy interface, using the Java
 * 8 standard reflection mechanism (if available), and falling back to the ASM-based {@link
 * LocalVariableTableParameterNameDiscoverer} for checking debug information in the class file.
 *
 * <p>If a Kotlin reflection implementation is present,
 * {@link KotlinReflectionParameterNameDiscoverer} is added first in the list and used for Kotlin
 * classes and interfaces. When compiling or running as a GraalVM native image, the {@code
 * KotlinReflectionParameterNameDiscoverer} is not used.
 *
 * <p>Further discoverers may be added through {@link #addDiscoverer(ParameterNameDiscoverer)}.
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:23:11
 */
public class DingerParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {

	public DingerParameterNameDiscoverer() {
//        addDiscoverer(new StandardReflectionParameterNameDiscoverer());
		addDiscoverer(new AnnotationParameterNameDiscoverer());
	}
}
