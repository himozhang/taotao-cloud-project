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
package com.taotao.cloud.dingtalk.multi;

import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.dingtalk.model.DingerConfig;
import java.util.List;

/**
 * 轮询算法
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:24:25
 */
public class RoundRobinHandler implements AlgorithmHandler {

	/**
	 * 索引值
	 */
	private volatile int index = DEFAULT_INDEX;

	@Override
	public DingerConfig handler(List<DingerConfig> dingerConfigs,
		DingerConfig defaultDingerConfig) {
		int size = dingerConfigs.size();
		int idx = index;

		synchronized (this) {
			index++;
			index = index >= size ? DEFAULT_INDEX : index;

			LogUtil.debug("#{}# 当前使用第{}个机器人", algorithmId(), idx);
//            System.out.println(String.format("#%s# 当前使用第%d个机器人", algorithmId(), idx));
		}

		return dingerConfigs.get(idx);
	}
}
