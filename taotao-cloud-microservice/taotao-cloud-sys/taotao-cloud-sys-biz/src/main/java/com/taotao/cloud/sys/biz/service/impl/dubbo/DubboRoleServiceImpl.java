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

import com.querydsl.core.types.dsl.BooleanExpression;
import com.taotao.cloud.sys.api.dubbo.IDubboRoleService;
import com.taotao.cloud.sys.api.dubbo.response.RoleBO;
import com.taotao.cloud.sys.biz.mapper.IRoleMapper;
import com.taotao.cloud.sys.biz.mapstruct.IRoleMapStruct;
import com.taotao.cloud.sys.biz.model.entity.system.QRole;
import com.taotao.cloud.sys.biz.model.entity.system.Role;
import com.taotao.cloud.sys.biz.repository.cls.RoleRepository;
import com.taotao.cloud.sys.biz.repository.inf.IRoleRepository;
import com.taotao.cloud.sys.biz.service.IRoleMenuService;
import com.taotao.cloud.sys.biz.service.IRoleService;
import com.taotao.cloud.web.base.service.BaseSuperServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoleServiceImpl
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2021-10-09 20:46:26
 */
@Service
@AllArgsConstructor
@DubboService(interfaceClass = IDubboRoleService.class, validation = "true")
public class DubboRoleServiceImpl extends
	BaseSuperServiceImpl<IRoleMapper, Role, RoleRepository, IRoleRepository, Long>
	implements IDubboRoleService {

	private final static QRole SYS_ROLE = QRole.role;

	private final IRoleMenuService sysRoleResourceService;

}
