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

import static com.taotao.cloud.dingtalk.enums.ExceptionEnum.DINGERDEFINITION_ERROR;

import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.dingtalk.exception.DingerException;
import java.util.HashMap;
import java.util.Map;


/**
 * Dinger Definition工厂类
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:22:43
 */
public class DingerDefinitionGeneratorFactory {

	/**
	 * dingerDefinition生成器
	 */
	static final Map<String, DingerDefinitionGenerator> dingTalkDefinitionGeneratorMap = new HashMap<>();

	/**
	 * 根据key获取对应生成处理逻辑类
	 *
	 * @param key key
	 * @return dingerDefinitionGenerator {@link DingerDefinitionGenerator}
	 */
	public static DingerDefinitionGenerator get(String key) {
		DingerDefinitionGenerator dingTalkDefinitionGenerator = dingTalkDefinitionGeneratorMap.get(
			key);
		if (dingTalkDefinitionGenerator == null) {
			LogUtil.debug("key={}, dingTalkDefinitionGeneratorMap={}.",
				key, dingTalkDefinitionGeneratorMap.keySet());
			throw new DingerException(DINGERDEFINITION_ERROR, key);
		}
		return dingTalkDefinitionGenerator;
	}

}
