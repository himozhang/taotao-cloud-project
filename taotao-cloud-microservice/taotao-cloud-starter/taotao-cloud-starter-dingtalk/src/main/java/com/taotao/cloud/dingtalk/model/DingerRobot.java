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

import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.dingtalk.entity.DingerRequest;
import com.taotao.cloud.dingtalk.entity.DingerResponse;
import com.taotao.cloud.dingtalk.entity.MsgType;
import com.taotao.cloud.dingtalk.enums.DingerResponseCodeEnum;
import com.taotao.cloud.dingtalk.enums.DingerType;
import com.taotao.cloud.dingtalk.enums.MediaTypeEnum;
import com.taotao.cloud.dingtalk.enums.MessageSubType;
import com.taotao.cloud.dingtalk.exception.AsyncCallException;
import com.taotao.cloud.dingtalk.exception.SendMsgException;
import com.taotao.cloud.dingtalk.properties.DingerProperties;
import com.taotao.cloud.dingtalk.support.CustomMessage;
import com.taotao.cloud.dingtalk.support.SignBase;
import com.taotao.cloud.dingtalk.utils.DingerUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeanUtils;


/**
 * DingTalk Robot
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:23:19
 */
public class DingerRobot extends AbstractDingerSender {

	public DingerRobot(DingerProperties dingerProperties,
		DingerManagerBuilder dingTalkManagerBuilder) {
		super(dingerProperties, dingTalkManagerBuilder);
	}

	@Override
	public DingerResponse send(MessageSubType messageSubType, DingerRequest request) {
		return send(dingerProperties.getDefaultDinger(), messageSubType, request);
	}

	@Override
	public DingerResponse send(DingerType dingerType, MessageSubType messageSubType,
		DingerRequest request) {
		if (!messageSubType.isSupport()) {
			return DingerResponse.failed(DingerResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED);
		}
		CustomMessage customMessage = customMessage(messageSubType);
		String msgContent = customMessage.message(
			dingerProperties.getProjectId(), request
		);
		request.setContent(msgContent);

		MsgType msgType = messageSubType.msgType(
			dingerType, request
		);

		return send(msgType);
	}


	/**
	 * @param message 消息内容
	 * @param <T>     T
	 * @return 响应内容 {@link DingerResponse}
	 */
	protected <T extends MsgType> DingerResponse send(T message) {
		DingerType dingerType = message.getDingerType();
		String dkid = dingTalkManagerBuilder.getDingerIdGenerator().dingerId();
		Map<DingerType, DingerProperties.Dinger> dingers = dingerProperties.getDingers();
		if (!(dingerProperties.getEnabled() && dingers.containsKey(dingerType))) {
			return DingerResponse.failed(dkid, DingerResponseCodeEnum.DINGER_DISABLED);
		}

		DingerConfig localDinger = getLocalDinger();
		// dinger is null? use global configuration and check whether dinger send
		boolean dingerConfig = localDinger != null;
		try {
			DingerProperties.Dinger dinger;
			if (dingerConfig) {
				dinger = new DingerProperties.Dinger();
				BeanUtils.copyProperties(localDinger, dinger);
				dinger.setAsync(localDinger.getAsyncExecute());
				dinger.setRobotUrl(dingers.get(dingerType).getRobotUrl());
			} else {
				dinger = dingers.get(dingerType);
			}

			StringBuilder webhook = new StringBuilder();
			webhook.append(dinger.getRobotUrl()).append("=").append(dinger.getTokenId());

			LogUtil.info("dingerId={} send message and use dinger={}, tokenId={}.", dkid,
				dingerType, dinger.getTokenId());

			// 处理签名问题(只支持DingTalk)
			if (dingerType == DingerType.DINGTALK &&
				DingerUtils.isNotEmpty((dinger.getSecret()))) {
				SignBase sign = dingTalkManagerBuilder.getDingerSignAlgorithm().sign(
					dinger.getSecret().trim());
				webhook.append(sign.transfer());
			}

			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", MediaTypeEnum.JSON.type());

			// 异步处理, 直接返回标识id
			if (dinger.isAsync()) {
				dingTalkManagerBuilder.getDingTalkExecutor().execute(() -> {
					try {
						String result = dingTalkManagerBuilder.getDingerHttpClient().post(
							webhook.toString(), headers, message
						);
						dingTalkManagerBuilder.getDingerAsyncCallback().execute(dkid, result);
					} catch (Exception e) {
						exceptionCallback(dkid, message, new AsyncCallException(e));
					}
				});
				return DingerResponse.success(dkid, dkid);
			}

			String response = dingTalkManagerBuilder.getDingerHttpClient().post(
				webhook.toString(), headers, message
			);
			LogUtil.info(response);
			return DingerResponse.success(dkid, response);
		} catch (Exception e) {
			LogUtil.error(e);
			exceptionCallback(dkid, message, new SendMsgException(e));
			return DingerResponse.failed(dkid, DingerResponseCodeEnum.SEND_MESSAGE_FAILED);
		}
	}

}
