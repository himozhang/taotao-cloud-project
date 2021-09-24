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
package com.taotao.cloud.logistics.biz.service.impl;

import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.logistics.biz.entity.ExpressCompany;
import com.taotao.cloud.logistics.biz.repository.ExpressCompanyRepository;
import com.taotao.cloud.logistics.biz.service.IExpressCompanyService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author shuigedeng
 * @since 2020/11/13 10:00
 * @version 1.0.0
 */
@Service
public class ExpressCompanyServiceImpl implements IExpressCompanyService {

	private final ExpressCompanyRepository expressCompanyRepository;

	public ExpressCompanyServiceImpl(ExpressCompanyRepository expressCompanyRepository) {
		this.expressCompanyRepository = expressCompanyRepository;
	}

	@Override
	public ExpressCompany findExpressCompanyById(Long id) {
		Optional<ExpressCompany> optionalExpressCompany = expressCompanyRepository.findById(id);
		return optionalExpressCompany.orElseThrow(() -> new BusinessException(ResultEnum.FILE_NOT_EXIST));
	}
}
