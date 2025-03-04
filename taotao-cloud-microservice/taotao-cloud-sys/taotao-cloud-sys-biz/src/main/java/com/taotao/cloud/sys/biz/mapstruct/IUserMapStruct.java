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
package com.taotao.cloud.sys.biz.mapstruct;

import com.taotao.cloud.sys.api.web.dto.user.UserQueryDTO;
import com.taotao.cloud.sys.api.web.vo.user.UserQueryVO;
import com.taotao.cloud.sys.api.web.vo.user.UserRegisterVO;
import com.taotao.cloud.sys.biz.model.entity.system.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * UserMapper
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 13:39:56
 */
@Mapper(
	unmappedSourcePolicy = ReportingPolicy.IGNORE,
	unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IUserMapStruct {

	/**
	 * 实例
	 */
	IUserMapStruct INSTANCE = Mappers.getMapper(IUserMapStruct.class);

	/**
	 * 系统用户用户查询签证官
	 *
	 * @param sysUser 系统用户
	 * @return {@link UserQueryVO }
	 * @since 2022-04-28 13:39:56
	 */
	UserQueryVO sysUserToUserQueryVO(User sysUser);

	/**
	 * 系统用户添加用户签证官
	 *
	 * @param sysUser 系统用户
	 * @return {@link UserRegisterVO }
	 * @since 2022-04-28 13:39:57
	 */
	UserRegisterVO sysUserToAddUserVO(User sysUser);

	/**
	 * 系统用户用户查询签证官
	 *
	 * @param userList 用户列表
	 * @return {@link List }<{@link UserQueryVO }>
	 * @since 2022-04-28 13:39:57
	 */
	List<UserQueryVO> sysUserToUserQueryVO(List<User> userList);

	/**
	 * 用户dto复制到系统用户
	 *
	 * @param userQueryDTO 用户查询dto
	 * @param user         用户
	 * @since 2022-04-28 13:39:57
	 */
	void copyUserDtoToSysUser(UserQueryDTO userQueryDTO, @MappingTarget User user);
}
