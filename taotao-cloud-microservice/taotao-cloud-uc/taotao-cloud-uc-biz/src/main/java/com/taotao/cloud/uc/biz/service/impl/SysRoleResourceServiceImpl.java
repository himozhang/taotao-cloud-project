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
package com.taotao.cloud.uc.biz.service.impl;

import com.taotao.cloud.uc.api.service.ISysRoleResourceService;
import com.taotao.cloud.uc.biz.entity.SysRoleResource;
import com.taotao.cloud.uc.biz.mapper.SysRoleResourceMapper;
import com.taotao.cloud.uc.biz.repository.SysRoleResourceRepository;
import com.taotao.cloud.web.base.service.BaseSuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author shuigedeng
 * @version 1.0.0
 * @since 2020/10/21 09:48
 */
@Service
public class SysRoleResourceServiceImpl extends
	BaseSuperServiceImpl<SysRoleResourceMapper, SysRoleResource, SysRoleResourceRepository, Long>
	implements ISysRoleResourceService<SysRoleResource, Long> {

	//private final SysRoleResourceRepository roleResourceRepository;
	//
	//public SysRoleResourceServiceImpl(
	//	SysRoleResourceRepository roleResourceRepository) {
	//	this.roleResourceRepository = roleResourceRepository;
	//}
	//
	//private final static QSysRoleResource SYS_ROLE_RESOURCE = QSysRoleResource.sysRoleResource;
	//
	//@Override
	//public Boolean saveRoleResource(Long roleId, Set<Long> resourceIds) {
	//    BooleanExpression expression = SYS_ROLE_RESOURCE.roleId.eq(roleId);
	//    List<SysRoleResource> roleResources = roleResourceRepository.fetch(expression);
	//    if (CollUtil.isNotEmpty(roleResources)) {
	//        // 删除数据
	//        roleResourceRepository.deleteAll(roleResources);
	//    }
	//    // 批量添加数据
	//    List<SysRoleResource> collect = resourceIds.stream().map(resourceId -> SysRoleResource.builder()
	//            .roleId(roleId)
	//            .resourceId(resourceId)
	//            .build()).collect(Collectors.toList());
	//    roleResourceRepository.saveAll(collect);
	//    return true;
	//}
}
