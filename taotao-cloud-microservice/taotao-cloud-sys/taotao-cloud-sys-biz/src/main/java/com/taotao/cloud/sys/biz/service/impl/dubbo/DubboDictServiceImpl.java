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
package com.taotao.cloud.sys.biz.service.impl.dubbo;

import com.taotao.cloud.sys.api.dubbo.IDubboDictService;
import com.taotao.cloud.sys.api.dubbo.response.DubboDictRes;
import com.taotao.cloud.sys.biz.mapper.IDictMapper;
import com.taotao.cloud.sys.biz.model.entity.dict.Dict;
import com.taotao.cloud.sys.biz.repository.cls.DictRepository;
import com.taotao.cloud.sys.biz.repository.inf.IDictRepository;
import com.taotao.cloud.sys.biz.service.IDictService;
import com.taotao.cloud.web.base.service.BaseSuperServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * DictServiceImpl
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2021-10-09 20:26:36
 */
@Service
@AllArgsConstructor
@DubboService(interfaceClass = IDubboDictService.class, validation = "true")
public class DubboDictServiceImpl extends
	BaseSuperServiceImpl<IDictMapper, Dict, DictRepository, IDictRepository, Long>
	implements IDubboDictService {

	@Override
	public DubboDictRes findByCode(Integer code) {
		return new DubboDictRes();
	}
}
