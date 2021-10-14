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
package com.taotao.cloud.uc.biz.controller;

import com.taotao.cloud.uc.api.dto.user.UserQueryDTO;
import com.taotao.cloud.uc.api.dto.user.UserSaveDTO;
import com.taotao.cloud.uc.api.dto.user.UserUpdateDTO;
import com.taotao.cloud.uc.api.service.ISysUserService;
import com.taotao.cloud.uc.api.vo.user.UserQueryVO;
import com.taotao.cloud.uc.biz.entity.SysUser;
import com.taotao.cloud.web.base.controller.SuperController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台用户管理API
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2021-10-09 15:13:54
 */
@Validated
@RestController
@RequestMapping("/uc/user")
@Tag(name = "后台用户管理API", description = "后台用户管理API")
public class SysUserController extends
	SuperController<ISysUserService<SysUser, Long>, SysUser, Long, UserQueryDTO, UserSaveDTO, UserUpdateDTO, UserQueryVO> {


	//private final ISysUserService sysUserService;
	//
	//public SysUserController(ISysUserService sysUserService) {
	//	this.sysUserService = sysUserService;
	//}
	//
	///**
	// * 保存(添加)用户
	// *
	// * @param userDTO 保存(添加)用户DTO
	// * @return {@link Result&lt;com.taotao.cloud.uc.api.vo.user.AddUserVO&gt;}
	// * @author shuigedeng
	// * @since 2021-10-09 15:14:06
	// */
	//@Operation(summary = "保存(添加)用户", description = "保存(添加)用户", method = CommonConstant.POST,
	//	security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "保存(添加)用户")
	//@PreAuthorize("hasAuthority('sys:user:add')")
	//@PostMapping
	//public Result<AddUserVO> saveUser(
	//	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "保存(添加)用户DTO", required = true)
	//	@Validated @RequestBody UserDTO userDTO) {
	//	SysUser sysUser = UserMapper.INSTANCE.userDtoToSysUser(userDTO);
	//	SysUser result = sysUserService.saveUser(sysUser);
	//	AddUserVO addUserVO = UserMapper.INSTANCE.sysUserToAddUserVO(result);
	//	return Result.success(addUserVO);
	//}
	//
	///**
	// * 更新用户
	// *
	// * @param id      用户id
	// * @param userDTO 更新用户对象DTO
	// * @return {@link Result&lt;com.taotao.cloud.uc.api.vo.user.UserVO&gt;}
	// * @author shuigedeng
	// * @since 2021-10-09 15:14:24
	// */
	//@Operation(summary = "更新用户", description = "更新用户", method = CommonConstant.PUT,
	//	security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "更新用户")
	//@PreAuthorize("hasAuthority('sys:user:update')")
	//@PutMapping("/{id:[0-9]*}")
	//public Result<UserVO> updateUser(
	//	@Parameter(name = "id", description = "用户id", required = true, in = ParameterIn.PATH)
	//	@PathVariable(value = "id") Long id,
	//	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "更新用户对象DTO", required = true)
	//	@Validated @RequestBody UserDTO userDTO) {
	//	SysUser user = sysUserService.findUserInfoById(id);
	//	UserMapper.INSTANCE.copyUserDtoToSysUser(userDTO, user);
	//	SysUser updateUser = sysUserService.updateUser(user);
	//	UserVO result = UserMapper.INSTANCE.sysUserToUserVO(updateUser);
	//	return Result.success(result);
	//}
	//
	///**
	// * 根据手机号码查询用户是否存在
	// *
	// * @param phone 手机号码
	// * @return {@link Result&lt;java.lang.Boolean&gt;}
	// * @author shuigedeng
	// * @since 2021-10-09 15:14:58
	// */
	//@Operation(summary = "根据手机号码查询用户是否存在", description = "根据手机号码查询用户是否存在",
	//	method = CommonConstant.GET, security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "根据手机号码查询用户是否存在")
	//@PreAuthorize("hasAuthority('sys:user:exists:phone')")
	//@GetMapping("/exists/phone")
	//public Result<Boolean> existsByPhone(
	//	@Parameter(name = "phone", description = "手机号码", required = true, in = ParameterIn.QUERY)
	//	@NotBlank(message = "手机号码不能为空")
	//	@RequestParam(value = "phone") String phone) {
	//	Boolean result = sysUserService.existsByPhone(phone);
	//	return Result.success(result);
	//}
	//
	///**
	// * 根据用户id查询用户是否存在
	// *
	// * @param id 用户id
	// * @return {@link Result&lt;java.lang.Boolean&gt; }
	// * @author shuigedeng
	// * @since 2021-10-09 15:15:05
	// */
	//@Operation(summary = "根据用户id查询用户是否存在", description = "根据用户id查询用户是否存在",
	//	method = CommonConstant.GET, security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "根据用户id查询用户是否存在")
	//@PreAuthorize("hasAuthority('sys:user:exists:id')")
	//@GetMapping("/exists/id")
	//public Result<Boolean> existsByPhone(
	//	@Parameter(name = "id", description = "用户id", required = true, in = ParameterIn.QUERY)
	//	@NotNull(message = "用户id不能为空")
	//	@RequestParam(value = "id") Long id) {
	//	Boolean result = sysUserService.existsById(id);
	//	return Result.success(result);
	//}
	//
	///**
	// * 根据用户id删除用户
	// *
	// * @param id 用户id
	// * @return {@link Result&lt;java.lang.Boolean&gt; }
	// * @author shuigedeng
	// * @since 2021-10-09 15:15:12
	// */
	//@Operation(summary = "根据用户id删除用户", description = "根据用户id删除用户", method = CommonConstant.DELETE,
	//	security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "根据用户id删除用户")
	//@PreAuthorize("hasAuthority('sys:user:delete')")
	//@DeleteMapping("/{id:[0-9]*}")
	//public Result<Boolean> deleteUser(
	//	@Parameter(name = "id", description = "用户id", required = true, in = ParameterIn.PATH)
	//	@NotNull(message = "用户id不能为空")
	//	@PathVariable(value = "id") Long id) {
	//	Boolean result = sysUserService.removeUser(id);
	//	return Result.success(result);
	//}
	//
	///**
	// * 分页查询用户集合
	// *
	// * @param userQuery 分页查询用户集合DTO
	// * @return {@link Result&lt;PageModel&lt;com.taotao.cloud.uc.api.vo.user.UserVO&gt;&gt;}
	// * @author shuigedeng
	// * @since 2021-10-09 15:15:19
	// */
	//@Operation(summary = "分页查询用户集合", description = "分页查询用户集合", method = CommonConstant.GET,
	//	security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "分页查询用户集合")
	//@PreAuthorize("hasAuthority('sys:user:view:page')")
	//@GetMapping(value = "/page")
	//public Result<PageModel<UserVO>> findUserPage(
	//	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "分页查询用户集合DTO", required = true)
	//	@Validated UserPageQuery userQuery) {
	//	Pageable pageable = PageRequest.of(userQuery.getCurrentPage(), userQuery.getPageSize());
	//	Page<SysUser> page = sysUserService.findUserPage(pageable, userQuery);
	//	return Result.success(PageModel.convertJpaPage(new PageImpl<>(
	//			UserMapper.INSTANCE.sysUserToUserVO(page.getContent()),
	//			pageable,
	//			page.getTotalElements())
	//		)
	//	);
	//}
	//
	///**
	// * 重置密码
	// *
	// * @param id              用户id
	// * @param restPasswordDTO 重置密码DTO
	// * @return {@link Result&lt;java.lang.Boolean&gt; }
	// * @author shuigedeng
	// * @since 2021-10-09 15:15:57
	// */
	//@Operation(summary = "重置密码", description = "重置密码", method = CommonConstant.PUT,
	//	security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "重置密码")
	//@PreAuthorize("hasAuthority('sys:user:rest:password')")
	//@PutMapping("/rest/password/{id:[0-9]*}")
	//public Result<Boolean> restPass(
	//	@Parameter(name = "id", description = "用户id", required = true, in = ParameterIn.PATH)
	//	@NotNull(message = "用户id不能为空")
	//	@PathVariable(value = "id") Long id,
	//	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "重置密码DTO", required = true)
	//	@Validated @RequestBody RestPasswordUserDTO restPasswordDTO) {
	//	Boolean result = sysUserService.restPass(id, restPasswordDTO);
	//	return Result.success(result);
	//}
	//
	///**
	// * 获取当前登录人信息
	// *
	// * @return {@link Result&lt;com.taotao.cloud.uc.api.vo.user.UserVO&gt;}
	// * @author shuigedeng
	// * @since 2021-10-09 15:16:08
	// */
	//@Operation(summary = "获取当前登录人信息", description = "获取当前登录人信息",
	//	method = CommonConstant.GET, security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "获取当前登录人信息")
	//@PreAuthorize("hasAuthority('sys:user:info:current')")
	//@GetMapping("/current/info")
	//public Result<UserVO> getCurrentUser() {
	//	SecurityUser securityUser = SecurityUtil.getUser();
	//	if (Objects.isNull(securityUser)) {
	//		throw new BusinessException("用户未登录");
	//	}
	//	Long userId = securityUser.getUserId();
	//	SysUser user = sysUserService.findUserInfoById(userId);
	//	UserVO result = UserMapper.INSTANCE.sysUserToUserVO(user);
	//	return Result.success(result);
	//}
	//
	///**
	// * 根据id获取用户信息
	// *
	// * @param id 用户id
	// * @return {@link Result&lt;com.taotao.cloud.uc.api.vo.user.UserVO&gt; }
	// * @author shuigedeng
	// * @since 2021-10-09 16:42:03
	// */
	//@Operation(summary = "根据id获取用户信息", description = "根据id获取用户信息",
	//	method = CommonConstant.GET, security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "根据id获取用户信息")
	////@PreAuthorize("hasAuthority('sys:user:info:id')")
	//@GetMapping("/info/id/{id:[0-9]*}")
	//public Result<UserVO> findUserInfoById(
	//	@Parameter(name = "id", description = "用户id", required = true, in = ParameterIn.PATH)
	//	@NotNull(message = "用户id不能为空")
	//	@PathVariable(value = "id") Long id) {
	//	SysUser user = sysUserService.findUserInfoById(id);
	//	UserVO result = UserMapper.INSTANCE.sysUserToUserVO(user);
	//	return Result.success(result);
	//}
	//
	//
	///**
	// * 根据username获取用户信息
	// *
	// * @param username 用户名称
	// * @return {@link Result&lt;com.taotao.cloud.uc.api.vo.user.UserVO&gt; }
	// * @author shuigedeng
	// * @since 2021-10-09 16:41:53
	// */
	//@Operation(summary = "根据username获取用户信息", description = "根据username获取用户信息",
	//	method = CommonConstant.GET, security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "根据username获取用户信息")
	//@PreAuthorize("hasAuthority('sys:user:info:username')")
	//@GetMapping("/info/username")
	//public Result<UserVO> findUserInfoByUsername(
	//	@Parameter(name = "username", description = "用户名称", required = true, in = ParameterIn.QUERY)
	//	@NotBlank(message = "用户名称不能为空")
	//	@RequestParam(value = "username") String username) {
	//	SysUser user = sysUserService.findUserInfoByUsername(username);
	//	UserVO result = UserMapper.INSTANCE.sysUserToUserVO(user);
	//	return Result.success(result);
	//}
	//
	///**
	// * 查询用户集合
	// *
	// * @param userQuery 查询用户集合DTO
	// * @return {@link Result&lt;java.util.List&lt;com.taotao.cloud.uc.api.vo.user.UserVO&gt;&gt; }
	// * @author shuigedeng
	// * @since 2021-10-09 16:41:25
	// */
	//@Operation(summary = "查询用户集合", description = "查询用户集合", method = CommonConstant.GET,
	//	security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "查询用户集合")
	//@PreAuthorize("hasAuthority('sys:user:info:list')")
	//@GetMapping("/info")
	//public Result<List<UserVO>> findUserList(
	//	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "查询用户集合DTO", required = true)
	//	@Validated UserQuery userQuery) {
	//	List<SysUser> userList = sysUserService.findUserList(userQuery);
	//	List<UserVO> result = UserMapper.INSTANCE.sysUserToUserVO(userList);
	//	return Result.success(result);
	//}
	//
	///**
	// * 根据用户id更新角色信息(用户分配角色)
	// *
	// * @param userRoleDTO 用户分配角色DTO
	// * @return {@link Result&lt;java.lang.Boolean&gt; }
	// * @author shuigedeng
	// * @since 2021-10-09 16:41:06
	// */
	//@Operation(summary = "根据用户id更新角色信息(用户分配角色)", description = "根据用户id更新角色信息(用户分配角色)",
	//	method = CommonConstant.GET, security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "根据用户id更新角色信息(用户分配角色)")
	//@PreAuthorize("hasAuthority('sys:user:role')")
	//@PutMapping("/role")
	//public Result<Boolean> updateUserRoles(
	//	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户分配角色DTO", required = true)
	//	@Validated @RequestBody UserRoleDTO userRoleDTO) {
	//	Boolean result = sysUserService.updateUserRoles(userRoleDTO);
	//	return Result.success(result);
	//}

	// **********************内部微服务接口*****************************

	// @ApiIgnore
	// @ApiOperation("第三方登录调用获取用户信息")
	// @SysOperateLog(description = "第三方登录调用获取用户信息")
	// @GetMapping("/info/social")
	// public Result<SecurityUser> getUserInfoBySocial(@RequestParam(value = "providerId") String providerId,
	//                                                 @RequestParam(value = "providerUserId") int providerUserId) {
	//     SysUser sysUser = sysUserService.getUserBySocial(providerId, providerUserId);
	//     SecurityUser securityUser = new SecurityUser(sysUser.getId(), sysUser.getUsername(),
	//             sysUser.getPassword(), CollectionUtil.newHashSet(), CollectionUtil.newHashSet());
	//     BeanUtil.copyIncludeNull(sysUser, securityUser);
	//     return Result.succeed(securityUser);
	// }

}

