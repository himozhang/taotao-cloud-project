/*
 * Copyright (c) 2012-2019 the original author or authors.
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

package com.taotao.cloud.core.version;

import java.util.jar.Attributes.Name;

/**
 * Class that exposes the Spring Boot version. Fetches the {@link Name#IMPLEMENTATION_VERSION
 * Implementation-Version} manifest attribute from the jar file via {@link
 * Package#getImplementationVersion()}, falling back to locating the jar file that contains this
 * class and reading the {@code Implementation-Version} attribute from its manifest.
 * <p>
 * This class might not be able to determine the Spring Boot version in all environments. Consider
 * using a reflection-based check instead: For example, checking for the presence of a specific
 * Spring Boot method that you intend to call.
 *
 * @author Drummond Dawson
 * @author Hendrig Sellik
 * @author Andy Wilkinson
 * @since 1.3.0
 */
public final class SpringCloudDependenciesVersion {

	private SpringCloudDependenciesVersion() {
	}

	/**
	 * Return the full version string of the present Spring Boot codebase, or {@code null} if it
	 * cannot be determined.
	 *
	 * @return the version of Spring Boot or {@code null}
	 * @see Package#getImplementationVersion()
	 */
	public static String getVersion() {
		return "2021.0.3";
	}


}
