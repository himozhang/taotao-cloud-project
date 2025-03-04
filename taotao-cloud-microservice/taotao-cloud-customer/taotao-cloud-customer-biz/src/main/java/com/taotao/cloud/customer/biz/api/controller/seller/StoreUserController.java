package com.taotao.cloud.customer.biz.api.controller.seller;

import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.common.model.SecurityUser;
import com.taotao.cloud.common.utils.common.SecurityUtil;
import com.taotao.cloud.logger.annotation.RequestLogger;
import com.taotao.cloud.member.api.feign.IFeignMemberService;
import com.taotao.cloud.member.api.vo.MemberVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 店铺端,管理员接口
 */
@Validated
@RestController
@Tag(name = "店铺端-管理员接口", description = "店铺端-管理员接口")
@RequestMapping("/store/user")
public class StoreUserController {

	@Autowired
	private IFeignMemberService memberService;

	@Operation(summary = "获取当前登录用户接口", description = "获取当前登录用户接口")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/info")
	public Result<MemberVO> getUserInfo() {
		SecurityUser tokenUser = SecurityUtil.getCurrentUser();
		if (tokenUser != null) {
			MemberVO member = memberService.findByUsername(tokenUser.getUsername());
			// member.setPassword(null);
			return Result.success(member);
		}
		throw new BusinessException(ResultEnum.USER_NOT_LOGIN);
	}


}
