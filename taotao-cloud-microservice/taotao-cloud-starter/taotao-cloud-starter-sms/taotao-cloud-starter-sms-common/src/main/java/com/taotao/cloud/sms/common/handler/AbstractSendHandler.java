/*
 * Copyright (c) 2018-2022 the original author or authors.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.sms.common.handler;

import com.taotao.cloud.sms.common.event.SmsSendFailEvent;
import com.taotao.cloud.sms.common.event.SmsSendFinallyEvent;
import com.taotao.cloud.sms.common.event.SmsSendSuccessEvent;
import com.taotao.cloud.sms.common.model.AbstractHandlerProperties;
import com.taotao.cloud.sms.common.model.NoticeData;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * 抽象发送处理
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-27 17:48:15
 */
public abstract class AbstractSendHandler<P extends AbstractHandlerProperties<?>> implements
	SendHandler {

	protected final P properties;

	private final ApplicationEventPublisher eventPublisher;

	public AbstractSendHandler(P properties, @Nullable ApplicationEventPublisher eventPublisher) {
		this.properties = properties;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public boolean acceptSend(@Nullable String type) {
		return properties.getTemplates().containsKey(type);
	}

	/**
	 * 获取通道名称
	 *
	 * @return {@link String }
	 * @since 2022-04-27 17:48:18
	 */
	public abstract String getChannelName();

	/**
	 * 发布发送成功事件
	 *
	 * @param noticeData 通知内容
	 * @param phones     手机号列表
	 * @since 2022-04-27 17:48:21
	 */
	protected final void publishSendSuccessEvent(NoticeData noticeData, Collection<String> phones) {
		if (eventPublisher == null) {
			return;
		}
		eventPublisher.publishEvent(
			new SmsSendSuccessEvent(
				this,
				getChannelName(),
				phones,
				noticeData.getType(),
				noticeData.getParams()));
		publishSendFinallyEvent(noticeData, phones);
	}

	/**
	 * 发布发送失败事件
	 *
	 * @param noticeData 通知内容
	 * @param phones     手机号列表
	 * @param cause      源异常
	 * @since 2022-04-27 17:48:23
	 */
	protected final void publishSendFailEvent(NoticeData noticeData, Collection<String> phones,
		Throwable cause) {
		if (eventPublisher == null) {
			return;
		}

		eventPublisher.publishEvent(
			new SmsSendFailEvent(this,
				getChannelName(),
				phones,
				noticeData.getType(),
				noticeData.getParams(),
				cause));

		publishSendFinallyEvent(noticeData, phones);
	}

	/**
	 * 发布发送结束事件
	 *
	 * @param noticeData 通知内容
	 * @param phones     手机号列表
	 * @since 2022-04-27 17:48:26
	 */
	private void publishSendFinallyEvent(NoticeData noticeData, Collection<String> phones) {
		if (eventPublisher == null) {
			return;
		}
		eventPublisher.publishEvent(
			new SmsSendFinallyEvent(this, getChannelName(), phones, noticeData.getType(),
				noticeData.getParams()));
	}
}
