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
package com.taotao.cloud.customer.biz.service.impl;

import com.taotao.cloud.customer.biz.entity.Chatbot;
import com.taotao.cloud.customer.biz.repository.ChatbotRepository;
import com.taotao.cloud.customer.biz.service.IChatbotService;
import org.springframework.stereotype.Service;

/**
 * @author shuigedeng
 * @since 2020/11/13 10:00
 * @version 1.0.0
 */
@Service
public class ChatbotServiceImpl implements IChatbotService {

	private final ChatbotRepository chatbotRepository;

	public ChatbotServiceImpl(
		ChatbotRepository chatbotRepository) {
		this.chatbotRepository = chatbotRepository;
	}

	@Override
	public Chatbot findChatbotById(Long id) {
		return chatbotRepository.getOne(id);
	}
}
