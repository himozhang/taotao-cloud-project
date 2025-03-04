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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dinger钉钉机器人算法 <br>
 *
 * <blockquote>
 * VM options: {@code -Dmulti.dinger.minute.limit.count=2}
 * </blockquote>
 *
 * <blockquote>
 * 机器人发送消息频率限制
 * <pre>
 * https://ding-doc.dingtalk.com/doc#/serverapi3/pghqkk
 * </pre>
 * </blockquote>
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:23:56
 */
public class DingerHandler implements AlgorithmHandler {

	public static final String DINGTALK_MULTI_DINGER_COUNT = "multi.dinger.minute.limit.count";
	private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyMMddHHmm");
	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

	/**
	 * 每分钟内限制发送 COUNT 条
	 */
	private static final int COUNT_THRESHOLD;

	static {
		COUNT_THRESHOLD = System.getProperty(DINGTALK_MULTI_DINGER_COUNT) == null ?
			20 : Integer.parseInt(
			System.getProperty(DINGTALK_MULTI_DINGER_COUNT)
		);
	}

	/**
	 * 索引号
	 */
	private volatile int index = DEFAULT_INDEX;
	/**
	 * 当前分钟
	 */
	private String currentMinite = null;
	/**
	 * 计数器
	 */
	private AtomicInteger counter = new AtomicInteger(DEFAULT_INDEX);


	@Override
	public DingerConfig handler(List<DingerConfig> dingerConfigs,
		DingerConfig defaultDingerConfig) {
		int size = dingerConfigs.size();

		synchronized (this) {
			if (currentMinite == null) {
				currentMinite = LocalDateTime.now(ZONE_ID).format(DATETIME_FMT);
			}

			int count = counter.getAndIncrement();

			boolean countBool = count >= COUNT_THRESHOLD;
			String now = LocalDateTime.now(ZONE_ID).format(DATETIME_FMT);
			boolean inMinute = now.equals(currentMinite);
			if (countBool) {
				if (inMinute) {
					index++;
					index = index >= size ? DEFAULT_INDEX : index;
				}
				currentMinite = now;
				counter.set(1);

				LogUtil.debug("#{}-{}# 在{}分内发送了{}次, 当前分钟={}, 下一个机器人={}.",
					algorithmId(), COUNT_THRESHOLD, currentMinite, count, now, index);

//                System.out.println(String.format("#%s-%d# 在%s分内发送了%d次, 当前分钟=%s, 下一个机器人=%d.",
//                        algorithmId(), COUNT_THRESHOLD, currentMinite, count, now, index));
			} else if (!countBool && !inMinute) {
				currentMinite = now;
				counter.set(1);

				LogUtil.debug("#{}-{}# 在{}分内发送了{}次, 当前分钟={}, 当前机器人={}.",
					algorithmId(), COUNT_THRESHOLD, currentMinite, count, now, index);
//                System.out.println(String.format("#%s-%d# 在%s分内发送了%d次, 当前分钟=%s, 当前机器人=%d.",
//                        algorithmId(), COUNT_THRESHOLD, currentMinite, count, now, index));
			}

		}

		return dingerConfigs.get(index);
	}
}
