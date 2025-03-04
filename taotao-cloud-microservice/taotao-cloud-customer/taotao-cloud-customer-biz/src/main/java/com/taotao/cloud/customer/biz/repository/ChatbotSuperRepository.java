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
package com.taotao.cloud.customer.biz.repository;

import com.taotao.cloud.customer.biz.model.entity.Chatbot;
import com.taotao.cloud.data.jpa.repository.JpaSuperRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

/**
 * @author shuigedeng
 * @since 2020/11/13 10:01
 * @version 2022.03
 */
@Repository
public class ChatbotSuperRepository extends JpaSuperRepository<Chatbot, Long> {
	public ChatbotSuperRepository(EntityManager em) {
		super(Chatbot.class, em);
	}

}
