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
package com.taotao.cloud.core.utils;

import com.taotao.cloud.common.utils.context.ContextUtil;
import com.taotao.cloud.common.model.Callable;
import com.taotao.cloud.core.monitor.Monitor;
import java.util.Collection;

/**
 * 提供线程池操作类 默认使用自定义的全局线程池
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 20:57:25
 */
public class ThreadUtil {

	/**
	 * 使用系统线程池并行for循环
	 *
	 * @param taskName      任务名称
	 * @param parallelCount 并行数量
	 * @param taskList      任务列表
	 * @param action        action
	 * @since 2021-09-02 20:57:35
	 */
	public static <T> void parallelFor(String taskName, int parallelCount, Collection<T> taskList,
		final Callable.Action1<T> action) {
		if (parallelCount < 2) {
			for (T t : taskList) {
				action.invoke(t);
			}
		} else {
			Monitor monitorThreadPool = ContextUtil.getBean(Monitor.class, false);
			monitorThreadPool.monitorParallelFor2(taskName, parallelCount, taskList, action);
		}
	}
}
